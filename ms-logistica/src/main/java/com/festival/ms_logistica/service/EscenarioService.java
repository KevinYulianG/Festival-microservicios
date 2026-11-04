package com.festival.ms_logistica.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.festival.ms_logistica.client.EventoClient;
import com.festival.ms_logistica.client.NotificacioneClient;
import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.dto.EscenarioRequestDTO; // <-- NUEVO IMPORT
import com.festival.ms_logistica.dto.EventoDTO;
import com.festival.ms_logistica.dto.NotificacionDTO;
import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.mapper.EscenarioMapper;
import com.festival.ms_logistica.model.Escenario;
import com.festival.ms_logistica.repository.EscenarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EscenarioService {
    
    private final EscenarioRepository escenarioRepository;
    private final EscenarioMapper escenarioMapper;
    private final EventoClient eventoClient;
    private final NotificacioneClient notificacioneClient;

    // <-- NUEVO MÉTODO COMPATIBLE CON TU CONTROLLER V2
    public EscenarioDTO crearEscenario(EscenarioRequestDTO dto) {
        log.info("Creando escenario ligero V2 para el evento {}", dto.getEventoId());

        try {
            EventoDTO evento = eventoClient.obtenerEvento(dto.getEventoId());

            if (evento == null) {
                log.warn("El evento {} no existe", dto.getEventoId());
                throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no existe");
            }

            if (!evento.getEstado().equalsIgnoreCase("PUBLICADO")) {
                log.warn("El evento {} no esta publicado, estado actual: {}", dto.getEventoId(), evento.getEstado());
                throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no esta publicado");
            }

            // Mapeo directo y manual del DTO corto a la entidad
            Escenario escenario = new Escenario();
            escenario.setNombre(dto.getNombre());
            escenario.setCapacidad(dto.getCapacidad());
            escenario.setEventoId(dto.getEventoId());
            escenario.setEventoNombre(evento.getNombre());
            
            Escenario escenarioGuardado = escenarioRepository.save(escenario);

            log.info("Escenario creado con id {}", escenarioGuardado.getId());

            return escenarioMapper.toDTO(escenarioGuardado);
            
        } catch (feign.FeignException.NotFound e) {
            log.error("Evento {} no encontrado en ms-evento", dto.getEventoId());
            throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no existe");
        } catch (feign.FeignException e) {
            log.error("Error al comunicarse con ms-evento: {}", e.getMessage());
            throw new RuntimeException("No se pudo validar el evento en este momento");
        }
    }

    // Mantenemos el método original de EscenarioDTO para asegurar la retrocompatibilidad
    public EscenarioDTO crearEscenario(EscenarioDTO dto) {
        log.info("Creando escenario {}", dto.getEventoId());

        try {
            EventoDTO evento = eventoClient.obtenerEvento(dto.getEventoId());

            if (evento == null) {
                log.warn("El evento {} no existe", dto.getEventoId());
                throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no existe");
            }

            if (!evento.getEstado().equalsIgnoreCase("PUBLICADO")) {
                log.warn("El evento {} no esta publicado, estado actual: {}", dto.getEventoId(), evento.getEstado());
                throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no esta publicado");
            }

            Escenario escenario = escenarioMapper.toEntity(dto);
            escenario.setEventoNombre(evento.getNombre());
            Escenario escenarioGuardado = escenarioRepository.save(escenario);

            log.info("Escenario creado con id {}", escenarioGuardado.getId());

            return escenarioMapper.toDTO(escenarioGuardado);
            
        } catch (feign.FeignException.NotFound e) {
            log.error("Evento {} no encontrado en ms-evento", dto.getEventoId());
            throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no existe");
        } catch (feign.FeignException e) {
            log.error("Error al comunicarse con ms-evento: {}", e.getMessage());
            throw new RuntimeException("No se pudo validar el evento en este momento");
        }
    }

    public EscenarioDTO actualizEscenario(Long id, EscenarioDTO dto) {
        log.info("Actualizando escenario {}", id);

        Escenario escenario = escenarioRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la zona" + id));
        
        if (!escenario.getEventoId().equals(dto.getEventoId())) {
            EventoDTO evento = eventoClient.obtenerEvento(dto.getEventoId());
        
            if (!evento.getEstado().equalsIgnoreCase("PUBLICADO")) {
                throw new ResourceNotFoundException("El evento " + dto.getEventoId() + " no esta publicado");
            }
            
            escenario.setEventoId(dto.getEventoId());
        }

        escenario.setNombre(dto.getNombre());
        escenario.setCapacidad(dto.getCapacidad());

        Escenario escenarioActualizado = escenarioRepository.save(escenario);

        log.info("Zona {} actualizado", id);

        try {
            notificacioneClient.crearNotificacion(new NotificacionDTO(
                "CAMBIO_ESCENARIO",
                "Se actualizo el escenario " + escenarioActualizado.getNombre(),
                dto.getEventoId()
            ));
        } catch (feign.FeignException e) {
            log.warn("No se pudo conectar a ms-notificaciones: {}", e.getMessage());
            throw new RuntimeException("No se pudo enviar la notificacion en este momento");
        }

        return escenarioMapper.toDTO(escenarioActualizado);
    }

    public List<EscenarioDTO> listaDeEscenariosPorEvento(Long eventoId) {
        log.info("Listando los escenarios del evento {}", eventoId);
    
        List<Escenario> escenario = escenarioRepository.findByEventoId(eventoId);

        if (escenario.isEmpty()) {
            log.warn("No se encontraron escenarios para el evento {}", eventoId);
            throw new ResourceNotFoundException("No existen zonas para el evento" + eventoId);
        }

        return escenario.stream()
                .map(e -> escenarioMapper.toDTO(e))
                .toList();
    }

    public void eliminarEscenario(long id) {
        log.info("Eliminando escenario {}", id);
        
        escenarioRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("No se encontro el escenario {}", id);
                return new ResourceNotFoundException("no existe el escenario " + id);
            });
        
        escenarioRepository.deleteById(id);
    }
}