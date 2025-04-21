package com.cudev.demo_auth.repository;

import com.cudev.demo_auth.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query(value = """
        SELECT DISTINCT m.* 
        FROM userrole ur 
        INNER JOIN role r ON ur.ID_ROLE = r.ID_ROLE 
        INNER JOIN role_menu rm ON r.ID_ROLE = rm.ID_ROLE 
        INNER JOIN menu m ON rm.ID_MENU = m.ID_MENU 
        WHERE ur.ID_USER = :userId
    """, nativeQuery = true)
    List<Menu> findMenusByUserId(@Param("userId") Long userId);

}
