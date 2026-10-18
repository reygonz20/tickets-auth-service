package com.tickets.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUsuarioDto {

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ingresar un formato de correo válido")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}