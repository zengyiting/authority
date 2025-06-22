package com.code.permissionservice.controller;

import com.code.permissionservice.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permission")
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
    public boolean upgradeToAdmin(@RequestParam Long userId) {
        return userRoleService.upgradeToAdmin(userId);
    }
    @PostMapping("/downgrade")
    public boolean downgradeToUser(@RequestParam Long userId) {
        return userRoleService.downgradeToUser(userId);
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
