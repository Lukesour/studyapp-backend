package com.example.studyapp.backend.service;

import com.example.studyapp.backend.dto.task.TaskCreateRequest;
import com.example.studyapp.backend.entity.StudyTask;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.entity.enums.TaskType;
import com.example.studyapp.backend.repository.StudyTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 导入Transactional

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final StudyTaskRepository studyTaskRepository;

    /**
     * 获取当前登录用户的所有任务
     * @param currentUser 当前登录的用户实体
     * @return 任务列表
     */
    public List<StudyTask> getTasksForUser(User currentUser) {
        return studyTaskRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    /**
     * 为当前登录用户创建一个新任务
     * @param request 任务创建请求的数据
     * @param currentUser 当前登录的用户实体
     * @return 创建好的任务实体
     */
    @Transactional // 声明这是一个事务性方法，确保数据一致性
    public StudyTask createTask(TaskCreateRequest request, User currentUser) {
        TaskType type = (request.getTimingMode() == null || request.getTimingMode() == com.example.studyapp.backend.entity.enums.TimingMode.NONE)
                ? TaskType.HABIT
                : TaskType.TIMED_GOAL;

        StudyTask newTask = StudyTask.builder()
                .user(currentUser) // 关键：将任务与当前用户关联
                .title(request.getTitle())
                .type(type)
                .timeInterval(request.getTimeInterval())
                .timingMode(request.getTimingMode())
                .targetMinutes(request.getTargetMinutes())
                .targetCount(request.getTargetCount())
                .build(); // 其他字段会使用实体类中定义的默认值

        return studyTaskRepository.save(newTask);
    }
}