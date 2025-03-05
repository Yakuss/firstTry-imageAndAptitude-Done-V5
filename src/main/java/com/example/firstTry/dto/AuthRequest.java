package com.example.firstTry.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String fullname;
    private String email;
    private String password;
}
