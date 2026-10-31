package com.festival.ms_tickets.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TicketPedidoDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El ID de evento es obligatorio")
    private Long eventoId;

    @NotBlank(message = "El tipo de entrada es obligatorio")
    @Pattern(
        regexp = "GENERAL|VIP|BACKSTAGE",
        message = "El tipo debe ser GENERAL, VIP o BACKSTAGE"
    )
    private String tipoEntrada;

    @NotNull(message = "El precio pagado es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    private BigDecimal precioPagado;
}