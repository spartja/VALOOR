package com.uniremington.valoor.model.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class InventarioVO {

    @Positive
    private Integer idInventario;

    @NotNull
    @Positive
    private Integer idProducto;

    @NotBlank
    @Size(max = 10)
    private String talla;

    @NotNull
    @PositiveOrZero
    private Integer stock;

    @NotNull
    @PositiveOrZero
    private Integer stockMinimo;

    public InventarioVO() {
    }

    public InventarioVO(Integer idInventario, Integer idProducto, String talla, Integer stock, Integer stockMinimo) {
        this.idInventario = idInventario;
        this.idProducto = idProducto;
        this.talla = talla;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
    }

    public Integer getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(Integer idInventario) {
        this.idInventario = idInventario;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
