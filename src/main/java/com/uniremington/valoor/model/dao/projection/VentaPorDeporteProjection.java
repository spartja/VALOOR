package com.uniremington.valoor.model.dao.projection;

import java.math.BigDecimal;

public interface VentaPorDeporteProjection {

    Integer getIdDeporte();

    String getNombreDeporte();

    BigDecimal getTotalVendido();
}
