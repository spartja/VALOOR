package com.uniremington.valoor.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PedidoVO {

    @NotNull
    @Positive
    private Integer idPedido;

    @NotNull
    @Positive
    private Integer idUsuario;

    @NotNull
    private LocalDateTime fecha;

    @NotNull
    private EstadoPedido estado;

    @NotNull
    @Size(max = 30)
    private String metodoPago;

    @Size(max = 200)
    private String direccionEnvio;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal total;

    private List<DetallePedidoVO> detalles = new ArrayList<>();

    public PedidoVO() {
    }

    public PedidoVO(Integer idPedido, Integer idUsuario, LocalDateTime fecha, EstadoPedido estado,
            String metodoPago, String direccionEnvio, BigDecimal total) {
        this.idPedido = idPedido;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.estado = estado;
        this.metodoPago = metodoPago;
        this.direccionEnvio = direccionEnvio;
        this.total = total;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetallePedidoVO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoVO> detalles) {
        this.detalles = detalles;
    }
}
