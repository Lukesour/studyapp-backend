package com.example.studyapp.backend.controller;

import com.example.studyapp.backend.dto.task.TaskCreateRequest;
import com.example.studyapp.backend.dto.task.TaskDto;
import com.example.studyapp.backend.dto.task.TaskUpdateRequest;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.entity.enums.TaskStatus;
import com.example.studyapp.backend.entity.enums.TaskType;
import com.example.studyapp.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取当前登录用户的所有任务
     */
    @GetMapping
    public ResponseEntity<List<TaskDto>> getMyTasks(@AuthenticationPrincipal User currentUser) {
        List<TaskDto> tasks = taskService.getTasksForUser(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 为当前登录用户创建一个新任务
     */
    @PostMapping
    public ResponseEntity<TaskDto> createTask(
            @RequestBody TaskCreateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskDto createdTask = taskService.createTask(request, currentUser);
        return ResponseEntity.status(201).body(createdTask);
    }

    /**
     * 更新任务信息
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskUpdateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskDto updatedTask = taskService.updateTask(taskId, request, currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        taskService.deleteTask(taskId, currentUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTaskById(
            @PathVariable Long taskId,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskDto task = taskService.getTaskById(taskId, currentUser);
        return ResponseEntity.ok(task);
    }

    /**
     * 获取用户指定状态的任务
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(
            @PathVariable String status,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        List<TaskDto> tasks = taskService.getTasksByStatus(currentUser, taskStatus);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取用户指定类型的任务
     */
    @GetMapping("/type/{taskType}")
    public ResponseEntity<List<TaskDto>> getTasksByType(
            @PathVariable String taskType,
            @AuthenticationPrincipal User currentUser
    ) {
        TaskType type = TaskType.valueOf(taskType.toUpperCase());
        List<TaskDto> tasks = taskService.getTasksByType(currentUser, type);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取用户即将到期的任务（3天内）
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<TaskDto>> getUpcomingTasks(@AuthenticationPrincipal User currentUser) {
        List<TaskDto> tasks = taskService.getUpcomingTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 获取用户已过期的任务
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks(@AuthenticationPrincipal User currentUser) {
        List<TaskDto> tasks = taskService.getOverdueTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }
}