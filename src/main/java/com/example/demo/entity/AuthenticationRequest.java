package com.example.demo.entity;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String usernameOrEmail;
    private String password;
}
