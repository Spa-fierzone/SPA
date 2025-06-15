package com.spazone.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    // Constructor with role name
    public Role(String roleName) {
        this.roleName = roleName;
    }

    public Role() {
    }

    public Role(Integer roleId, String roleName, String description, Set<User> users) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.description = description;
        this.users = users;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public String getDescription() {
        return description;
    }

    public String getRoleName() {
        return roleName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}