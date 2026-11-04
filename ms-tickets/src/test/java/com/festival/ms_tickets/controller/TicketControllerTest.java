package com.festival.ms_tickets.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.festival.ms_tickets.dto.TicketPedidoDTO;
import com.festival.ms_tickets.dto.TicketRespuestaDTO;
import com.festival.ms_tickets.exception.AforoAgotadoException;
import com.festival.ms_tickets.exception.EventoNoDisponibleException;
import com.festival.ms_tickets.exception.TicketNoEncontradoException;
import com.festival.ms_tickets.exception.TicketYaUsadoException;
import com.festival.ms_tickets.exception.TicketDuplicadoException;
import com.festival.ms_tickets.model.TicketEstado;
import com.festival.ms_tickets.security.JwtFilter;
import com.festival.ms_tickets.security.JwtUtil;
import com.festival.ms_tickets.service.TicketService;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc(addFilters = false)
class TicketControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        // @MockBean correcto (no @MockitoBean)
        @MockBean
        private TicketService ticketService;

        @MockBean
        private JwtFilter jwtFilter;

        @MockBean
        private JwtUtil jwtUtil;

        private TicketRespuestaDTO crearTicketRespuesta(Long id, TicketEstado estado) {
                TicketRespuestaDTO dto = new TicketRespuestaDTO();
                dto.setId(id);
                dto.setUsuarioId(4L);
                dto.setEventoId(1L);
                dto.setTipoEntrada("GENERAL");
                dto.setCodigoQr("QR-TEST-" + id);
                dto.setEstado(estado);
                dto.setPrecioPagado(new BigDecimal("15000"));
                dto.setFechaCompra(LocalDateTime.now());
                dto.setFechaValidacion(null);
                return dto;
        }


        @Test
        @DisplayName("POST /api/tickets debe retornar 201 al comprar ticket exitosamente")
        @WithMockUser(roles = "ASISTENTE")
        void comprarTicket_debeRetornar201_cuandoCompraExitosa() throws Exception {
                TicketPedidoDTO pedido = new TicketPedidoDTO();
                pedido.setUsuarioId(4L);
                pedido.setEventoId(1L);
                pedido.setTipoEntrada("GENERAL");
                pedido.setPrecioPagado(new BigDecimal("15000"));

                TicketRespuestaDTO respuesta = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                when(ticketService.comprarTicket(any(TicketPedidoDTO.class))).thenReturn(respuesta);

                mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.tipoEntrada").value("GENERAL"))
                        .andExpect(jsonPath("$.estado").value("COMPRADO"))
                        .andExpect(jsonPath("$.codigoQr").value("QR-TEST-1"));

                verify(ticketService, times(1)).comprarTicket(any(TicketPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/tickets debe retornar error cuando evento no esta publicado")
        @WithMockUser(roles = "ASISTENTE")
        void comprarTicket_debeRetornarError_cuandoEventoNoPuplicado() throws Exception {
                TicketPedidoDTO pedido = new TicketPedidoDTO();
                pedido.setUsuarioId(4L);
                pedido.setEventoId(2L);
                pedido.setTipoEntrada("GENERAL");
                pedido.setPrecioPagado(new BigDecimal("15000"));

                when(ticketService.comprarTicket(any(TicketPedidoDTO.class)))
                        .thenThrow(new EventoNoDisponibleException("El evento no está disponible para venta"));

                mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isUnprocessableEntity());

                verify(ticketService, times(1)).comprarTicket(any(TicketPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/tickets debe retornar error cuando aforo esta agotado")
        @WithMockUser(roles = "ASISTENTE")
        void comprarTicket_debeRetornarError_cuandoAforoAgotado() throws Exception {
                TicketPedidoDTO pedido = new TicketPedidoDTO();
                pedido.setUsuarioId(4L);
                pedido.setEventoId(1L);
                pedido.setTipoEntrada("VIP");
                pedido.setPrecioPagado(new BigDecimal("35000"));

                when(ticketService.comprarTicket(any(TicketPedidoDTO.class)))
                        .thenThrow(new AforoAgotadoException("No hay entradas disponibles para este evento"));

                mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isConflict());

                verify(ticketService, times(1)).comprarTicket(any(TicketPedidoDTO.class));
        }

        @Test
        @DisplayName("POST /api/tickets debe retornar error cuando ticket es duplicado")
        @WithMockUser(roles = "ASISTENTE")
        void comprarTicket_debeRetornarError_cuandoTicketDuplicado() throws Exception {
                TicketPedidoDTO pedido = new TicketPedidoDTO();
                pedido.setUsuarioId(4L);
                pedido.setEventoId(1L);
                pedido.setTipoEntrada("GENERAL");
                pedido.setPrecioPagado(new BigDecimal("15000"));

                when(ticketService.comprarTicket(any(TicketPedidoDTO.class)))
                        .thenThrow(new TicketDuplicadoException("Ya tienes un ticket de tipo GENERAL para este evento"));

                mockMvc.perform(post("/api/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                        .andExpect(status().isConflict());

                verify(ticketService, times(1)).comprarTicket(any(TicketPedidoDTO.class));
        }

        // ─── GET /api/tickets/{id} ──────────────────────────────────────────────

        @Test
        @DisplayName("GET /api/tickets/{id} debe retornar 200 y el ticket cuando existe")
        @WithMockUser(roles = "ADMIN")
        void obtenerTicket_debeRetornar200_cuandoTicketExiste() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                when(ticketService.obtenerTicket(1L)).thenReturn(respuesta);

                mockMvc.perform(get("/api/tickets/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.estado").value("COMPRADO"))
                        .andExpect(jsonPath("$.usuarioId").value(4))
                        .andExpect(jsonPath("$.eventoId").value(1));

                verify(ticketService, times(1)).obtenerTicket(1L);
        }

        @Test
        @DisplayName("GET /api/tickets/{id} debe retornar 404 cuando ticket no existe")
        @WithMockUser(roles = "ADMIN")
        void obtenerTicket_debeRetornar404_cuandoTicketNoExiste() throws Exception {
                when(ticketService.obtenerTicket(999L))
                        .thenThrow(new TicketNoEncontradoException("Ticket no encontrado con ID: 999"));

                mockMvc.perform(get("/api/tickets/999"))
                        .andExpect(status().isNotFound());

                verify(ticketService, times(1)).obtenerTicket(999L);
        }

        @Test
        @DisplayName("GET /api/tickets/{id} debe retornar ticket con estado USADO")
        @WithMockUser(roles = "ADMIN")
        void obtenerTicket_debeRetornarTicketConEstadoUsado() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(2L, TicketEstado.USADO);
                respuesta.setFechaValidacion(LocalDateTime.now());
                when(ticketService.obtenerTicket(2L)).thenReturn(respuesta);

                mockMvc.perform(get("/api/tickets/2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("USADO"))
                        .andExpect(jsonPath("$.fechaValidacion").isNotEmpty());

                verify(ticketService, times(1)).obtenerTicket(2L);
        }

        @Test
        @DisplayName("GET /api/tickets/{id} debe retornar ticket con codigoQr generado")
        @WithMockUser(roles = "ADMIN")
        void obtenerTicket_debeRetornarTicketConCodigoQr() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(3L, TicketEstado.COMPRADO);
                when(ticketService.obtenerTicket(3L)).thenReturn(respuesta);

                mockMvc.perform(get("/api/tickets/3"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.codigoQr").value("QR-TEST-3"))
                        .andExpect(jsonPath("$.precioPagado").value(15000));

                verify(ticketService, times(1)).obtenerTicket(3L);
        }

        // ─── GET /api/tickets/usuario/{usuarioId} ───────────────────────────────

        @Test
        @DisplayName("GET /api/tickets/usuario/{usuarioId} debe retornar lista de tickets del usuario")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorUsuario_debeRetornarListaDeTickets() throws Exception {
                TicketRespuestaDTO t1 = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                TicketRespuestaDTO t2 = crearTicketRespuesta(2L, TicketEstado.USADO);
                when(ticketService.obtenerPorUsuario(4L)).thenReturn(List.of(t1, t2));

                mockMvc.perform(get("/api/tickets/usuario/4"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].estado").value("COMPRADO"))
                        .andExpect(jsonPath("$[1].estado").value("USADO"));

                verify(ticketService, times(1)).obtenerPorUsuario(4L);
        }

        @Test
        @DisplayName("GET /api/tickets/usuario/{usuarioId} debe retornar lista vacia si no tiene tickets")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorUsuario_debeRetornarListaVacia_cuandoUsuarioSinTickets() throws Exception {
                when(ticketService.obtenerPorUsuario(99L)).thenReturn(List.of());

                mockMvc.perform(get("/api/tickets/usuario/99"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));

                verify(ticketService, times(1)).obtenerPorUsuario(99L);
        }

        @Test
        @DisplayName("GET /api/tickets/usuario/{usuarioId} debe retornar tickets de distintos tipos")
        @WithMockUser(roles = "ASISTENTE")
        void obtenerPorUsuario_debeRetornarTicketsDistintosTipos() throws Exception {
                TicketRespuestaDTO general = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                general.setTipoEntrada("GENERAL");

                TicketRespuestaDTO vip = crearTicketRespuesta(2L, TicketEstado.COMPRADO);
                vip.setTipoEntrada("VIP");

                when(ticketService.obtenerPorUsuario(4L)).thenReturn(List.of(general, vip));

                mockMvc.perform(get("/api/tickets/usuario/4"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].tipoEntrada").value("GENERAL"))
                        .andExpect(jsonPath("$[1].tipoEntrada").value("VIP"));

                verify(ticketService, times(1)).obtenerPorUsuario(4L);
        }

        @Test
        @DisplayName("GET /api/tickets/usuario/{usuarioId} debe verificar que el servicio es llamado con el id correcto")
        @WithMockUser(roles = "ADMIN")
        void obtenerPorUsuario_debeVerificarLlamadaAlServicio() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                when(ticketService.obtenerPorUsuario(7L)).thenReturn(List.of(respuesta));

                mockMvc.perform(get("/api/tickets/usuario/7"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)));

                verify(ticketService, times(1)).obtenerPorUsuario(7L);
                verify(ticketService, never()).obtenerPorUsuario(4L);
        }

        // ─── GET /api/tickets/evento/{eventoId} ─────────────────────────────────

        @Test
        @DisplayName("GET /api/tickets/evento/{eventoId} debe retornar lista de tickets del evento")
        @WithMockUser(roles = "ADMIN")
        void obtenerPorEvento_debeRetornarListaDeTickets() throws Exception {
                TicketRespuestaDTO t1 = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                TicketRespuestaDTO t2 = crearTicketRespuesta(2L, TicketEstado.COMPRADO);
                TicketRespuestaDTO t3 = crearTicketRespuesta(3L, TicketEstado.USADO);
                when(ticketService.obtenerPorEvento(1L)).thenReturn(List.of(t1, t2, t3));

                mockMvc.perform(get("/api/tickets/evento/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(3)));

                verify(ticketService, times(1)).obtenerPorEvento(1L);
        }

        @Test
        @DisplayName("GET /api/tickets/evento/{eventoId} debe retornar lista vacia si el evento no tiene tickets")
        @WithMockUser(roles = "ADMIN")
        void obtenerPorEvento_debeRetornarListaVacia_cuandoEventoSinTickets() throws Exception {
                when(ticketService.obtenerPorEvento(99L)).thenReturn(List.of());

                mockMvc.perform(get("/api/tickets/evento/99"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));

                verify(ticketService, times(1)).obtenerPorEvento(99L);
        }

        @Test
        @DisplayName("GET /api/tickets/evento/{eventoId} debe retornar tickets con eventoId correcto")
        @WithMockUser(roles = "ADMIN")
        void obtenerPorEvento_debeRetornarTicketsConEventoIdCorrecto() throws Exception {
                TicketRespuestaDTO t1 = crearTicketRespuesta(1L, TicketEstado.COMPRADO);
                TicketRespuestaDTO t2 = crearTicketRespuesta(2L, TicketEstado.CANCELADO);
                when(ticketService.obtenerPorEvento(1L)).thenReturn(List.of(t1, t2));

                mockMvc.perform(get("/api/tickets/evento/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].eventoId").value(1))
                        .andExpect(jsonPath("$[1].eventoId").value(1))
                        .andExpect(jsonPath("$[1].estado").value("CANCELADO"));

                verify(ticketService, times(1)).obtenerPorEvento(1L);
        }

        @Test
        @DisplayName("GET /api/tickets/evento/{eventoId} debe verificar llamada al servicio con eventoId correcto")
        @WithMockUser(roles = "ADMIN")
        void obtenerPorEvento_debeVerificarLlamadaAlServicio() throws Exception {
                when(ticketService.obtenerPorEvento(6L)).thenReturn(List.of());

                mockMvc.perform(get("/api/tickets/evento/6"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));

                verify(ticketService, times(1)).obtenerPorEvento(6L);
                verify(ticketService, never()).obtenerPorEvento(1L);
        }

        // ─── PUT /api/tickets/validar/{codigoQR} ────────────────────────────────

        @Test
        @DisplayName("PUT /api/tickets/validar/{codigoQR} debe retornar 200 y ticket en estado USADO")
        @WithMockUser(roles = "STAFF")
        void validarEntrada_debeRetornar200_cuandoValidacionExitosa() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(1L, TicketEstado.USADO);
                respuesta.setFechaValidacion(LocalDateTime.now());
                when(ticketService.validarEntrada("QR-TEST-1")).thenReturn(respuesta);

                mockMvc.perform(put("/api/tickets/validar/QR-TEST-1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.estado").value("USADO"))
                        .andExpect(jsonPath("$.fechaValidacion").isNotEmpty());

                verify(ticketService, times(1)).validarEntrada("QR-TEST-1");
        }

        @Test
        @DisplayName("PUT /api/tickets/validar/{codigoQR} debe retornar error cuando QR ya fue usado")
        @WithMockUser(roles = "STAFF")
        void validarEntrada_debeRetornarError_cuandoQrYaUsado() throws Exception {
                when(ticketService.validarEntrada("QR-YA-USADO"))
                        .thenThrow(new TicketYaUsadoException("Este ticket ya fue utilizado"));

                mockMvc.perform(put("/api/tickets/validar/QR-YA-USADO"))
                        .andExpect(status().isConflict());

                verify(ticketService, times(1)).validarEntrada("QR-YA-USADO");
        }

        @Test
        @DisplayName("PUT /api/tickets/validar/{codigoQR} debe retornar 404 cuando QR no existe")
        @WithMockUser(roles = "STAFF")
        void validarEntrada_debeRetornar404_cuandoQrNoExiste() throws Exception {
                when(ticketService.validarEntrada("QR-INEXISTENTE"))
                        .thenThrow(new TicketNoEncontradoException("Código QR no válido o no existe"));

                mockMvc.perform(put("/api/tickets/validar/QR-INEXISTENTE"))
                        .andExpect(status().isNotFound());

                verify(ticketService, times(1)).validarEntrada("QR-INEXISTENTE");
        }

        @Test
        @DisplayName("PUT /api/tickets/validar/{codigoQR} debe retornar 404 cuando ticket esta cancelado")
        @WithMockUser(roles = "STAFF")
        void validarEntrada_debeRetornar404_cuandoTicketCancelado() throws Exception {
                when(ticketService.validarEntrada("QR-CANCELADO"))
                        .thenThrow(new TicketNoEncontradoException("Este ticket fue cancelado y no es válido"));

                mockMvc.perform(put("/api/tickets/validar/QR-CANCELADO"))
                        .andExpect(status().isNotFound());

                verify(ticketService, times(1)).validarEntrada("QR-CANCELADO");
        }

        // ─── PUT /api/tickets/cancelar/{id} ─────────────────────────────────────

        @Test
        @DisplayName("PUT /api/tickets/cancelar/{id} debe retornar 200 y ticket en estado CANCELADO")
        @WithMockUser(roles = "ADMIN")
        void cancelarTicket_debeRetornar200_cuandoCancelacionExitosa() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(2L, TicketEstado.CANCELADO);
                when(ticketService.cancelarTicket(2L)).thenReturn(respuesta);

                mockMvc.perform(put("/api/tickets/cancelar/2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(2))
                        .andExpect(jsonPath("$.estado").value("CANCELADO"));

                verify(ticketService, times(1)).cancelarTicket(2L);
        }

        @Test
        @DisplayName("PUT /api/tickets/cancelar/{id} debe retornar error cuando ticket ya fue usado")
        @WithMockUser(roles = "ADMIN")
        void cancelarTicket_debeRetornarError_cuandoTicketYaUsado() throws Exception {
                when(ticketService.cancelarTicket(1L))
                        .thenThrow(new TicketYaUsadoException(
                                "Solo se pueden cancelar tickets en estado COMPRADO. Estado actual: USADO"));

                mockMvc.perform(put("/api/tickets/cancelar/1"))
                        .andExpect(status().isConflict());

                verify(ticketService, times(1)).cancelarTicket(1L);
        }

        @Test
        @DisplayName("PUT /api/tickets/cancelar/{id} debe retornar 404 cuando ticket no existe")
        @WithMockUser(roles = "ADMIN")
        void cancelarTicket_debeRetornar404_cuandoTicketNoExiste() throws Exception {
                when(ticketService.cancelarTicket(999L))
                        .thenThrow(new TicketNoEncontradoException("Ticket no encontrado con ID: 999"));

                mockMvc.perform(put("/api/tickets/cancelar/999"))
                        .andExpect(status().isNotFound());

                verify(ticketService, times(1)).cancelarTicket(999L);
        }

        @Test
        @DisplayName("PUT /api/tickets/cancelar/{id} debe verificar que el servicio es llamado con el id correcto")
        @WithMockUser(roles = "ADMIN")
        void cancelarTicket_debeVerificarLlamadaAlServicio() throws Exception {
                TicketRespuestaDTO respuesta = crearTicketRespuesta(5L, TicketEstado.CANCELADO);
                when(ticketService.cancelarTicket(5L)).thenReturn(respuesta);

                mockMvc.perform(put("/api/tickets/cancelar/5"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(5));

                verify(ticketService, times(1)).cancelarTicket(5L);
                verify(ticketService, never()).cancelarTicket(1L);
        }
}