package com.teamharmony.newscommunity.users.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	
	public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
	}
	// 로그인 시도
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		String username = request.getParameter("username_give");
		String password = request.getParameter("password_give");
		User user = (User) userDetailsService.loadUserByUsername(username);
		// todo
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, password);
		return authenticationManager.authenticate(authenticationToken);
	}
	// 로그인 성공
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
		User user = (User)authentication.getPrincipal();
		Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
		String access_token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
				.withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		String refresh_token = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + 60*60*1000))
				.sign(algorithm);

		response.setHeader("token", access_token);
		ResponseCookie refresh = ResponseCookie.from("ref_uid", refresh_token)
		                                       .maxAge(60*60*1000) // 밀리세컨인지 확인해야됨
		                                       .httpOnly(true)
			                                     .secure(true)
						                               .sameSite("None")
				                                   .path("/") // have to modify
				                                   .build();
		response.addHeader(SET_COOKIE, refresh.toString());
		
		response.setContentType(APPLICATION_JSON_VALUE);
		new ObjectMapper().writeValue(response.getOutputStream(), "success");
	}
}
