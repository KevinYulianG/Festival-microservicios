package com.festival.ms_usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TokenResponseDto {

    private String token;
    private String tipo = "Bearer";
    private String email;
    private String rol;

    public TokenResponseDto(String token, String email, String rol) {
        this.token = token;
        this.email = email;
        this.rol = rol;
    }
}