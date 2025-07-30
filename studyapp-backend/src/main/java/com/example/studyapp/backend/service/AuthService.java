package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.AuthenticationRequest;
import com.example.studyapp.backend.dto.AuthenticationResponse;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    // 注入我们新配置的 AuthenticationManager 和 JwtService
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // 注意：这里的参数类型从 RegisterRequest 改为了 AuthenticationRequest
    public User register(AuthenticationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname("新用户")
                .avatarUrl("https://placehold.co/128x128/007AFF/FFFFFF?text=学")
                .studySignature("天行健，君子以自强不息。")
                .build();

        return userRepository.save(user);
    }

    // --- 新增的登录方法 ---
    public AuthenticationResponse login(AuthenticationRequest request) {
        // 1. 使用 AuthenticationManager 执行认证
        // 这一步会调用我们之前配置的 AuthenticationProvider，
        // 它内部会使用 UserDetailsService 获取用户信息，并用 PasswordEncoder 比对密码。
        // 如果用户名或密码错误，这里会自动抛出异常。
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. 如果认证成功，程序会继续往下走。我们根据用户名从数据库查找用户。
        // 此时我们确信用户一定存在，否则上一步就已抛出异常。
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(); // 这里理论上不会发生

        // 3. 使用 JwtService 为该用户生成一个 JWT
        var jwtToken = jwtService.generateToken(user);

        // 4. 将生成的 JWT 包装在 AuthenticationResponse 中并返回
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}