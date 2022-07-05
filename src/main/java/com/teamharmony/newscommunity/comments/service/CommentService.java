package com.teamharmony.newscommunity.comments.service;

import com.teamharmony.newscommunity.comments.dto.CommentRequestDto;
import com.teamharmony.newscommunity.comments.entity.Comment;
import com.teamharmony.newscommunity.comments.repository.CommentRepository;
import com.teamharmony.newscommunity.users.entity.User;
import com.teamharmony.newscommunity.users.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(CommentRequestDto commentRequestDto, String username) {
        Comment comment = new Comment(commentRequestDto);

        User user = userRepository.findByUsername(username);

        user.addComment(comment);
        return comment.getCommentId();
    }

    public List<Comment> findComments(String newsId) {
        return commentRepository.findAllByNewsId(newsId);
    }

    @Transactional
    public void updateComment(Long id, CommentRequestDto commentRequestDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("댓글을 찾을 수 없습니다")
        );
        comment.update(commentRequestDto);
    }
}
