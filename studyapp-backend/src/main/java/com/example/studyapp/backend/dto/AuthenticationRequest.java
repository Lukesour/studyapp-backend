package com.example.studyapp.backend.dto;

import lombok.Data;

// 这个类非常简单，它的唯一目的就是作为数据载体，
// 将前端发送的 JSON 数据 {"username": "...", "password": "..."} 映射为 Java 对象。
@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}