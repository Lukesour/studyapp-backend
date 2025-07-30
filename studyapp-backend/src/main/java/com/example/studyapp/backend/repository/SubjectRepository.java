package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.Subject;
import com.example.studyapp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    // 这个查询会找出所有公共科目 (user IS NULL)
    // 或者属于当前用户的私有科目 (user = ?1)
    @Query("SELECT s FROM Subject s WHERE s.user IS NULL OR s.user = ?1")
    List<Subject> findPublicAndUserPrivateSubjects(User user);
}