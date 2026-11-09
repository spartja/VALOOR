package com.uniremington.valoor.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.valoor.model.entity.DetallePedidoEntity;

public interface DetallePedidoDAO extends JpaRepository<DetallePedidoEntity, Integer> {

    List<DetallePedidoEntity> findByPedido_IdPedido(Integer idPedido);
}
