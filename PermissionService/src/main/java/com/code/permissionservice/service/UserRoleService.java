package com.code.permissionservice.service;

import com.code.permissionservice.model.entity.UserRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 寒春初一
* @description 针对表【user_roles_0】的数据库操作Service
* @createDate 2025-06-15 20:14:37
*/
public interface UserRoleService extends IService<UserRole> {
    public boolean bindDefaultRole(Long userId);
    public int getUserRoleCode(Long userId);
    // 超管调用：升级用户为管理员
    public boolean upgradeToAdmin(Long userId,Long myUserId,String ip);

    // 超管调用：降级用户为普通角色
    public boolean downgradeToUser(Long userId,Long myUserId,String ip);
    public List<Long> getUserRoleCodeList(Long userId);
    public List<Long> getAllRoleCodeList(Long userId );
}
