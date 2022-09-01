package com.teamharmony.newscommunity.domain.users.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ProfileRequestDto {
	@ApiModelProperty(value = "닉네임", example = "하모니", required = true)
	@Size(min = 1, max = 10, message = "닉네임은 최대 10자까지 입력 가능합니다.")
	private String name;
	@ApiModelProperty(value = "프로필 사진", required = false)
	private MultipartFile file;
	@ApiModelProperty(value = "소개글", example = "안녕하세요! 하모니입니다.", required = false)
	@Size(max = 60, message = "소개글은 최대 60자까지 입력 가능합니다.")
	private String about;
}