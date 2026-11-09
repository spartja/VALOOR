package com.uniremington.valoor.controller.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.uniremington.valoor.model.dao.InventarioDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;
import com.uniremington.valoor.model.entity.ProductoEntity;

@Controller
public class ProductoDetalleMvcController {

    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;

    public ProductoDetalleMvcController(ProductoDAO productoDAO, InventarioDAO inventarioDAO) {
        this.productoDAO = productoDAO;
        this.inventarioDAO = inventarioDAO;
    }

    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable Integer id, Model model) {
        ProductoEntity producto = productoDAO.findById(id)
                .filter(item -> Boolean.TRUE.equals(item.getActivo()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
        model.addAttribute("producto", producto);
        model.addAttribute("tallas", inventarioDAO.findByProducto_IdProducto(id));
        return "producto-detalle";
    }
}
