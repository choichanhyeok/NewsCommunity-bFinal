package com.teamharmony.newscommunity.users.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamharmony.newscommunity.users.dto.SignupDto;
import com.teamharmony.newscommunity.users.entity.Role;
import com.teamharmony.newscommunity.users.entity.RoleType;
import com.teamharmony.newscommunity.users.entity.User;
import com.teamharmony.newscommunity.users.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class UserController {
	private final UserService userService;
	// 전체 유저 불러오기
	@GetMapping("/admin/users")
	public ResponseEntity<List<User>>getUsers() {
		return ResponseEntity.ok().body(userService.getUsers());
	}
	// 회원 가입 요청 처리
	@PostMapping("/signup")
	public ResponseEntity<User>saveUser(SignupDto dto) {
		User user = User.builder().dto(dto).build();
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/signup").toUriString());
		userService.saveUser(user);
		Role role = new Role(RoleType.USER);
		role = userService.getRole(role.getName());
		try {
			if (role == null) {
				userService.saveRole(new Role(RoleType.USER));
			}
			userService.addRoleToUser(user.getUsername(),RoleType.USER);
			return ResponseEntity.created(uri).body(user);
		} catch (DataIntegrityViolationException|ConstraintViolationException e) {
			return ResponseEntity.badRequest().build();
		}
	}
	// 회원 ID 중복 확인
	@PostMapping("/signup/checkdup")
	public ResponseEntity<?>checkUser(@RequestParam("username_give") String username) {
		//todo validator
		
		// 회원 ID 입력값 공백 제거
		username = username.replaceAll("\\s", "");
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/signup/checkdup").toUriString());
		return ResponseEntity.created(uri).body(userService.checkUser(username));
	}
	
	// 현재 로그인한 유저 ID
	@GetMapping("/user/me")
	public ResponseEntity<String>getUsername(@AuthenticationPrincipal UserDetails user) {
		String username = user.getUsername();
		return ResponseEntity.ok().body(username);
	}
	
	// 권한 추가
	@PostMapping("/role/save")
	public ResponseEntity<Role>saveUser(@RequestBody Role role) {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
		return ResponseEntity.created(uri).body(userService.saveRole(role));
	}
	// 회원 권한 추가
	@PostMapping("/role/addtouser")
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form) {
		userService.addRoleToUser(form.getUsername(), form.getRoleName());
		return ResponseEntity.ok().build();
	}
	// 토큰 리프레쉬
	@GetMapping("/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// refresh_token 쿠키 가져오기 todo
		Cookie[] cookies = request.getCookies();
		Cookie cookie = null;
		if (cookies != null && cookies.length > 0) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName()
				              .equals("ref_uid")) {
					cookie = cookies[i];
				}
			}
		}
		String refresh_token = cookie.getValue();
		if (refresh_token != null) {
			try {
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm)
				                          .build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				// Once we get the username, we need to load that user
				// use the user(loaded using the username) to create a new token( using the refresh token)
				User user = userService.getUser(username);
				String access_token = JWT.create()
				                         .withSubject(user.getUsername())
				                         .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
				                         .withClaim("roles", userService.getRoles(user).stream().map(Role::getName).map(Enum::toString).collect(Collectors.toList()))
				                         .sign(algorithm);
				
				response.setHeader("access_token", access_token);
				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), "success");
			} catch (Exception e) {
				response.setHeader("error", e.getMessage());
				response.setStatus(FORBIDDEN.value());
				Map<String, String> error = new HashMap<>();
				error.put("error_msg",e.getMessage());
				response.setContentType(APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh token is missing");
		}
	}
}

@Data
class RoleToUserForm{
	private String username;
	private RoleType roleName;
}

