package com.code.usermanagerservice.service.feignclient;//package com.code.usermanagerservice.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("PermissionService")
public interface PermissionClient {
    @PostMapping("/api/p1/default")
    boolean bindDefaultRole (@RequestParam("userId")Long userId);
    @PostMapping("/api/p1/getRoleCode")
    int getUserRoleCode (@RequestParam("userId")Long userId);
    @PostMapping("/api/p1/upgrade")
    public boolean upgradeToAdmin(@RequestParam Long userId);
    @PostMapping("/api/p1/downgrade")
    public boolean downgradeToUser(@RequestParam Long userId);
    @GetMapping ("/api/p1/getUserRoleCodeList")
    public List<Long> getUserRoleCodeList(@RequestParam Long userId);
    @GetMapping("/api/p1/getAllRoleCodeList")
    public List<Long> getAllRoleCodeList(@RequestParam Long userId);
}
