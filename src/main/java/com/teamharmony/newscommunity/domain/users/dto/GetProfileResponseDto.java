package com.teamharmony.newscommunity.domain.users.dto;

import com.teamharmony.newscommunity.domain.users.entity.UserProfile;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetProfileResponseDto {

	@ApiModelProperty(value = "프로필 사진 URL", example = "https://s3.ap-northeast-2.amazonaws.com/harmony-profile/chlcksgur1/image.jpg", required = true)
	private String link;
	@ApiModelProperty(value = "프로필 회원과 사용자의 일치 여부", example = "false", required = true)
	private boolean status;
	@ApiModelProperty(value = "유저 아이디", example = "guswns", required = true)
	private String username;
	@ApiModelProperty(value = "유저 닉네임", example = "김쫀떡", required = true)
	private String nickname;
	@ApiModelProperty(value = "유저 프로필 사진 링크", example = "KakaoTalk_20220611_160613387.jpg", required = true)
	private String profile_pic;
	@ApiModelProperty(value = "유저 프로필 소개", example = "반가워요!", required = true)
	private String profile_info;

	@Builder
	public GetProfileResponseDto(String link, boolean status, UserProfile profile) {
		this.link = link;
		this.status = status;
		this.username = profile.getUser().getUsername();
		this.nickname = profile.getNickname();
		this.profile_pic = profile.getProfile_pic();
		this.profile_info = profile.getProfile_info();
	}
}
