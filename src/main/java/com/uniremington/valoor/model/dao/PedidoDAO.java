package com.uniremington.valoor.model.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uniremington.valoor.model.dao.projection.VentaPorDeporteProjection;
import com.uniremington.valoor.model.entity.PedidoEntity;
import com.uniremington.valoor.model.vo.EstadoPedido;

public interface PedidoDAO extends JpaRepository<PedidoEntity, Integer> {

    List<PedidoEntity> findByUsuario_IdUsuarioOrderByFechaDesc(Integer idUsuario);

    List<PedidoEntity> findByEstadoOrderByFechaDesc(EstadoPedido estado);

    @Query("""
            select coalesce(sum(p.total), 0)
            from PedidoEntity p
            where p.estado <> :estadoExcluido
              and (:desde is null or p.fecha >= :desde)
              and (:hasta is null or p.fecha < :hasta)
            """)
    BigDecimal calcularVentas(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("estadoExcluido") EstadoPedido estadoExcluido);

    @Query("""
            select pr.deporte.idDeporte as idDeporte,
                   pr.deporte.nombre as nombreDeporte,
                   sum(d.subtotal) as totalVendido
            from DetallePedidoEntity d
            join d.pedido pe
            join d.producto pr
            where pe.estado <> :estadoExcluido
              and (:desde is null or pe.fecha >= :desde)
              and (:hasta is null or pe.fecha < :hasta)
            group by pr.deporte.idDeporte, pr.deporte.nombre
            order by sum(d.subtotal) desc
            """)
    List<VentaPorDeporteProjection> buscarVentasPorDeporte(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("estadoExcluido") EstadoPedido estadoExcluido);
}
