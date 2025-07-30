package com.example.studyapp.backend.repository;

import com.example.studyapp.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // Spring: 声明这是一个仓库 Bean，让 Spring 容器来管理它
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA 的神奇之处：我们不需要写这个方法的实现！
    // 只需要按照 "findBy + 字段名" 的格式定义方法，Spring 会自动为我们生成查询。
    // 例如这个方法，会自动生成 "SELECT * FROM users WHERE username = ?" 的 SQL。
    // 使用 Optional<User> 是一个好习惯，因为查询结果可能为空，这样可以优雅地处理，避免空指针异常。
    Optional<User> findByUsername(String username);

}