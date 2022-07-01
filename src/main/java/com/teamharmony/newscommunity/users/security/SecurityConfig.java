package com.teamharmony.newscommunity.users.security;

import com.teamharmony.newscommunity.users.filter.CustomAuthenticationFilter;
import com.teamharmony.newscommunity.users.filter.CustomAuthorizationFilter;
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
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
		customAuthenticationFilter.setFilterProcessesUrl("/api/login");
		http.csrf()
		    .ignoringAntMatchers("/h2-console/**")
		    .disable();
		http.formLogin().disable();
		http.httpBasic().disable();
		http.sessionManagement().sessionCreationPolicy(STATELESS);
//		http.authorizeRequests()
//		    .antMatchers("/templates/**").permitAll()
//		    .antMatchers("/static/**").permitAll();
		http.authorizeRequests().antMatchers("/h2-console/**").permitAll();

		http.authorizeRequests().antMatchers("/api/login/**", "/api/token/**", "/api/signup/**").permitAll();
		http.authorizeRequests().antMatchers(GET, "/api/user/**").hasAuthority("ROLE_USER");
		http.authorizeRequests().antMatchers(GET, "/api/admin/**").hasAuthority("ROLE_ADMIN");
		http.authorizeRequests().anyRequest().authenticated();
		
		http.addFilter(customAuthenticationFilter);
		// this filter comes before the other filters
		http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
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
