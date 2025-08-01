package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.content.KnowledgePointCreateRequest;
import com.example.studyapp.backend.dto.content.KnowledgePointDto;
import com.example.studyapp.backend.dto.content.KnowledgePointUpdateRequest;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.service.KnowledgePointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge-points")
@RequiredArgsConstructor
public class KnowledgePointController {

    private final KnowledgePointService knowledgePointService;

    /**
     * 为指定章节创建新知识点
     */
    @PostMapping("/chapters/{chapterId}")
    public ResponseEntity<KnowledgePointDto> createKnowledgePoint(
            @PathVariable Long chapterId,
            @RequestBody KnowledgePointCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        KnowledgePointDto createdKnowledgePoint = knowledgePointService.createKnowledgePoint(chapterId, request, currentUser);
        return ResponseEntity.status(201).body(createdKnowledgePoint);
    }

    /**
     * 更新知识点信息
     */
    @PutMapping("/{knowledgePointId}")
    public ResponseEntity<KnowledgePointDto> updateKnowledgePoint(
            @PathVariable Long knowledgePointId,
            @RequestBody KnowledgePointUpdateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        KnowledgePointDto updatedKnowledgePoint = knowledgePointService.updateKnowledgePoint(knowledgePointId, request, currentUser);
        return ResponseEntity.ok(updatedKnowledgePoint);
    }

    /**
     * 删除知识点
     */
    @DeleteMapping("/{knowledgePointId}")
    public ResponseEntity<Void> deleteKnowledgePoint(
            @PathVariable Long knowledgePointId,
            @AuthenticationPrincipal User currentUser
    ) {
        knowledgePointService.deleteKnowledgePoint(knowledgePointId, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取章节的所有知识点
     */
    @GetMapping("/chapters/{chapterId}")
    public ResponseEntity<List<KnowledgePointDto>> getKnowledgePointsByChapter(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<KnowledgePointDto> knowledgePoints = knowledgePointService.getKnowledgePointsByChapter(chapterId, currentUser);
        return ResponseEntity.ok(knowledgePoints);
    }

    /**
     * 获取科目的所有知识点
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<List<KnowledgePointDto>> getKnowledgePointsBySubject(
            @PathVariable Long subjectId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<KnowledgePointDto> knowledgePoints = knowledgePointService.getKnowledgePointsBySubject(subjectId, currentUser);
        return ResponseEntity.ok(knowledgePoints);
    }

    /**
     * 获取知识点详情
     */
    @GetMapping("/{knowledgePointId}")
    public ResponseEntity<KnowledgePointDto> getKnowledgePointById(
            @PathVariable Long knowledgePointId,
            @AuthenticationPrincipal User currentUser
    ) {
        KnowledgePointDto knowledgePoint = knowledgePointService.getKnowledgePointById(knowledgePointId, currentUser);
        return ResponseEntity.ok(knowledgePoint);
    }

    /**
     * 获取用户可见的所有知识点
     */
    @GetMapping
    public ResponseEntity<List<KnowledgePointDto>> getUserVisibleKnowledgePoints(@AuthenticationPrincipal User currentUser) {
        List<KnowledgePointDto> knowledgePoints = knowledgePointService.getUserVisibleKnowledgePoints(currentUser);
        return ResponseEntity.ok(knowledgePoints);
    }
} 