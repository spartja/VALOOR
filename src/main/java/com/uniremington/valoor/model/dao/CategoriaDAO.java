package com.uniremington.valoor.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.valoor.model.entity.CategoriaEntity;

public interface CategoriaDAO extends JpaRepository<CategoriaEntity, Integer> {

    Optional<CategoriaEntity> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
}
