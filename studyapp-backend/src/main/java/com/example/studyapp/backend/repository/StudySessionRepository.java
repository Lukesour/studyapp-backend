package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.StudySession;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.entity.enums.StudySessionStatus;
import com.example.studyapp.backend.entity.enums.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // 查找用户的所有学习会话
    List<StudySession> findByUserOrderByCreatedAtDesc(User user);

    // 查找用户的活动会话
    Optional<StudySession> findByUserAndStatus(User user, StudySessionStatus status);

    // 查找用户指定时间范围内的学习会话
    @Query("SELECT s FROM StudySession s WHERE s.user = :user AND s.startTime >= :startDate AND s.startTime <= :endDate")
    List<StudySession> findByUserAndDateRange(@Param("user") User user, 
                                             @Param("startDate") OffsetDateTime startDate, 
                                             @Param("endDate") OffsetDateTime endDate);

    // 查找待同步的会话
    List<StudySession> findByUserAndSyncStatus(User user, SyncStatus syncStatus);

    // 查找用户指定科目的学习会话
    List<StudySession> findByUserAndSubjectIdOrderByCreatedAtDesc(User user, Long subjectId);

    // 查找用户指定章节的学习会话
    List<StudySession> findByUserAndChapterIdOrderByCreatedAtDesc(User user, Long chapterId);

    // 查找用户指定知识点的学习会话
    List<StudySession> findByUserAndKnowledgePointIdOrderByCreatedAtDesc(User user, Long knowledgePointId);

    // 统计用户总学习时长
    @Query("SELECT COALESCE(SUM(s.effectiveDurationSeconds), 0) FROM StudySession s WHERE s.user = :user AND s.status = 'COMPLETED'")
    Long sumEffectiveDurationByUser(@Param("user") User user);

    // 统计用户今日学习时长
    @Query("SELECT COALESCE(SUM(s.effectiveDurationSeconds), 0) FROM StudySession s WHERE s.user = :user AND s.status = 'COMPLETED' AND DATE(s.startTime) = DATE(:today)")
    Long sumTodayEffectiveDurationByUser(@Param("user") User user, @Param("today") OffsetDateTime today);
} 