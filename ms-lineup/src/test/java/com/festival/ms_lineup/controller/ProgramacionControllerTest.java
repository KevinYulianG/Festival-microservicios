package com.festival.ms_lineup.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.ms_lineup.dto.ProgramacionDTORespuesta;
import com.festival.ms_lineup.dto.ProgramacionPedidoDTO;
import com.festival.ms_lineup.exception.ArtistaNoEncontrado;
import com.festival.ms_lineup.exception.ConflictoHorario;
import com.festival.ms_lineup.exception.EventoNoDisponible;
import com.festival.ms_lineup.exception.ProgramacionNoEncontrada;
import com.festival.ms_lineup.security.JwtFilter;
import com.festival.ms_lineup.security.JwtUtil;
import com.festival.ms_lineup.service.ProgramacionService;
import com.festival.ms_lineup.util.LineupTestDataFactory;


@WebMvcTest(ProgramacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProgramacionControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ProgramacionService programacionService;

        // Se mockean para que el contexto de Spring pueda levantar la app,
        // pero no se ejecutan realmente gracias a addFilters = false
        @MockitoBean
        private JwtFilter jwtFilter;

        @MockitoBean
        private JwtUtil jwtUtil;

        // TESTS PARA POST /api/programaciones — programarArtista

        @Test
        @DisplayName("POST /api/programaciones debe retornar 201 al programar artista exitosamente")
        @WithMockUser(roles = "ADMIN")
        void programarArtista_debeRetornar201_cuandoProgramacionExitosa() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaProgramado();

                when(programacionService.programarArtista(any(ProgramacionPedidoDTO.class)))
                        .thenReturn(respuesta);

                // When + Then
                mockMvc.perform(post("/api/programaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.nombreArtista").value("Bad Bunny"))
                        .andExpect(jsonPath("$.estado").value("PROGRAMADO"));

                verify(programacionService, times(1)).programarArtista(any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/programaciones debe retornar error cuando el artista no existe")
        @WithMockUser(roles = "ADMIN")
        void programarArtista_debeRetornarError_cuandoArtistaNoExiste() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();

                when(programacionService.programarArtista(any(ProgramacionPedidoDTO.class)))
                        .thenThrow(new ArtistaNoEncontrado("Artista no encontrado"));

                // When + Then
                mockMvc.perform(post("/api/programaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isNotFound());

                verify(programacionService, times(1)).programarArtista(any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/programaciones debe retornar error cuando evento no esta publicado")
        @WithMockUser(roles = "ADMIN")
        void programarArtista_debeRetornarError_cuandoEventoNoPuplicado() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();

                when(programacionService.programarArtista(any(ProgramacionPedidoDTO.class)))
                        .thenThrow(new EventoNoDisponible("El evento no está disponible para programar artistas"));

                // When + Then
                mockMvc.perform(post("/api/programaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isUnprocessableEntity());

                verify(programacionService, times(1)).programarArtista(any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/programaciones debe retornar 409 cuando hay conflicto de horario")
        @WithMockUser(roles = "ADMIN")
        void programarArtista_debeRetornar409_cuandoConflictoHorario() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();

                when(programacionService.programarArtista(any(ProgramacionPedidoDTO.class)))
                        .thenThrow(new ConflictoHorario("Ya existe un artista programado en ese escenario"));

                // When + Then
                mockMvc.perform(post("/api/programaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isConflict());

                verify(programacionService, times(1)).programarArtista(any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/programaciones debe retornar 400 cuando faltan campos obligatorios")
        @WithMockUser(roles = "ADMIN")
        void programarArtista_debeRetornar400_cuandoCamposInvalidos() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();
                pedido.setNombreEscenario("");

                // When + Then
                mockMvc.perform(post("/api/programaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isBadRequest());

                verify(programacionService, never()).programarArtista(any(ProgramacionPedidoDTO.class));
        }

        // TESTS PARA GET /api/programaciones/{id} — obtenerProgramacion

        @Test
        @DisplayName("GET /api/programaciones/{id} debe retornar 200 y la programacion cuando existe")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerProgramacion_debeRetornar200_cuandoExiste() throws Exception {
                // Given
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaProgramado();
                when(programacionService.obtenerProgramacion(1L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(get("/api/programaciones/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.nombreEscenario").value("Escenario Principal"))
                        .andExpect(jsonPath("$.estado").value("PROGRAMADO"));

                verify(programacionService, times(1)).obtenerProgramacion(1L);
        }

        @Test
        @DisplayName("GET /api/programaciones/{id} debe retornar 404 cuando no existe")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerProgramacion_debeRetornar404_cuandoNoExiste() throws Exception {
                // Given
                when(programacionService.obtenerProgramacion(999L))
                        .thenThrow(new ProgramacionNoEncontrada("Programación no encontrada con ID: 999"));

                // When + Then
                mockMvc.perform(get("/api/programaciones/999"))
                        .andExpect(status().isNotFound());

                verify(programacionService, times(1)).obtenerProgramacion(999L);
        }

        @Test
        @DisplayName("GET /api/programaciones/{id} debe retornar programacion con estado FINALIZADO")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerProgramacion_debeRetornarConEstadoFinalizado() throws Exception {
                // Given
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaFinalizado();
                when(programacionService.obtenerProgramacion(2L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(get("/api/programaciones/2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("FINALIZADO"));

                verify(programacionService, times(1)).obtenerProgramacion(2L);
        }

        @Test
        @DisplayName("GET /api/programaciones/{id} debe retornar programacion con estado CANCELADO")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerProgramacion_debeRetornarConEstadoCancelado() throws Exception {
                // Given
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaCancelado();
                when(programacionService.obtenerProgramacion(3L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(get("/api/programaciones/3"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("CANCELADO"));

                verify(programacionService, times(1)).obtenerProgramacion(3L);
        }

        // TESTS PARA GET /api/programaciones/evento/{eventoId}

        @Test
        @DisplayName("GET /api/programaciones/evento/{eventoId} debe retornar lista del evento")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorEvento_debeRetornarListaDelEvento() throws Exception {
                // Given
                ProgramacionDTORespuesta p1 = LineupTestDataFactory.programacionRespuestaProgramado();
                ProgramacionDTORespuesta p2 = LineupTestDataFactory.programacionRespuestaFinalizado();
                when(programacionService.obtenerPorEvento(1L)).thenReturn(List.of(p1, p2));

                // When + Then
                mockMvc.perform(get("/api/programaciones/evento/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)));

                verify(programacionService, times(1)).obtenerPorEvento(1L);
        }

        @Test
        @DisplayName("GET /api/programaciones/evento/{eventoId} debe retornar lista vacia si evento sin programaciones")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorEvento_debeRetornarListaVacia() throws Exception {
                // Given
                when(programacionService.obtenerPorEvento(99L)).thenReturn(List.of());

                // When + Then
                mockMvc.perform(get("/api/programaciones/evento/99"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));

                verify(programacionService, times(1)).obtenerPorEvento(99L);
        }

        @Test
        @DisplayName("GET /api/programaciones/evento/{eventoId} debe retornar programaciones con eventoId correcto")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorEvento_debeRetornarConEventoIdCorrecto() throws Exception {
                // Given
                ProgramacionDTORespuesta p1 = LineupTestDataFactory.programacionRespuestaProgramado();
                when(programacionService.obtenerPorEvento(1L)).thenReturn(List.of(p1));

                // When + Then
                mockMvc.perform(get("/api/programaciones/evento/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].eventoId").value(1));

                verify(programacionService, times(1)).obtenerPorEvento(1L);
        }

        @Test
        @DisplayName("GET /api/programaciones/evento/{eventoId} debe verificar llamada con id correcto")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorEvento_debeVerificarLlamadaAlServicio() throws Exception {
                // Given
                when(programacionService.obtenerPorEvento(6L)).thenReturn(List.of());

                // When + Then
                mockMvc.perform(get("/api/programaciones/evento/6"))
                        .andExpect(status().isOk());

                verify(programacionService, times(1)).obtenerPorEvento(6L);
                verify(programacionService, never()).obtenerPorEvento(1L);
        }

        // TESTS PARA GET /api/programaciones/artista/{artistaId}

        @Test
        @DisplayName("GET /api/programaciones/artista/{artistaId} debe retornar lista del artista")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorArtista_debeRetornarListaDelArtista() throws Exception {
                // Given
                ProgramacionDTORespuesta p1 = LineupTestDataFactory.programacionRespuestaProgramado();
                ProgramacionDTORespuesta p2 = LineupTestDataFactory.programacionRespuestaCancelado();
                when(programacionService.obtenerPorArtista(1L)).thenReturn(List.of(p1, p2));

                // When + Then
                mockMvc.perform(get("/api/programaciones/artista/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)));

                verify(programacionService, times(1)).obtenerPorArtista(1L);
        }

        @Test
        @DisplayName("GET /api/programaciones/artista/{artistaId} debe retornar lista vacia si artista sin programaciones")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorArtista_debeRetornarListaVacia() throws Exception {
                // Given
                when(programacionService.obtenerPorArtista(99L)).thenReturn(List.of());

                // When + Then
                mockMvc.perform(get("/api/programaciones/artista/99"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));

                verify(programacionService, times(1)).obtenerPorArtista(99L);
        }

        @Test
        @DisplayName("GET /api/programaciones/artista/{artistaId} debe retornar programaciones con artistaId correcto")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorArtista_debeRetornarConArtistaIdCorrecto() throws Exception {
                // Given
                ProgramacionDTORespuesta p1 = LineupTestDataFactory.programacionRespuestaProgramado();
                when(programacionService.obtenerPorArtista(1L)).thenReturn(List.of(p1));

                // When + Then
                mockMvc.perform(get("/api/programaciones/artista/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].artistaId").value(1))
                        .andExpect(jsonPath("$[0].nombreArtista").value("Bad Bunny"));

                verify(programacionService, times(1)).obtenerPorArtista(1L);
        }

        @Test
        @DisplayName("GET /api/programaciones/artista/{artistaId} debe verificar llamada con id correcto")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorArtista_debeVerificarLlamadaAlServicio() throws Exception {
                // Given
                when(programacionService.obtenerPorArtista(4L)).thenReturn(List.of());

                // When + Then
                mockMvc.perform(get("/api/programaciones/artista/4"))
                        .andExpect(status().isOk());

                verify(programacionService, times(1)).obtenerPorArtista(4L);
                verify(programacionService, never()).obtenerPorArtista(1L);
        }

        // TESTS PARA PUT /api/programaciones/cancelar/{id}

        @Test
        @DisplayName("PUT /api/programaciones/cancelar/{id} debe retornar 200 y estado CANCELADO")
        @WithMockUser(roles = "ADMIN")
        void cancelarProgramacion_debeRetornar200_cuandoCancelacionExitosa() throws Exception {
                // Given
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaCancelado();
                when(programacionService.cancelarProgramacion(3L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(put("/api/programaciones/cancelar/3"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(3))
                        .andExpect(jsonPath("$.estado").value("CANCELADO"));

                verify(programacionService, times(1)).cancelarProgramacion(3L);
        }

        @Test
        @DisplayName("PUT /api/programaciones/cancelar/{id} debe retornar 404 cuando no existe")
        @WithMockUser(roles = "ADMIN")
        void cancelarProgramacion_debeRetornar404_cuandoNoExiste() throws Exception {
                // Given
                when(programacionService.cancelarProgramacion(999L))
                        .thenThrow(new ProgramacionNoEncontrada("Programación no encontrada con ID: 999"));

                // When + Then
                mockMvc.perform(put("/api/programaciones/cancelar/999"))
                        .andExpect(status().isNotFound());

                verify(programacionService, times(1)).cancelarProgramacion(999L);
        }

        @Test
        @DisplayName("PUT /api/programaciones/cancelar/{id} debe retornar 409 cuando ya esta finalizada")
        @WithMockUser(roles = "ADMIN")
        void cancelarProgramacion_debeRetornar409_cuandoYaFinalizada() throws Exception {
                // Given
                when(programacionService.cancelarProgramacion(2L))
                        .thenThrow(new ConflictoHorario("No se puede cancelar una programación ya finalizada"));

                // When + Then
                mockMvc.perform(put("/api/programaciones/cancelar/2"))
                        .andExpect(status().isConflict());

                verify(programacionService, times(1)).cancelarProgramacion(2L);
        }

        @Test
        @DisplayName("PUT /api/programaciones/cancelar/{id} debe verificar que el servicio es llamado con el id correcto")
        @WithMockUser(roles = "ADMIN")
        void cancelarProgramacion_debeVerificarLlamadaAlServicio() throws Exception {
                // Given
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaCancelado();
                when(programacionService.cancelarProgramacion(3L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(put("/api/programaciones/cancelar/3"))
                        .andExpect(status().isOk());

                verify(programacionService, times(1)).cancelarProgramacion(3L);
                verify(programacionService, never()).cancelarProgramacion(1L);
        }

        // TESTS PARA PUT /api/programaciones/horario/{id}

        @Test
        @DisplayName("PUT /api/programaciones/horario/{id} debe retornar 200 al actualizar horario exitosamente")
        @WithMockUser(roles = "ADMIN")
        void actualizarHorario_debeRetornar200_cuandoActualizacionExitosa() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();
                ProgramacionDTORespuesta respuesta = LineupTestDataFactory.programacionRespuestaProgramado();

                when(programacionService.actualizarHorario(eq(1L), any(ProgramacionPedidoDTO.class)))
                        .thenReturn(respuesta);

                // When + Then
                mockMvc.perform(put("/api/programaciones/horario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1));

                verify(programacionService, times(1))
                        .actualizarHorario(eq(1L), any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("PUT /api/programaciones/horario/{id} debe retornar 404 cuando no existe")
        @WithMockUser(roles = "ADMIN")
        void actualizarHorario_debeRetornar404_cuandoNoExiste() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();

                when(programacionService.actualizarHorario(eq(999L), any(ProgramacionPedidoDTO.class)))
                        .thenThrow(new ProgramacionNoEncontrada("Programación no encontrada con ID: 999"));

                // When + Then
                mockMvc.perform(put("/api/programaciones/horario/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isNotFound());

                verify(programacionService, times(1))
                        .actualizarHorario(eq(999L), any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("PUT /api/programaciones/horario/{id} debe retornar 409 cuando nuevo horario genera conflicto")
        @WithMockUser(roles = "ADMIN")
        void actualizarHorario_debeRetornar409_cuandoConflictoHorario() throws Exception {
                // Given
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoFalso();

                when(programacionService.actualizarHorario(eq(1L), any(ProgramacionPedidoDTO.class)))
                        .thenThrow(new ConflictoHorario("El nuevo horario genera conflicto con otra programación"));

                // When + Then
                mockMvc.perform(put("/api/programaciones/horario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isConflict());

                verify(programacionService, times(1))
                        .actualizarHorario(eq(1L), any(ProgramacionPedidoDTO.class));
        }

        @Test
        @DisplayName("PUT /api/programaciones/horario/{id} debe retornar 400 cuando hora fin es anterior a hora inicio")
        @WithMockUser(roles = "ADMIN")
        void actualizarHorario_debeRetornar400_cuandoHoraInvalida() throws Exception {
                // Given - el DTO con hora fin antes que hora inicio
                ProgramacionPedidoDTO pedido = LineupTestDataFactory.programacionPedidoHoraInvalida();

                when(programacionService.actualizarHorario(eq(1L), any(ProgramacionPedidoDTO.class)))
                        .thenThrow(new ConflictoHorario("La hora de fin debe ser posterior a la hora de inicio"));

                // When + Then
                mockMvc.perform(put("/api/programaciones/horario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isConflict());

                verify(programacionService, times(1))
                        .actualizarHorario(eq(1L), any(ProgramacionPedidoDTO.class));
        }
}