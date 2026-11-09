package com.uniremington.valoor.controller.res;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uniremington.valoor.model.dao.DetallePedidoDAO;
import com.uniremington.valoor.model.dao.PedidoDAO;
import com.uniremington.valoor.model.entity.DetallePedidoEntity;
import com.uniremington.valoor.model.vo.DetallePedidoVO;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoRestController {

    private final DetallePedidoDAO detallePedidoDAO;
    private final PedidoDAO pedidoDAO;

    public DetallePedidoRestController(DetallePedidoDAO detallePedidoDAO, PedidoDAO pedidoDAO) {
        this.detallePedidoDAO = detallePedidoDAO;
        this.pedidoDAO = pedidoDAO;
    }

    @GetMapping("/pedido/{idPedido}")
    public List<DetallePedidoVO> listarPorPedido(
            @PathVariable Integer idPedido,
            Authentication authentication) {
        var pedido = pedidoDAO.findById(idPedido)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado."));
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (!administrador && !pedido.getUsuario().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado.");
        }
        return detallePedidoDAO.findByPedido_IdPedido(idPedido).stream().map(this::toVO).toList();
    }

    private DetallePedidoVO toVO(DetallePedidoEntity detalle) {
        return new DetallePedidoVO(detalle.getIdDetalle(), detalle.getPedido().getIdPedido(),
                detalle.getProducto().getIdProducto(), detalle.getTalla(), detalle.getCantidad(),
                detalle.getPrecioUnitario(), detalle.getSubtotal());
    }
}
