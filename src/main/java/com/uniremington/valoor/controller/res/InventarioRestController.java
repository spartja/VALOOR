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

import com.uniremington.valoor.model.dao.InventarioDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;
import com.uniremington.valoor.model.entity.InventarioEntity;
import com.uniremington.valoor.model.vo.InventarioVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventarios")
public class InventarioRestController {

    private final InventarioDAO inventarioDAO;
    private final ProductoDAO productoDAO;

    public InventarioRestController(InventarioDAO inventarioDAO, ProductoDAO productoDAO) {
        this.inventarioDAO = inventarioDAO;
        this.productoDAO = productoDAO;
    }

    @GetMapping
    public List<InventarioVO> listar() {
        return inventarioDAO.findAll().stream().map(this::toVO).toList();
    }

    @GetMapping("/bajo-stock")
    public List<InventarioVO> listarBajoStock() {
        return inventarioDAO.buscarBajoStock().stream().map(this::toVO).toList();
    }

    @GetMapping("/producto/{idProducto}")
    public List<InventarioVO> listarPorProducto(@PathVariable Integer idProducto) {
        if (!productoDAO.existsById(idProducto)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado.");
        }
        return inventarioDAO.findByProducto_IdProducto(idProducto).stream().map(this::toVO).toList();
    }

    @PostMapping
    public ResponseEntity<InventarioVO> crear(@Valid @RequestBody InventarioVO vo) {
        InventarioEntity inventario = new InventarioEntity();
        aplicarVO(inventario, vo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toVO(inventarioDAO.save(inventario)));
    }

    @PutMapping("/{id}")
    public InventarioVO actualizar(@PathVariable Integer id, @Valid @RequestBody InventarioVO vo) {
        InventarioEntity inventario = inventarioDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado."));
        aplicarVO(inventario, vo);
        return toVO(inventarioDAO.save(inventario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!inventarioDAO.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventario no encontrado.");
        }
        inventarioDAO.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private void aplicarVO(InventarioEntity entity, InventarioVO vo) {
        entity.setProducto(productoDAO.findById(vo.getIdProducto())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Producto no encontrado.")));
        entity.setTalla(vo.getTalla().trim());
        entity.setStock(vo.getStock());
        entity.setStockMinimo(vo.getStockMinimo());
    }

    private InventarioVO toVO(InventarioEntity entity) {
        return new InventarioVO(entity.getIdInventario(), entity.getProducto().getIdProducto(),
                entity.getTalla(), entity.getStock(), entity.getStockMinimo());
    }
}
