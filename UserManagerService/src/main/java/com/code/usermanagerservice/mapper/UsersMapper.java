package com.code.usermanagerservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.code.usermanagerservice.model.entity.User;

import org.apache.ibatis.annotations.Mapper;

/**
* @author 寒春初一
* @description 针对表【users_0(用户表)】的数据库操作Mapper
* @createDate 2025-06-14 20:58:50
* @Entity generator.com.code.usermanagerservice.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<User> {


}
