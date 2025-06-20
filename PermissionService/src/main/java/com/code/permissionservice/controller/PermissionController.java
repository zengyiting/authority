package com.code.permissionservice.controller;

import com.code.permissionservice.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private UserRoleService userRoleService;
    @PostMapping("/default")
    public boolean bindDefaultRole(@RequestParam Long userId) {
        return userRoleService.bindDefaultRole(userId);
    }
}
