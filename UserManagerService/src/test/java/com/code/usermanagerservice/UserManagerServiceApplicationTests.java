package com.code.usermanagerservice;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.code.usermanagerservice.enums.ConfigEnum;
import com.code.usermanagerservice.mapper.UsersMapper;
import com.code.usermanagerservice.model.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
//@EnableFeignClients
class UserManagerServiceApplicationTests {
    @Autowired
    private UsersMapper usersMapper;
    @Test
    void contextLoads() {
    }
    @Test
    public void testCreateUser() {

    }

}
