package com.example.studyapp.backend.dto.content;

import lombok.Data;

@Data
public class ChapterUpdateRequest {
    private String title;
    private String description;
    private Integer orderIndex;
} 