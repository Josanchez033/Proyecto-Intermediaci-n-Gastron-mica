package cl.dsy1105.cocinacapital.promociones.controller;

import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.CanjeRequest;
import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.ConteoResponse;
import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.PuntosRequest;
import cl.dsy1105.cocinacapital.promociones.entity.Canje;
import cl.dsy1105.cocinacapital.promociones.entity.Preferencia;
import cl.dsy1105.cocinacapital.promociones.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Canjes de beneficios y perfil de consumo del comprador. */
@RestController
public class CanjeController {

    private final PromocionService servicio;

    public CanjeController(PromocionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping("/api/canjes")
    public List<Canje> canjes(@RequestParam String usuario) {
        return servicio.canjesDe(usuario);
    }

    @PostMapping("/api/canjes")
    @ResponseStatus(HttpStatus.CREATED)
    public Canje canjear(@Valid @RequestBody CanjeRequest req) {
        return servicio.canjear(req.emailUsuario(), req.localId());
    }

    @DeleteMapping("/api/canjes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void anular(@PathVariable Long id) {
        servicio.anularCanje(id);
    }

    /** Canjes por promoción de un local: [{"localId": 2, "total": 5}, ...] */
    @GetMapping("/api/canjes/conteo")
    public List<ConteoResponse> conteo(@RequestParam String vendedor) {
        return servicio.conteoDelLocal(vendedor);
    }

    @GetMapping("/api/preferencias")
    public List<Preferencia> preferencias(@RequestParam String usuario) {
        return servicio.preferenciasDe(usuario);
    }

    /** +1 al revisar una promoción, +3 al canjear (el canje ya los suma solo). */
    @PostMapping("/api/preferencias/sumar")
    public Preferencia sumar(@Valid @RequestBody PuntosRequest req) {
        return servicio.sumarPuntos(req.emailUsuario(), req.categoria(), req.puntos());
    }
}
