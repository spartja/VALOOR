package com.uniremington.valoor.controller.mvc;

import java.math.BigDecimal;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.uniremington.valoor.controller.res.ProductoVOMapper;
import com.uniremington.valoor.model.dao.CategoriaDAO;
import com.uniremington.valoor.model.dao.DeporteDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;

@Controller
public class CatalogoMvcController {

    private final ProductoDAO productoDAO;
    private final ProductoVOMapper mapper;
    private final DeporteDAO deporteDAO;
    private final CategoriaDAO categoriaDAO;

    public CatalogoMvcController(
            ProductoDAO productoDAO,
            ProductoVOMapper mapper,
            DeporteDAO deporteDAO,
            CategoriaDAO categoriaDAO) {
        this.productoDAO = productoDAO;
        this.mapper = mapper;
        this.deporteDAO = deporteDAO;
        this.categoriaDAO = categoriaDAO;
    }

    @GetMapping({ "/", "/catalogo" })
    public String catalogo(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer idDeporte,
            @RequestParam(required = false) Integer idCategoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String talla,
            @RequestParam(required = false) BigDecimal precioMinimo,
            @RequestParam(required = false) BigDecimal precioMaximo,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        if (pagina < 0 || (precioMinimo != null && precioMaximo != null
                && precioMinimo.compareTo(precioMaximo) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los filtros del catálogo no son válidos.");
        }

        model.addAttribute("productos", productoDAO.buscarCatalogo(
                normalizar(nombre), idDeporte, idCategoria, normalizar(marca), normalizar(talla),
                precioMinimo, precioMaximo, true,
                PageRequest.of(pagina, 12, Sort.by("nombre").ascending()))
                .map(mapper::toVO));
        model.addAttribute("nombre", nombre);
        model.addAttribute("marca", marca);
        model.addAttribute("talla", talla);
        model.addAttribute("precioMinimo", precioMinimo);
        model.addAttribute("precioMaximo", precioMaximo);
        model.addAttribute("idDeporte", idDeporte);
        model.addAttribute("idCategoria", idCategoria);
        model.addAttribute("deportes", deporteDAO.findAll());
        model.addAttribute("categorias", categoriaDAO.findAll());
        return "catalogo";
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
