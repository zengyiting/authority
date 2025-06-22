package com.code.usermanagerservice.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.usermanagerservice.common.BusinessException;
import com.code.usermanagerservice.common.PageResult;
import com.code.usermanagerservice.enums.*;
import com.code.usermanagerservice.mapper.UserPhoneIndexMapper;
import com.code.usermanagerservice.model.dto.*;
import com.code.usermanagerservice.model.entity.User;
import com.code.usermanagerservice.model.entity.UserPhoneIndex;

import com.code.usermanagerservice.service.UsersService;
import com.code.usermanagerservice.mapper.UsersMapper;

import com.code.usermanagerservice.service.feignclient.PermissionClient;
import com.code.usermanagerservice.utils.JwtUtil;
import com.code.usermanagerservice.utils.RandomNumUtil;

//import io.seata.spring.annotation.GlobalTransactional;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, User>
        implements UsersService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PermissionClient permissionClient;
    @Autowired
    private UserPhoneIndexMapper userPhoneIndexMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendSMS(UserSmsRequest userSmsRequest) {
        String code = new RandomNumUtil().getRandomNum();
        stringRedisTemplate.opsForValue().set(userSmsRequest.getPhone(), code, TimeOutEnum.CODE_TIME_OUT.getTimeOut(), TimeUnit.MINUTES);
        log.info("发送短信验证码：{}", code);
    }

    @GlobalTransactional
    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        String phone = userRegisterRequest.getPhone();
        String password = userRegisterRequest.getPassword();
        String code = stringRedisTemplate.opsForValue().get(userRegisterRequest.getPhone());
        if (code == null || !code.equals(userRegisterRequest.getCode())) {
            //验证码错误
            throw new BusinessException(ErrorEnum.CODE_ERROR.getMessage());
        }
        LambdaQueryWrapper<UserPhoneIndex> indexWrapper = new LambdaQueryWrapper<>();
        indexWrapper.eq(UserPhoneIndex::getPhone, phone);
        UserPhoneIndex userPhoneIndex = userPhoneIndexMapper.selectOne(indexWrapper);
        if (userPhoneIndex != null) {
            //如果手机号已存在，就说用户已经注册
            throw new BusinessException(ErrorEnum.REGISTER_ERROR.getMessage());//
        }
        //创建用户
        User user = new User();
        Snowflake snowflake = IdUtil.getSnowflake(Integer.parseInt(ConfigEnum.WORKED_ID.getValue()), Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
        String encodedPassword = DigestUtils.md5DigestAsHex((ConfigEnum.PASSWORD_SALT.getValue() + password).getBytes());
        log.info("用户密码：{}", encodedPassword);
        user.setUserId(snowflake.nextId());
        log.info("用户ID：{}", user.getUserId());
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(encodedPassword);
        int insert = usersMapper.insert(user);
        if (insert == 0) {
            throw new BusinessException(ErrorEnum.MYSQL_ERROR.getMessage());
        }
        boolean bindSuccess = permissionClient.bindDefaultRole(user.getUserId());
        if (!bindSuccess) {
            throw new BusinessException(ErrorEnum.BIND_ERROR.getMessage());
        }
        UserPhoneIndex phoneIndex= new UserPhoneIndex();
        phoneIndex.setPhone(phone);
        phoneIndex.setUserId(user.getUserId());
        int insert1 = userPhoneIndexMapper.insert(phoneIndex);
        if (insert1 == 0) {
            throw new BusinessException(ErrorEnum.MYSQL_ERROR.getMessage());
        }
        LogSaveRequest logSaveRequest = new LogSaveRequest();
        logSaveRequest.setUserId(user.getUserId());
        logSaveRequest.setAction(ActionEnum.REGISTER.getAction());
        logSaveRequest.setIp(userRegisterRequest.getIp());
        logSaveRequest.setDetail("用户注册成功");
        log.info("用户{}注册成功，来自{}",userRegisterRequest.getUsername(),userRegisterRequest.getIp());
        rabbitTemplate.convertAndSend(ConfigEnum.EXCHANGE_NAME.getValue(), ConfigEnum.ROUTING_KEY.getValue(), logSaveRequest);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        String userName = userLoginRequest.getUsername();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userName);
        User user = usersMapper.selectOne(wrapper);
        if (user == null) {
            //用户不存在
            throw new BusinessException(ErrorEnum.USER_NOT_EXIST.getMessage());
        }
        String encodedPassword = DigestUtils.md5DigestAsHex((ConfigEnum.PASSWORD_SALT.getValue() + userLoginRequest.getPassword()).getBytes());
        if (!encodedPassword.equals(user.getPassword())) {
            //密码错误
            throw new BusinessException(ErrorEnum.USER_PASSWORD_ERROR.getMessage());
        }
        if (user.getStatus() == 0) {
            //用户被禁用
            throw new BusinessException(ErrorEnum.USER_DISABLED.getMessage());
        }

        //生成token
        String userId = user.getUserId().toString();
        String accessToken = JwtUtil.generateAccessToken(userId);
        String refreshToken = JwtUtil.generateRefreshToken(userId);
        stringRedisTemplate.opsForValue().set(
                ConfigEnum.REFRESH_TOKEN_KEY.getValue() + userId,
                refreshToken,
                TimeOutEnum.REFRESH_TOKEN_TIMEOUT.getTimeOut(),
                TimeUnit.MINUTES
        );
        UserLoginResponse userLoginResponse = new UserLoginResponse();
        userLoginResponse.setAccessToken(accessToken);
        userLoginResponse.setRefreshToken(refreshToken);
        userLoginResponse.setExpiresIn(TimeOutEnum.ACCESS_TOKEN_TIMEOUT.getTimeOut());
        userLoginResponse.setUserId(user.getUserId());
        userLoginResponse.setName(user.getUsername());
        log.info("用户{}登录成功，id{}", userName, user.getUserId());
        return userLoginResponse;


    }

    @Override
    public RefreshTokenResponse refresh(String refreshToken) {
        if (StringUtils.isBlank(refreshToken)) {
            throw new BusinessException("refreshToken不能为空");
        }
        String userId = JwtUtil.getUserId(refreshToken);
        if (StringUtils.isBlank(userId)) {
            throw new BusinessException("refreshToken无效");
        }
        String redisRefreshToken = stringRedisTemplate.opsForValue().get(
                ConfigEnum.REFRESH_TOKEN_KEY.getValue() + userId
        );
        if (!refreshToken.equals(redisRefreshToken)) {
            throw new BusinessException("refreshToken无效");
        }
        stringRedisTemplate.delete(ConfigEnum.REFRESH_TOKEN_KEY.getValue() + userId);
        String accessToken = JwtUtil.generateAccessToken(userId);
        String newRefreshToken = JwtUtil.generateRefreshToken(userId);
        stringRedisTemplate.opsForValue().set(
                ConfigEnum.REFRESH_TOKEN_KEY.getValue() + userId,
                newRefreshToken,
                TimeOutEnum.REFRESH_TOKEN_TIMEOUT.getTimeOut(),
                TimeUnit.MINUTES
        );
        RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse();
        refreshTokenResponse.setAccessToken(accessToken);
        refreshTokenResponse.setRefreshToken(newRefreshToken);
        refreshTokenResponse.setExpiresIn(TimeOutEnum.ACCESS_TOKEN_TIMEOUT.getTimeOut());
        log.info("用户{}刷新token成功", userId);
        return refreshTokenResponse;

    }

    @Override
    public UserInfoDto getUserInfo(Long myUserId, Long userId) {
        if (myUserId == null) {
            throw new BusinessException(ErrorEnum.USER_ID_NOT_NULL.getMessage());
        }
        if (userId == null) {
            throw new BusinessException(ErrorEnum.TARGET_USER_ID_NOT_NULL.getMessage());
        }
        int myRoleCode = permissionClient.getUserRoleCode(myUserId);
        int roleCode = permissionClient.getUserRoleCode(userId);
        if (roleCode == 0 || myRoleCode == 0) {
            throw new BusinessException(ErrorEnum.GET_ROLE_ERROR.getMessage());
        }
        User user = usersMapper.selectById(userId);
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setUserId(user.getUserId());
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setPhone(user.getPhone());
        userInfoDto.setStatus(user.getStatus());


        if (myRoleCode == RoleEnum.USER.getCode()) {
            if (!userId.equals(myUserId)) {

                throw new BusinessException(ErrorEnum.FORBIDDEN.getMessage());
            }
        }

        if (myRoleCode == RoleEnum.ADMIN.getCode()) {
            if (roleCode == RoleEnum.SUPER_ADMIN.getCode()) {
                throw new BusinessException(ErrorEnum.FORBIDDEN.getMessage());
            }
        }
        log.info("获取用户信息成功：{}", userInfoDto);
        return userInfoDto;


    }

    @Override
    public PageResult<UserInfoDto> getUserList(UserPageRequest userPageRequest, Long userId) {
        Page<User> page = new Page<>(userPageRequest.getPageNum(), userPageRequest.getPageSize());
        int roleCode = permissionClient.getUserRoleCode(userId);
        if (roleCode == 0) {
            throw new BusinessException(ErrorEnum.GET_ROLE_ERROR.getMessage());
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (roleCode == RoleEnum.USER.getCode()) {
            queryWrapper.eq(User::getUserId, userId);
        } else if (roleCode == RoleEnum.ADMIN.getCode()) {
            List<Long> userIdList = permissionClient.getUserRoleCodeList(userId);
            queryWrapper.in(User::getUserId, userIdList);

        } else {
            queryWrapper.in(User::getUserId, permissionClient.getAllRoleCodeList(userId));
        }
        Page<User> userPage = usersMapper.selectPage(page, queryWrapper);
        PageResult<UserInfoDto> pageResult = new PageResult<>();
        pageResult.setPageNum(userPage.getCurrent());
        pageResult.setPageSize(userPage.getSize());
        pageResult.setTotal(userPage.getTotal());
        List<UserInfoDto> userResponseList = userPage.getRecords().stream().map(user -> {
            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setUserId(user.getUserId());
            userInfoDto.setUsername(user.getUsername());
            userInfoDto.setEmail(user.getEmail());
            userInfoDto.setPhone(user.getPhone());
            userInfoDto.setStatus(user.getStatus());

            return userInfoDto;
        }).collect(Collectors.toList());
        pageResult.setRecords(userResponseList);

        log.info("用户{}获取用户列表成功", userId);
        return pageResult;


    }

    @Override
    public void updateUser(Long myUserId, UserInfoDto userInfoDto, String ip) {
        Long userId = userInfoDto.getUserId();
        if (myUserId == null) {
            throw new BusinessException(ErrorEnum.USER_ID_NOT_NULL.getMessage());
        }
        if (userId == null) {
            throw new BusinessException(ErrorEnum.TARGET_USER_ID_NOT_NULL.getMessage());
        }

        int myRoleCode = permissionClient.getUserRoleCode(myUserId);
        int roleCode = permissionClient.getUserRoleCode(userId);
        if (roleCode == 0 || myRoleCode == 0) {
            throw new BusinessException(ErrorEnum.GET_ROLE_ERROR.getMessage());
        }

        if (myRoleCode == RoleEnum.USER.getCode()) {
            if (!userId.equals(myUserId)) {
                throw new BusinessException(ErrorEnum.FORBIDDEN.getMessage());
            }
        }
        if (myRoleCode == RoleEnum.ADMIN.getCode()) {
            if (roleCode == RoleEnum.SUPER_ADMIN.getCode()) {
                throw new BusinessException(ErrorEnum.FORBIDDEN.getMessage());
            }
        }

        User user = usersMapper.selectById(userId);
        List<Map<String, String>> detailList = new ArrayList<>();

        if (!Objects.equals(user.getUsername(), userInfoDto.getUsername())) {
            detailList.add(safeMap("username", user.getUsername(), userInfoDto.getUsername()));
            user.setUsername(userInfoDto.getUsername());
        }

        if (!Objects.equals(user.getEmail(), userInfoDto.getEmail())) {
            detailList.add(safeMap("email", user.getEmail(), userInfoDto.getEmail()));
            user.setEmail(userInfoDto.getEmail());
        }

        if (!Objects.equals(user.getPhone(), userInfoDto.getPhone())) {
            UserPhoneIndex  userPhoneIndex = userPhoneIndexMapper.selectById(user.getPhone());
            userPhoneIndex.setPhone(userInfoDto.getPhone());
            userPhoneIndexMapper.updateById(userPhoneIndex);
            detailList.add(safeMap("phone", user.getPhone(), userInfoDto.getPhone()));
            user.setPhone(userInfoDto.getPhone());
        }

        if (!Objects.equals(user.getStatus(), userInfoDto.getStatus())) {
            detailList.add(safeMap("status",
                    user.getStatus() == null ? null : String.valueOf(user.getStatus()),
                    userInfoDto.getStatus() == null ? null : String.valueOf(userInfoDto.getStatus())
            ));
            user.setStatus(userInfoDto.getStatus());
        }

        int rows = usersMapper.updateById(user);
        if (rows == 0) {
            throw new BusinessException(ErrorEnum.MYSQL_ERROR.getMessage());
        }

        if (!detailList.isEmpty()) {
            detailList.add(safeMap("fixedUserId", String.valueOf(userInfoDto.getUserId()), null));
            LogSaveRequest logSaveRequest = new LogSaveRequest();
            logSaveRequest.setUserId(myUserId);
            logSaveRequest.setAction("更新用户信息");
            logSaveRequest.setIp(ip);
            logSaveRequest.setDetail(JSON.toJSONString(detailList));
            rabbitTemplate.convertAndSend(ConfigEnum.EXCHANGE_NAME.getValue(), ConfigEnum.ROUTING_KEY.getValue(), logSaveRequest);
            log.info("发送日志成功");
        }
    }//部分通过ai修复，因为多处地方需要进行同样的修改，直接让ai代做了，修改后我进行审核后无误。

    //防止空指针问题
    private Map<String, String> safeMap(String field, String oldVal, String newVal) {
        Map<String, String> map = new HashMap<>();
        map.put("field", field);
        map.put("old", oldVal == null ? "null" : oldVal);
        map.put("new", newVal == null ? "null" : newVal);
        return map;
    }//by ai


    @Override
    public void resetPassword(Long userId, ResetPasswordRequest resetPasswordRequest) {
        String password = resetPasswordRequest.getPassword();
        String ip = resetPasswordRequest.getIp();
        if (userId == null) {
            throw new BusinessException(ErrorEnum.USER_ID_NOT_NULL.getMessage());
        }
        int roleCode = permissionClient.getUserRoleCode(userId);
        if (roleCode == 0) {
            throw new BusinessException(ErrorEnum.GET_ROLE_ERROR.getMessage());
        }
        String encodedPassword = DigestUtils.md5DigestAsHex((ConfigEnum.PASSWORD_SALT.getValue() + password).getBytes());
        if (roleCode == RoleEnum.USER.getCode()) {
            User user = usersMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(ErrorEnum.USER_NOT_EXIST.getMessage());
            }
            user.setPassword(encodedPassword);
            usersMapper.updateById(user);
            log.info("用户重置密码成功");
            LogSaveRequest logUserSaveRequest = new LogSaveRequest();
            logUserSaveRequest.setUserId(userId);
            logUserSaveRequest.setAction("重置密码");
            logUserSaveRequest.setIp(ip);
            Map<String, String> detail = new HashMap<>();
            detail.put("filed", "password");
            detail.put("new", encodedPassword);
            logUserSaveRequest.setDetail(JSON.toJSONString(detail));
            rabbitTemplate.convertAndSend(ConfigEnum.EXCHANGE_NAME.getValue(), ConfigEnum.ROUTING_KEY.getValue(), logUserSaveRequest);
            return;
        }
        List<Long> userIdList = new ArrayList<>();
        if (roleCode == RoleEnum.ADMIN.getCode()) {
            userIdList = permissionClient.getUserRoleCodeList(userId);
        } else {
            userIdList = permissionClient.getAllRoleCodeList(userId);
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(User::getUserId, userIdList);
        List<User> userList = usersMapper.selectList(queryWrapper);
        userList.forEach(user -> {
            user.setPassword(encodedPassword);
        });
        this.updateBatchById(userList);
        log.info("重置密码成功");
        LogSaveRequest logSaveRequest = new LogSaveRequest();
        logSaveRequest.setUserId(userId);
        logSaveRequest.setAction("重置密码");
        logSaveRequest.setIp(ip);
        Map<String, String> detail = new HashMap<>();
        detail.put("filed", "password");
        detail.put("new", encodedPassword);
        logSaveRequest.setDetail(JSON.toJSONString(detail));
        rabbitTemplate.convertAndSend(ConfigEnum.EXCHANGE_NAME.getValue(), ConfigEnum.ROUTING_KEY.getValue(), logSaveRequest);


    }


}
