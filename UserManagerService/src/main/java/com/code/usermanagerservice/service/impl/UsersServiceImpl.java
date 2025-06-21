package com.code.usermanagerservice.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.usermanagerservice.common.BusinessException;
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

import java.util.ArrayList;
import java.util.List;
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
        String code =  new RandomNumUtil().getRandomNum();
        stringRedisTemplate.opsForValue().set(userSmsRequest.getPhone(),code, TimeOutEnum.CODE_TIME_OUT.getTimeOut(), TimeUnit.MINUTES);
        log.info("发送短信验证码：{}",code);
    }
    @GlobalTransactional
    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        String phone = userRegisterRequest.getPhone();
        String password = userRegisterRequest.getPassword();
        String code = stringRedisTemplate.opsForValue().get(userRegisterRequest.getPhone());
        if (code==null||!code.equals(userRegisterRequest.getCode())){
            //验证码错误
            throw new BusinessException(ErrorEnum.CODE_ERROR.getMessage());
        }
        LambdaQueryWrapper<UserPhoneIndex> indexWrapper = new LambdaQueryWrapper<>();
        indexWrapper.eq(UserPhoneIndex::getPhone,phone);
        UserPhoneIndex userPhoneIndex = userPhoneIndexMapper.selectOne(indexWrapper);
        if (userPhoneIndex != null){
            //如果手机号已存在，就说用户已经注册
            throw new BusinessException(ErrorEnum.REGISTER_ERROR.getMessage());//
        }
        //创建用户
        User user = new User();
        Snowflake snowflake = IdUtil.getSnowflake(Integer.parseInt(ConfigEnum.WORKED_ID.getValue()), Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
        String encodedPassword = DigestUtils.md5DigestAsHex((ConfigEnum.PASSWORD_SALT.getValue() + password).getBytes());
        log.info("用户密码：{}",encodedPassword);
        user.setUserId(snowflake.nextId());
        log.info("用户ID：{}",user.getUserId());
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(encodedPassword);
        int insert = usersMapper.insert(user);
        if(insert==0){
            throw new BusinessException(ErrorEnum.MYSQL_ERROR.getMessage());
        }
        boolean bindSuccess = permissionClient.bindDefaultRole(user.getUserId());
        if (!bindSuccess){
            throw new BusinessException(ErrorEnum.BIND_ERROR.getMessage());
        }
        LogSaveRequest logSaveRequest = new LogSaveRequest();
        logSaveRequest.setUserId(user.getUserId());
        logSaveRequest.setAction(ActionEnum.REGISTER.getAction());
        logSaveRequest.setIp(userRegisterRequest.getIp());
        logSaveRequest.setDetail("用户注册成功");
        rabbitTemplate.convertAndSend(ConfigEnum.EXCHANGE_NAME.getValue(),ConfigEnum.ROUTING_KEY.getValue(),logSaveRequest);
    }

    @Override
    public UserLoginResponse login(UserLoginRequest userLoginRequest) {
        String userName = userLoginRequest.getUsername();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,userName);
        User user = usersMapper.selectOne(wrapper);
        if (user==null){
            //用户不存在
            throw new BusinessException(ErrorEnum.USER_NOT_EXIST.getMessage());
        }
        String encodedPassword = DigestUtils.md5DigestAsHex((ConfigEnum.PASSWORD_SALT.getValue() + userLoginRequest.getPassword()).getBytes());
        if (!encodedPassword.equals(user.getPassword())){
            //密码错误
            throw new BusinessException(ErrorEnum.USER_PASSWORD_ERROR.getMessage());
        }
        if (user.getStatus()==0){
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
        log.info("用户{}登录成功，id{}",userName,user.getUserId());
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
        return refreshTokenResponse;

    }

    @Override
    public List<UserResponse> getUserList(Long userId) {
        if (userId==null){
            throw new BusinessException("用户ID不能为空");
        }
        int roleCode = permissionClient.getUserRoleCode(userId);
        if (roleCode==0){
            throw new BusinessException(ErrorEnum.GET_ROLE_ERROR.getMessage());
        }
        User user = usersMapper.selectById(userId);
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(user.getUserId());
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setPhone(user.getPhone());
        userResponse.setStatus(user.getStatus());
        if(roleCode == RoleEnum.USER.getCode()){
            List<UserResponse> userList = new ArrayList<>();
            userList.add(userResponse);
            return userList;
        }
            List<Long> userIdList = new ArrayList<>();
            if (roleCode == RoleEnum.ADMIN.getCode()) {
              userIdList = permissionClient.getUserRoleCodeList(userId);
              userIdList.add(userId);
            }else if (roleCode == RoleEnum.SUPER_ADMIN.getCode()) {
                userIdList = permissionClient.getAllRoleCodeList(userId);
            }
            List<User> userList = usersMapper.selectBatchIds(userIdList);
            List<UserResponse> userResponseList = userList.stream().map(userItem -> {
                UserResponse userResponseItem = new UserResponse();
                userResponseItem.setUserId(userItem.getUserId());
                userResponseItem.setUsername(userItem.getUsername());
                userResponseItem.setEmail(userItem.getEmail());
                userResponseItem.setPhone(userItem.getPhone());
                userResponseItem.setStatus(userItem.getStatus());
                return userResponseItem;
            }).collect(Collectors.toList());
            log.info("用户{}获取用户列表成功",userId);
            return userResponseList;

        }





}
