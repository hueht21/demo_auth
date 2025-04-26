package com.cudev.demo_auth.service;

import com.cudev.demo_auth.dto.UserAppInfo;
import com.cudev.demo_auth.repository.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppService {

    @Autowired
    private AppRepository appRepository;

    public  Set<String> getListCodeAppByUser(Long userId) {
       return appRepository.getAllUserAppMappings(userId).stream().map(UserAppInfo::getAppCode).collect(Collectors.toSet());
    }

}
