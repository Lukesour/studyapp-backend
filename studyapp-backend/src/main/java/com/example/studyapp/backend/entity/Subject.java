package com.example.studyapp.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 如果 user 为 null，表示这是公共科目
    // 否则，表示这是该用户私有的科目
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 外键，可以为NULL
    private User user;

    @Column(nullable = false)
    private String name;

    // 存储 Material Icons 的名称, e.g., "Gavel", "AccountBalance"
    @Column(name = "icon_name")
    private String iconName;

    // 一个科目 (One) 包含多个章节 (Many)
    // cascade = CascadeType.ALL: 当删除科目时，其下的所有章节也一并删除
    // orphanRemoval = true: 当从科目的章节列表中移除一个章节时，该章节将从数据库中删除
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Chapter> chapters = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}