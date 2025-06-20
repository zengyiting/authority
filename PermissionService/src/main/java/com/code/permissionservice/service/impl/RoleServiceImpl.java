package com.code.permissionservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.code.permissionservice.service.RoleService;
import com.code.permissionservice.model.entity.Role;
import com.code.permissionservice.mapper.RoleMapper;
import org.springframework.stereotype.Service;

/**
* @author 寒春初一
* @description 针对表【roles】的数据库操作Service实现
* @createDate 2025-06-15 20:14:51
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService {

}




