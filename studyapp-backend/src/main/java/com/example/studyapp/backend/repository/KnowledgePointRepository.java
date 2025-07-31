package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.KnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Long> {
    // JpaRepository 提供的基础方法已足够使用
}