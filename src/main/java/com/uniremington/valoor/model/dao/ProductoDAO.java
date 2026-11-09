package com.uniremington.valoor.model.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uniremington.valoor.model.dao.projection.ProductoMasVendidoProjection;
import com.uniremington.valoor.model.entity.ProductoEntity;
import com.uniremington.valoor.model.vo.EstadoPedido;

public interface ProductoDAO extends JpaRepository<ProductoEntity, Integer> {

    @Query("""
            select distinct p
            from ProductoEntity p
            where (:nombre is null or lower(p.nombre) like lower(concat('%', :nombre, '%')))
              and (:idDeporte is null or p.deporte.idDeporte = :idDeporte)
              and (:idCategoria is null or p.categoria.idCategoria = :idCategoria)
              and (:marca is null or lower(p.marca) = lower(:marca))
              and (:precioMinimo is null or p.precio >= :precioMinimo)
              and (:precioMaximo is null or p.precio <= :precioMaximo)
              and (:talla is null or exists (
                  select i.idInventario
                  from InventarioEntity i
                  where i.producto = p and lower(i.talla) = lower(:talla) and i.stock > 0
              ))
              and (:soloActivos = false or p.activo = true)
            """)
    Page<ProductoEntity> buscarCatalogo(
            @Param("nombre") String nombre,
            @Param("idDeporte") Integer idDeporte,
            @Param("idCategoria") Integer idCategoria,
            @Param("marca") String marca,
            @Param("talla") String talla,
            @Param("precioMinimo") BigDecimal precioMinimo,
            @Param("precioMaximo") BigDecimal precioMaximo,
            @Param("soloActivos") boolean soloActivos,
            Pageable pageable);

    @Query("""
            select p.idProducto as idProducto,
                   p.nombre as nombre,
                   sum(d.cantidad) as unidadesVendidas,
                   sum(d.subtotal) as totalVendido
            from DetallePedidoEntity d
            join d.pedido pe
            join d.producto p
            where pe.estado <> :estadoExcluido
              and (:desde is null or pe.fecha >= :desde)
              and (:hasta is null or pe.fecha < :hasta)
            group by p.idProducto, p.nombre
            order by sum(d.cantidad) desc
            """)
    List<ProductoMasVendidoProjection> buscarMasVendidos(
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("estadoExcluido") EstadoPedido estadoExcluido,
            Pageable pageable);
}
