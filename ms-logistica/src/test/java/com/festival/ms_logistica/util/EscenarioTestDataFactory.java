package com.festival.ms_logistica.util;

import com.festival.ms_logistica.dto.EscenarioDTO;
import com.festival.ms_logistica.model.Escenario;

public class EscenarioTestDataFactory {

    public static EscenarioDTO escenarioDtoFalso() {
        EscenarioDTO dto = new EscenarioDTO();
        dto.setNombre("Escenario Principal");
        dto.setCapacidad(2000);
        dto.setEventoId(1L);
        dto.setEventoNombre("Festival Reggae 2025");
        return dto;
    }

    public static Escenario escenarioEntidadFalso() {
        Escenario escenario = new Escenario();
        escenario.setId(1L);
        escenario.setNombre("Escenario Principal");
        escenario.setCapacidad(2000);
        escenario.setEventoId(1L);
        escenario.setEventoNombre("Festival Reggae 2025");
        return escenario;
    }
}
