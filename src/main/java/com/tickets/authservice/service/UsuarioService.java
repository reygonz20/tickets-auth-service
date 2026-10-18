package com.tickets.authservice.service;

import com.tickets.authservice.dto.*;
import com.tickets.authservice.entity.Rol;
import com.tickets.authservice.entity.Usuario;
import com.tickets.authservice.repository.RolRepository;
import com.tickets.authservice.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UsuarioResponseDto> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::mapearADto).toList();
    }

    public UsuarioResponseDto obtenerPorId(Integer id) {
        return mapearADto(find(id));
    }

    @Transactional
    public UsuarioResponseDto registrarUsuario(NuevoUsuarioDto dto) {
        if (usuarioRepository.existsByCorreo(dto.getCorreo()))
            throw new IllegalArgumentException("El correo ya está registrado");
        Rol rol = rolValido(dto.getRolAsignado());
        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setNombres(dto.getNombres().trim());
        usuario.setApellidos(dto.getApellidos().trim());
        usuario.setTelefono(dto.getTelefono());
        usuario.setEstado(true);
        usuario.setRol(rol);
        return mapearADto(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDto actualizarUsuario(Integer id, ActualizarUsuarioDto dto, Integer idActor) {
        Usuario usuario = find(id);
        if (!usuario.getCorreo().equalsIgnoreCase(dto.getCorreo()) && usuarioRepository.existsByCorreo(dto.getCorreo()))
            throw new IllegalArgumentException("El correo ya está en uso por otro usuario");

        Rol rol = rolValido(dto.getRolAsignado());
        if (id.equals(idActor) && usuario.getRol() != null && !usuario.getRol().getNombre().equalsIgnoreCase(rol.getNombre()))
            throw new IllegalArgumentException("El administrador no puede cambiar su propio rol");
        if (id.equals(idActor) && Boolean.FALSE.equals(dto.getEstado()))
            throw new IllegalArgumentException("El administrador no puede desactivar su propia cuenta");

        usuario.setNombres(dto.getNombres().trim());
        usuario.setApellidos(dto.getApellidos().trim());
        usuario.setCorreo(dto.getCorreo().trim());
        usuario.setTelefono(dto.getTelefono());
        usuario.setRol(rol);
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getEstado() != null) usuario.setEstado(dto.getEstado());
        return mapearADto(usuarioRepository.save(usuario));
    }

    @Transactional
    public void desactivarUsuario(Integer id, Integer idActor) {
        if (id.equals(idActor))
            throw new IllegalArgumentException("El administrador no puede desactivar su propia cuenta");
        Usuario usuario = find(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public UsuarioResponseDto actualizarPerfil(Integer idUsuario, ActualizarPerfilDto dto) {
        Usuario usuario = find(idUsuario);
        if (!usuario.getCorreo().equalsIgnoreCase(dto.getCorreo()) && usuarioRepository.existsByCorreo(dto.getCorreo()))
            throw new IllegalArgumentException("El correo ya está en uso por otro usuario");
        usuario.setNombres(dto.getNombres().trim());
        usuario.setApellidos(dto.getApellidos().trim());
        usuario.setCorreo(dto.getCorreo().trim());
        usuario.setTelefono(dto.getTelefono());
        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        return mapearADto(usuarioRepository.save(usuario));
    }

    private Usuario find(Integer id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
    }

    private Rol rolValido(String nombre) {
        Rol rol = rolRepository.findByNombre(nombre.toUpperCase());
        if (rol == null)
            throw new IllegalArgumentException("Rol inválido. Los roles permitidos son ADMIN, TECNICO o USUARIO.");
        return rol;
    }

    private UsuarioResponseDto mapearADto(Usuario u) {
        return UsuarioResponseDto.builder().idUsuario(u.getIdUsuario()).nombres(u.getNombres()).apellidos(u.getApellidos())
                .correo(u.getCorreo()).telefono(u.getTelefono()).rol(u.getRol() != null ? u.getRol().getNombre() : null)
                .estado(u.isEstado()).fechaCreacion(u.getFechaCreacion()).build();
    }
}
