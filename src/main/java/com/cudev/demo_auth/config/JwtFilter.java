package com.cudev.demo_auth.config;

import com.cudev.demo_auth.service.MyUserDetailsService;
import com.cudev.demo_auth.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    private static final List<String> WHITELIST = List.of(
            "/api/auth/validate",
            "/api/login",
            "/api/logout",
            "/login-web",
            "/api/login-web",
            "/login-auth-web",
            "/favicon.ico",
            "/login",
            "/logout-web",
            "/login/oauth2/code/google",
            "/assets/css/login.css"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String path = request.getRequestURI();
        System.out.println("Request URI: " + path);
        if (WHITELIST.contains(path)) {
            System.out.println("Bypass các URL không cần xác thực");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.print("vào day\n");
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtUtil.extractUserName(token);

            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            System.out.print("Token đã hết hạn");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token đã hết hạn");
        } catch (MalformedJwtException e) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Token không hợp lệ");
        } catch (Exception e) {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Lỗi server khi xử lý token");
        }



    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.resetBuffer(); // Đảm bảo status code được đặt đúng
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(message);
        response.flushBuffer();
    }
}
