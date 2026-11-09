package com.uniremington.valoor.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.valoor.model.entity.DeporteEntity;

public interface DeporteDAO extends JpaRepository<DeporteEntity, Integer> {

    Optional<DeporteEntity> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
