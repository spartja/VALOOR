package com.uniremington.valoor.controller.res;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.uniremington.valoor.model.dao.UsuarioDAO;
import com.uniremington.valoor.model.entity.UsuarioEntity;
import com.uniremington.valoor.model.vo.UsuarioVO;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioRestController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioRestController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public List<UsuarioVO> listar() {
        return usuarioDAO.findAll().stream().map(this::toVO).toList();
    }

    @GetMapping("/{id}")
    public UsuarioVO buscarPorId(@PathVariable Integer id) {
        return usuarioDAO.findById(id)
                .map(this::toVO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    @GetMapping("/buscar")
    public UsuarioVO buscarPorEmail(@RequestParam String email) {
        return usuarioDAO.findByEmailIgnoreCase(email.trim())
                .map(this::toVO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));
    }

    private UsuarioVO toVO(UsuarioEntity usuario) {
        return new UsuarioVO(usuario.getIdUsuario(), usuario.getNombre(), usuario.getEmail(), null,
                usuario.getTelefono(), usuario.getDireccion(), usuario.getRol(), usuario.getFechaRegistro());
    }
}
