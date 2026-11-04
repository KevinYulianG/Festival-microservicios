package com.festival.ms_logistica.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.ms_logistica.assembler.EscenarioAssembler;
import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.dto.EscenarioRequestDTO;
import com.festival.ms_logistica.security.JwtUtil;
import com.festival.ms_logistica.service.EscenarioService;

@WebMvcTest(EscenarioControllerV2.class)
class EscenarioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private EscenarioService escenarioService;

        @MockBean
        private EscenarioAssembler assembler;

        @MockBean
        private JwtUtil jwtUtil;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /api/v2/escenarios debe retornar 201 al crear escenario")
        void crearEscenarioDebeRetornarCreated() throws Exception {

                EscenarioRequestDTO requestDto = new EscenarioRequestDTO("Escenario Principal", 2000, 1L);

                EscenarioDTO guardado = new EscenarioDTO();
                guardado.setId(1L);
                guardado.setNombre("Escenario Principal");
                guardado.setCapacidad(2000);
                guardado.setEventoId(1L);
                guardado.setEventoNombre("Festival Reggae 2025");

                when(escenarioService.crearEscenario(any(EscenarioRequestDTO.class)))
                                .thenReturn(guardado);

                when(assembler.toModel(any(EscenarioDTO.class)))
                                .thenReturn(EntityModel.of(guardado));

                mockMvc.perform(post("/api/v2/escenarios")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.nombre").value("Escenario Principal"))
                                .andExpect(jsonPath("$.capacidad").value(2000))
                                .andExpect(jsonPath("$.eventoId").value(1L))
                                .andExpect(jsonPath("$.eventoNombre").value("Festival Reggae 2025"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("PUT /api/v2/escenarios/{id} debe retornar 200 al actualizar escenario")
        void actualizarEscenarioDebeRetornarOk() throws Exception {

                EscenarioDTO dto = new EscenarioDTO();
                dto.setNombre("Escenario Principal");
                dto.setCapacidad(2000);
                dto.setEventoId(1L);

                EscenarioDTO actualizado = new EscenarioDTO();
                actualizado.setId(1L);
                actualizado.setNombre("Escenario Principal");
                actualizado.setCapacidad(2000);
                actualizado.setEventoId(1L);

                when(escenarioService.actualizEscenario(eq(1L), any(EscenarioDTO.class)))
                                .thenReturn(actualizado);

                when(assembler.toModel(any(EscenarioDTO.class)))
                                .thenReturn(EntityModel.of(actualizado));

                mockMvc.perform(put("/api/v2/escenarios/{id}", 1L)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.nombre").value("Escenario Principal"))
                                .andExpect(jsonPath("$.capacidad").value(2000))
                                .andExpect(jsonPath("$.eventoId").value(1L));
        }
}