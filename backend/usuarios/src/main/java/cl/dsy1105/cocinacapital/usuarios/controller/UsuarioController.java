package cl.dsy1105.cocinacapital.usuarios.controller;

import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.ActivoRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.CambioClaveRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.ClaveTemporalRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.LocalResponse;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.NuevoLocalRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.PerfilRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.UsuarioResponse;
import cl.dsy1105.cocinacapital.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CRUD de cuentas. {email:.+} permite correos con puntos en la ruta. */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService servicio;

    public UsuarioController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    /** Todas las cuentas, o solo las de un rol: ?rol=Comprador | Vendedor | Admin */
    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) String rol) {
        return servicio.listar(rol);
    }

    @GetMapping("/{email:.+}")
    public UsuarioResponse obtener(@PathVariable String email) {
        return servicio.obtener(email);
    }

    /** "Mi cuenta": nombre y foto. */
    @PutMapping("/{email:.+}")
    public UsuarioResponse actualizarPerfil(@PathVariable String email, @Valid @RequestBody PerfilRequest req) {
        return servicio.actualizarPerfil(email, req);
    }

    @PutMapping("/{email:.+}/clave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarClave(@PathVariable String email, @Valid @RequestBody CambioClaveRequest req) {
        servicio.cambiarClave(email, req);
    }

    @DeleteMapping("/{email:.+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String email) {
        servicio.eliminar(email);
    }

    // ---------- Panel del Admin: cuentas de local ----------

    @GetMapping("/locales")
    public List<LocalResponse> locales() {
        return servicio.listarLocales();
    }

    @PostMapping("/locales")
    @ResponseStatus(HttpStatus.CREATED)
    public LocalResponse crearLocal(@Valid @RequestBody NuevoLocalRequest req) {
        return servicio.crearLocal(req);
    }

    @PatchMapping("/{email:.+}/activo")
    public UsuarioResponse cambiarActivo(@PathVariable String email, @Valid @RequestBody ActivoRequest req) {
        return servicio.cambiarActivo(email, req.activo());
    }

    @PutMapping("/{email:.+}/clave-temporal")
    public UsuarioResponse restablecerClave(@PathVariable String email, @Valid @RequestBody ClaveTemporalRequest req) {
        return servicio.restablecerClave(email, req.temporal());
    }
}
