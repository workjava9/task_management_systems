package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.model.Comment;
import com.example.repository.CommentRepository;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void createComment(Comment comment){
        commentRepository.save(comment);
    }

    public Optional<Comment> getComment(int id){
        return commentRepository.findById(id);
    }
}
