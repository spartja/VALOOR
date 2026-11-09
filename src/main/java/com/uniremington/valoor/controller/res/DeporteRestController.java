package com.uniremington.valoor.controller.res;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uniremington.valoor.model.dao.DeporteDAO;
import com.uniremington.valoor.model.entity.DeporteEntity;
import com.uniremington.valoor.model.vo.DeporteVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/deportes")
public class DeporteRestController {

    private final DeporteDAO deporteDAO;

    public DeporteRestController(DeporteDAO deporteDAO) {
        this.deporteDAO = deporteDAO;
    }

    @GetMapping
    public List<DeporteVO> listar() {
        return deporteDAO.findAll().stream().map(this::toVO).toList();
    }

    @GetMapping("/{id}")
    public DeporteVO buscarPorId(@PathVariable Integer id) {
        return toVO(obtener(id));
    }

    @PostMapping
    public ResponseEntity<DeporteVO> crear(@Valid @RequestBody DeporteVO vo) {
        DeporteEntity deporte = new DeporteEntity();
        deporte.setNombre(vo.getNombre().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(toVO(deporteDAO.save(deporte)));
    }

    @PutMapping("/{id}")
    public DeporteVO actualizar(@PathVariable Integer id, @Valid @RequestBody DeporteVO vo) {
        DeporteEntity deporte = obtener(id);
        deporte.setNombre(vo.getNombre().trim());
        return toVO(deporteDAO.save(deporte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!deporteDAO.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deporte no encontrado.");
        }
        deporteDAO.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private DeporteEntity obtener(Integer id) {
        return deporteDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deporte no encontrado."));
    }

    private DeporteVO toVO(DeporteEntity entity) {
        return new DeporteVO(entity.getIdDeporte(), entity.getNombre());
    }
}
