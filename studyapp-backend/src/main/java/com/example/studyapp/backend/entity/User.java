package com.example.studyapp.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
// 实现 UserDetails 接口，让我们的 User 类能被 Spring Security 识别
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // 我们将存储加密后的密码

    @Column(length = 50)
    private String nickname;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "study_signature")
    private String studySignature;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;


    // ------------------- UserDetails 接口实现 -------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 在本应用中，我们暂时不使用复杂的角色权限。这里返回一个空列表。
        // 未来如果需要，可以在这里返回用户的角色列表（如 ROLE_USER, ROLE_ADMIN）。
        return List.of();
    }

    // getPassword() 和 getUsername() 方法 Lombok 已经通过 @Data 自动生成了

    @Override
    public boolean isAccountNonExpired() {
        // 账户是否未过期。我们暂时都返回 true。
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 账户是否未被锁定。我们暂时都返回 true。
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 凭证（密码）是否未过期。我们暂时都返回 true。
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 账户是否启用。我们暂时都返回 true。
        return true;
    }
}