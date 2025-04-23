package com.cudev.demo_auth.controller;

import com.cudev.demo_auth.constant.SecurityConstants;
import com.cudev.demo_auth.dto.UserRoleDto;
import com.cudev.demo_auth.model.LoginRequest;
import com.cudev.demo_auth.model.LoginResponse;
import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.AuthenticationService;
import com.cudev.demo_auth.service.MyUserDetailsService;
import com.cudev.demo_auth.util.CookieUtil;
import com.cudev.demo_auth.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationService service;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @PostMapping("/login")
    public ResponseEntity<ReponseObject> login(@RequestBody @Valid LoginRequest user, HttpServletResponse response) {
        try {
            ReponseObject reponseObject = service.verify(user);
            if (reponseObject.getStatus()) {
                LoginResponse loginResponse = (LoginResponse) reponseObject.getData();
                CookieUtil.addCookie(response, SecurityConstants.ACCESS_TOKEN_KEY, loginResponse.getToken());
                return new ResponseEntity<ReponseObject>(
                        reponseObject,
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<ReponseObject>(
                        reponseObject,
                        HttpStatus.OK);
            }
        } catch (ValidationException ex) {
            return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Thất bại", ""), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login-web")
    public String loginWeb(@RequestBody @Valid LoginRequest user, HttpServletResponse response) {
        try {
            ReponseObject reponseObject = service.verify(user);
            if (reponseObject.getStatus()) {
                LoginResponse loginResponse = (LoginResponse) reponseObject.getData();
                String redirectUrl = "http://localhost:3000/login-success?token=" + loginResponse.getToken();

                CookieUtil.addCookie(response, SecurityConstants.ACCESS_TOKEN_KEY, loginResponse.getToken());

                return "redirect:" + redirectUrl;
            } else {
                return "";
            }
        } catch (ValidationException ex) {
            return "";
        }
    }

    @PostMapping("/auth/validate")
    public ResponseEntity<ReponseObject> validateToken(@RequestHeader("Authorization") String authHeader) {

        System.out.println("Đã call đen day");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Thiếu hoặc sai định dạng token", null), HttpStatus.BAD_REQUEST);
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUserName(token);

            // Bạn có thể check thêm user có bị khóa, token đã revoke chưa nếu cần
            if (username != null) {

                Long id = service.getIdUserByUserName(username);
                Set<String> roles = service.getRolesByUserName(id);
                UserRoleDto userRoleDto = new UserRoleDto(username, roles);

                return new ResponseEntity<ReponseObject>(new ReponseObject(true, "VALID", userRoleDto), HttpStatus.OK);
            } else {
                return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Token không hợp lệ hoặc đã hết hạn", null), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Token không hợp lệ hoặc lỗi xử lý", null), HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/check")
    public ResponseEntity<ReponseObject> checkLogin(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        if (token != null) {

            try{
                if(jwtUtil.isTokenExpired(token)) {
                    // Đã login → redirect ngay về domain2
                    String username = jwtUtil.extractUserName(token);
                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(token, userDetails)) {
                        ReponseObject reponseObject = new ReponseObject(true, "Đã đăng nhập", token);
                        return ResponseEntity.ok(reponseObject);
                    }else {
                        ReponseObject reponseObject = new ReponseObject(false, "Token không hợp lệ", null);
                        return ResponseEntity.ok(reponseObject);
                    }
                }else {
                    // Token chưa hết hạn
                    ReponseObject reponseObject = new ReponseObject(true, "Token hết hạn", token);
                    return ResponseEntity.ok(reponseObject);
                }
            }catch (Exception e) {
                return ResponseEntity.ok(new ReponseObject(false, "Token không hợp lệ", null));
            }

        }

        // Chưa login
        ReponseObject reponseObject = new ReponseObject(false, "Chưa đăng nhập", null);
        return ResponseEntity.ok(reponseObject);
    }
}
