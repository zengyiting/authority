package com.code.usermanagerservice.service.feignclient;//package com.code.usermanagerservice.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("PermissionService")
public interface PermissionClient {
    @PostMapping("/permission/default")
    boolean bindDefaultRole (@RequestParam("userId")Long userId);
    @PostMapping("/permission/getRoleCode")
    int getUserRoleCode (@RequestParam("userId")Long userId);
    @PostMapping("/permission/upgrade")
    public boolean upgradeToAdmin(@RequestParam Long userId);
    @PostMapping("permission/downgrade")
    public boolean downgradeToUser(@RequestParam Long userId);
    @GetMapping ("permission/getUserRoleCodeList")
    public List<Long> getUserRoleCodeList(@RequestParam Long userId);
    @GetMapping("permission/getAllRoleCodeList")
    public List<Long> getAllRoleCodeList(@RequestParam Long userId);
}
