package com.cudev.demo_auth.service;

import com.cudev.demo_auth.dto.MenuDto;
import com.cudev.demo_auth.repository.MenuRepository;
import com.cudev.demo_auth.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

    public List<MenuDto> getMenusByUserId(Long userId) {
        List<Menu> menus = menuRepository.findMenusByUserId(userId);
        return menus.stream().map(menu -> MenuDto.builder().id(menu.getId()).menuName(menu.getMenuName()).menuUrl(menu.getLinkUri()).build()).collect(Collectors.toList());
    }


}
