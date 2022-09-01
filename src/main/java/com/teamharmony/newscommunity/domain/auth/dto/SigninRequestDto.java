package com.teamharmony.newscommunity.domain.auth.dto;

import com.teamharmony.newscommunity.domain.auth.util.SigninValidator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
public class SigninRequestDto {
	@ApiModelProperty(value = "로그인 ID", example = "chlcksgur1", required = true)
	@Pattern(regexp = "^(?=.*[a-zA-Z])[-a-zA-Z0-9_.]{2,10}$")
	private String username;
	@ApiModelProperty(value = "로그인 PW", example = "secret123", required = true)
	@Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9a-zA-Z!@#$%^&*]{8,20}$")
	private String password;
	
	public SigninRequestDto(String username, String password) {
		SigninValidator.isValid(username, password);
		this.username = username;
		this.password = password;
	}
}