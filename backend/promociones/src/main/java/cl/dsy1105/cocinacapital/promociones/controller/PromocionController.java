package cl.dsy1105.cocinacapital.promociones.controller;

import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.EstadoRequest;
import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.PromocionRequest;
import cl.dsy1105.cocinacapital.promociones.entity.Promocion;
import cl.dsy1105.cocinacapital.promociones.service.PromocionService;
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

/** CRUD de promociones. "/api/ofertas" se mantiene porque es la ruta que ya usaba la app. */
@RestController
@RequestMapping({"/api/promociones", "/api/ofertas"})
public class PromocionController {

    private final PromocionService servicio;

    public PromocionController(PromocionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Promocion> listar(@RequestParam(required = false) String vendedor,
                                  @RequestParam(defaultValue = "false") boolean activas) {
        return servicio.listar(vendedor, activas);
    }

    @GetMapping("/{id}")
    public Promocion obtener(@PathVariable Long id) {
        return servicio.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Promocion crear(@Valid @RequestBody PromocionRequest req) {
        return servicio.crear(req);
    }

    @PutMapping("/{id}")
    public Promocion actualizar(@PathVariable Long id, @Valid @RequestBody PromocionRequest req) {
        return servicio.actualizar(id, req);
    }

    /** Pausar o reactivar: {"activa": false} */
    @PatchMapping("/{id}/estado")
    public Promocion cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest req) {
        return servicio.cambiarEstado(id, req.activa());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
