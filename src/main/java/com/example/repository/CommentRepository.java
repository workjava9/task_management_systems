package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
