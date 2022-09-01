package com.teamharmony.newscommunity.domain.auth.config.security;

import com.teamharmony.newscommunity.domain.auth.filter.CustomAuthenticationFilter;
import com.teamharmony.newscommunity.domain.auth.filter.CustomAuthorizationFilter;
import com.teamharmony.newscommunity.domain.auth.filter.ExceptionHandlerFilter;
import com.teamharmony.newscommunity.domain.auth.repository.TokensRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final TokensRepository tokensRepository;
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), userDetailsService, tokensRepository);
		customAuthenticationFilter.setFilterProcessesUrl("/api/login");
		http.csrf().disable();
		http.formLogin().disable();
		http.httpBasic().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);

		http.authorizeRequests()
				.antMatchers("/api/login/**", "/api/signup/**", "/api/news/**", "/api/bookmarks/**", "/actuator/health").permitAll()
				.antMatchers("/v3/api-docs", "/swagger-resources/**", "/swagger-ui.html","/swagger-ui/**").permitAll()
				.antMatchers(GET, "/api/**").permitAll()
				.antMatchers("/api/user/**").hasAuthority("ROLE_USER")
				.antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
				.anyRequest().authenticated();
		
		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(new CustomAuthorizationFilter(userDetailsService, tokensRepository), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(new ExceptionHandlerFilter(), CustomAuthorizationFilter.class);
		http.addFilterBefore(new ExceptionHandlerFilter(), CustomAuthenticationFilter.class);
	}
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/h2-console/**");
		web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
	}
}
