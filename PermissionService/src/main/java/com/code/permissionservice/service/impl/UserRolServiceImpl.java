package com.code.permissionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.permissionservice.model.enums.RoleEnum;
import com.code.permissionservice.service.UserRoleService;
import com.code.permissionservice.model.entity.UserRole;
import com.code.permissionservice.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author 寒春初一
* @description 针对表【user_roles_0】的数据库操作Service实现
* @createDate 2025-06-15 20:14:37
*/
@Service
public class UserRolServiceImpl extends ServiceImpl<UserRoleMapper, UserRole>
    implements UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Override
    public boolean bindDefaultRole(Long userId) {

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(RoleEnum.USER.getCode());
        return userRoleMapper.insert(userRole) > 0? true : false;
    }
}




