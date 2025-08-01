package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.Chapter;
import com.example.studyapp.backend.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    // 查找科目的所有章节，按顺序排列
    List<Chapter> findBySubjectOrderByOrderIndexAsc(Subject subject);

    // 查找科目的所有章节，按顺序排列
    List<Chapter> findBySubjectIdOrderByOrderIndexAsc(Long subjectId);

    // 查找用户可见的章节（公共科目或用户私有科目）
    @Query("SELECT c FROM Chapter c WHERE c.subject.user IS NULL OR c.subject.user.id = :userId ORDER BY c.orderIndex ASC")
    List<Chapter> findVisibleChaptersByUserId(@Param("userId") Long userId);

    // 查找指定科目的可见章节
    @Query("SELECT c FROM Chapter c WHERE c.subject.id = :subjectId AND (c.subject.user IS NULL OR c.subject.user.id = :userId) ORDER BY c.orderIndex ASC")
    List<Chapter> findVisibleChaptersBySubjectIdAndUserId(@Param("subjectId") Long subjectId, @Param("userId") Long userId);
}