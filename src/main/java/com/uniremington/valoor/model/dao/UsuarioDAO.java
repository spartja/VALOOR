package com.uniremington.valoor.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uniremington.valoor.model.entity.UsuarioEntity;

public interface UsuarioDAO extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}
