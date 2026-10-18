package com.tickets.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolResponseDto {
    private Integer idRol;
    private String nombre;
    private String descripcion;
    private boolean estado;
}