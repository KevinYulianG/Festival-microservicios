package com.festival.ms_logistica.controller;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.festival.ms_logistica.assembler.ZonaAssembler;
import com.festival.ms_logistica.security.JwtUtil;
import com.festival.ms_logistica.service.ZonaService;

@WebMvcTest(ZonaControllerV2.class)
class ZonaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ZonaService zonaService;

        @MockBean
        private ZonaAssembler assembler;

        @MockBean
        private JwtUtil jwtUtil;

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /api/v2/zonas/evento/{eventoId} debe retornar 200 y lista vacia")
        void listarZonasPorEventoListaVacia() throws Exception {

                Long eventoId = 99L;
                when(zonaService.listaZonasPorEvento(eventoId))
                                .thenReturn(List.of());

                mockMvc.perform(get("/api/v2/zonas/evento/{eventoId}", eventoId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded").doesNotExist());

                verify(zonaService).listaZonasPorEvento(eventoId);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("GET /api/v2/zonas/evento/{eventoId}/sinstaff debe retornar 200 y lista vacia")
        void listarZonasSinStaffListaVacia() throws Exception {

                Long eventoId = 99L;
                when(zonaService.listaZonasSinStaff(eventoId))
                                .thenReturn(List.of());

                mockMvc.perform(get("/api/v2/zonas/evento/{eventoId}/sinstaff", eventoId)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$._embedded").doesNotExist());

                verify(zonaService).listaZonasSinStaff(eventoId);
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("DELETE /api/v2/zonas/{id} debe retornar 204 al eliminar")
        void eliminarZonaDebeRetornarNoContent() throws Exception {

                Long id = 1L;
                doNothing().when(zonaService).eliminarZona(id);

                mockMvc.perform(delete("/api/v2/zonas/{id}", id)
                                .with(csrf()))
                                .andExpect(status().isNoContent());

                verify(zonaService).eliminarZona(id);
        }
}