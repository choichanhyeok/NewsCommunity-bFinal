package com.teamharmony.newscommunity.users.entity;

public enum RoleType {
	USER {
		public String toString() {
			return "ROLE_USER";
		}},  // 기본 사용자 권한
	ADMIN {
		public String toString() {
			return "ROLE_ADMIN";
		}}  // 관리자 권한
}