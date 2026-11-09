package com.uniremington.valoor.controller.res;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uniremington.valoor.model.dao.InventarioDAO;
import com.uniremington.valoor.model.dao.PedidoDAO;
import com.uniremington.valoor.model.dao.ProductoDAO;
import com.uniremington.valoor.model.dao.UsuarioDAO;
import com.uniremington.valoor.model.dao.projection.ProductoMasVendidoProjection;
import com.uniremington.valoor.model.dao.projection.VentaPorDeporteProjection;
import com.uniremington.valoor.model.entity.DetallePedidoEntity;
import com.uniremington.valoor.model.entity.PedidoEntity;
import com.uniremington.valoor.model.entity.ProductoEntity;
import com.uniremington.valoor.model.vo.DetallePedidoVO;
import com.uniremington.valoor.model.vo.EstadoPedido;
import com.uniremington.valoor.model.vo.PedidoVO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
@Transactional(readOnly = true)
public class PedidoRestController {

    private final PedidoDAO pedidoDAO;
    private final UsuarioDAO usuarioDAO;
    private final ProductoDAO productoDAO;
    private final InventarioDAO inventarioDAO;

    public PedidoRestController(
            PedidoDAO pedidoDAO,
            UsuarioDAO usuarioDAO,
            ProductoDAO productoDAO,
            InventarioDAO inventarioDAO) {
        this.pedidoDAO = pedidoDAO;
        this.usuarioDAO = usuarioDAO;
        this.productoDAO = productoDAO;
        this.inventarioDAO = inventarioDAO;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<PedidoVO> crear(
            @Valid @RequestBody CrearPedidoRequest request,
            Authentication authentication) {
        PedidoEntity pedido = new PedidoEntity();
        boolean administrador = esAdministrador(authentication);
        Integer idUsuario = administrador && request.idUsuario() != null
                ? request.idUsuario()
                : usuarioDAO.findByEmailIgnoreCase(authentication.getName())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Usuario autenticado no encontrado."))
                        .getIdUsuario();
        pedido.setUsuario(usuarioDAO.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado.")));
        pedido.setFecha(LocalDateTime.now());
        pedido.setMetodoPago(request.metodoPago().trim());
        pedido.setDireccionEnvio(request.direccionEnvio());
        pedido.setEstado(EstadoPedido.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;
        for (CrearPedidoRequest.ItemPedidoRequest item : request.items()) {
            ProductoEntity producto = productoDAO.findById(item.idProducto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado."));
            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El producto no está disponible.");
            }
            String talla = item.talla().trim();
            int filasActualizadas = inventarioDAO.descontarStockSiDisponible(
                    producto.getIdProducto(), talla, item.cantidad());
            if (filasActualizadas != 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "No hay stock suficiente para " + producto.getNombre() + " en talla " + talla + ".");
            }
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.cantidad()));
            DetallePedidoEntity detalle = new DetallePedidoEntity();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setTalla(talla);
            detalle.setCantidad(item.cantidad());
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(subtotal);
            pedido.getDetalles().add(detalle);
            total = total.add(subtotal);
        }

        pedido.setTotal(total);
        PedidoEntity guardado = pedidoDAO.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(toVO(guardado));
    }

