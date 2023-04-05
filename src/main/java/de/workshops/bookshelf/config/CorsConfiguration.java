package de.workshops.bookshelf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        // Example for global CORS configuration
        corsRegistry
                .addMapping("/**")
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://localhost:3000"
                )
                .allowedMethods(HttpMethod.GET.name());
    }
}
