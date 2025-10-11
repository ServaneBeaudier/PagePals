package com.pagepals.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    
    private String token;
    private String refreshToken;
    private String email;
    private String role;
    private Long id;
}

