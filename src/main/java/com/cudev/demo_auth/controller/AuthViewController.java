package com.cudev.demo_auth.controller;


import com.cudev.demo_auth.constant.SecurityConstants;
import com.cudev.demo_auth.service.MyUserDetailsService;
import com.cudev.demo_auth.util.CookieUtil;
import com.cudev.demo_auth.util.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class AuthViewController {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @RequestMapping("/login-auth-web")
    public String loginCheckUser(@RequestParam(value = "redirect_uri", required = false) String redirectUri, HttpServletRequest request, Model model,
                                 HttpServletResponse response) {

        if (redirectUri == null || redirectUri.isEmpty() || CookieUtil.getCookieByName(SecurityConstants.REDIRECT_URI_KEY, request) != null) {
            redirectUri = CookieUtil.getCookieByName(SecurityConstants.REDIRECT_URI_KEY, request);
        }else {
            Map<String, Object> data = new HashMap<>();
            data.put("redirect_uri", "http://localhost:3006/home");
            model.addAllAttributes(data);
        }

        String preAction = request.getParameter("pre_action");
        if(preAction != null) {
            CookieUtil.deleteCookie(request, response, SecurityConstants.ACCESS_TOKEN_KEY);
            CookieUtil.deleteCookie(request, response, SecurityConstants.REDIRECT_URI_KEY);
        }

        System.out.println(redirectUri);

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
