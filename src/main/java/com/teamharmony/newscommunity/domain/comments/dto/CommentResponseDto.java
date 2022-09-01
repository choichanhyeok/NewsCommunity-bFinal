package com.teamharmony.newscommunity.domain.comments.dto;

import com.teamharmony.newscommunity.domain.comments.entity.Comment;
import com.teamharmony.newscommunity.domain.users.dto.ProfileResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

	@ApiModelProperty(value = "댓글 식별 id", example = "34", required = true)
	private Long commentId;
	@ApiModelProperty(value = "댓글 수정 시간", example = "2022-07-23 07:19:37.945735", required = true)
	private LocalDateTime modifiedAt;
	@ApiModelProperty(value = "댓글 생성 시간", example = "2022-07-23 07:19:37.945735", required = true)
	private LocalDateTime createdAt;
	@ApiModelProperty(value = "댓글 내용", example = "흥미로운 기사네요!", required = true)
	private String content;
	private ProfileResponseDto profileResponseDto;
	@ApiModelProperty(value = "댓글 좋아요 여부", example = "false", required = true)
	private Boolean like;



    @Builder
    public CommentResponseDto(Long commentId,
                              LocalDateTime modifiedAt,
                              LocalDateTime createdAt,
                              String content,
                              ProfileResponseDto profileResponseDto,
                              Boolean like) {

        this.commentId = commentId;
        this.modifiedAt = modifiedAt;
        this.createdAt = createdAt;
        this.content = content;
        this.profileResponseDto = profileResponseDto;
        this.like = like;
    }

    public static CommentResponseDto toDto(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .modifiedAt(comment.getModifiedAt())
                .createdAt(comment.getCreatedAt())
                .content(comment.getContent())
                .profileResponseDto(new ProfileResponseDto(comment.getUser().getProfile()))
                .build();
    }

    public void likeUser(Boolean like) {
        this.like = like;
    }
}
