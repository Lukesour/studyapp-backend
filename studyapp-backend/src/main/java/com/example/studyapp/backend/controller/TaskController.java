package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.task.TaskCreateRequest;
import com.example.studyapp.backend.dto.task.TaskResponse;
import com.example.studyapp.backend.entity.StudyTask;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks") // 所有与任务相关的API都在 /api/tasks 路径下
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取当前登录用户的所有任务
     * @param currentUser Spring Security 会自动注入当前登录的用户对象
     * @return 任务列表的响应
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getMyTasks(@AuthenticationPrincipal User currentUser) {
        // 1. 调用 Service，传入当前用户信息，获取任务实体列表
        List<StudyTask> tasks = taskService.getTasksForUser(currentUser);

        // 2. 将实体列表转换为 DTO 列表
        List<TaskResponse> taskResponses = tasks.stream()
                .map(TaskResponse::fromEntity) // 使用我们在DTO中创建的静态方法
                .collect(Collectors.toList());

        // 3. 返回 200 OK 和任务列表
        return ResponseEntity.ok(taskResponses);
    }

    /**
     * 为当前登录用户创建一个新任务
     * @param request 创建任务的请求体
     * @param currentUser Spring Security 自动注入的当前用户
     * @return 创建成功后的任务信息
     */
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        // 1. 调用 Service 创建任务，并传入当前用户信息
        StudyTask createdTask = taskService.createTask(request, currentUser);

        // 2. 将创建成功的实体转换为 DTO
        TaskResponse response = TaskResponse.fromEntity(createdTask);

        // 3. 返回 201 Created 状态码和新创建的任务信息
        // （201 是 RESTful API 中表示资源创建成功的标准状态码）
        return ResponseEntity.status(201).body(response);
    }
}