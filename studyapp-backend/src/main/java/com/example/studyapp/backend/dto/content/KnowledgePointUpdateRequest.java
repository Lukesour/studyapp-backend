package com.example.studyapp.backend.dto.content;

import lombok.Data;

@Data
public class KnowledgePointUpdateRequest {
    private String text;
    private String details;
    private Integer orderIndex;
} 