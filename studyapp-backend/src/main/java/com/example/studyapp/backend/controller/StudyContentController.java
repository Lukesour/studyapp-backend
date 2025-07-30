package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.content.SubjectDto;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.service.StudyContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.studyapp.backend.dto.content.SubjectCreateRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.util.List;

@RestController
@RequestMapping("/api/subjects") // 所有与学习科目相关的API都在 /api/subjects 路径下
@RequiredArgsConstructor
public class StudyContentController {

    private final StudyContentService studyContentService;

    /**
     * 获取对当前用户可见的所有科目列表（公共科目 + 用户自己的私有科目）
     * @param currentUser 由 Spring Security 自动注入的当前登录用户
     * @return 科目列表
     */
    @GetMapping
    public ResponseEntity<List<SubjectDto>> getMyVisibleSubjects(@AuthenticationPrincipal User currentUser) {
        // 直接调用 Service 方法，传入当前用户，获取数据
        List<SubjectDto> subjects = studyContentService.getSubjectsForUser(currentUser);
        // 返回 200 OK 和科目列表
        return ResponseEntity.ok(subjects);
    }
    /**
     * 创建一个新的私有科目
     * @param request 请求体，包含科目名称和图标
     * @param currentUser 当前登录用户
     * @return 创建成功后的科目信息
     */
    @PostMapping
    public ResponseEntity<SubjectDto> createPrivateSubject(
            @RequestBody SubjectCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        SubjectDto createdSubjectDto = studyContentService.createPrivateSubject(request, currentUser);
        return ResponseEntity.status(201).body(createdSubjectDto);
    }

}