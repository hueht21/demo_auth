package com.cudev.demo_auth.controller;

import com.cudev.demo_auth.model.LoginRequest;
import com.cudev.demo_auth.model.LoginResponse;
import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.AuthenticationService;
import com.cudev.demo_auth.util.JWTUtil;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.springframework.http.ResponseEntity.ok;


@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationService service;

    @Autowired
    private JWTUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<ReponseObject> login(@RequestBody @Valid LoginRequest user) {
        try {
            ReponseObject reponseObject = service.verify(user);
            if(reponseObject.getStatus()) {
                return new ResponseEntity<ReponseObject>(
                        reponseObject,
                        HttpStatus.OK);
            }else {
                return new ResponseEntity<ReponseObject>(
                        reponseObject,
                        HttpStatus.OK);
            }
        } catch (ValidationException ex) {
            return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Thất bại", ""), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login-web")
    public String loginWeb(@RequestBody @Valid LoginRequest user) {
        try {
            ReponseObject reponseObject = service.verify(user);
            if(reponseObject.getStatus()) {
                LoginResponse loginResponse = (LoginResponse) reponseObject.getData();
                String redirectUrl = "http://localhost:3000/login-success?token=" + loginResponse.getToken();
                return "redirect:" + redirectUrl;
            }else {
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
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu hoặc sai định dạng token");
                return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Thiếu hoặc sai định dạng token", null), HttpStatus.BAD_REQUEST);
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUserName(token); // bạn có thể trả thêm roles/menus ở đây

            // Bạn có thể check thêm user có bị khóa, token đã revoke chưa nếu cần
            if (username != null) {
                return new ResponseEntity<ReponseObject>(new ReponseObject(true, "VALID", null), HttpStatus.OK);
            } else {
                return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Token không hợp lệ hoặc đã hết hạn", null), HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception e) {
            return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Token không hợp lệ hoặc lỗi xử lý", null), HttpStatus.UNAUTHORIZED);}
    }
}
