package com.example.accessingdatamysql.repository;

import com.example.accessingdatamysql.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}