package com.teamharmony.newscommunity.comments.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.teamharmony.newscommunity.comments.dto.CommentRequestDto;
import com.teamharmony.newscommunity.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Comment extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long commentId;
    private String content;
    private String newsId;

    @JsonManagedReference
    @ManyToOne
    private User user;


    public Comment(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
        this.newsId = commentRequestDto.getNewsId();
    }

    public void update(CommentRequestDto commentRequestDto) {
        this.content = commentRequestDto.getContent();
    }
}
