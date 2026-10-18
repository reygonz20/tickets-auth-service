package com.tickets.authservice.controller;

import com.tickets.authservice.dto.GenericResponse;
import com.tickets.authservice.dto.UsuarioResponseDto;
import com.tickets.authservice.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/usuarios")
@RequiredArgsConstructor
public class UsuarioInternoController {
    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> obtener(@PathVariable Integer id) {
        UsuarioResponseDto usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(new GenericResponse<>("success", 1, "Usuario encontrado", usuario));
    }
}
