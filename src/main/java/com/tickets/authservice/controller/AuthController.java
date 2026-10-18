package com.tickets.authservice.controller;

import com.tickets.authservice.dto.GenericResponse;
import com.tickets.authservice.dto.JwtDto;
import com.tickets.authservice.dto.LoginUsuarioDto;
import com.tickets.authservice.dto.NuevoUsuarioDto;
import com.tickets.authservice.dto.UsuarioResponseDto;
import com.tickets.authservice.security.JwtProvider;
import com.tickets.authservice.security.UsuarioPrincipal;
import com.tickets.authservice.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<GenericResponse<JwtDto>> login(
            @Valid @RequestBody LoginUsuarioDto loginUsuarioDto,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new GenericResponse<>("error", 0, "Campos inválidos", null), HttpStatus.BAD_REQUEST);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUsuarioDto.getCorreo(), loginUsuarioDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateToken(authentication);
            UsuarioPrincipal userDetails = (UsuarioPrincipal) authentication.getPrincipal();

            String rol = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(r -> r.getAuthority())
                    .orElse("ROLE_USUARIO");

            JwtDto jwtDto = new JwtDto(
                    jwt,
                    userDetails.getIdUsuario(),
                    userDetails.getUsername(),
                    userDetails.getNombres(),
                    rol
            );

            return new ResponseEntity<>(new GenericResponse<>("success", 1, "¡Login exitoso!", jwtDto), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(new GenericResponse<>("error", 0, "Credenciales incorrectas o usuario inactivo", null), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<GenericResponse<UsuarioResponseDto>> registrarUsuario(
            @Valid @RequestBody NuevoUsuarioDto nuevoUsuario, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new GenericResponse<>("error", 0, "Campos inválidos", null), HttpStatus.BAD_REQUEST);
        }

        try {
            UsuarioResponseDto creado = usuarioService.registrarUsuario(nuevoUsuario);
            return new ResponseEntity<>(new GenericResponse<>("success", 1, "Usuario registrado exitosamente", creado), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new GenericResponse<>("error", 0, e.getMessage(), null), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new GenericResponse<>("error", 0, "Error: " + e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}