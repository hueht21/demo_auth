package com.cudev.demo_auth.config;

import com.cudev.demo_auth.util.CustomOAuth2FailureHandler;
import com.cudev.demo_auth.util.CustomOAuth2SuccessHandler;
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
    @Autowired
    private CustomOAuth2FailureHandler oAuth2FailureHandler;

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

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
                                new AntPathRequestMatcher("/key-manager/**"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/logout-web"),
                                new AntPathRequestMatcher("/api/login-web"),
                                new AntPathRequestMatcher("/favicon.ico"),
                                new AntPathRequestMatcher("/login-auth-web"),
                                new AntPathRequestMatcher("/login/oauth2/code/google"),
                                new AntPathRequestMatcher("/assets/**"),
                                new AntPathRequestMatcher("/api/product/get-all-product")
                        ).permitAll()
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/cart/**")
                        ).hasAnyAuthority("ROLE_CUS")
                        .anyRequest().authenticated())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth -> oauth
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                ).
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
}
