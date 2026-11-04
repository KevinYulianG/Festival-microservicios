package com.festival.ms_notificaciones.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.festival.ms_notificaciones.dto.NotificacionDTO;
import com.festival.ms_notificaciones.dto.NotificacionRequestDTO;
import com.festival.ms_notificaciones.exception.ResourceNotFoundException;
import com.festival.ms_notificaciones.mapper.NotificacionMapper;
import com.festival.ms_notificaciones.model.Notificacion;
import com.festival.ms_notificaciones.repository.NotificacionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacionesService {
    
    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    public NotificacionDTO agregarNotificacion(NotificacionRequestDTO dto){
        log.info("Agregando notificacion {}", dto);

        // Si tu mapper no tiene toEntity(NotificacionRequestDTO), puedes mapearlo manualmente 
        // o añadirlo a tu interfaz de MapStruct.
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(dto.getTipo());
        notificacion.setMensaje(dto.getMensaje());
        notificacion.setUsuarioId(dto.getUsuarioId());
        notificacion.setFechaEnvio(LocalDateTime.now());
       
        Notificacion notificacionGuardada = notificacionRepository.save(notificacion);

        return notificacionMapper.toDTO(notificacionGuardada);
    }

    public List<NotificacionDTO> listaNotificacionesPorTipo(String tipo){
        log.info("Listando las notificaciones por tipo {}", tipo);

        List<Notificacion> notificacion = notificacionRepository.findByTipo(tipo);

        if (notificacion.isEmpty()) {
            log.warn("No se encontraron notificaciones del tipo {}", tipo);
            throw new ResourceNotFoundException("No existe el tipo de notificacion" + tipo);
        }
        return notificacion.stream()
            .map(notificacionMapper::toDTO)
            .toList(); 
    }
    
    public List<NotificacionDTO> listaNotificacionesPorUsuario(Long usuarioId){
        log.info("Listando las notificaciones del usuario {}", usuarioId);

        List<Notificacion> notificacion = notificacionRepository.findByUsuarioId(usuarioId);

        if (notificacion.isEmpty()) {
            log.warn("No se encontraron notificaciones del usuario {}", usuarioId);
            throw new ResourceNotFoundException("No existe el tipo de notificacion" + usuarioId);
        }
        return notificacion.stream()
            .map(notificacionMapper::toDTO)
            .toList(); 
    }

    public void eliminarNotificaciones(Long id){
        log.info("Eliminando la notificacion {}", id);

        notificacionRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("No se encontro la notificacion {}", id);
                return new ResourceNotFoundException("no existe la notificacion" + id);
            });
        
        notificacionRepository.deleteById(id);
    }
}