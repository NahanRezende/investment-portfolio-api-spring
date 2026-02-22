package com.investments.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Permitir credenciais
        config.setAllowCredentials(true);
        
        // Permitir origens específicas
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://127.0.0.1:3000"
        ));
        
        // Permitir todos os métodos HTTP
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Permitir todos os headers
        config.setAllowedHeaders(Arrays.asList("*"));
        
        // Expor headers customizados
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        // Aplicar configuração para todas as rotas
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
