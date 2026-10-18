package com.tickets.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtDto {
    private String token;
    private String bearer = "Bearer";
    private Integer idUsuario;
    private String correo;
    private String nombres;
    private String rol;

    public JwtDto(String token, Integer idUsuario, String correo, String nombres, String rol) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.nombres = nombres;
        this.rol = rol;
    }
}