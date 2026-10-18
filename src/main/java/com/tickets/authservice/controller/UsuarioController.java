package com.tickets.authservice.controller;

import com.tickets.authservice.dto.*;
import com.tickets.authservice.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<GenericResponse<List<UsuarioResponseDto>>> listarUsuarios() {
        return ResponseEntity.ok(new GenericResponse<>("success", 1, "Listado de usuarios", usuarioService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> obtenerUsuarioPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Usuario encontrado", usuarioService.obtenerPorId(id)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>("error", 0, e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> crearUsuario(@Valid @RequestBody NuevoUsuarioDto dto, BindingResult br) {
        if (br.hasErrors())
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, br.getFieldError().getDefaultMessage(), null));
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(new GenericResponse<>("success", 1, "Usuario creado exitosamente", usuarioService.registrarUsuario(dto)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, e.getMessage(), null));
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> obtenerPerfil(Authentication auth) {
        try {
            Integer id = (Integer) auth.getDetails();

            return ResponseEntity.ok(
                    new GenericResponse<>(
                            "success",
                            1,
                            "Perfil del usuario",
                            usuarioService.obtenerPorId(id)
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<>(
                            "error",
                            0,
                            e.getMessage(),
                            null
                    ));
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> actualizarPerfil(@Valid @RequestBody ActualizarPerfilDto dto, BindingResult br, Authentication auth) {
        if (br.hasErrors())
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, br.getFieldError().getDefaultMessage(), null));
        try {
            Integer id = (Integer) auth.getDetails();
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Perfil actualizado", usuarioService.actualizarPerfil(id, dto)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>("error", 0, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> actualizarUsuario(@PathVariable Integer id, @Valid @RequestBody ActualizarUsuarioDto dto, BindingResult br, Authentication auth) {
        if (br.hasErrors())
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, br.getFieldError().getDefaultMessage(), null));
        try {
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Usuario actualizado", usuarioService.actualizarUsuario(id, dto, (Integer) auth.getDetails())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>("error", 0, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GenericResponse<Void>> desactivarUsuario(@PathVariable Integer id, Authentication auth) {
        try {
            usuarioService.desactivarUsuario(id, (Integer) auth.getDetails());
            return ResponseEntity.ok(new GenericResponse<>("success", 1, "Usuario desactivado lógicamente", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new GenericResponse<>("error", 0, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GenericResponse<>("error", 0, e.getMessage(), null));
        }
    }
}
