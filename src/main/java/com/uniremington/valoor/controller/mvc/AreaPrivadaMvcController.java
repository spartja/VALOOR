package com.uniremington.valoor.controller.mvc;

import java.security.Principal;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.uniremington.valoor.model.dao.InventarioDAO;
import com.uniremington.valoor.model.dao.PedidoDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;
import com.uniremington.valoor.model.dao.UsuarioDAO;

@Controller
public class AreaPrivadaMvcController {

    private final ProductoDAO productoDAO;
    private final UsuarioDAO usuarioDAO;
    private final InventarioDAO inventarioDAO;
    private final PedidoDAO pedidoDAO;

    public AreaPrivadaMvcController(
            ProductoDAO productoDAO,
            UsuarioDAO usuarioDAO,
            InventarioDAO inventarioDAO,
            PedidoDAO pedidoDAO) {
        this.productoDAO = productoDAO;
        this.usuarioDAO = usuarioDAO;
        this.inventarioDAO = inventarioDAO;
        this.pedidoDAO = pedidoDAO;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String administrador(Model model) {
        model.addAttribute("totalProductos", productoDAO.count());
        model.addAttribute("totalUsuarios", usuarioDAO.count());
        model.addAttribute("productosBajoStock", inventarioDAO.buscarBajoStock());
        model.addAttribute("pedidosRecientes", pedidoDAO.findAll(
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "fecha"))
                .stream().limit(8).toList());
        return "admin/dashboard";
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public String misPedidos(Principal principal, Model model) {
        var usuario = usuarioDAO.findByEmailIgnoreCase(principal.getName())
                .orElseThrow(() -> new IllegalStateException("El usuario autenticado ya no existe."));
        model.addAttribute("pedidos", pedidoDAO.findByUsuario_IdUsuarioOrderByFechaDesc(usuario.getIdUsuario()));
        return "cliente/pedidos";
    }
}
