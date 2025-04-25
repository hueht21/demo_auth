package com.cudev.demo_auth.repository;

import com.cudev.demo_auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findById(Long id);

    Optional<Role> findByNameRole(String nameRole);

    @Query(value = """
        SELECT r.* 
        FROM userrole ur 
        INNER JOIN role r ON ur.ID_ROLE = r.ID_ROLE 
        WHERE ur.ID_USER = :userId
    """, nativeQuery = true)
    List<Role> findRolesByUserId(@Param("userId") Long userId);

}