package com.example.studyapp.backend.dto.content;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class ChapterDto {
    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Long subjectId;
    private String subjectName;
    private List<KnowledgePointDto> knowledgePoints;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}