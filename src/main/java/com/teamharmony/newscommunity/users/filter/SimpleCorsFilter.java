package com.teamharmony.newscommunity.users.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {
	
	public SimpleCorsFilter() {
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
//		response.setHeader("Access-Control-Allow-Origin", "https://d37biwqy4pok6o.cloudfront.net");  // have to modify
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");  // have to modify
		response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Authorization, x-csrftoken");
		response.setHeader("Access-Control-Expose-Headers", "token");
		
		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			chain.doFilter(req, res);
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) {
	}
	
	@Override
	public void destroy() {
	}
}