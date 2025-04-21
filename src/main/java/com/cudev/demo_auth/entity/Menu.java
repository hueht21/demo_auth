package com.cudev.demo_auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu")
@Getter
@Setter
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_menu")
    private Long id;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "link_uri")
    private String linkUri;

    @Column(name = "user_id_create")
    private Integer idUserCreate;

    @Column(name = "user_id_update", nullable = false)
    private Integer idUserUpdate;

    @ManyToMany(mappedBy = "menus")
    private Set<Role> roles = new HashSet<>();
}
