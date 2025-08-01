package com.example.studyapp.backend.dto.content;

import lombok.Data;

@Data
public class ChapterCreateRequest {
    private String title;
    private String description;
    private Integer orderIndex; // 章节顺序
} 