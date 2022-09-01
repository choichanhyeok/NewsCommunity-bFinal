package com.teamharmony.newscommunity.domain.supports.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class SupportRequestUpdateDto {
	@ApiModelProperty(value = "게시물 내용", example = "뉴스 업데이트가 언제 되는지 궁금해요!!", required = true)
	private String post_content;
}