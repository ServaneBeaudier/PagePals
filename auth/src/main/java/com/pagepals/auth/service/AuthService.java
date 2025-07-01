package com.pagepals.auth.service;

import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;
import com.pagepals.auth.dto.UpdateMailDTO;
import com.pagepals.auth.dto.UpdatePasswordDTO;

public interface AuthService {

    AuthResponseDTO register (RegisterDTO dto);
    AuthResponseDTO login (LoginDTO dto);
    void updateMail(UpdateMailDTO dto);
    void updatePassword(UpdatePasswordDTO dto);
    void anonymiserUtilisateur(Long userId);
}