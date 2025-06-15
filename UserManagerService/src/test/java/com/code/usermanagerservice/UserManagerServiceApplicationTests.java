package com.code.usermanagerservice;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.code.usermanagerservice.mapper.UsersMapper;
import com.code.usermanagerservice.model.entity.User;
import com.code.usermanagerservice.model.enums.ConfigEnum;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserManagerServiceApplicationTests {
    @Autowired
    private UsersMapper usersMapper;
    @Test
    void contextLoads() {
    }
    @Test
    public void testCreateUser() {
            for(int i = 0; i < 10; i++){
                User user = new User();
                Snowflake snowflake = IdUtil.getSnowflake(Integer.parseInt(ConfigEnum.WORKED_ID.getValue()), Integer.parseInt(ConfigEnum.DATACENTER_ID.getValue()));
                user.setUserId(snowflake.nextId());
                user.setUsername("user" + i);
                user.setPassword("password" + i);
                user.setEmail("user" + i + "@example.com");
                user.setPhone("1234567890" + i);
                user.setStatus(1);
                user.setIsDeleted(0);

                usersMapper.insert(user);
            }
    }

}
