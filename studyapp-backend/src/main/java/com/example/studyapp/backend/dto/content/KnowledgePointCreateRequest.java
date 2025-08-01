package com.example.studyapp.backend.dto.content;

import lombok.Data;

@Data
public class KnowledgePointCreateRequest {
    private String text;
    private String details;
    private Integer orderIndex; // 知识点顺序
} 