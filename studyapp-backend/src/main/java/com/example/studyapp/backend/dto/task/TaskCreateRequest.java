package com.example.studyapp.backend.dto.task;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TaskCreateRequest {
    private String title;
    private String description;
    private Long subjectId; // 关联的科目ID（可选）
    private Long chapterId; // 关联的章节ID（可选）
    private Long knowledgePointId; // 关联的知识点ID（可选）
    private OffsetDateTime dueDate; // 截止日期
    private Integer priority; // 优先级：1-低，2-中，3-高
    private String taskType; // 任务类型：STUDY, REVIEW, PRACTICE, EXAM
}