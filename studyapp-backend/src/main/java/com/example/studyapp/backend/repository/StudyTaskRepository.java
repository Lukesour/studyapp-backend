package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.StudyTask;
import com.example.studyapp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyTaskRepository extends JpaRepository<StudyTask, Long> {

    // Spring Data JPA 的又一个强大功能：
    // 我们可以根据方法名自动生成更复杂的查询。
    // 这个方法会自动翻译为 "SELECT * FROM study_tasks WHERE user = ? ORDER BY created_at DESC"
    // 它会找出属于特定用户的所有任务，并按创建时间降序排列。
    List<StudyTask> findByUserOrderByCreatedAtDesc(User user);

}