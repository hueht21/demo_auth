package com.cudev.demo_auth.config;

import com.cudev.demo_auth.util.CustomAccessDeniedHandler;
import com.cudev.demo_auth.util.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(UserDetailsService userDetailsService, CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Bật CORS theo cấu hình mặc định
                .csrf(customizer -> customizer.disable()).
                authorizeHttpRequests(request -> request
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/login-web"),
                                new AntPathRequestMatcher("/api/login"),
                                new AntPathRequestMatcher("/api/logout"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/logout-web"),
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/login-web"),
                                new AntPathRequestMatcher("/api/login-web"),
                                new AntPathRequestMatcher("/api/check"),
                                new AntPathRequestMatcher("/favicon.ico"),
                                new AntPathRequestMatcher("/login-auth-web"),
                                new AntPathRequestMatcher("/login-check-user"),
                                new AntPathRequestMatcher("/assets/**") // ✅ Đúng cú pháp
                        ).permitAll()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/roles/**"),
                                new AntPathRequestMatcher("/api/menus/**")

                        ).hasAnyAuthority("ROLE_ROOT")
                        .anyRequest().authenticated())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).
                build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        provider.setUserDetailsService(userDetailsService);


        return provider;
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Domain được phép
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // HTTP Methods được phép
        configuration.setAllowedHeaders(List.of("*")); // Headers được phép
        configuration.setAllowCredentials(false); // Cho phép gửi thông tin xác thực (như cookies)

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả các endpoint
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();

    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
//        var configuration = new org.springframework.web.cors.CorsConfiguration();
//
//        // Cập nhật origin để chỉ định domain frontend
//        configuration.setAllowedOrigins(List.of("http://localhost:3006", "http://localhost:3000")); // Thay đổi domain frontend của bạn
//
//        // Cấu hình các phương thức HTTP được phép
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        // Cho phép tất cả các headers
//        configuration.setAllowedHeaders(List.of("*"));
//
//        // Cho phép gửi thông tin xác thực (cookies, headers Authorization...)
//        configuration.setAllowCredentials(true);
//
//        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration); // Áp dụng cho tất cả các endpoint
//        return source;
//    }
}
