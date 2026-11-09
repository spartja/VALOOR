package com.uniremington.valoor.controller.mvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uniremington.valoor.model.dao.UsuarioDAO;
import com.uniremington.valoor.model.entity.UsuarioEntity;
import com.uniremington.valoor.model.vo.RolUsuario;

import jakarta.validation.Valid;

@Controller
public class AccesoMvcController {

    private final UsuarioDAO usuarioDAO;
    private final PasswordEncoder passwordEncoder;

    public AccesoMvcController(UsuarioDAO usuarioDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("registroRequest", new RegistroRequest("", "", "", "", "", ""));
        return "registro";
    }

    @PostMapping("/registro")
    public String registrar(
            @Valid @ModelAttribute("registroRequest") RegistroRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (request.contrasena() == null || !request.contrasena().equals(request.confirmarContrasena())) {
            bindingResult.rejectValue("confirmarContrasena", "error.confirmarContrasena",
                    "Las contraseñas no coinciden.");
        }
        if (request.contrasena() != null
                && request.contrasena().getBytes(StandardCharsets.UTF_8).length > 72) {
            bindingResult.rejectValue("contrasena", "error.contrasena",
                    "La contraseña no puede superar 72 bytes en UTF-8.");
        }
        if (request.email() != null && usuarioDAO.existsByEmailIgnoreCase(request.email().trim())) {
            bindingResult.rejectValue("email", "error.email", "Ya existe una cuenta con ese correo.");
        }
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(request.nombre().trim());
        usuario.setEmail(request.email().trim().toLowerCase());
        usuario.setContrasena(passwordEncoder.encode(request.contrasena()));
        usuario.setTelefono(limpiar(request.telefono()));
        usuario.setDireccion(limpiar(request.direccion()));
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuarioDAO.save(usuario);

        redirectAttributes.addFlashAttribute("registroExitoso", true);
        return "redirect:/login";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }

    private String limpiar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
