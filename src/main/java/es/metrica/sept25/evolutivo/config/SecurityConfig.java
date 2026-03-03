package es.metrica.sept25.evolutivo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

	@Override
	// Allows Angular to access Spring Boot's endpoints, without CORS errors
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://localhost:4200")
				.allowedMethods("*")
				.allowedHeaders("*")
				.allowCredentials(true);
	}
}
