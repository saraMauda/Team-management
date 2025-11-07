package com.example.demo.security;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

//הגדרות אבטחה
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //********תפקיד הפונקציה:
    //מגדירה את שרשרת מסנן האבטחה
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //משבית את הגנת CSRF על ידי הפעלת שיטת `csrf()` והשבתתה
        http.csrf(csrf -> csrf.disable()).cors(cors->cors.configurationSource(request -> {
                    CorsConfiguration corsConfiguration=new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:5173"));
                    corsConfiguration.setAllowedMethods(List.of("*"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    return corsConfiguration;
                }))

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                                auth.requestMatchers("/h2-console/**").permitAll()
                                        .requestMatchers("/api/users/**").permitAll()

//                  .requestMatchers("/api/user/signIn").permitAll()
                                        .anyRequest().authenticated()
                ) .httpBasic(Customizer.withDefaults());

        // fix H2 database console: Refused to display ' in a frame because it set 'X-Frame-Options' to 'deny'
        http.headers(headers -> headers.frameOptions(frameOption -> frameOption.sameOrigin()));




        return http.build();
    }
}
