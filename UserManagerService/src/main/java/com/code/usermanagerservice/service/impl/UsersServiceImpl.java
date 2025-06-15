package com.code.usermanagerservice.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.usermanagerservice.model.entity.User;
import com.code.usermanagerservice.service.UsersService;
import com.code.usermanagerservice.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

/**
* @author 寒春初一
* @description 针对表【users_0(用户表)】的数据库操作Service实现
* @createDate 2025-06-14 20:58:50
*/
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, User>
implements UsersService {

}
