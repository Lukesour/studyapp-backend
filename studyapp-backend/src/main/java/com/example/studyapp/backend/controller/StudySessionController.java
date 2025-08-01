package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.timer.StudySessionCreateRequest;
import com.example.studyapp.backend.dto.timer.StudySessionDto;
import com.example.studyapp.backend.dto.timer.StudySessionUpdateRequest;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.service.StudySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/study-sessions")
@RequiredArgsConstructor
public class StudySessionController {

    private final StudySessionService studySessionService;

    /**
     * 创建新的学习会话
     */
    @PostMapping
    public ResponseEntity<StudySessionDto> createStudySession(
            @RequestBody StudySessionCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        StudySessionDto createdSession = studySessionService.createStudySession(request, currentUser);
        return ResponseEntity.status(201).body(createdSession);
    }

    /**
     * 更新学习会话
     */
    @PutMapping("/{sessionId}")
    public ResponseEntity<StudySessionDto> updateStudySession(
            @PathVariable Long sessionId,
            @RequestBody StudySessionUpdateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        StudySessionDto updatedSession = studySessionService.updateStudySession(sessionId, request, currentUser);
        return ResponseEntity.ok(updatedSession);
    }

    /**
     * 获取用户的所有学习会话
     */
    @GetMapping
    public ResponseEntity<List<StudySessionDto>> getUserStudySessions(@AuthenticationPrincipal User currentUser) {
        List<StudySessionDto> sessions = studySessionService.getUserStudySessions(currentUser);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 获取用户的活动会话
     */
    @GetMapping("/active")
    public ResponseEntity<StudySessionDto> getActiveSession(@AuthenticationPrincipal User currentUser) {
        Optional<StudySessionDto> activeSession = studySessionService.getActiveSession(currentUser);
        return activeSession.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取用户指定时间范围的学习会话
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<StudySessionDto>> getStudySessionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @AuthenticationPrincipal User currentUser
    ) {
        List<StudySessionDto> sessions = studySessionService.getUserStudySessionsByDateRange(currentUser, startDate, endDate);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 获取用户指定科目的学习会话
     */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<StudySessionDto>> getStudySessionsBySubject(
            @PathVariable Long subjectId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<StudySessionDto> sessions = studySessionService.getUserStudySessionsBySubject(currentUser, subjectId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * 获取用户总学习时长
     */
    @GetMapping("/total-time")
    public ResponseEntity<Long> getUserTotalStudyTime(@AuthenticationPrincipal User currentUser) {
        Long totalTime = studySessionService.getUserTotalStudyTime(currentUser);
        return ResponseEntity.ok(totalTime);
    }

    /**
     * 获取用户今日学习时长
     */
    @GetMapping("/today-time")
    public ResponseEntity<Long> getUserTodayStudyTime(@AuthenticationPrincipal User currentUser) {
        Long todayTime = studySessionService.getUserTodayStudyTime(currentUser);
        return ResponseEntity.ok(todayTime);
    }

    /**
     * 完成学习会话
     */
    @PostMapping("/{sessionId}/complete")
    public ResponseEntity<StudySessionDto> completeStudySession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User currentUser
    ) {
        StudySessionDto completedSession = studySessionService.completeStudySession(sessionId, currentUser);
        return ResponseEntity.ok(completedSession);
    }

    /**
     * 暂停学习会话
     */
    @PostMapping("/{sessionId}/pause")
    public ResponseEntity<StudySessionDto> pauseStudySession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User currentUser
    ) {
        StudySessionDto pausedSession = studySessionService.pauseStudySession(sessionId, currentUser);
        return ResponseEntity.ok(pausedSession);
    }

    /**
     * 恢复学习会话
     */
    @PostMapping("/{sessionId}/resume")
    public ResponseEntity<StudySessionDto> resumeStudySession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User currentUser
    ) {
        StudySessionDto resumedSession = studySessionService.resumeStudySession(sessionId, currentUser);
        return ResponseEntity.ok(resumedSession);
    }
} 