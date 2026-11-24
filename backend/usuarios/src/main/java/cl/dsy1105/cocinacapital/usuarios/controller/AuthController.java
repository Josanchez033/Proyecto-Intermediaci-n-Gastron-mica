package cl.dsy1105.cocinacapital.usuarios.controller;

import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.LoginRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.RegistroRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.UsuarioResponse;
import cl.dsy1105.cocinacapital.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService servicio;

    public AuthController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    /** Registro público: crea una cuenta de comprador. */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registro(@Valid @RequestBody RegistroRequest req) {
        return servicio.registrarComprador(req);
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest req) {
        return servicio.login(req.email(), req.password());
    }

    /**
     * Recuperar contraseña de un comprador. En el prototipo el código de 6 dígitos lo verifica la app
     * (no hay servidor de correo); en producción el código se enviaría por email y se validaría aquí.
     */
    @PostMapping("/restablecer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecer(@Valid @RequestBody RegistroRequest req) {
        servicio.restablecerComprador(req.email(), req.password());
    }

    /** Para validar en vivo en el registro si el correo ya está tomado. */
    @GetMapping("/existe")
    public Map<String, Boolean> existe(@RequestParam String email) {
        return Map.of("existe", servicio.existe(email));
    }
}
