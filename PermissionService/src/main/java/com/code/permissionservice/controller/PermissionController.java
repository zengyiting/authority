package com.code.permissionservice.controller;

import com.code.permissionservice.Utils.IpUtil;
import com.code.permissionservice.common.Result;
import com.code.permissionservice.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/p1")
public class PermissionController {
    @Autowired
    private UserRoleService userRoleService;
    @PostMapping("/default")
    public boolean bindDefaultRole(@RequestParam Long userId) {

        return userRoleService.bindDefaultRole(userId);
    }
    @PostMapping("/getRoleCode")
    public int getUserRoleCode(@RequestParam Long userId) {
        return userRoleService.getUserRoleCode(userId);
    }
    @PostMapping("/upgrade")
    public Result<String> upgradeToAdmin(@RequestParam Long userId,HttpServletRequest request) {
        String ip = IpUtil.getClientIp(request);
        String myUserId = request.getHeader("X-User-Id");
       if(userRoleService.upgradeToAdmin(userId,Long.valueOf(myUserId),ip)){
           return Result.success("升级成功");
       }else {
           return Result.fail("升级失败");
       }
    }
    @PostMapping("/downgrade")
    public Result<String> downgradeToUser(@RequestParam Long userId, HttpServletRequest request) {
        String myUserId = request.getHeader("X-User-Id");
        String ip = IpUtil.getClientIp(request);
        if(userRoleService.downgradeToUser(userId,Long.parseLong(myUserId),ip)){
            return Result.success("降级成功");
        }else {
            return Result.fail("降级失败");
        }
    }
    @GetMapping("/getUserRoleCodeList")
    public List<Long> getUserRoleCodeList(@RequestParam Long userId) {
        return userRoleService.getUserRoleCodeList(userId);
    }
    @GetMapping ("/getAllRoleCodeList")
    public List<Long> getAllRoleCodeList(@RequestParam Long userId) {
        return userRoleService.getAllRoleCodeList(userId);
    }
}
