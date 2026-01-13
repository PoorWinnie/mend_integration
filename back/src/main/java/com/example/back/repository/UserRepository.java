package com.example.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.back.domain.UserBean;

@Repository
public interface UserRepository extends JpaRepository<UserBean, Integer> {
    Optional<UserBean> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
