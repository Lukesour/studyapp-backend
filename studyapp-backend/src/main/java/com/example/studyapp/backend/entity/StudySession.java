package com.example.studyapp.backend.entity;

import com.example.studyapp.backend.entity.enums.StudySessionStatus;
import com.example.studyapp.backend.entity.enums.SyncStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "study_sessions")
public class StudySession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_point_id")
    private KnowledgePoint knowledgePoint;

    @Column(name = "session_name")
    private String sessionName;

    @Column(name = "total_duration_seconds", nullable = false)
    private Long totalDurationSeconds; // 总学习时长（秒）

    @Column(name = "effective_duration_seconds", nullable = false)
    private Long effectiveDurationSeconds; // 有效学习时长（秒）

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time")
    private OffsetDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false)
    private StudySessionStatus status;

    @Column(name = "notes")
    private String notes; // 学习笔记

    @Column(name = "device_id")
    private String deviceId; // 设备ID，用于多设备同步

    @Column(name = "sync_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus = SyncStatus.SYNCED; // 同步状态

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
} 