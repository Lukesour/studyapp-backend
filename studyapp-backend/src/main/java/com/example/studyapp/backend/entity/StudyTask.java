package com.example.studyapp.backend.entity;

import com.example.studyapp.backend.entity.enums.TimeInterval;
import com.example.studyapp.backend.entity.enums.TaskType;
import com.example.studyapp.backend.entity.enums.TimingMode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_tasks")
public class StudyTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 关键：建立与User的多对一关系
    // 多个任务可以属于一个用户
    @ManyToOne(fetch = FetchType.LAZY) // LAZY表示延迟加载，性能更好
    @JoinColumn(name = "user_id", nullable = false) // 外键列
    private User user;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING) // 将枚举类型以字符串形式存储到数据库
    @Column(name = "task_type", nullable = false)
    private TaskType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_interval", nullable = false)
    private TimeInterval timeInterval;

    @Enumerated(EnumType.STRING)
    @Column(name = "timing_mode", nullable = false)
    private TimingMode timingMode;

    @Column(name = "target_minutes")
    private int targetMinutes;

    @Column(name = "completed_minutes")
    private int completedMinutes = 0; // 默认为0

    @Column(name = "target_count")
    private int targetCount;

    @Column(name = "completed_count")
    private int completedCount = 0; // 默认为0

    @Column(name = "focus_count")
    private int focusCount = 0; // 默认为0

    @Column(name = "is_completed")
    private boolean isCompleted = false; // 默认为false

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "completion_timestamp")
    private OffsetDateTime completionTimestamp;
}