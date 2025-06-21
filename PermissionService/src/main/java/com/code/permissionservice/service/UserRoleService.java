package com.code.permissionservice.service;

import com.code.permissionservice.model.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 寒春初一
* @description 针对表【user_roles_0】的数据库操作Service
* @createDate 2025-06-15 20:14:37
*/
public interface UserRoleService extends IService<UserRole> {
    public boolean bindDefaultRole(Long userId);
}
