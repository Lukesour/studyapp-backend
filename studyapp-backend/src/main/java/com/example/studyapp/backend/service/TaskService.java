package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.task.TaskCreateRequest;
import com.example.studyapp.backend.dto.task.TaskDto;
import com.example.studyapp.backend.dto.task.TaskUpdateRequest;
import com.example.studyapp.backend.entity.*;
import com.example.studyapp.backend.entity.enums.TaskStatus;
import com.example.studyapp.backend.entity.enums.TaskType;
import com.example.studyapp.backend.repository.*;
import com.example.studyapp.backend.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final StudyTaskRepository studyTaskRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final KnowledgePointRepository knowledgePointRepository;

    /**
     * 获取当前登录用户的所有任务
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksForUser(User currentUser) {
        List<StudyTask> tasks = studyTaskRepository.findByUserOrderByPriorityDescCreatedAtDesc(currentUser);
        return tasks.stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * 为当前登录用户创建一个新任务
     */
    @Transactional
    public TaskDto createTask(TaskCreateRequest request, User currentUser) {
        StudyTask newTask = StudyTask.builder()
                .user(currentUser)
                .title(request.getTitle())
                .description(request.getDescription())
                .subject(getSubjectIfProvided(request.getSubjectId()))
                .chapter(getChapterIfProvided(request.getChapterId()))
                .knowledgePoint(getKnowledgePointIfProvided(request.getKnowledgePointId()))
                .dueDate(request.getDueDate())
                .priority(request.getPriority())
                .taskType(parseTaskType(request.getTaskType()))
                .status(TaskStatus.PENDING)
                .build();

        StudyTask savedTask = studyTaskRepository.save(newTask);
        return convertToTaskDto(savedTask);
    }

    /**
     * 更新任务信息
     */
    @Transactional
    public TaskDto updateTask(Long taskId, TaskUpdateRequest request, User currentUser) {
        StudyTask task = studyTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 验证用户权限
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to update this task");
        }

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getSubjectId() != null) {
            task.setSubject(getSubjectIfProvided(request.getSubjectId()));
        }
        if (request.getChapterId() != null) {
            task.setChapter(getChapterIfProvided(request.getChapterId()));
        }
        if (request.getKnowledgePointId() != null) {
            task.setKnowledgePoint(getKnowledgePointIfProvided(request.getKnowledgePointId()));
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getTaskType() != null) {
            task.setTaskType(parseTaskType(request.getTaskType()));
        }
        if (request.getStatus() != null) {
            TaskStatus newStatus = parseTaskStatus(request.getStatus());
            task.setStatus(newStatus);
            
            // 如果状态变为已完成，设置完成时间
            if (newStatus == TaskStatus.COMPLETED && task.getCompletedAt() == null) {
                task.setCompletedAt(OffsetDateTime.now());
            }
        }

        StudyTask updatedTask = studyTaskRepository.save(task);
        return convertToTaskDto(updatedTask);
    }

    /**
     * 删除任务
     */
    @Transactional
    public void deleteTask(Long taskId, User currentUser) {
        StudyTask task = studyTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 验证用户权限
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to delete this task");
        }

        studyTaskRepository.delete(task);
    }

    /**
     * 获取任务详情
     */
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long taskId, User currentUser) {
        StudyTask task = studyTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 验证用户权限
        if (!task.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("User not authorized to access this task");
        }

        return convertToTaskDto(task);
    }

    /**
     * 获取用户指定状态的任务
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByStatus(User currentUser, TaskStatus status) {
        List<StudyTask> tasks = studyTaskRepository.findByUserAndStatusOrderByCreatedAtDesc(currentUser, status);
        return tasks.stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户指定类型的任务
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByType(User currentUser, TaskType taskType) {
        List<StudyTask> tasks = studyTaskRepository.findByUserAndTaskTypeOrderByCreatedAtDesc(currentUser, taskType);
        return tasks.stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户即将到期的任务（3天内）
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getUpcomingTasks(User currentUser) {
        OffsetDateTime deadline = OffsetDateTime.now().plusDays(3);
        List<StudyTask> tasks = studyTaskRepository.findUpcomingTasks(currentUser, deadline);
        return tasks.stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户已过期的任务
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getOverdueTasks(User currentUser) {
        List<StudyTask> tasks = studyTaskRepository.findOverdueTasks(currentUser, OffsetDateTime.now());
        return tasks.stream()
                .map(this::convertToTaskDto)
                .collect(Collectors.toList());
    }

    // 私有辅助方法
    private Subject getSubjectIfProvided(Long subjectId) {
        if (subjectId == null) return null;
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
    }

    private Chapter getChapterIfProvided(Long chapterId) {
        if (chapterId == null) return null;
        return chapterRepository.findById(chapterId)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
    }

    private KnowledgePoint getKnowledgePointIfProvided(Long knowledgePointId) {
        if (knowledgePointId == null) return null;
        return knowledgePointRepository.findById(knowledgePointId)
                .orElseThrow(() -> new IllegalArgumentException("Knowledge point not found"));
    }

    private TaskType parseTaskType(String taskType) {
        if (taskType == null) return null;
        try {
            return TaskType.valueOf(taskType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task type: " + taskType);
        }
    }

    private TaskStatus parseTaskStatus(String status) {
        if (status == null) return null;
        try {
            return TaskStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid task status: " + status);
        }
    }

    private TaskDto convertToTaskDto(StudyTask task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .subjectId(task.getSubject() != null ? task.getSubject().getId() : null)
                .subjectName(task.getSubject() != null ? task.getSubject().getName() : null)
                .chapterId(task.getChapter() != null ? task.getChapter().getId() : null)
                .chapterTitle(task.getChapter() != null ? task.getChapter().getTitle() : null)
                .knowledgePointId(task.getKnowledgePoint() != null ? task.getKnowledgePoint().getId() : null)
                .knowledgePointText(task.getKnowledgePoint() != null ? task.getKnowledgePoint().getText() : null)
                .dueDate(task.getDueDate())
                .priority(task.getPriority())
                .taskType(task.getTaskType() != null ? task.getTaskType().name() : null)
                .status(task.getStatus().name())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .completedAt(task.getCompletedAt())
                .build();
    }
}