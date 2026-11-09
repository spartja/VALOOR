package com.uniremington.valoor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uniremington.valoor.model.dao.UsuarioDAO;
import com.uniremington.valoor.model.entity.UsuarioEntity;
import com.uniremington.valoor.model.vo.RolUsuario;

@Configuration
public class AdminBootstrapConfiguration {

    @Bean
    CommandLineRunner bootstrapAdministrator(
            UsuarioDAO usuarioDAO,
            PasswordEncoder passwordEncoder,
            @Value("${valoor.admin.email:}") String email,
            @Value("${valoor.admin.password:}") String password,
            @Value("${valoor.admin.name:Administrador}") String name) {
        return args -> {
            if (email.isBlank() && password.isBlank()) {
                return;
            }
            if (email.isBlank() || password.isBlank() || password.length() < 12) {
                throw new IllegalStateException(
                        "Configure valoor.admin.email y una contraseña valoor.admin.password de al menos 12 caracteres.");
            }
            if (!usuarioDAO.existsByEmailIgnoreCase(email.trim())) {
                UsuarioEntity admin = new UsuarioEntity();
                admin.setNombre(name.trim());
                admin.setEmail(email.trim().toLowerCase());
                admin.setContrasena(passwordEncoder.encode(password));
                admin.setRol(RolUsuario.ADMIN);
                usuarioDAO.save(admin);
            }
        };
    }
}
