package com.arij.fashionecommerce.dto;


import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
