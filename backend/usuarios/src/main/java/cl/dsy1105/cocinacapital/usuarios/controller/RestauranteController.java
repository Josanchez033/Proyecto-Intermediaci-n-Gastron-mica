package cl.dsy1105.cocinacapital.usuarios.controller;

import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.RestauranteRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.RestauranteResponse;
import cl.dsy1105.cocinacapital.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Perfil público de cada restaurante ("Mi restaurante" en la app del local). */
@RestController
@RequestMapping("/api/restaurantes")
public class RestauranteController {

    private final UsuarioService servicio;

    public RestauranteController(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<RestauranteResponse> listar() {
        return servicio.listarRestaurantes();
    }

    @GetMapping("/{email:.+}")
    public RestauranteResponse obtener(@PathVariable String email) {
        return servicio.obtenerRestaurante(email);
    }

    @PutMapping("/{email:.+}")
    public RestauranteResponse actualizar(@PathVariable String email, @Valid @RequestBody RestauranteRequest req) {
        return servicio.actualizarRestaurante(email, req);
    }
}
