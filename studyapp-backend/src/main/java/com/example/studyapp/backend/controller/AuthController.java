package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.AuthenticationRequest;
import com.example.studyapp.backend.dto.AuthenticationResponse;
import com.example.studyapp.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 更新参数类型为 AuthenticationRequest
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody AuthenticationRequest registerRequest) {
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("User registered successfully!");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- 新增的登录端点 ---
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody AuthenticationRequest loginRequest) {
        // 直接调用 service 的 login 方法。
        // 如果登录失败，AuthService 会抛出异常，Spring Security 会处理它并返回一个 401 或 403 错误。
        // 如果成功，则返回 200 OK 和包含 token 的 JSON。
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}