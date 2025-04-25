package com.cudev.demo_auth.util;

import com.cudev.demo_auth.constant.SecurityConstants;
import com.cudev.demo_auth.dto.MenuDto;
import com.cudev.demo_auth.dto.UserForLogin;
import com.cudev.demo_auth.entity.Role;
import com.cudev.demo_auth.entity.User;
import com.cudev.demo_auth.repository.UserRepository;
import com.cudev.demo_auth.service.AuthenticationService;
import com.cudev.demo_auth.service.MenuService;
import com.cudev.demo_auth.service.UserService;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {


    @Autowired
    private JWTUtil jwtService;


    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repo;

    @Autowired
    private MenuService menuService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {


        System.out.println("Voà onAuthenticationSuccess");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        // Optional: tạo user nếu chưa có

        User user = userService.getUser(email, name);

        System.out.println("Vào đây ồi này");
        UserForLogin userForLogin = toUserForLogin(user);

        List<String> menuNames = userForLogin.getListMenu()
                .stream()
                .map(MenuDto::getMenuUrl)
                .distinct()
                .toList();

        // Sinh JWT token
        String jwt = jwtService.generateToken(user.getUserName(), userForLogin.getListRoles().stream().toList(), menuNames);

        CookieUtil.addCookie(response, SecurityConstants.ACCESS_TOKEN_KEY, jwt);

        // Redirect về frontend
        response.sendRedirect("http://localhost:3006/dashboard?access_token=" + jwt + "&userName=" + email);

    }

    public UserForLogin toUserForLogin(User user) {
        UserForLogin res = new UserForLogin();
        BeanUtils.copyProperties(user, res);
        res.setListRoles(getRolesByUserName(user.getId()));
        res.setListMenu(getMenusByUserId(user.getId()));
        return res;
    }


    public Set<String> getRolesByUserName(Long userId) {
        return repo.findById(userId)
                .map(user -> user.getRoles()
                        .stream()
                        .map(Role::getNameRole) // chỉ lấy name
                        .collect(Collectors.toSet()))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    public List<MenuDto> getMenusByUserId(Long userId) {
        return menuService.getMenusByUserId(userId);
    }

}
