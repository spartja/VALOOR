package com.uniremington.valoor.controller.mvc;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniremington.valoor.controller.res.ProductoVOMapper;
import com.uniremington.valoor.model.dao.CategoriaDAO;
import com.uniremington.valoor.model.dao.DeporteDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;
import com.uniremington.valoor.model.entity.ProductoEntity;
import com.uniremington.valoor.model.vo.ProductoVO;

import jakarta.validation.Valid;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductoMvcController {

    private final ProductoDAO productoDAO;
    private final DeporteDAO deporteDAO;
    private final CategoriaDAO categoriaDAO;
    private final ProductoVOMapper mapper;

    public AdminProductoMvcController(
            ProductoDAO productoDAO,
            DeporteDAO deporteDAO,
            CategoriaDAO categoriaDAO,
            ProductoVOMapper mapper) {
        this.productoDAO = productoDAO;
        this.deporteDAO = deporteDAO;
        this.categoriaDAO = categoriaDAO;
        this.mapper = mapper;
    }

    @GetMapping("/admin/productos")
    public String productos(Model model) {
        cargarListas(model);
        ProductoVO form = new ProductoVO();
        form.setActivo(true);
        model.addAttribute("productoForm", form);
        model.addAttribute("modoEdicion", false);
        return "admin/productos";
    }

    @GetMapping("/admin/productos/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        ProductoEntity producto = obtenerProducto(id);
        cargarListas(model);
        model.addAttribute("productoForm", mapper.toVO(producto));
        model.addAttribute("modoEdicion", true);
        return "admin/productos";
    }

    @PostMapping("/admin/productos")
    public String guardar(
            @Valid @ModelAttribute("productoForm") ProductoVO form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            cargarListas(model);
            model.addAttribute("modoEdicion", form.getIdProducto() != null);
            return "admin/productos";
        }

        ProductoEntity producto = form.getIdProducto() == null
                ? new ProductoEntity()
                : obtenerProducto(form.getIdProducto());
        mapper.updateEntity(producto, form);
        producto.setDeporte(deporteDAO.findById(form.getIdDeporte())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Deporte no encontrado.")));
        producto.setCategoria(categoriaDAO.findById(form.getIdCategoria())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoría no encontrada.")));
        productoDAO.save(producto);
        redirectAttributes.addFlashAttribute("mensaje", "El producto quedó guardado correctamente.");
        return "redirect:/admin/productos";
    }

    @PostMapping("/admin/productos/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        ProductoEntity producto = obtenerProducto(id);
        producto.setActivo(!Boolean.TRUE.equals(producto.getActivo()));
        productoDAO.save(producto);
        redirectAttributes.addFlashAttribute("mensaje",
                producto.getActivo() ? "Producto publicado." : "Producto desactivado.");
        return "redirect:/admin/productos";
    }

    private ProductoEntity obtenerProducto(Integer id) {
        return productoDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
    }

    private void cargarListas(Model model) {
        model.addAttribute("productos", productoDAO.findAll());
        model.addAttribute("deportes", deporteDAO.findAll());
        model.addAttribute("categorias", categoriaDAO.findAll());
    }
}
