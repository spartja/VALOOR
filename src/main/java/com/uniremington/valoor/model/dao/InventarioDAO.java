package com.uniremington.valoor.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.uniremington.valoor.model.entity.InventarioEntity;

public interface InventarioDAO extends JpaRepository<InventarioEntity, Integer> {

    List<InventarioEntity> findByProducto_IdProducto(Integer idProducto);

    Optional<InventarioEntity> findByProducto_IdProductoAndTallaIgnoreCase(Integer idProducto, String talla);

    @Query("""
            select i
            from InventarioEntity i
            join fetch i.producto p
            where i.stock <= i.stockMinimo
            order by i.stock asc
            """)
    List<InventarioEntity> buscarBajoStock();

    @Modifying
    @Transactional
    @Query("""
            update InventarioEntity i
            set i.stock = i.stock - :cantidad
            where i.producto.idProducto = :idProducto
              and lower(i.talla) = lower(:talla)
              and i.stock >= :cantidad
            """)
    int descontarStockSiDisponible(
            @Param("idProducto") Integer idProducto,
            @Param("talla") String talla,
            @Param("cantidad") int cantidad);

    @Modifying
    @Transactional
    @Query("""
            update InventarioEntity i
            set i.stock = i.stock + :cantidad
            where i.producto.idProducto = :idProducto
              and lower(i.talla) = lower(:talla)
            """)
    int reintegrarStock(
            @Param("idProducto") Integer idProducto,
            @Param("talla") String talla,
            @Param("cantidad") int cantidad);
}
