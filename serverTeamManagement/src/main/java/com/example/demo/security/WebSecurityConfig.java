package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200", "http://localhost:8080"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    return corsConfiguration;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // נפתח במפורש את נקודת ההרשמה
                        .requestMatchers("/api/users/signup").permitAll()
                        // וגם את ה-H2 Console
                        .requestMatchers("/h2-console/**").permitAll()
                        // כל שאר הנתיבים ב-API של users פתוחים
                        .requestMatchers("/api/users/**").permitAll()
                        .requestMatchers("/api/projects/**").permitAll()
                        // כל שאר הנתיבים – דורשים התחברות
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        // לאפשר ל-H2 לעבוד בתוך iframe
        http.headers(headers -> headers.frameOptions(frameOption -> frameOption.sameOrigin()));

        return http.build();
    }
}
