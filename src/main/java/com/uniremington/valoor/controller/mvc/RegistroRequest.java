package com.uniremington.valoor.controller.mvc;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank @Size(max = 100) String nombre,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(min = 12, max = 72) String contrasena,
        @NotBlank @Size(max = 72) String confirmarContrasena,
        @Size(max = 20) String telefono,
        @Size(max = 200) String direccion) {
}
