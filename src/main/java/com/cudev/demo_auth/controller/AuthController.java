package com.cudev.demo_auth.controller;

import com.cudev.demo_auth.constant.SecurityConstants;
import com.cudev.demo_auth.model.LoginRequest;
import com.cudev.demo_auth.model.LoginResponse;
import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.AuthenticationService;

import com.cudev.demo_auth.util.CookieUtil;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity<ReponseObject> login(@RequestBody  LoginRequest user, HttpServletResponse response) {
        try {
            ReponseObject reponseObject = service.verify(user);
            if (reponseObject.getStatus()) {
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
    public ResponseEntity<ReponseObject> loginWeb(@RequestBody  LoginRequest user, HttpServletResponse response) {
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
                        HttpStatus.UNAUTHORIZED);
            }
        } catch (ValidationException ex) {
            return new ResponseEntity<ReponseObject>(new ReponseObject(false, "Thất bại", ""), HttpStatus.BAD_REQUEST);
        }
    }

//
//    @PostMapping("/check")
//    public ResponseEntity<ReponseObject> checkLogin(@RequestBody Map<String, String> body) {
//        String token = body.get("token");
//        if (token != null) {
//
//            try{
//                if(jwtUtil.isTokenExpired(token)) {
//                    // Đã login → redirect ngay về domain2
//                    String username = jwtUtil.extractUserName(token);
//                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);
//
//                    if (jwtUtil.validateToken(token, userDetails)) {
//                        ReponseObject reponseObject = new ReponseObject(true, "Đã đăng nhập", token);
//                        return ResponseEntity.ok(reponseObject);
//                    }else {
//                        ReponseObject reponseObject = new ReponseObject(false, "Token không hợp lệ", null);
//                        return ResponseEntity.ok(reponseObject);
//                    }
//                }else {
//                    // Token chưa hết hạn
//                    ReponseObject reponseObject = new ReponseObject(true, "Token hết hạn", token);
//                    return ResponseEntity.ok(reponseObject);
//                }
//            }catch (Exception e) {
//                return ResponseEntity.ok(new ReponseObject(false, "Token không hợp lệ", null));
//            }
//
//        }
//
//        // Chưa login
//        ReponseObject reponseObject = new ReponseObject(false, "Chưa đăng nhập", null);
//        return ResponseEntity.ok(reponseObject);
//    }
}
