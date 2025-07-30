package com.example.studyapp.backend.dto.task;

import com.example.studyapp.backend.entity.StudyTask;
import com.example.studyapp.backend.entity.enums.TimeInterval;
import com.example.studyapp.backend.entity.enums.TaskType;
import com.example.studyapp.backend.entity.enums.TimingMode;
import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;

// 用于向前端返回任务的数据
@Data
@Builder
public class TaskResponse {
    private Long id;
    private String title;
    private TaskType type;
    private TimeInterval timeInterval;
    private TimingMode timingMode;
    private int targetMinutes;
    private int completedMinutes;
    private int targetCount;
    private int completedCount;
    private int focusCount;
    private boolean isCompleted;
    private OffsetDateTime createdAt;
    private OffsetDateTime completionTimestamp;

    // 提供一个静态工厂方法，方便地从实体类转换到DTO
    public static TaskResponse fromEntity(StudyTask task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .type(task.getType())
                .timeInterval(task.getTimeInterval())
                .timingMode(task.getTimingMode())
                .targetMinutes(task.getTargetMinutes())
                .completedMinutes(task.getCompletedMinutes())
                .targetCount(task.getTargetCount())
                .completedCount(task.getCompletedCount())
                .focusCount(task.getFocusCount())
                .isCompleted(task.isCompleted())
                .createdAt(task.getCreatedAt())
                .completionTimestamp(task.getCompletionTimestamp())
                .build();
    }
}