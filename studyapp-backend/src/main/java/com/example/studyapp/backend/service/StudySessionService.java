package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.timer.StudySessionCreateRequest;
import com.example.studyapp.backend.dto.timer.StudySessionDto;
import com.example.studyapp.backend.dto.timer.StudySessionUpdateRequest;
import com.example.studyapp.backend.entity.*;
import com.example.studyapp.backend.entity.enums.StudySessionStatus;
import com.example.studyapp.backend.entity.enums.SyncStatus;
import com.example.studyapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgePointRepository knowledgePointRepository;

    /**
     * 创建新的学习会话
     */
    @Transactional
    public StudySessionDto createStudySession(StudySessionCreateRequest request, User currentUser) {
        // 验证关联实体是否存在
        Subject subject = null;
        Chapter chapter = null;
        KnowledgePoint knowledgePoint = null;

        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
        }

        if (request.getChapterId() != null) {
            chapter = chapterRepository.findById(request.getChapterId())
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
        }

        if (request.getKnowledgePointId() != null) {
            knowledgePoint = knowledgePointRepository.findById(request.getKnowledgePointId())
                    .orElseThrow(() -> new IllegalArgumentException("Knowledge point not found"));
        }

        // 检查用户是否已有活动会话
        Optional<StudySession> activeSession = studySessionRepository.findByUserAndStatus(currentUser, StudySessionStatus.ACTIVE);
        if (activeSession.isPresent()) {
            throw new IllegalStateException("User already has an active study session");
        }

        // 创建新的学习会话
        StudySession newSession = StudySession.builder()
                .user(currentUser)
                .subject(subject)
                .chapter(chapter)
                .knowledgePoint(knowledgePoint)
                .sessionName(request.getSessionName())
                .totalDurationSeconds(0L)
                .effectiveDurationSeconds(0L)
                .startTime(OffsetDateTime.now())
                .status(StudySessionStatus.ACTIVE)
                .deviceId(request.getDeviceId())
                .syncStatus(SyncStatus.SYNCED)
                .build();

        StudySession savedSession = studySessionRepository.save(newSession);
        return convertToStudySessionDto(savedSession);
    }

    /**
     * 更新学习会话
     */
    @Transactional
    public StudySessionDto updateStudySession(Long sessionId, StudySessionUpdateRequest request, User currentUser) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Study session not found"));

        // 验证用户权限
        if (!session.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to update this session");
        }

        // 更新会话信息
        if (request.getTotalDurationSeconds() != null) {
            session.setTotalDurationSeconds(request.getTotalDurationSeconds());
        }
        if (request.getEffectiveDurationSeconds() != null) {
            session.setEffectiveDurationSeconds(request.getEffectiveDurationSeconds());
        }
        if (request.getStatus() != null) {
            session.setStatus(request.getStatus());
            if (request.getStatus() == StudySessionStatus.COMPLETED) {
                session.setEndTime(OffsetDateTime.now());
            }
        }
        if (request.getNotes() != null) {
            session.setNotes(request.getNotes());
        }

        StudySession updatedSession = studySessionRepository.save(session);
        return convertToStudySessionDto(updatedSession);
    }

    /**
     * 获取用户的学习会话列表
     */
    @Transactional(readOnly = true)
    public List<StudySessionDto> getUserStudySessions(User currentUser) {
        List<StudySession> sessions = studySessionRepository.findByUserOrderByCreatedAtDesc(currentUser);
        return sessions.stream()
                .map(this::convertToStudySessionDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户的活动会话
     */
    @Transactional(readOnly = true)
    public Optional<StudySessionDto> getActiveSession(User currentUser) {
        return studySessionRepository.findByUserAndStatus(currentUser, StudySessionStatus.ACTIVE)
                .map(this::convertToStudySessionDto);
    }

    /**
     * 获取用户指定时间范围的学习会话
     */
    @Transactional(readOnly = true)
    public List<StudySessionDto> getUserStudySessionsByDateRange(User currentUser, OffsetDateTime startDate, OffsetDateTime endDate) {
        List<StudySession> sessions = studySessionRepository.findByUserAndDateRange(currentUser, startDate, endDate);
        return sessions.stream()
                .map(this::convertToStudySessionDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户指定科目的学习会话
     */
    @Transactional(readOnly = true)
    public List<StudySessionDto> getUserStudySessionsBySubject(User currentUser, Long subjectId) {
        List<StudySession> sessions = studySessionRepository.findByUserAndSubjectIdOrderByCreatedAtDesc(currentUser, subjectId);
        return sessions.stream()
                .map(this::convertToStudySessionDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户总学习时长
     */
    @Transactional(readOnly = true)
    public Long getUserTotalStudyTime(User currentUser) {
        return studySessionRepository.sumEffectiveDurationByUser(currentUser);
    }

    /**
     * 获取用户今日学习时长
     */
    @Transactional(readOnly = true)
    public Long getUserTodayStudyTime(User currentUser) {
        return studySessionRepository.sumTodayEffectiveDurationByUser(currentUser, OffsetDateTime.now());
    }

    /**
     * 完成学习会话
     */
    @Transactional
    public StudySessionDto completeStudySession(Long sessionId, User currentUser) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Study session not found"));

        if (!session.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to complete this session");
        }

        session.setStatus(StudySessionStatus.COMPLETED);
        session.setEndTime(OffsetDateTime.now());

        StudySession completedSession = studySessionRepository.save(session);
        return convertToStudySessionDto(completedSession);
    }

    /**
     * 暂停学习会话
     */
    @Transactional
    public StudySessionDto pauseStudySession(Long sessionId, User currentUser) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Study session not found"));

        if (!session.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to pause this session");
        }

        session.setStatus(StudySessionStatus.PAUSED);

        StudySession pausedSession = studySessionRepository.save(session);
        return convertToStudySessionDto(pausedSession);
    }

    /**
     * 恢复学习会话
     */
    @Transactional
    public StudySessionDto resumeStudySession(Long sessionId, User currentUser) {
        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Study session not found"));

        if (!session.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to resume this session");
        }

        session.setStatus(StudySessionStatus.ACTIVE);

        StudySession resumedSession = studySessionRepository.save(session);
        return convertToStudySessionDto(resumedSession);
    }

    // 私有转换方法
    private StudySessionDto convertToStudySessionDto(StudySession session) {
        return StudySessionDto.builder()
                .id(session.getId())
                .subjectId(session.getSubject() != null ? session.getSubject().getId() : null)
                .subjectName(session.getSubject() != null ? session.getSubject().getName() : null)
                .chapterId(session.getChapter() != null ? session.getChapter().getId() : null)
                .chapterTitle(session.getChapter() != null ? session.getChapter().getTitle() : null)
                .knowledgePointId(session.getKnowledgePoint() != null ? session.getKnowledgePoint().getId() : null)
                .knowledgePointText(session.getKnowledgePoint() != null ? session.getKnowledgePoint().getText() : null)
                .sessionName(session.getSessionName())
                .totalDurationSeconds(session.getTotalDurationSeconds())
                .effectiveDurationSeconds(session.getEffectiveDurationSeconds())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .notes(session.getNotes())
                .deviceId(session.getDeviceId())
                .syncStatus(session.getSyncStatus())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
} 