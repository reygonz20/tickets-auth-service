package com.tickets.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarUsuarioDto {
    @NotBlank(message = "Los nombres son obligatorios")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$", message = "Los nombres solo pueden contener letras")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$", message = "Los apellidos solo pueden contener letras")
    private String apellidos;

    @Email(message = "Debe ingresar un formato de correo válido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y comenzar con 9")
    private String telefono;

    @NotBlank(message = "El rol es obligatorio")
    @Pattern(regexp = "^(ADMIN|TECNICO|USUARIO)$", message = "El rol debe ser ADMIN, TECNICO o USUARIO")
    private String rolAsignado;

    @Pattern(regexp = "^$|^.{6,100}$", message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;
    private Boolean estado;
}
