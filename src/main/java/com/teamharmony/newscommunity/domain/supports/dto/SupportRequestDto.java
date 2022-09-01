package com.teamharmony.newscommunity.domain.supports.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Email;

@Getter
public class SupportRequestDto {
	@ApiModelProperty(value = "게시물 제목", example = "문의드립니다.", required = true)
	private String post_title;
	@ApiModelProperty(value = "게시물 내용", example = "뉴스 업데이트가 언제 되나요?", required = true)
	private String post_content;
	@ApiModelProperty(value = "이메일", example = "teamharmoney22@gmail.com", required = true)
    @Email
    private String post_email;
}
