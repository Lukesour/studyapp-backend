package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.StudyTask;
import com.example.studyapp.backend.entity.User;
import com.example.studyapp.backend.entity.enums.TaskStatus;
import com.example.studyapp.backend.entity.enums.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

    // 查找用户的所有任务，按创建时间降序排列
    List<StudyTask> findByUserOrderByCreatedAtDesc(User user);

    // 查找用户的所有任务，按优先级和创建时间排序
    List<StudyTask> findByUserOrderByPriorityDescCreatedAtDesc(User user);

    // 查找用户指定状态的任务
    List<StudyTask> findByUserAndStatusOrderByCreatedAtDesc(User user, TaskStatus status);

    // 查找用户指定类型的任务
    List<StudyTask> findByUserAndTaskTypeOrderByCreatedAtDesc(User user, TaskType taskType);

    // 查找用户指定科目的任务
    List<StudyTask> findByUserAndSubjectIdOrderByCreatedAtDesc(User user, Long subjectId);

    // 查找用户指定章节的任务
    List<StudyTask> findByUserAndChapterIdOrderByCreatedAtDesc(User user, Long chapterId);

    // 查找用户指定知识点的任务
    List<StudyTask> findByUserAndKnowledgePointIdOrderByCreatedAtDesc(User user, Long knowledgePointId);

    // 查找用户即将到期的任务（3天内）
    @Query("SELECT t FROM StudyTask t WHERE t.user = :user AND t.dueDate IS NOT NULL AND t.dueDate <= :deadline AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<StudyTask> findUpcomingTasks(@Param("user") User user, @Param("deadline") OffsetDateTime deadline);

    // 查找用户已过期的任务
    @Query("SELECT t FROM StudyTask t WHERE t.user = :user AND t.dueDate IS NOT NULL AND t.dueDate < :now AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<StudyTask> findOverdueTasks(@Param("user") User user, @Param("now") OffsetDateTime now);

    // 统计用户各状态的任务数量
    @Query("SELECT t.status, COUNT(t) FROM StudyTask t WHERE t.user = :user GROUP BY t.status")
    List<Object[]> countTasksByStatus(@Param("user") User user);

    // 统计用户各类型的任务数量
    @Query("SELECT t.taskType, COUNT(t) FROM StudyTask t WHERE t.user = :user GROUP BY t.taskType")
    List<Object[]> countTasksByType(@Param("user") User user);
}