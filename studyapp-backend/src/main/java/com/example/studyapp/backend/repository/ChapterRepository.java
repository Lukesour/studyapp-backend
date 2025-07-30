package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    // JpaRepository 提供的基础方法已足够使用
}