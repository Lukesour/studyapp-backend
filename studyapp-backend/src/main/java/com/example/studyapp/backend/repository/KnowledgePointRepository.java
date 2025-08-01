package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.Chapter;
import com.example.studyapp.backend.entity.KnowledgePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgePointRepository extends JpaRepository<KnowledgePoint, Long> {

    // 查找章节的所有知识点，按顺序排列
    List<KnowledgePoint> findByChapterOrderByOrderIndexAsc(Chapter chapter);

    // 查找章节的所有知识点，按顺序排列
    List<KnowledgePoint> findByChapterIdOrderByOrderIndexAsc(Long chapterId);

    // 查找用户可见的知识点（公共科目或用户私有科目）
    @Query("SELECT kp FROM KnowledgePoint kp WHERE kp.chapter.subject.user IS NULL OR kp.chapter.subject.user.id = :userId ORDER BY kp.orderIndex ASC")
    List<KnowledgePoint> findVisibleKnowledgePointsByUserId(@Param("userId") Long userId);

    // 查找指定章节的可见知识点
    @Query("SELECT kp FROM KnowledgePoint kp WHERE kp.chapter.id = :chapterId AND (kp.chapter.subject.user IS NULL OR kp.chapter.subject.user.id = :userId) ORDER BY kp.orderIndex ASC")
    List<KnowledgePoint> findVisibleKnowledgePointsByChapterIdAndUserId(@Param("chapterId") Long chapterId, @Param("userId") Long userId);

    // 查找指定科目的所有知识点
    @Query("SELECT kp FROM KnowledgePoint kp WHERE kp.chapter.subject.id = :subjectId AND (kp.chapter.subject.user IS NULL OR kp.chapter.subject.user.id = :userId) ORDER BY kp.chapter.orderIndex ASC, kp.orderIndex ASC")
    List<KnowledgePoint> findVisibleKnowledgePointsBySubjectIdAndUserId(@Param("subjectId") Long subjectId, @Param("userId") Long userId);
}