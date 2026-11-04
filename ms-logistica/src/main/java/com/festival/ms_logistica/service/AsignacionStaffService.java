package com.festival.ms_logistica.service;

import com.festival.ms_logistica.mapper.AsignacionStaffMapper;
import java.util.List;

import org.springframework.stereotype.Service;

import com.festival.ms_logistica.client.NotificacioneClient;
import com.festival.ms_logistica.client.UsuarioClient;
import com.festival.ms_logistica.dto.AsignacionStaffDTO;
import com.festival.ms_logistica.dto.AsignacionStaffRequestDTO; // <-- NUEVO IMPORT
import com.festival.ms_logistica.dto.NotificacionDTO;
import com.festival.ms_logistica.dto.UsuarioDTO;
import com.festival.ms_logistica.exception.ResourceNotFoundException;
import com.festival.ms_logistica.model.AsignacionStaff;
import com.festival.ms_logistica.model.Zona;
import com.festival.ms_logistica.repository.AsignacionStaffRepository;
import com.festival.ms_logistica.repository.ZonaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsignacionStaffService {

    private final AsignacionStaffMapper asignacionStaffMapper;
    private final AsignacionStaffRepository asignacionStaffRepository;
    private final UsuarioClient usuarioClient;
    private final ZonaRepository zonaRepository;
    private final NotificacioneClient notificacioneClient;

    public AsignacionStaffDTO asignarStaff(Long usuarioId, AsignacionStaffRequestDTO dto) { 
        log.info("Asignando labor del staff {}", dto);

        try {
            UsuarioDTO usuario = usuarioClient.obtenerUsuario(usuarioId);

            if (usuario == null) {
                log.warn("El usuario {} no existe", usuarioId);
                throw new ResourceNotFoundException("El usuario " + usuarioId + " no existe");
            }

            if (!usuario.getRolNombre().equalsIgnoreCase("STAFF")) {
                log.warn("El usuario {} no tiene rol STAFF, rol actual: {}", usuarioId, usuario.getRolNombre());
                throw new ResourceNotFoundException("El usuario no tiene rol staff " + usuarioId);
            }

            Zona zona = zonaRepository.findById(dto.getZonaId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe la zona " + dto.getZonaId()));

            boolean yaAsignado = asignacionStaffRepository.existsByUsuarioIdAndZona_Id(usuarioId, dto.getZonaId());

            if (yaAsignado) {
                log.warn("El staff {} ya está asignado a la zona {}", usuarioId, dto.getZonaId());
                throw new IllegalStateException("El staff ya está asignado a esta zona");
            }

            AsignacionStaff asignacion = new AsignacionStaff();
            asignacion.setUsuarioId(usuario.getId());
            asignacion.setUsuarioNombre(usuario.getNombre());
            asignacion.setLabor(dto.getLabor());
            asignacion.setZona(zona);

            AsignacionStaff guardada = asignacionStaffRepository.save(asignacion);

            log.info("Staff {} asignado a zona {} con éxito", usuarioId, dto.getZonaId());

            return asignacionStaffMapper.toDTO(guardada);

        } catch (feign.FeignException.NotFound e) {
            log.error("Usuario {} no encontrado en ms-usuario", usuarioId);
            throw new ResourceNotFoundException("El usuario " + usuarioId + " no existe");

        } catch (feign.FeignException e) {
            log.error("Error al comunicarse con ms-usuario: {}", e.getMessage());
            throw new RuntimeException("No se pudo validar el usuario en este momento");
        }
    }

    public AsignacionStaffDTO actualizarAsignacion(Long usuarioId, AsignacionStaffDTO dto) {
        log.info("Actualizando asignacion de usuario {}", usuarioId);

        AsignacionStaff staff = asignacionStaffRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario " + usuarioId));

        if (!staff.getUsuarioId().equals(dto.getUsuarioId())) {
            log.warn("El usuarioId almacenado {} no coincide con el usuarioId ingresado {}", staff.getUsuarioId(),
                    dto.getUsuarioId());
            throw new ResourceNotFoundException("No puedes cambiar el usuario de la asignacion");
        }

        Zona zona = zonaRepository.findById(dto.getZonaId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe la zona " + dto.getZonaId()));

        staff.setLabor(dto.getLabor());
        staff.setZona(zona);

        AsignacionStaff staffActualizado = asignacionStaffRepository.save(staff);

        log.info("Staff {} actualizado", usuarioId);

        try {
            notificacioneClient.crearNotificacion(new NotificacionDTO(
                    "ACTUALIZACION_STAFF",
                    "Se realizo un cambio en el staff: " + staffActualizado.getUsuarioNombre(),
                    dto.getUsuarioId()));
        } catch (feign.FeignException e) {
            log.warn("No se pudo enviar notificacion para staff {}: {}", usuarioId, e.getMessage());
            throw new ResourceNotFoundException("No se pudo enviar la notificacion en este momento");
        }

        return asignacionStaffMapper.toDTO(staffActualizado);
    }

    public List<AsignacionStaffDTO> ListaStaffPorZona(Long zonaId) {
        log.info("Listando al staff por zona {}", zonaId);

        List<AsignacionStaff> staffs = asignacionStaffRepository.findByZonaId(zonaId);

        if (staffs.isEmpty()) {
            log.warn("No se encontro ningun staff para la zona {}", zonaId);
            throw new ResourceNotFoundException("No existe staff para la zona" + zonaId);
        }
        return staffs.stream()
                .map(s -> asignacionStaffMapper.toDTO(s))
                .toList();
    }

    public List<AsignacionStaffDTO> listaStaffPorEvento(Long eventoId) {
        log.info("Listando al staff del evento {}", eventoId);

        List<AsignacionStaff> staffs = asignacionStaffRepository.findByZona_EventoId(eventoId);

        if (staffs.isEmpty()) {
            log.warn("No se encontro el staff para el evento {}", eventoId);
            throw new ResourceNotFoundException("No se encontro el staff para el evento" + eventoId);
        }
        return staffs.stream()
                .map(s -> asignacionStaffMapper.toDTO(s))
                .toList();
    }

    public void eliminarAsignacion(Long id) {
        log.warn("Eliminando la asignacion del staff {}", id);

        asignacionStaffRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontro la asignacion {}", id);
                    return new ResourceNotFoundException("No existe la zona" + id);
                });

        asignacionStaffRepository.deleteById(id);

    }

}