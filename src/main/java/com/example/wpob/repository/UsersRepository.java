package com.example.wpob.repository;

import com.example.wpob.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    // 회원가입, 로그인
    Optional<Users> findByEmailAndIsDeletedIsFalse(String email);

}
