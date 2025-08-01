package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.content.KnowledgePointCreateRequest;
import com.example.studyapp.backend.dto.content.KnowledgePointDto;
import com.example.studyapp.backend.dto.content.KnowledgePointUpdateRequest;
import com.example.studyapp.backend.entity.Chapter;
import com.example.studyapp.backend.entity.KnowledgePoint;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.repository.ChapterRepository;
import com.example.studyapp.backend.repository.KnowledgePointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KnowledgePointService {

    private final KnowledgePointRepository knowledgePointRepository;
    private final ChapterRepository chapterRepository;

    /**
     * 为指定章节创建新知识点
     */
    @Transactional
    public KnowledgePointDto createKnowledgePoint(Long chapterId, KnowledgePointCreateRequest request, User currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        // 验证用户权限（只能为自己的私有科目创建知识点）
        if (chapter.getSubject().getUser() != null && !chapter.getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to create knowledge point for this chapter");
        }

        KnowledgePoint newKnowledgePoint = KnowledgePoint.builder()
                .chapter(chapter)
                .text(request.getText())
                .details(request.getDetails())
                .orderIndex(request.getOrderIndex())
                .build();

        KnowledgePoint savedKnowledgePoint = knowledgePointRepository.save(newKnowledgePoint);
        return convertToKnowledgePointDto(savedKnowledgePoint);
    }

    /**
     * 更新知识点信息
     */
    @Transactional
    public KnowledgePointDto updateKnowledgePoint(Long knowledgePointId, KnowledgePointUpdateRequest request, User currentUser) {
        KnowledgePoint knowledgePoint = knowledgePointRepository.findById(knowledgePointId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge point not found"));

        // 验证用户权限
        if (knowledgePoint.getChapter().getSubject().getUser() != null && !knowledgePoint.getChapter().getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to update this knowledge point");
        }

        if (request.getText() != null) {
            knowledgePoint.setText(request.getText());
        }
        if (request.getDetails() != null) {
            knowledgePoint.setDetails(request.getDetails());
        }
        if (request.getOrderIndex() != null) {
            knowledgePoint.setOrderIndex(request.getOrderIndex());
        }

        KnowledgePoint updatedKnowledgePoint = knowledgePointRepository.save(knowledgePoint);
        return convertToKnowledgePointDto(updatedKnowledgePoint);
    }

    /**
     * 删除知识点
     */
    @Transactional
    public void deleteKnowledgePoint(Long knowledgePointId, User currentUser) {
        KnowledgePoint knowledgePoint = knowledgePointRepository.findById(knowledgePointId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge point not found"));

        // 验证用户权限
        if (knowledgePoint.getChapter().getSubject().getUser() != null && !knowledgePoint.getChapter().getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to delete this knowledge point");
        }

        knowledgePointRepository.delete(knowledgePoint);
    }

    /**
     * 获取章节的所有知识点
     */
    @Transactional(readOnly = true)
    public List<KnowledgePointDto> getKnowledgePointsByChapter(Long chapterId, User currentUser) {
        List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findVisibleKnowledgePointsByChapterIdAndUserId(chapterId, currentUser.getId());
        return knowledgePoints.stream()
                .map(this::convertToKnowledgePointDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取科目的所有知识点
     */
    @Transactional(readOnly = true)
    public List<KnowledgePointDto> getKnowledgePointsBySubject(Long subjectId, User currentUser) {
        List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findVisibleKnowledgePointsBySubjectIdAndUserId(subjectId, currentUser.getId());
        return knowledgePoints.stream()
                .map(this::convertToKnowledgePointDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取知识点详情
     */
    @Transactional(readOnly = true)
    public KnowledgePointDto getKnowledgePointById(Long knowledgePointId, User currentUser) {
        KnowledgePoint knowledgePoint = knowledgePointRepository.findById(knowledgePointId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge point not found"));

        // 验证用户权限
        if (knowledgePoint.getChapter().getSubject().getUser() != null && !knowledgePoint.getChapter().getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to access this knowledge point");
        }

        return convertToKnowledgePointDto(knowledgePoint);
    }

    /**
     * 获取用户可见的所有知识点
     */
    @Transactional(readOnly = true)
    public List<KnowledgePointDto> getUserVisibleKnowledgePoints(User currentUser) {
        List<KnowledgePoint> knowledgePoints = knowledgePointRepository.findVisibleKnowledgePointsByUserId(currentUser.getId());
        return knowledgePoints.stream()
                .map(this::convertToKnowledgePointDto)
                .collect(Collectors.toList());
    }

    // 私有转换方法
    private KnowledgePointDto convertToKnowledgePointDto(KnowledgePoint knowledgePoint) {
        return KnowledgePointDto.builder()
                .id(knowledgePoint.getId())
                .text(knowledgePoint.getText())
                .details(knowledgePoint.getDetails())
                .orderIndex(knowledgePoint.getOrderIndex())
                .chapterId(knowledgePoint.getChapter().getId())
                .chapterTitle(knowledgePoint.getChapter().getTitle())
                .subjectId(knowledgePoint.getChapter().getSubject().getId())
                .subjectName(knowledgePoint.getChapter().getSubject().getName())
                .createdAt(knowledgePoint.getCreatedAt())
                .updatedAt(knowledgePoint.getUpdatedAt())
                .build();
    }
} 