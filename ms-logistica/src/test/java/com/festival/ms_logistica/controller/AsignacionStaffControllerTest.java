package com.festival.ms_logistica.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.ms_logistica.assembler.AsignacionStaffAssembler;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.dto.AsignacionStaffRequestDTO;
import com.festival.ms_logistica.controller.AsignacionStaffControllerV2;
import com.festival.ms_logistica.security.JwtUtil;
import com.festival.ms_logistica.service.AsignacionStaffService;

@WebMvcTest(AsignacionStaffControllerV2.class)
class AsignacionStaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AsignacionStaffService asignacionStaffService;

    @MockBean
    private AsignacionStaffAssembler assembler;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/v2/asignaciones/{usuarioId} debe retornar 201 al asignar staff")
    void asignarStaffDebeRetornarCreated() throws Exception {

        Long usuarioId = 1L;

        AsignacionStaffRequestDTO request = new AsignacionStaffRequestDTO();
        request.setUsuarioId(usuarioId);
        request.setZonaId(2L);
        request.setLabor("Guardia");

        AsignacionStaffDTO respuesta = new AsignacionStaffDTO();
        respuesta.setId(1L);
        respuesta.setUsuarioId(usuarioId);
        respuesta.setZonaId(2L);
        respuesta.setLabor("Guardia");

        when(asignacionStaffService.asignarStaff(eq(usuarioId), any(AsignacionStaffRequestDTO.class)))
            .thenReturn(respuesta);
        when(assembler.toModel(respuesta))
            .thenReturn(org.springframework.hateoas.EntityModel.of(respuesta));

        mockMvc.perform(post("/api/v2/asignaciones/{usuarioId}", usuarioId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(1))
                .andExpect(jsonPath("$.labor").value("Guardia"));

        verify(asignacionStaffService).asignarStaff(eq(usuarioId), any(AsignacionStaffRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/v2/asignaciones/{id} debe retornar 200 al actualizar staff")
    void actualizarAsignacionDebeRetornarOk() throws Exception {

        Long id = 1L;

        AsignacionStaffDTO dto = new AsignacionStaffDTO();
        dto.setId(id);
        dto.setUsuarioId(1L);
        dto.setZonaId(3L);
        dto.setLabor("Control acceso");

        when(asignacionStaffService.actualizarAsignacion(eq(id), any(AsignacionStaffDTO.class)))
            .thenReturn(dto);
        when(assembler.toModel(dto))
            .thenReturn(org.springframework.hateoas.EntityModel.of(dto));

        mockMvc.perform(put("/api/v2/asignaciones/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labor").value("Control acceso"));

        verify(asignacionStaffService).actualizarAsignacion(eq(id), any(AsignacionStaffDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /api/v2/asignaciones/staffZona/{zonaId} debe retornar lista")
    void listarStaffPorZonaDebeRetornarOk() throws Exception {

        long zonaId = 1L;

        AsignacionStaffDTO dto = new AsignacionStaffDTO();
        dto.setId(1L);
        dto.setZonaId(zonaId);
        dto.setLabor("Seguridad");

        when(asignacionStaffService.ListaStaffPorZona(zonaId)).thenReturn(List.of(dto));
        when(assembler.toModel(dto))
            .thenReturn(org.springframework.hateoas.EntityModel.of(dto));

        mockMvc.perform(get("/api/v2/asignaciones/staffZona/{zonaId}", zonaId)
                .with(csrf()))
                .andExpect(status().isOk());

        verify(asignacionStaffService).ListaStaffPorZona(zonaId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/v2/asignaciones/{id} debe retornar 204")
    void eliminarAsignacionDebeRetornarNoContent() throws Exception {

        Long id = 1L;

        mockMvc.perform(delete("/api/v2/asignaciones/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(asignacionStaffService).eliminarAsignacion(id);
    }
}