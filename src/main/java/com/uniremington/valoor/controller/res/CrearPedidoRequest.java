package com.uniremington.valoor.controller.res;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CrearPedidoRequest(
        @Positive Integer idUsuario,
        @NotBlank @Size(max = 30) String metodoPago,
        @Size(max = 200) String direccionEnvio,
        @NotEmpty List<@Valid ItemPedidoRequest> items) {

    public record ItemPedidoRequest(
            @NotNull @Positive Integer idProducto,
            @NotBlank @Size(max = 10) String talla,
            @NotNull @Positive Integer cantidad) {
    }
}
