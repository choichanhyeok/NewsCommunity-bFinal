package com.teamharmony.newscommunity.users.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthConstants {
	public static final String TOKEN_TYPE = "Bearer "; // 시크릿이랑 같이 설정 파일로 옮기기
}