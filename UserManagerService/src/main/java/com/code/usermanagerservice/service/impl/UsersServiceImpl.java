package com.code.usermanagerservice.service.impl;



import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.usermanagerservice.common.BusinessException;
import com.code.usermanagerservice.enums.ActionEnum;
import com.code.usermanagerservice.enums.ConfigEnum;
import com.code.usermanagerservice.enums.ErrorEnum;
import com.code.usermanagerservice.enums.TimeOutEnum;
import com.code.usermanagerservice.mapper.UserPhoneIndexMapper;
import com.code.usermanagerservice.model.dto.LogSaveRequest;
import com.code.usermanagerservice.model.dto.UserRegisterRequest;
import com.code.usermanagerservice.model.dto.UserSmsRequest;
import com.code.usermanagerservice.model.entity.User;
import com.code.usermanagerservice.model.entity.UserPhoneIndex;

import com.code.usermanagerservice.service.UsersService;
import com.code.usermanagerservice.mapper.UsersMapper;

import com.code.usermanagerservice.service.feignclient.PermissionClient;
import com.code.usermanagerservice.utils.RandomNumUtil;

//import io.seata.spring.annotation.GlobalTransactional;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.util.DigestUtils;

import java.util.concurrent.TimeUnit;

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


}
