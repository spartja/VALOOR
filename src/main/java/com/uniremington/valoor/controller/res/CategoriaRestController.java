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

import com.uniremington.valoor.model.dao.CategoriaDAO;
import com.uniremington.valoor.model.entity.CategoriaEntity;
import com.uniremington.valoor.model.vo.CategoriaVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaRestController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaRestController(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @GetMapping
    public List<CategoriaVO> listar() {
        return categoriaDAO.findAll().stream().map(this::toVO).toList();
    }

    @GetMapping("/{id}")
    public CategoriaVO buscarPorId(@PathVariable Integer id) {
        return toVO(obtener(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaVO> crear(@Valid @RequestBody CategoriaVO vo) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNombre(vo.getNombre().trim());
        return ResponseEntity.status(HttpStatus.CREATED).body(toVO(categoriaDAO.save(categoria)));
    }

    @PutMapping("/{id}")
    public CategoriaVO actualizar(@PathVariable Integer id, @Valid @RequestBody CategoriaVO vo) {
        CategoriaEntity categoria = obtener(id);
        categoria.setNombre(vo.getNombre().trim());
        return toVO(categoriaDAO.save(categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!categoriaDAO.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada.");
        }
        categoriaDAO.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private CategoriaEntity obtener(Integer id) {
        return categoriaDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada."));
    }

    private CategoriaVO toVO(CategoriaEntity entity) {
        return new CategoriaVO(entity.getIdCategoria(), entity.getNombre());
    }
}
