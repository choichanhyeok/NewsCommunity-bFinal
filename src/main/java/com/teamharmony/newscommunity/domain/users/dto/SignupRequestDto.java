package com.teamharmony.newscommunity.domain.users.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class SignupRequestDto {
	@ApiModelProperty(value = "회원 가입 ID", example = "chlcksgur1", required = true)
	@Pattern(regexp = "(?=.*[a-zA-Z])[-a-zA-Z0-9_.]{2,10}")
	private String username;
	@ApiModelProperty(value = "회원 가입 PW", example = "secret123", required = true)
	@Pattern(regexp = "(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z!@#$%^&*]{8,20}")
	private String password;
	
	public static class CheckDup {
		@ApiModelProperty(value = "중복 확인할 ID", example = "chlcksgur1", required = true)
		@Pattern(regexp = "(?=.*[a-zA-Z])[-a-zA-Z0-9_.]{2,10}")
		public String username;
	}
}
