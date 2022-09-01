package com.teamharmony.newscommunity.domain.auth.controller;

import com.teamharmony.newscommunity.domain.auth.dto.SigninRequestDto;
import com.teamharmony.newscommunity.domain.auth.service.AuthService;
import com.teamharmony.newscommunity.common.annotation.CurrentUser;
import com.teamharmony.newscommunity.exception.AuthException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AuthController {
	private final AuthService authService;
	
	/**
	 * 접근 토큰, 갱신 토큰 갱신
	 *
	 * @return 성공 확인
	 * @see AuthService#refreshToken
	 */
	@ApiOperation(value = "토큰 갱신", notes = "갱신 토큰으로 접근 토큰과 갱신 토큰 모두 갱신")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "Cookie", value = "갱신 토큰", required = true, dataType = "string", paramType = "header")
	})
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "success"))),
	})
	@GetMapping("/token/refresh")
	public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) throws AuthException {
		return ResponseEntity.ok().body(authService.refreshToken(request, response));
	}

	/**
	 * 로그인(swagger를 위한 method로, 실제 처리는 filter에서 이루어짐)
	 *
	 * @param requestDto 로그인하려는 사용자 ID와 비밀번호를 담은 객체
	 * @return 성공 확인
	 */
	@ApiOperation(value = "로그인", notes = "요청받은 ID와 PW로 인증(실제 처리는 filter에서 이루어짐)")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "success"))),
	})
	@PostMapping(
			path = "/login",
			consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
	)
	public ResponseEntity<String> signUp(SigninRequestDto requestDto) {
		return ResponseEntity.ok().body("success");
	}
	
	/**
	 * 로그아웃
	 *
	 * @return 성공 확인
	 * @see AuthService#signOut
	 */
	@ApiOperation(value = "로그 아웃", notes = "DB에 저장된 토큰 값을 공백 처리하고 갱신 토큰이 저장된 쿠키 삭제")
	@ApiImplicitParam(name = "Cookie", value = "갱신 토큰", required = false, dataType = "string", paramType = "header")
	@ApiResponses({
			@ApiResponse(code = 200, message = "OK", examples = @Example(value = @ExampleProperty(value = "success"))),
	})
	@GetMapping("/user/signout")
	public ResponseEntity<String> signOut(HttpServletRequest request, HttpServletResponse response, @CurrentUser String username) throws AuthException {
		return ResponseEntity.ok().body(authService.signOut(request, response, username));
	}
}
