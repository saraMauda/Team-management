package com.example.demo.security;

import com.example.demo.security.jwt.AuthEntryPointJwt;
import com.example.demo.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Qualifier("customUserDetailsService")
    CustomUserDetailsService userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    public WebSecurityConfig(CustomUserDetailsService userDetailsService, AuthEntryPointJwt unauthorizedHandler) {
        this.userDetailsService = userDetailsService;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

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
                    corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🟢 פתוחים לכולם (לא דורשים אימות)
                        .requestMatchers("/api/users/signin").permitAll()
                        .requestMatchers("/api/users/authenticated").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // 🟡 הרשאות לפי תפקידים:
                        // ADMIN
                        .requestMatchers("/api/users/**").hasAnyRole("ADMIN","TEAMLEADER")
<<<<<<< Updated upstream

                        .requestMatchers("/api/projects/**").permitAll()

=======
>>>>>>> Stashed changes


                        // TEAMLEADER + EMPLOYEE
                        .requestMatchers("/api/reports/**").permitAll()

                        // כל המשתמשים המחוברים (כולל EMPLOYEE)
//                        .requestMatchers("/api/meetings/**").authenticated()
                        .requestMatchers("/api/meetings/**").permitAll()


                        // כל השאר - דורש התחברות
                        .anyRequest().authenticated()
                );

        // טיפול בשגיאות גישה והרשאות
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));

        // הגדרות ל-H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        // Provider + Filter
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