    @GetMapping
    public List<PedidoVO> listar(
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) EstadoPedido estado,
            Authentication authentication) {
        if (!esAdministrador(authentication)) {
            Integer propietarioId = usuarioDAO.findByEmailIgnoreCase(authentication.getName())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Usuario autenticado no encontrado."))
                    .getIdUsuario();
            return pedidoDAO.findByUsuario_IdUsuarioOrderByFechaDesc(propietarioId)
                    .stream().map(this::toVO).toList();
        }
        if (idUsuario != null) {
            return pedidoDAO.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario).stream().map(this::toVO).toList();
        }
        if (estado != null) {
            return pedidoDAO.findByEstadoOrderByFechaDesc(estado).stream().map(this::toVO).toList();
        }
        return pedidoDAO.findAll(Sort.by(Sort.Direction.DESC, "fecha")).stream().map(this::toVO).toList();
    }

    @GetMapping("/{id}")
    public PedidoVO buscarPorId(@PathVariable Integer id, Authentication authentication) {
        PedidoEntity pedido = pedidoDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado."));
        if (!esAdministrador(authentication)
                && !pedido.getUsuario().getEmail().equalsIgnoreCase(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado.");
        }
        return toVO(pedido);
    }

    @PatchMapping("/{id}/estado")
    @Transactional
    public PedidoVO actualizarEstado(@PathVariable Integer id, @RequestBody ActualizarEstadoRequest request) {
        if (request == null || request.estado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }
        PedidoEntity pedido = pedidoDAO.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado."));
        EstadoPedido estadoActual = pedido.getEstado();
        if (!transicionPermitida(estadoActual, request.estado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se permite cambiar el pedido de " + estadoActual + " a " + request.estado() + ".");
        }
        if (request.estado() == EstadoPedido.CANCELADO) {
            pedido.getDetalles().forEach(detalle -> {
                int filasActualizadas = inventarioDAO.reintegrarStock(
                        detalle.getProducto().getIdProducto(), detalle.getTalla(), detalle.getCantidad());
                if (filasActualizadas != 1) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "No se pudo reintegrar el stock del producto " + detalle.getProducto().getIdProducto() + ".");
                }
            });
        }
        pedido.setEstado(request.estado());
        return toVO(pedidoDAO.save(pedido));
    }

    @GetMapping("/reportes/ventas")
    public BigDecimal ventas(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        validarRango(desde, hasta);
        return pedidoDAO.calcularVentas(inicio(desde), finExclusivo(hasta), EstadoPedido.CANCELADO);
    }

    @GetMapping("/reportes/ventas-por-deporte")
    public List<VentaPorDeporteProjection> ventasPorDeporte(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        validarRango(desde, hasta);
        return pedidoDAO.buscarVentasPorDeporte(inicio(desde), finExclusivo(hasta), EstadoPedido.CANCELADO);
    }

    @GetMapping("/reportes/productos-mas-vendidos")
    public List<ProductoMasVendidoProjection> productosMasVendidos(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(defaultValue = "10") int limite) {
        validarRango(desde, hasta);
        if (limite < 1 || limite > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El límite debe estar entre 1 y 100.");
        }
        return productoDAO.buscarMasVendidos(
                inicio(desde), finExclusivo(hasta), EstadoPedido.CANCELADO, PageRequest.of(0, limite));
    }

    private PedidoVO toVO(PedidoEntity pedido) {
        PedidoVO vo = new PedidoVO(pedido.getIdPedido(), pedido.getUsuario().getIdUsuario(),
                pedido.getFecha(), pedido.getEstado(), pedido.getMetodoPago(),
                pedido.getDireccionEnvio(), pedido.getTotal());
        List<DetallePedidoVO> detalles = pedido.getDetalles().stream()
                .map(detalle -> new DetallePedidoVO(detalle.getIdDetalle(), pedido.getIdPedido(),
                        detalle.getProducto().getIdProducto(), detalle.getTalla(), detalle.getCantidad(),
                        detalle.getPrecioUnitario(), detalle.getSubtotal()))
                .toList();
        vo.setDetalles(detalles);
        return vo;
    }

    private LocalDateTime inicio(LocalDate fecha) {
        return fecha == null ? null : fecha.atStartOfDay();
    }

    private LocalDateTime finExclusivo(LocalDate fecha) {
        return fecha == null ? null : fecha.plusDays(1).atStartOfDay();
    }

    private void validarRango(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha inicial supera la fecha final.");
        }
    }

    private boolean transicionPermitida(EstadoPedido actual, EstadoPedido nuevo) {
        return switch (actual) {
            case PENDIENTE -> nuevo == EstadoPedido.CONFIRMADO || nuevo == EstadoPedido.CANCELADO;
            case CONFIRMADO -> nuevo == EstadoPedido.ENVIADO || nuevo == EstadoPedido.CANCELADO;
            case ENVIADO -> nuevo == EstadoPedido.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }

    private boolean esAdministrador(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public record ActualizarEstadoRequest(EstadoPedido estado) {
    }
}
