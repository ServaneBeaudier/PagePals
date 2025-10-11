package com.pagepals.auth.service;

import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.dto.UpdateMailDTO;
import com.pagepals.auth.dto.UpdatePasswordDTO;
import com.pagepals.auth.model.UserEntity;

import io.jsonwebtoken.Claims;

public interface AuthService {

    AuthResponseDTO register (RegisterDTO dto);
    AuthResponseDTO login (LoginDTO dto);
    void updateMail(UpdateMailDTO dto);
    void updatePassword(UpdatePasswordDTO dto);
    String getEmailByUserId(Long userId);
    void deleteUserById(Long userId);
    
    //pour le refresh token
    Claims parseToken(String token);
    UserEntity findUserById(Long id);
    String generateAccessToken(UserEntity user);
    String generateRefreshToken(UserEntity user);
}