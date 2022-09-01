package com.teamharmony.newscommunity.domain.comments.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.teamharmony.newscommunity.common.util.Timestamped;
import com.teamharmony.newscommunity.domain.comments.dto.CommentCreateRequestDto;
import com.teamharmony.newscommunity.domain.comments.dto.CommentEditRequestDto;
import com.teamharmony.newscommunity.domain.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Comment extends Timestamped {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long commentId;
    @Size(max = 300, message = "글자수 초과")
    private String content;
    private String newsId;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonBackReference
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<Likes> likesList;


    public Comment(CommentCreateRequestDto commentCreateRequestDto) {
        this.content = commentCreateRequestDto.getContent();
        this.newsId = commentCreateRequestDto.getNewsId();
    }

    // Comment 객체가 가지고 있는 likesList 필드에 새로운 likes를 추가하는 메소드
    public void addLikes(Likes likes) {
        likes.setComment(this);
        likesList.add(likes);
    }

    // 댓글을 수정하는 메소드
    public void update(CommentEditRequestDto commentEditRequestDto) {
        this.content = commentEditRequestDto.getContent();
    }
}
