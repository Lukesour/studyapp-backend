package com.example.studyapp.backend.dto.content;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class KnowledgePointDto {
    private Long id;
    private String text;
    private String details;
    private Integer orderIndex;
    private Long chapterId;
    private String chapterTitle;
    private Long subjectId;
    private String subjectName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}