package com.cudev.demo_auth.service;


import com.cudev.demo_auth.entity.Role;
import com.cudev.demo_auth.entity.User;
import com.cudev.demo_auth.repository.RoleRepository;
import com.cudev.demo_auth.repository.UserRepository;
import com.cudev.demo_auth.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    @Autowired
    RoleRepository roleRepository;


    public User getUser(String userName, String nameAcc) {
        // Kiểm tra userName đã tồn tại hay chưa
        Optional<User> existingUser = userRepository.findByUserName(userName);
        if (existingUser.isPresent()) {
            return existingUser.get();
        } else {
            // Tạo đối tượng User
            User user = new User();
            user.setUserName(userName);
            user.setNameUser(nameAcc);
            user.setStatusUser(1);

            // Gán danh sách Role
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByNameRole("ROLE_CUS").get());
            user.setRoles(roles);

            // Lưu User
            User savedUser = userRepository.save(user);
            return savedUser;

        }

    }



}