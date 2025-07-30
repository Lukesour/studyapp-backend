package com.example.studyapp.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo-controller") // 我们为受保护的资源使用一个新的路径前缀
public class DemoController {

    // 这个 GET 端点是受保护的，因为在 SecurityConfig 中，
    // 我们配置了 .anyRequest().authenticated()，
    // 并且这个路径不匹配 /api/auth/** 这个公开路径。
    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from a SECURED endpoint!");
    }
}