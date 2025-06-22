package com.pagepals.auth.service;

import com.pagepals.auth.dto.AuthResponseDTO;
import com.pagepals.auth.dto.LoginDTO;
import com.pagepals.auth.dto.RegisterDTO;

public interface AuthService {

    AuthResponseDTO register (RegisterDTO dto);
    AuthResponseDTO login (LoginDTO dto);
}