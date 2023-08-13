package com.example.wpob.repository;

import com.example.wpob.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmailAndIsDeletedIsFalse(String email);

    Optional<Users> findByIdAndIsDeletedIsFalse(Long id);

}
