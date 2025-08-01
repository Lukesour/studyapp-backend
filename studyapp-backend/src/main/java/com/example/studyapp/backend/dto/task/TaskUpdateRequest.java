package com.example.studyapp.backend.dto.task;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TaskUpdateRequest {
    private String title;
    private String description;
    private Long subjectId;
    private Long chapterId;
    private Long knowledgePointId;
    private OffsetDateTime dueDate;
    private Integer priority;
    private String taskType;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
} 