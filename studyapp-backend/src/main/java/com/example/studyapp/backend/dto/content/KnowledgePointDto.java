package com.example.studyapp.backend.dto.content;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KnowledgePointDto {
    private Long id;
    private String text;
    private String details;
}