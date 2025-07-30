package com.example.studyapp.backend.config;

import com.example.studyapp.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // 声明这是一个Spring组件，让Spring容器来管理它
@RequiredArgsConstructor // 使用Lombok注入final字段
public class JwtAuthenticationFilter extends OncePerRequestFilter { // 继承OncePerRequestFilter确保每次请求只执行一次此过滤器

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 从请求头中获取 Authorization Header
        final String authHeader = request.getHeader("Authorization");

        // 2. 如果 Header 为空，或者不以 "Bearer " 开头，则直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // 放行，让后续的过滤器去处理
            return;
        }

        // 3. 提取 JWT (去掉 "Bearer " 前缀)
        final String jwt = authHeader.substring(7);

        // 4. 从 JWT 中提取用户名
        final String username = jwtService.extractUsername(jwt);

        // 5. 检查用户名是否存在，并且当前安全上下文中没有已认证的用户
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. 根据用户名从数据库加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 7. 验证JWT是否有效
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 8. 如果Token有效，则创建一个认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 凭证我们已经验证过了，所以这里是null
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. 更新安全上下文，将用户标记为已认证
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. 无论如何都放行，让请求继续前进
        filterChain.doFilter(request, response);
    }
}