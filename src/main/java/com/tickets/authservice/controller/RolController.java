package com.tickets.authservice.controller;

import com.tickets.authservice.dto.GenericResponse;
import com.tickets.authservice.dto.RolResponseDto;
import com.tickets.authservice.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<GenericResponse<List<RolResponseDto>>> listarRoles() {
        return ResponseEntity.ok(
                new GenericResponse<>("success", 1, "Listado de roles", rolService.listarRoles())
        );
    }
}