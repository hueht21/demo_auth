package com.cudev.demo_auth.controller;


import com.cudev.demo_auth.constant.SecurityConstants;
import com.cudev.demo_auth.entity.User;
import com.cudev.demo_auth.model.LoginRequest;
import com.cudev.demo_auth.model.LoginResponse;
import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.AuthenticationService;
import com.cudev.demo_auth.service.MyUserDetailsService;
import com.cudev.demo_auth.util.CookieUtil;
import com.cudev.demo_auth.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.Optional;

@Controller
public class AuthViewController {


    @Autowired
    private AuthenticationService service;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

//    @RequestMapping(value = "login-web", method = RequestMethod.POST)
//    public String loginWeb(@RequestBody @Valid LoginRequest user,
////                           @RequestParam("redirect_uri") String redirectUri,
//                           HttpServletRequest request,
//                           Model model,
//                           HttpServletResponse response) throws IOException {
//        try {
//            ReponseObject reponseObject = service.verify(user);
//            if (reponseObject.getStatus()) {
//                LoginResponse loginResponse = (LoginResponse) reponseObject.getData();
//                String redirectUrl = "http://localhost:3000/login-success?token=" + loginResponse.getToken();
//                return "redirect:" + redirectUrl;
//            } else {
//                // Nếu xác thực thất bại, trả về thông báo lỗi và ở lại trang login
//                model.addAttribute("error", "Đăng nhập thất bại");
//                return "html/login";
//            }
//        } catch (ValidationException ex) {
//            model.addAttribute("error", "Thông tin đăng nhập không hợp lệ");
//            return "html/login";
//        }
//    }


    @RequestMapping("/login-auth-web")
    public String loginCheckUser(@RequestParam(value = "redirect_uri", required = false) String redirectUri, HttpServletRequest request,
                                 HttpServletResponse response) {

        if (redirectUri == null || redirectUri.isEmpty()) {
            redirectUri = CookieUtil.getCookieByName(SecurityConstants.REDIRECT_URI_KEY, request);
        }

        Optional<Cookie> cookie = CookieUtil.getCookie(request, SecurityConstants.ACCESS_TOKEN_KEY);

        if (cookie.isEmpty()) {
            return "login-auth-web";
        }else  {
            try{
                String accessToken = cookie.get().getValue();
                if(!jwtUtil.isTokenExpired(accessToken)) {
                    // Đã login → redirect ngay về domain2
                    String username = jwtUtil.extractUserName(accessToken);
                    UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

                    if (jwtUtil.validateToken(accessToken, userDetails)) {
                        return "redirect:" + redirectUri + "?access_token=" + accessToken + "&userName=" + username;
                    }else {
                        CookieUtil.deleteCookie(request, response, SecurityConstants.ACCESS_TOKEN_KEY);
                        CookieUtil.addCookie(response, SecurityConstants.REDIRECT_URI_KEY, redirectUri);
                        return "login-auth-web";
                    }
                }else  {
                    CookieUtil.addCookie(response, SecurityConstants.REDIRECT_URI_KEY, redirectUri);
                    CookieUtil.deleteCookie(request, response, SecurityConstants.ACCESS_TOKEN_KEY);
                    return "login-auth-web";
                }

            }catch (Exception e) {
                CookieUtil.addCookie(response, SecurityConstants.REDIRECT_URI_KEY, redirectUri);
                CookieUtil.deleteCookie(request, response, SecurityConstants.ACCESS_TOKEN_KEY);
                return "login-auth-web";
            }
        }

    }


    @RequestMapping("/logout-web")
    public String loginOut(@RequestParam(value = "uri", required = false) String redirectUri, HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("Đã call logout");
            CookieUtil.deleteCookie(request,response, SecurityConstants.ACCESS_TOKEN_KEY);

            return "redirect:" + redirectUri;

        } catch (ValidationException ex) {
            return "redirect:" + "http://localhost:3006/home";
        }
    }
}
