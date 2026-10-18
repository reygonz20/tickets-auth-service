package com.tickets.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponseDto {
    private Integer idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String rol;
    private boolean estado;
    private LocalDateTime fechaCreacion;
}