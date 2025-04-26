package com.cudev.demo_auth.repository;

import com.cudev.demo_auth.dto.UserAppInfo;
import com.cudev.demo_auth.entity.Apps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppRepository extends JpaRepository<Apps, Long> {


    @Query(value = """
        SELECT u.user_name AS userName, a.app_code AS appCode
        FROM user_app up
        INNER JOIN user u ON up.id_user = u.ID_USER
        INNER JOIN app a ON a.id = up.id_app
        WHERE u.ID_USER = :userId
        """, nativeQuery = true)
    List<UserAppInfo> getAllUserAppMappings(@Param("userId") Long userId);
}
