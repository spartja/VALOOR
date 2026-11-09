package com.uniremington.valoor.controller.res;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uniremington.valoor.model.dao.CategoriaDAO;
import com.uniremington.valoor.model.dao.DeporteDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;
import com.uniremington.valoor.model.entity.ProductoEntity;
import com.uniremington.valoor.model.vo.ProductoVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoDAO productoDAO;
    private final DeporteDAO deporteDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProductoVOMapper mapper;

    public ProductoRestController(
            ProductoDAO productoDAO,
            DeporteDAO deporteDAO,
            CategoriaDAO categoriaDAO,
            ProductoVOMapper mapper) {
        this.productoDAO = productoDAO;
        this.deporteDAO = deporteDAO;
        this.categoriaDAO = categoriaDAO;
        this.mapper = mapper;
    }

    @GetMapping
    public Page<ProductoVO> buscarCatalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idDeporte,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) BigDecimal precioMinimo,
            @RequestParam(required = false) BigDecimal precioMaximo,
            @RequestParam(defaultValue = "true") boolean soloActivos,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "12") int tamano,
            Authentication authentication) {
        if (pagina < 0 || tamano < 1 || tamano > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Paginación inválida.");
        }
        if (precioMinimo != null && precioMaximo != null && precioMinimo.compareTo(precioMaximo) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El precio mínimo no puede superar el precio máximo.");
        }
        return productoDAO.buscarCatalogo(
                normalizar(nombre), idDeporte, idCategoria, normalizar(marca), normalizar(talla),
                precioMinimo, precioMaximo, soloActivos || !esAdministrador(authentication),
                PageRequest.of(pagina, tamano, Sort.by("nombre").ascending()))
                .map(mapper::toVO);
    }

    @GetMapping("/{id}")
    public ProductoVO buscarPorId(@PathVariable Integer id, Authentication authentication) {
        ProductoEntity producto = productoDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
        if (!Boolean.TRUE.equals(producto.getActivo()) && !esAdministrador(authentication)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado.");
        }
        return mapper.toVO(producto);
    }

    @PostMapping
    public ResponseEntity<ProductoVO> crear(@Valid @RequestBody ProductoVO vo) {
        ProductoEntity producto = new ProductoEntity();
        mapper.updateEntity(producto, vo);
        producto.setDeporte(deporteDAO.findById(vo.getIdDeporte())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deporte no encontrado.")));
        producto.setCategoria(categoriaDAO.findById(vo.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada.")));
        ProductoEntity guardado = productoDAO.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toVO(guardado));
    }

    @PutMapping("/{id}")
    public ProductoVO actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoVO vo) {
        ProductoEntity producto = productoDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
        mapper.updateEntity(producto, vo);
        producto.setDeporte(deporteDAO.findById(vo.getIdDeporte())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deporte no encontrado.")));
        producto.setCategoria(categoriaDAO.findById(vo.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada.")));
        return mapper.toVO(productoDAO.save(producto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!productoDAO.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado.");
        }
        productoDAO.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
