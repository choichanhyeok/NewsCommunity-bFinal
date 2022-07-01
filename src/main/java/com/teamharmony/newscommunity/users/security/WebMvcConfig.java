package com.teamharmony.newscommunity.users.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 필요한가??
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/static/","classpath:/templates/", "classpath:/public/", "classpath:/",
			"classpath:/resources/", "classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/"};
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}
}
