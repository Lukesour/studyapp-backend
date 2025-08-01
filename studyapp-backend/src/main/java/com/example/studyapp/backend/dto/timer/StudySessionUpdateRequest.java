package com.example.studyapp.backend.dto.timer;

import com.example.studyapp.backend.entity.enums.StudySessionStatus;
import lombok.Data;

@Data
public class StudySessionUpdateRequest {
    private Long totalDurationSeconds;
    private Long effectiveDurationSeconds;
    private StudySessionStatus status;
    private String notes;
} 