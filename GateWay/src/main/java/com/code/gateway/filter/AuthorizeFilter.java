package com.code.gateway.filter;


import com.code.gateway.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@SuppressWarnings({"all"})
public class AuthorizeFilter implements GlobalFilter, Ordered {



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("进入全局过滤器");
        // 获取请求参数
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        try {
            if (request.getHeaders().get("Authorization") == null) {
                // 没有token 拦截
                if (path.matches("/api/u1/user/.*")) {
                    return chain.filter(exchange);
                }
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();

            } else {
                String token = request.getHeaders().get("Authorization").get(0);
                log.info("token:{}", token);
                // 判断是否合法
                Claims claims = JwtUtil.parse(token);
                if (claims == null) {
                    log.warn("token 无效或已过期");
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }
                if (path.matches("/api/u1/user/.*")) {
                    return chain.filter(exchange);
                }
                log.info("token合法");
                String userId = claims.getSubject();
                ServerHttpRequest newRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .build();
                log.info("token合法");
                return chain.filter(exchange.mutate().request(newRequest).build());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 否 拦截
        log.info("token不合法");
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
