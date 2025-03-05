package com.example.firstTry.model;

import com.example.firstTry.Enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "admins")
@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends User {
    // Admin-specific fields
    private String department;

    public Admin() {
        this.setRole(Role.ROLE_ADMIN);
    }

    @Override
    public Role getRole() {
        return Role.ROLE_ADMIN;
    }
}