package com.example.studyapp.backend.dto.task;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String subjectName;
    private Long chapterId;
    private String chapterTitle;
    private Long knowledgePointId;
    private String knowledgePointText;
    private OffsetDateTime dueDate;
    private Integer priority;
    private String taskType;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime completedAt;
} 