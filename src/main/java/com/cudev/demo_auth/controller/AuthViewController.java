package com.cudev.demo_auth.controller;


import com.cudev.demo_auth.model.LoginRequest;
import com.cudev.demo_auth.model.LoginResponse;
import com.cudev.demo_auth.model.ReponseObject;
import com.cudev.demo_auth.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class AuthViewController {


    @Autowired
    private AuthenticationService service;

    @RequestMapping(value = "login-web", method = RequestMethod.POST)
    public String loginWeb(@RequestBody @Valid LoginRequest user,
//                           @RequestParam("redirect_uri") String redirectUri,
                           HttpServletRequest request,
                           Model model,
                           HttpServletResponse response) throws IOException {
        try {
            ReponseObject reponseObject = service.verify(user);
            if (reponseObject.getStatus()) {
                LoginResponse loginResponse = (LoginResponse) reponseObject.getData();
                String redirectUrl = "http://localhost:3000/login-success?token=" + loginResponse.getToken();
                return "redirect:" + redirectUrl;
            } else {
                // Nếu xác thực thất bại, trả về thông báo lỗi và ở lại trang login
                model.addAttribute("error", "Đăng nhập thất bại");
                return "html/login";
            }
        } catch (ValidationException ex) {
            model.addAttribute("error", "Thông tin đăng nhập không hợp lệ");
            return "html/login";
        }
    }
}
