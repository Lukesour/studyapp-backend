package com.example.studyapp.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // 使用 @Value 注解从 application.properties 文件中注入我们配置的密钥和过期时间
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    /**
     * 从JWT中提取用户名（通常是 "subject" 声明）
     * @param token JWT字符串
     * @return 用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 生成JWT，不含额外的 claims
     * @param userDetails Spring Security 的用户信息
     * @return 生成的JWT字符串
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * 生成含有额外 claims 的JWT
     * @param extraClaims 额外的 claims
     * @param userDetails Spring Security 的用户信息
     * @return 生成的JWT字符串
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    /**
     * 验证JWT是否有效
     * @param token JWT字符串
     * @param userDetails Spring Security 的用户信息
     * @return 如果有效则返回true，否则返回false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        // 检查token中的用户名是否与UserDetails中的用户名匹配，并且token没有过期
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // ------------------- 私有辅助方法 -------------------

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .claims(extraClaims) // 设置自定义的 claims
                .subject(userDetails.getUsername()) // 设置主题为用户名
                .issuedAt(new Date(System.currentTimeMillis())) // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间
                .signWith(getSignInKey()) // 使用我们的密钥进行签名
                .compact(); // 构建并生成最终的字符串
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 通用的、用于从JWT中提取任何单个 claim 的方法
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 解析整个JWT并提取所有的 claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // 使用密钥验证签名
                .build()
                .parseSignedClaims(token) // 解析token
                .getPayload(); // 获取负载部分
    }

    /**
     * 将 application.properties 中 Base64 编码的密钥字符串转换为一个 SecretKey 对象
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}