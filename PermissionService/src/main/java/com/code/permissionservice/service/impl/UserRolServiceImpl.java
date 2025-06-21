package com.code.permissionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.permissionservice.model.enums.RoleEnum;
import com.code.permissionservice.service.UserRoleService;
import com.code.permissionservice.model.entity.UserRole;
import com.code.permissionservice.mapper.UserRoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author 寒春初一
* @description 针对表【user_roles_0】的数据库操作Service实现
* @createDate 2025-06-15 20:14:37
*/
@Slf4j
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

    @Override
    public int getUserRoleCode(Long userId) {
        if(userId == null){
            return 0;
        }

        UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("user_id", userId));
        if (userRole == null){
            return 0;
        }
        log.info("用户角色：{}", userRole.getRoleId());
        return userRole.getRoleId();
    }

    @Override
    public boolean upgradeToAdmin(Long userId) {
        if(userId == null){
            return false;
        }
        UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("user_id", userId));
        if(userRole == null){
            return false;
        }
        userRole.setRoleId(RoleEnum.ADMIN.getCode());
        return userRoleMapper.updateById(userRole) > 0? true : false;
    }

    @Override
    public boolean downgradeToUser(Long userId) {
        if(userId == null){
            return false;
        }
        UserRole userRole = userRoleMapper.selectOne(new QueryWrapper<UserRole>().eq("user_id", userId));
        if(userRole == null){
            return false;
        }
        userRole.setRoleId(RoleEnum.USER.getCode());
        return userRoleMapper.updateById(userRole) > 0? true : false;
    }

    @Override
    public List<Long> getUserRoleCodeList(Long userId) {
        LambdaQueryWrapper<UserRole> queryWrapperUser = new LambdaQueryWrapper<>();
        queryWrapperUser.eq(UserRole::getUserId, userId);
        UserRole userRole = userRoleMapper.selectOne(queryWrapperUser);
        int roleCode = userRole.getRoleId();
        if (roleCode == RoleEnum.ADMIN.getCode()){
            List<Long> roleCodeList = new ArrayList<>();
            LambdaQueryWrapper<UserRole> queryWrapperList = new LambdaQueryWrapper<>();
            queryWrapperList.eq(UserRole::getRoleId, RoleEnum.USER.getCode());
            List<UserRole>  userRoleList = userRoleMapper.selectList(queryWrapperList);
            for (UserRole userRole1 : userRoleList) {
                roleCodeList.add(userRole1.getUserId());
            }
            return roleCodeList;
        }
        return null;
    }

    @Override
    public List<Long> getAllRoleCodeList(Long userId) {
        LambdaQueryWrapper<UserRole> queryWrapperUser = new LambdaQueryWrapper<>();
        queryWrapperUser.eq(UserRole::getUserId, userId);
        UserRole userRole = userRoleMapper.selectOne(queryWrapperUser);
        int roleCode = userRole.getRoleId();
        if(roleCode == RoleEnum.SUPER_ADMIN.getCode()){
            List<Long> roleCodeList = new ArrayList<>();
            List<UserRole>  userRoleList = userRoleMapper.selectList(new LambdaQueryWrapper<>());
            for (UserRole userRole1 : userRoleList) {
                roleCodeList.add(userRole1.getUserId());
            }
            return roleCodeList;

        }
        return null;
    }
}




