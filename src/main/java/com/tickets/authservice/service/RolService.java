package com.tickets.authservice.service;

import com.tickets.authservice.dto.RolResponseDto;
import com.tickets.authservice.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<RolResponseDto> listarRoles() {
        return rolRepository.findAll().stream()
                .map(r -> RolResponseDto.builder()
                        .idRol(r.getIdRol())
                        .nombre(r.getNombre())
                        .descripcion(r.getDescripcion())
                        .estado(r.isEstado())
                        .build())
                .toList();
    }
}