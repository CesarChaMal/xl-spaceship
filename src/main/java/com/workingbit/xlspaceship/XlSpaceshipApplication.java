package com.workingbit.xlspaceship;

import com.workingbit.xlspaceship.common.AppConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class XlSpaceshipApplication {

	public static void main(String[] args) {
		SpringApplication.run(XlSpaceshipApplication.class, args);
	}

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(AppConstants.LOCAL_CLIENT_URL)
                        .allowedMethods("GET", "POST", "PUT", "OPTIONS")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }
}
