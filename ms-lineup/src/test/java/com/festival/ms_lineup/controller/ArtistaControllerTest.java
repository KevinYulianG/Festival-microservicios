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
import com.festival.ms_lineup.dto.ArtistaDTORespuesta;
import com.festival.ms_lineup.dto.ArtistaPedidoDTO;
import com.festival.ms_lineup.exception.ArtistaNoEncontrado;
import com.festival.ms_lineup.security.JwtFilter;
import com.festival.ms_lineup.security.JwtUtil;
import com.festival.ms_lineup.service.ArtistaService;
import com.festival.ms_lineup.util.LineupTestDataFactory;


@WebMvcTest(ArtistaController.class)
@AutoConfigureMockMvc(addFilters = false)
class ArtistaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private ArtistaService artistaService;

        // Se mockean para que el contexto de Spring pueda levantar la app,
        // pero no se ejecutan realmente gracias a addFilters = false
        @MockitoBean
        private JwtFilter jwtFilter;

        @MockitoBean
        private JwtUtil jwtUtil;

        // TESTS PARA POST /api/artistas — crearArtista

        @Test
        @DisplayName("POST /api/artistas debe retornar 201 al crear artista exitosamente")
        @WithMockUser(roles = "ADMIN")
        void crearArtista_debeRetornar201_cuandoCreacionExitosa() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaActivo();

                when(artistaService.crearArtista(any(ArtistaPedidoDTO.class))).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.nombre").value("Bad Bunny"))
                        .andExpect(jsonPath("$.estado").value("ACTIVO"));

                verify(artistaService, times(1)).crearArtista(any(ArtistaPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/artistas debe retornar el genero musical correcto")
        @WithMockUser(roles = "ADMIN")
        void crearArtista_debeRetornarGeneroMusicalCorrecto() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaActivo();

                when(artistaService.crearArtista(any(ArtistaPedidoDTO.class))).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.generoMusical").value("Reggaeton"))
                        .andExpect(jsonPath("$.paisOrigen").value("Puerto Rico"));

                verify(artistaService, times(1)).crearArtista(any(ArtistaPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/artistas debe retornar 400 cuando el nombre esta vacio")
        @WithMockUser(roles = "ADMIN")
        void crearArtista_debeRetornar400_cuandoNombreVacio() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                pedido.setNombre("");

                // When + Then
                mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isBadRequest());

                verify(artistaService, never()).crearArtista(any(ArtistaPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/artistas debe verificar que el servicio es invocado exactamente una vez")
        @WithMockUser(roles = "ADMIN")
        void crearArtista_debeVerificarLlamadaUnicaAlServicio() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaActivo();

                when(artistaService.crearArtista(any(ArtistaPedidoDTO.class))).thenReturn(respuesta);

                // When
                mockMvc.perform(post("/api/artistas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isCreated());

                // Then
                verify(artistaService, times(1)).crearArtista(any(ArtistaPedidoDTO.class));
                verifyNoMoreInteractions(artistaService);
        }

        // TESTS PARA GET /api/artistas/{id} — obtenerArtista

        @Test
        @DisplayName("GET /api/artistas/{id} debe retornar 200 y el artista cuando existe")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerArtista_debeRetornar200_cuandoArtistaExiste() throws Exception {
                // Given
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaActivo();
                when(artistaService.obtenerArtista(1L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(get("/api/artistas/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.nombre").value("Bad Bunny"))
                        .andExpect(jsonPath("$.estado").value("ACTIVO"));

                verify(artistaService, times(1)).obtenerArtista(1L);
        }

        @Test
        @DisplayName("GET /api/artistas/{id} debe retornar 404 cuando artista no existe")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerArtista_debeRetornar404_cuandoArtistaNoExiste() throws Exception {
                // Given
                when(artistaService.obtenerArtista(999L))
                        .thenThrow(new ArtistaNoEncontrado("Artista no encontrado con ID: 999"));

                // When + Then
                mockMvc.perform(get("/api/artistas/999"))
                        .andExpect(status().isNotFound());

                verify(artistaService, times(1)).obtenerArtista(999L);
        }

        @Test
        @DisplayName("GET /api/artistas/{id} debe retornar artista con estado INACTIVO")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerArtista_debeRetornarArtistaConEstadoInactivo() throws Exception {
                // Given
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaInactivo();
                when(artistaService.obtenerArtista(2L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(get("/api/artistas/2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("INACTIVO"));

                verify(artistaService, times(1)).obtenerArtista(2L);
        }

        @Test
        @DisplayName("GET /api/artistas/{id} debe retornar artista con estado CANCELADO")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerArtista_debeRetornarArtistaConEstadoCancelado() throws Exception {
                // Given
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaCancelado();
                when(artistaService.obtenerArtista(3L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(get("/api/artistas/3"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("CANCELADO"))
                        .andExpect(jsonPath("$.nombre").value("Coldplay"));

                verify(artistaService, times(1)).obtenerArtista(3L);
        }

        // TESTS PARA GET /api/artistas — listarArtistas

        @Test
        @DisplayName("GET /api/artistas debe retornar lista completa de artistas")
        @WithMockUser(roles = "ASISTENTE")
        void listarArtistas_debeRetornarListaCompleta() throws Exception {
                // Given
                ArtistaDTORespuesta a1 = LineupTestDataFactory.artistaRespuestaActivo();
                ArtistaDTORespuesta a2 = LineupTestDataFactory.artistaRespuestaInactivo();
                when(artistaService.listarArtistas()).thenReturn(List.of(a1, a2));

                // When + Then
                mockMvc.perform(get("/api/artistas"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].nombre").value("Bad Bunny"))
                        .andExpect(jsonPath("$[1].nombre").value("Artista Retirado"));

                verify(artistaService, times(1)).listarArtistas();
        }

        @Test
        @DisplayName("GET /api/artistas debe retornar lista vacia cuando no hay artistas")
        @WithMockUser(roles = "ASISTENTE")
        void listarArtistas_debeRetornarListaVacia_cuandoNoHayArtistas() throws Exception {
                // Given
                when(artistaService.listarArtistas()).thenReturn(List.of());

                // When + Then
                mockMvc.perform(get("/api/artistas"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));

                verify(artistaService, times(1)).listarArtistas();
        }

        @Test
        @DisplayName("GET /api/artistas debe retornar artistas con distintos generos musicales")
        @WithMockUser(roles = "ASISTENTE")
        void listarArtistas_debeRetornarArtistasConDistintosGeneros() throws Exception {
                // Given
                ArtistaDTORespuesta reggaeton = LineupTestDataFactory.artistaRespuestaActivo();
                ArtistaDTORespuesta rock = LineupTestDataFactory.artistaRespuestaCancelado();
                when(artistaService.listarArtistas()).thenReturn(List.of(reggaeton, rock));

                // When + Then
                mockMvc.perform(get("/api/artistas"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].generoMusical").value("Reggaeton"))
                        .andExpect(jsonPath("$[1].generoMusical").value("Rock Alternativo"));

                verify(artistaService, times(1)).listarArtistas();
        }

        @Test
        @DisplayName("GET /api/artistas debe verificar que el servicio se invoca sin parametros")
        @WithMockUser(roles = "ADMIN")
        void listarArtistas_debeVerificarLlamadaAlServicio() throws Exception {
                // Given
                when(artistaService.listarArtistas()).thenReturn(List.of());

                // When + Then
                mockMvc.perform(get("/api/artistas"))
                        .andExpect(status().isOk());

                verify(artistaService, times(1)).listarArtistas();
                verifyNoMoreInteractions(artistaService);
        }

        // TESTS PARA PUT /api/artistas/{id} — actualizarArtista

        @Test
        @DisplayName("PUT /api/artistas/{id} debe retornar 200 al actualizar artista exitosamente")
        @WithMockUser(roles = "ADMIN")
        void actualizarArtista_debeRetornar200_cuandoActualizacionExitosa() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaActivo();

                when(artistaService.actualizarArtista(eq(1L), any(ArtistaPedidoDTO.class)))
                        .thenReturn(respuesta);

                // When + Then
                mockMvc.perform(put("/api/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.nombre").value("Bad Bunny"));

                verify(artistaService, times(1)).actualizarArtista(eq(1L), any(ArtistaPedidoDTO.class));
        }

        @Test
        @DisplayName("PUT /api/artistas/{id} debe retornar 404 cuando artista no existe")
        @WithMockUser(roles = "ADMIN")
        void actualizarArtista_debeRetornar404_cuandoArtistaNoExiste() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();

                when(artistaService.actualizarArtista(eq(999L), any(ArtistaPedidoDTO.class)))
                        .thenThrow(new ArtistaNoEncontrado("Artista no encontrado con ID: 999"));

                // When + Then
                mockMvc.perform(put("/api/artistas/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isNotFound());

                verify(artistaService, times(1)).actualizarArtista(eq(999L), any(ArtistaPedidoDTO.class));
        }

        @Test
        @DisplayName("PUT /api/artistas/{id} debe retornar 400 cuando datos invalidos")
        @WithMockUser(roles = "ADMIN")
        void actualizarArtista_debeRetornar400_cuandoDatosInvalidos() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                pedido.setGeneroMusical("");

                // When + Then
                mockMvc.perform(put("/api/artistas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isBadRequest());

                verify(artistaService, never()).actualizarArtista(any(), any());
        }

        @Test
        @DisplayName("PUT /api/artistas/{id} debe verificar que el id usado es el correcto")
        @WithMockUser(roles = "ADMIN")
        void actualizarArtista_debeVerificarIdCorrecto() throws Exception {
                // Given
                ArtistaPedidoDTO pedido = LineupTestDataFactory.artistaPedidoFalso();
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaActivo();

                when(artistaService.actualizarArtista(eq(5L), any(ArtistaPedidoDTO.class)))
                        .thenReturn(respuesta);

                // When + Then
                mockMvc.perform(put("/api/artistas/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isOk());

                verify(artistaService, times(1)).actualizarArtista(eq(5L), any(ArtistaPedidoDTO.class));
                verify(artistaService, never()).actualizarArtista(eq(1L), any(ArtistaPedidoDTO.class));
        }

        // TESTS PARA DELETE /api/artistas/{id} — desactivarArtista

        @Test
        @DisplayName("DELETE /api/artistas/{id} debe retornar 200 y artista en estado INACTIVO")
        @WithMockUser(roles = "ADMIN")
        void desactivarArtista_debeRetornar200_cuandoDesactivacionExitosa() throws Exception {
                // Given
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaInactivo();
                when(artistaService.desactivarArtista(2L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(delete("/api/artistas/2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(2))
                        .andExpect(jsonPath("$.estado").value("INACTIVO"));

                verify(artistaService, times(1)).desactivarArtista(2L);
        }

        @Test
        @DisplayName("DELETE /api/artistas/{id} debe retornar 404 cuando artista no existe")
        @WithMockUser(roles = "ADMIN")
        void desactivarArtista_debeRetornar404_cuandoArtistaNoExiste() throws Exception {
                // Given
                when(artistaService.desactivarArtista(999L))
                        .thenThrow(new ArtistaNoEncontrado("Artista no encontrado con ID: 999"));

                // When + Then
                mockMvc.perform(delete("/api/artistas/999"))
                        .andExpect(status().isNotFound());

                verify(artistaService, times(1)).desactivarArtista(999L);
        }

        @Test
        @DisplayName("DELETE /api/artistas/{id} debe verificar que el servicio es llamado con el id correcto")
        @WithMockUser(roles = "ADMIN")
        void desactivarArtista_debeVerificarLlamadaConIdCorrecto() throws Exception {
                // Given
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaInactivo();
                when(artistaService.desactivarArtista(7L)).thenReturn(respuesta);

                // When + Then
                mockMvc.perform(delete("/api/artistas/7"))
                        .andExpect(status().isOk());

                verify(artistaService, times(1)).desactivarArtista(7L);
                verify(artistaService, never()).desactivarArtista(2L);
        }

        @Test
        @DisplayName("DELETE /api/artistas/{id} no debe afectar a otros artistas")
        @WithMockUser(roles = "ADMIN")
        void desactivarArtista_noDebeAfectarOtrosArtistas() throws Exception {
                // Given
                ArtistaDTORespuesta respuesta = LineupTestDataFactory.artistaRespuestaInactivo();
                when(artistaService.desactivarArtista(2L)).thenReturn(respuesta);

                // When
                mockMvc.perform(delete("/api/artistas/2"))
                        .andExpect(status().isOk());

                // Then
                verify(artistaService, times(1)).desactivarArtista(2L);
                verifyNoMoreInteractions(artistaService);
        }

}