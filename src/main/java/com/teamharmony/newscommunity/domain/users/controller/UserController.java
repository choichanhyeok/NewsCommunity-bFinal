package com.teamharmony.newscommunity.domain.users.controller;

import com.teamharmony.newscommunity.common.annotation.CurrentUser;
import com.teamharmony.newscommunity.domain.users.dto.GetProfileResponseDto;
import com.teamharmony.newscommunity.domain.users.dto.ProfileRequestDto;
import com.teamharmony.newscommunity.domain.users.dto.UserResponseDto;
import com.teamharmony.newscommunity.domain.users.entity.Role;
import com.teamharmony.newscommunity.domain.users.entity.RoleType;
import com.teamharmony.newscommunity.domain.users.service.UserService;
import com.teamharmony.newscommunity.domain.users.dto.SignupRequestDto;
import io.swagger.annotations.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * 사용자 관련 요청을 처리
 *
 * @author enyo9rt
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
	private final UserService userService;

	/**
	 * 회원 ID 중복 확인
	 *
	 * @param requestDto 가입하려는 사용자 ID
	 * @return 중복 여부
	 * @see UserService#checkUser
	 */
	@ApiOperation(value = "ID 중복 확인", notes = "중복되는 ID가 있는지 확인")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "requestDto", value = "가입하려는 사용자 ID", required = true, dataType = "SignupRequestDto.CheckDup")
	})
	@PostMapping("/signup/checkdup")
	public ResponseEntity<Boolean> checkUser(@RequestBody @Valid SignupRequestDto.CheckDup requestDto) {
		return ResponseEntity.ok().body(userService.checkUser(requestDto.username));
	}

	/**
	 * 회원 가입
	 *
	 * @param requestDto 가입하려는 사용자 ID와 비밀번호를 담은 객체
	 * @return 성공 확인
	 * @see UserService#signUp
	 */
	@ApiOperation(value = "회원 가입", notes = "요청받은 ID와 PW로 회원 생성")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "success"))),
	})
	@PostMapping("/signup")
	public ResponseEntity<String> signUp(@RequestBody @Valid SignupRequestDto requestDto) {
		return ResponseEntity.ok().body(userService.signUp(requestDto));
	}

	/**
	 * 현재 로그인한 유저 ID
	 *
	 * @param username 인증된 사용자 정보
	 * @return 인증된 사용자 ID
	 * @see UserDetails#getUsername
	 */
	@ApiOperation(value = "인증된 사용자 ID 조회", notes = "헤더로 전달된 접근 토큰으로 사용자 ID 확인")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "chlcksgur1"))),
	})
	@GetMapping("/user/me")
	public ResponseEntity<String> getUsername(@CurrentUser String username) {
		return ResponseEntity.ok().body(username);
	}

	/**
	 * 권한 추가
	 *
	 * @param 		role enum 타입 권한명이 담긴 객체
	 * @see				UserService#saveRole
	 */
	@ApiOperation(value = "권한 추가", notes = "Enum으로 지정된 권한명을 입력하여 권한 추가")
	@PostMapping("/admin/role/save")
	public ResponseEntity<Void> saveRole(@RequestBody Role role) {
		userService.saveRole(role);
		return ResponseEntity.ok().build();
	}

	/**
	 * 사용자 정보에 권한 추가
	 *
	 * @param form 사용자 ID와 권한명이 담긴 객체
	 * @see UserService#addRoleToUser
	 */
	@ApiOperation(value = "사용자에 권한 부여", notes = "요청받은 사용자 ID와 권한명으로 사용자에 특정 권한 부여")
	@PostMapping("/admin/role/addtouser")
	public ResponseEntity<Void> addRoleToUser(@RequestBody RoleToUserForm form) {
		userService.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}

	/**
	 * 전체 유저 불러오기
	 *
	 * @return 전체 유저를 담은 List
	 * @see UserService#getUsers
	 */
	@ApiOperation(value = "전체 회원 조회", notes = "전체 회원을 조회")
	@GetMapping("/admin/users")
	public ResponseEntity<List<UserResponseDto>> getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}

	/**
	 * 회원 프로필 정보 조회
	 *
	 * @param profileOwner 프로필 회원 ID
	 * @param username 인증된 사용자 정보
	 * @return 인증된 사용자 ID와 일치 여부, 프로필 사진 url, 프로필 정보
	 * @see UserService#getProfile
	 */
	@ApiOperation(value = "프로필 정보 조회", notes = "요청받은 프로필 회원 ID의 프로필 정보를 조회하고 현재 사용자와 일치하는지 확인")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "profileOwner", value = "프로필 회원 ID", required = true, dataType = "String", example = "chlcksgur1")
	})
	@GetMapping("/user/profile/{profileOwner}")
	public ResponseEntity<GetProfileResponseDto> getProfile(@PathVariable String profileOwner, @CurrentUser String username) {
		boolean status = profileOwner.equals(username);
		return ResponseEntity.ok().body(userService.getProfile(profileOwner, status));
	}

	/**
	 * 회원 프로필 사진 URL 조회
	 *
	 * @param username 프로필 회원 ID
	 * @return 프로필 사진 url
	 * @see UserService#getProfileImageUrl
	 */
	@ApiOperation(value = "프로필 사진 URL 조회", notes = "요청받은 프로필 회원 ID의 프로필 사진 URL을 조회(default일 경우 기본 이미지 적용)")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "https://s3.ap-northeast-2.amazonaws.com/harmony-profile/chlcksgur1/image.jpg"))),
	})
	@ApiImplicitParam(name = "username", value = "프로필 회원 ID", required = true, dataType = "String", example = "chlcksgur1")
	@GetMapping("/user/profile/pic/{username}")
	public ResponseEntity<String> getProfilePicUrl(@PathVariable String username) {
		return ResponseEntity.ok().body(userService.getProfileImageUrl(username));
	}
	
	/**
	 * 회원 프로필 변경
	 *
	 * @param requestDto 닉네임, 프로필 사진 파일, 소개글을 담은 객체
	 * @param username 인증된 사용자 정보
	 * @return 성공 확인
	 * @see UserService#updateProfile
	 */
	@ApiOperation(value = "프로필 변경", notes = "요청받은 프로필 정보로 프로필 변경")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "success"))),
	})
	@PostMapping(
			path = "/user/update_profile",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	)
	public ResponseEntity<String> updateProfile(ProfileRequestDto requestDto, @CurrentUser String username) {
		return ResponseEntity.ok().body(userService.updateProfile(username, requestDto));
	}

	@Data
	class RoleToUserForm{
		@ApiModelProperty(value = "사용자 ID", example = "chlcksgur1", required = true)
		private String username;
		@ApiModelProperty(value = "권한명", example = "USER", required = true)
		private RoleType roleName;
	}
}