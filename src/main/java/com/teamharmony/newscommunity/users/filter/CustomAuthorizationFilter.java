package com.teamharmony.newscommunity.users.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.teamharmony.newscommunity.users.security.AuthConstants.TOKEN_TYPE;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

// 사용자 권한 확인
@Slf4j @RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		if (request.getServletPath().equals("/api/login") ||
				request.getServletPath().equals("/api/token/refresh") ||
				request.getServletPath().equals("/api/signup") ||
				request.getServletPath().equals("/api/signup/checkdup")) {
			filterChain.doFilter(request, response);
		} else {
			String authorizationHeader = request.getHeader(AUTHORIZATION);
			// it has the word bearer in front of a token, we know that it's our token
			if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_TYPE)) {
				try {
					String token = authorizationHeader.substring(TOKEN_TYPE.length());
					Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
					JWTVerifier verifier = JWT.require(algorithm)
					                          .build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String username = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles")
					                           .asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					stream(roles).forEach(role -> {
						authorities.add(new SimpleGrantedAuthority(role));
					});
					User user = (User) userDetailsService.loadUserByUsername(username);
					UsernamePasswordAuthenticationToken authenticationToken =
							new UsernamePasswordAuthenticationToken(user, null, authorities);
						// SS determine what resource they can access and what they can access depending on the roles
						SecurityContextHolder.getContext()
																 .setAuthentication(authenticationToken);
						filterChain.doFilter(request, response);
					} catch (Exception e) {
						log.error("Error logging in: {}", e.getMessage());
						response.setHeader("error", e.getMessage());
						response.setStatus(FORBIDDEN.value());
						Map<String, String> error = new HashMap<>();
						error.put("error_msg", e.getMessage());
						response.setContentType(APPLICATION_JSON_VALUE);
						new ObjectMapper().writeValue(response.getOutputStream(), error);
					}
				} else {
					// otherwise, just let the request continue
					filterChain.doFilter(request, response);
				}
			}
			
		}
}
