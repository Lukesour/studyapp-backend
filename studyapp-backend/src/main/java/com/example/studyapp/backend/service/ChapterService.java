package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.content.ChapterCreateRequest;
import com.example.studyapp.backend.dto.content.ChapterDto;
import com.example.studyapp.backend.dto.content.ChapterUpdateRequest;
import com.example.studyapp.backend.entity.Chapter;
import com.example.studyapp.backend.entity.Subject;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.repository.ChapterRepository;
import com.example.studyapp.backend.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final SubjectRepository subjectRepository;

    /**
     * 为指定科目创建新章节
     */
    @Transactional
    public ChapterDto createChapter(Long subjectId, ChapterCreateRequest request, User currentUser) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        // 验证用户权限（只能为自己的私有科目创建章节）
        if (subject.getUser() != null && !subject.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to create chapter for this subject");
        }

        Chapter newChapter = Chapter.builder()
                .subject(subject)
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(request.getOrderIndex())
                .build();

        Chapter savedChapter = chapterRepository.save(newChapter);
        return convertToChapterDto(savedChapter);
    }

    /**
     * 更新章节信息
     */
    @Transactional
    public ChapterDto updateChapter(Long chapterId, ChapterUpdateRequest request, User currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        // 验证用户权限
        if (chapter.getSubject().getUser() != null && !chapter.getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to update this chapter");
        }

        if (request.getTitle() != null) {
            chapter.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            chapter.setDescription(request.getDescription());
        }
        if (request.getOrderIndex() != null) {
            chapter.setOrderIndex(request.getOrderIndex());
        }

        Chapter updatedChapter = chapterRepository.save(chapter);
        return convertToChapterDto(updatedChapter);
    }

    /**
     * 删除章节
     */
    @Transactional
    public void deleteChapter(Long chapterId, User currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        // 验证用户权限
        if (chapter.getSubject().getUser() != null && !chapter.getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to delete this chapter");
        }

        chapterRepository.delete(chapter);
    }

    /**
     * 获取科目的所有章节
     */
    @Transactional(readOnly = true)
    public List<ChapterDto> getChaptersBySubject(Long subjectId, User currentUser) {
        List<Chapter> chapters = chapterRepository.findVisibleChaptersBySubjectIdAndUserId(subjectId, currentUser.getId());
        return chapters.stream()
                .map(this::convertToChapterDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取章节详情
     */
    @Transactional(readOnly = true)
    public ChapterDto getChapterById(Long chapterId, User currentUser) {
        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        // 验证用户权限
        if (chapter.getSubject().getUser() != null && !chapter.getSubject().getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to access this chapter");
        }

        return convertToChapterDto(chapter);
    }

    /**
     * 获取用户可见的所有章节
     */
    @Transactional(readOnly = true)
    public List<ChapterDto> getUserVisibleChapters(User currentUser) {
        List<Chapter> chapters = chapterRepository.findVisibleChaptersByUserId(currentUser.getId());
        return chapters.stream()
                .map(this::convertToChapterDto)
                .collect(Collectors.toList());
    }

    // 私有转换方法
    private ChapterDto convertToChapterDto(Chapter chapter) {
        return ChapterDto.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .orderIndex(chapter.getOrderIndex())
                .subjectId(chapter.getSubject().getId())
                .subjectName(chapter.getSubject().getName())
                .knowledgePoints(chapter.getKnowledgePoints().stream()
                        .map(this::convertToKnowledgePointDto)
                        .collect(Collectors.toList()))
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .build();
    }

    private com.example.studyapp.backend.dto.content.KnowledgePointDto convertToKnowledgePointDto(com.example.studyapp.backend.entity.KnowledgePoint kp) {
        return com.example.studyapp.backend.dto.content.KnowledgePointDto.builder()
                .id(kp.getId())
                .text(kp.getText())
                .details(kp.getDetails())
                .orderIndex(kp.getOrderIndex())
                .chapterId(kp.getChapter().getId())
                .chapterTitle(kp.getChapter().getTitle())
                .subjectId(kp.getChapter().getSubject().getId())
                .subjectName(kp.getChapter().getSubject().getName())
                .createdAt(kp.getCreatedAt())
                .updatedAt(kp.getUpdatedAt())
                .build();
    }
} 