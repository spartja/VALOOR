package com.uniremington.valoor.model.dao.projection;

import java.math.BigDecimal;

public interface ProductoMasVendidoProjection {

    Integer getIdProducto();

    String getNombre();

    Long getUnidadesVendidas();

    BigDecimal getTotalVendido();
}
