package com.example.wpob.repository;

import com.example.wpob.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Long> {

    int countByUsers_IdAndIsDeletedIsFalse(Long userId);

}
