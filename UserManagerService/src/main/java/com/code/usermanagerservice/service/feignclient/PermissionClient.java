package com.code.usermanagerservice.service.feignclient;//package com.code.usermanagerservice.service.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("PermissionService")
public interface PermissionClient {
    @PostMapping("/permission/default")
    boolean bindDefaultRole (@RequestParam("userId")Long userId);
}
