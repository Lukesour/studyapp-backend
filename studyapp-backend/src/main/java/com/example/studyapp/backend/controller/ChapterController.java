package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.content.ChapterCreateRequest;
import com.example.studyapp.backend.dto.content.ChapterDto;
import com.example.studyapp.backend.dto.content.ChapterUpdateRequest;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    /**
     * 为指定科目创建新章节
     */
    @PostMapping("/subjects/{subjectId}")
    public ResponseEntity<ChapterDto> createChapter(
            @PathVariable Long subjectId,
            @RequestBody ChapterCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        ChapterDto createdChapter = chapterService.createChapter(subjectId, request, currentUser);
        return ResponseEntity.status(201).body(createdChapter);
    }

    /**
     * 更新章节信息
     */
    @PutMapping("/{chapterId}")
    public ResponseEntity<ChapterDto> updateChapter(
            @PathVariable Long chapterId,
            @RequestBody ChapterUpdateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        ChapterDto updatedChapter = chapterService.updateChapter(chapterId, request, currentUser);
        return ResponseEntity.ok(updatedChapter);
    }

    /**
     * 删除章节
     */
    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal User currentUser
    ) {
        chapterService.deleteChapter(chapterId, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取科目的所有章节
     */
    @GetMapping("/subjects/{subjectId}")
    public ResponseEntity<List<ChapterDto>> getChaptersBySubject(
            @PathVariable Long subjectId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<ChapterDto> chapters = chapterService.getChaptersBySubject(subjectId, currentUser);
        return ResponseEntity.ok(chapters);
    }

    /**
     * 获取章节详情
     */
    @GetMapping("/{chapterId}")
    public ResponseEntity<ChapterDto> getChapterById(
            @PathVariable Long chapterId,
            @AuthenticationPrincipal User currentUser
    ) {
        ChapterDto chapter = chapterService.getChapterById(chapterId, currentUser);
        return ResponseEntity.ok(chapter);
    }

    /**
     * 获取用户可见的所有章节
     */
    @GetMapping
    public ResponseEntity<List<ChapterDto>> getUserVisibleChapters(@AuthenticationPrincipal User currentUser) {
        List<ChapterDto> chapters = chapterService.getUserVisibleChapters(currentUser);
        return ResponseEntity.ok(chapters);
    }
} 