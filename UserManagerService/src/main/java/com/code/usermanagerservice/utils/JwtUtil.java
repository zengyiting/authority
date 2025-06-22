package com.code.usermanagerservice.utils;

import com.code.usermanagerservice.enums.ConfigEnum;
import com.code.usermanagerservice.enums.TimeOutEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
/**
 * JWT 工具类,用的以前项目的工具类，但有地方需要修改，直接借助了ai完成
 */
@Slf4j
public final class JwtUtil {

    private static final String SECRET_KEY = ConfigEnum.TOKEN_SECRET_KEY.getValue();

    /**
     * 生成 JWT Token
     *
     * @param id 用户唯一标识
     * @param expireMinutes 过期时间，单位：分钟
     * @return 生成的JWT字符串
     */
    public static String generateToken(String id, Long expireMinutes) {
        log.info("生成JWTsecret:{}",SECRET_KEY);
        Date expiryDate = new Date(System.currentTimeMillis() + expireMinutes * 60 * 1000L);
        return Jwts.builder()
                .setSubject(id)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * 生成 access token，默认 30 分钟过期
     */
    public static String generateAccessToken(String id) {
        return generateToken(id, TimeOutEnum.ACCESS_TOKEN_TIMEOUT.getTimeOut());
    }

    /**
     * 生成 refresh token，默认 7 天过期（单位转成分钟）
     */
    public static String generateRefreshToken(String id) {
        return generateToken(id, TimeOutEnum.REFRESH_TOKEN_TIMEOUT.getTimeOut());
    }

    /**
     * 解析JWT
     */
    public static Claims parse(String token) {
        log.info("解析JWTsecret:{}",SECRET_KEY);
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            System.err.println("解析失败！");
            return null;
        }
    }
    public static String getUserId(String token) {
        Claims claims = parse(token);
        return claims != null ? claims.getSubject() : null;
    }
}
