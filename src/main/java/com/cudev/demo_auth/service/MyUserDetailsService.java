package com.cudev.demo_auth.service;

import com.cudev.demo_auth.repository.UserRepository;
import com.cudev.demo_auth.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository repo;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.repo = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = repo.findByUserName(username).get();

        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }

        // Chuyển đổi danh sách roles thành danh sách GrantedAuthority
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNameRole()))
                .collect(Collectors.toList());

        // Nếu user không có password (tức đăng nhập bằng Google), gán giá trị mặc định
        String password = (user.getPassword() != null) ? user.getPassword() : "N/A";

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                password,
                authorities
        );
    }

}
