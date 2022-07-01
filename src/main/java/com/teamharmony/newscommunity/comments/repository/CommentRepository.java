package com.teamharmony.newscommunity.comments.repository;

import com.teamharmony.newscommunity.comments.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByNewsId(String newsId);
}
