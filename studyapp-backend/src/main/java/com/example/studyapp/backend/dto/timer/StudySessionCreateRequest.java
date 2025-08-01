package com.example.studyapp.backend.dto.timer;

import lombok.Data;

@Data
public class StudySessionCreateRequest {
    private Long subjectId;
    private Long chapterId;
    private Long knowledgePointId;
    private String sessionName;
    private String deviceId;
} 