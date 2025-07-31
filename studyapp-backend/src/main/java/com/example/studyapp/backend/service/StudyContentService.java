package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.content.ChapterDto;
import com.example.studyapp.backend.dto.content.KnowledgePointDto;
import com.example.studyapp.backend.dto.content.SubjectDto;
import com.example.studyapp.backend.entity.Subject;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyContentService {

    private final SubjectRepository subjectRepository;

    /**
     * 获取对当前用户可见的所有科目（公共+私有）
     * @param currentUser 当前登录的用户
     * @return 科目 DTO 列表
     */
    @Transactional(readOnly = true) // readOnly=true 优化只读事务的性能
    public List<SubjectDto> getSubjectsForUser(User currentUser) {
        List<Subject> subjects = subjectRepository.findPublicAndUserPrivateSubjects(currentUser);
        return subjects.stream()
                .map(this::convertToSubjectDto)
                .collect(Collectors.toList());
    }
    /**
     * 为当前用户创建一个新的私有科目
     * @param request 创建请求的数据
     * @param currentUser 当前登录的用户
     * @return 创建成功后的科目 DTO
     */
    @Transactional // 这是一个写操作，需要事务支持
    public SubjectDto createPrivateSubject(SubjectCreateRequest request, User currentUser) {
        // 创建一个新的 Subject 实体
        Subject newSubject = Subject.builder()
                .name(request.getName())
                .iconName(request.getIconName())
                .user(currentUser) // 关键：将这个科目与当前用户关联，使其成为私有
                .build();

        // 保存到数据库
        Subject savedSubject = subjectRepository.save(newSubject);

        // 将保存后的实体转换为 DTO 并返回
        return convertToSubjectDto(savedSubject);
    }


    // --- 私有转换方法 ---

    private SubjectDto convertToSubjectDto(Subject subject) {
        return SubjectDto.builder()
                .id(subject.getId())
                .name(subject.getName())
                .iconName(subject.getIconName())
                .chapters(subject.getChapters().stream()
                        .map(this::convertToChapterDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ChapterDto convertToChapterDto(com.example.studyapp.backend.entity.Chapter chapter) {
        return ChapterDto.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .knowledgePoints(chapter.getKnowledgePoints().stream()
                        .map(this::convertToKnowledgePointDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private KnowledgePointDto convertToKnowledgePointDto(com.example.studyapp.backend.entity.KnowledgePoint kp) {
        return KnowledgePointDto.builder()
                .id(kp.getId())
                .text(kp.getText())
                .details(kp.getDetails())
                .build();
    }
}