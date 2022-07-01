package com.teamharmony.newscommunity.comments.controller;

import com.teamharmony.newscommunity.comments.dto.CommentRequestDto;
import com.teamharmony.newscommunity.comments.entity.Comment;
import com.teamharmony.newscommunity.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comments")
    public Long saveComment(@RequestBody CommentRequestDto commentRequestDto) {
        return commentService.createComment(commentRequestDto);
    }

    @GetMapping("/comments/{news_id}")
    public List<Comment> getComment(@PathVariable String news_id) {
        return commentService.findComments(news_id);
    }

    @GetMapping("/test")
    public Map<String, Long> test() {
        Map<String, Long> result = new HashMap<>();
        result.put("userId", 1L);
        return result;
    }
//
//    @PutMapping("/comments/{id}")
//    public Long editComment(@PathVariable Long id,
//                            @RequestBody CommentRequestDto commentRequestDto) {
//        commentService.updateComment(id, commentRequestDto);
//        return id;
//    }
//
//    @DeleteMapping("/comments/{id}")
//    public Long deleteComment(@PathVariable Long id) {
//        commentRepository.deleteById(id);
//        return id;
//    }
}
