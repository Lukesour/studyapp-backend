package com.example.studyapp.backend.dto.timer;

import com.example.studyapp.backend.entity.enums.StudySessionStatus;
import com.example.studyapp.backend.entity.enums.SyncStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class StudySessionDto {
    private Long id;
    private Long subjectId;
    private String subjectName;
    private Long chapterId;
    private String chapterTitle;
    private Long knowledgePointId;
    private String knowledgePointText;
    private String sessionName;
    private Long totalDurationSeconds;
    private Long effectiveDurationSeconds;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private StudySessionStatus status;
    private String notes;
    private String deviceId;
    private SyncStatus syncStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
} 