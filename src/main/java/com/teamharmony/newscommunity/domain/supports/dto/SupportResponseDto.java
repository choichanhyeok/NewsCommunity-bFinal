package com.teamharmony.newscommunity.domain.supports.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportResponseDto {
	@ApiModelProperty(value = "게시물 식별 id", example = "1", required = true)
	private long id;
	@ApiModelProperty(value = "작성자 ID", example = "harmony", required = true)
	private String username;
	@ApiModelProperty(value = "게시물 제목", example = "문의드립니다.", required = true)
	private String post_title;
	@ApiModelProperty(value = "게시물 내용", example = "뉴스 업데이트가 언제 되나요?", required = true)
	private String post_content;
	@ApiModelProperty(value = "이메일", example = "teamharmoney22@gmail.com", required = true)
	private String post_email;
	@ApiModelProperty(value = "게시물 생성 시각", example = "2022-07-23 07:19:37.945735", required = true)
	private LocalDateTime created_at;
	@ApiModelProperty(value = "게시물 수정 시각", example = "2022-07-23 07:19:37.945735", required = true)
	private LocalDateTime modified_at;

    @Builder
    public SupportResponseDto(Long id, String username, String post_title, String post_content, String post_email, LocalDateTime created_at, LocalDateTime modified_at) {
        this.id = id;
        this.username = username;
        this.post_title = post_title;
        this.post_content = post_content;
        this.post_email = post_email;
        this.created_at = created_at;
        this.modified_at = modified_at;
    }
}