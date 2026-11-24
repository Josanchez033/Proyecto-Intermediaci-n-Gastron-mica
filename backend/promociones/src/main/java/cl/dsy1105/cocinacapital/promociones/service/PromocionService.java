package cl.dsy1105.cocinacapital.promociones.service;

import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.ConteoResponse;
import cl.dsy1105.cocinacapital.promociones.dto.PromocionDtos.PromocionRequest;
import cl.dsy1105.cocinacapital.promociones.entity.Canje;
import cl.dsy1105.cocinacapital.promociones.entity.Preferencia;
import cl.dsy1105.cocinacapital.promociones.entity.Promocion;
import cl.dsy1105.cocinacapital.promociones.repository.CanjeRepository;
import cl.dsy1105.cocinacapital.promociones.repository.PreferenciaRepository;
import cl.dsy1105.cocinacapital.promociones.repository.PromocionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PromocionService {

    private final PromocionRepository promociones;
    private final CanjeRepository canjes;
    private final PreferenciaRepository preferencias;

    public PromocionService(PromocionRepository promociones, CanjeRepository canjes, PreferenciaRepository preferencias) {
        this.promociones = promociones;
        this.canjes = canjes;
        this.preferencias = preferencias;
    }

    private Promocion buscar(Long id) {
        return promociones.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la promoción " + id));
    }

    // ---------- Promociones (CRUD) ----------

    /** Sin filtros: todo el catálogo. Con ?vendedor=: las de ese local. Con ?activas=true: solo las visibles. */
    public List<Promocion> listar(String vendedor, boolean soloActivas) {
        if (vendedor != null && !vendedor.isBlank()) return promociones.findByVendedorEmailIgnoreCaseOrderByIdDesc(vendedor.trim());
        return soloActivas ? promociones.findByActivaTrueOrderById() : promociones.findAll();
    }

    public Promocion obtener(Long id) {
        return buscar(id);
    }

    @Transactional
    public synchronized Promocion crear(PromocionRequest req) {
        Promocion p = new Promocion();
        p.setId(promociones.maxId() + 1);
        p.setCreada(System.currentTimeMillis());
        copiar(req, p);
        return promociones.save(p);
    }

    @Transactional
    public Promocion actualizar(Long id, PromocionRequest req) {
        Promocion p = buscar(id);
        copiar(req, p);
        return promociones.save(p);
    }

    /** Pausar (pasa al historial del local) o reactivar. */
    @Transactional
    public Promocion cambiarEstado(Long id, boolean activa) {
        Promocion p = buscar(id);
        p.setActiva(activa);
        return promociones.save(p);
    }

    @Transactional
    public void eliminar(Long id) {
        promociones.delete(buscar(id));
    }

    private static void copiar(PromocionRequest r, Promocion p) {
        p.setNombre(r.nombre().trim());
        p.setCategoria(r.categoria().trim());
        p.setComuna(r.comuna().trim());
        p.setDireccion(r.direccion().trim());
        p.setPromocion(r.promocion().trim());
        p.setIncluye(r.incluye() == null ? "" : r.incluye().trim());
        p.setDescripcion(r.descripcion() == null ? "" : r.descripcion().trim());
        p.setDescuento(r.descuento());
        p.setPrecioNormal(r.precioNormal());
        p.setDias(r.dias());
        p.setHoraInicio(r.horaInicio());
        p.setHoraFin(r.horaFin());
        p.setCapacidad(r.capacidad());
        if (r.rating() != null) p.setRating(r.rating());
        if (r.latitud() != null) p.setLatitud(r.latitud());
        if (r.longitud() != null) p.setLongitud(r.longitud());
        p.setFoto(r.foto() == null || r.foto().isBlank() ? "foto_salon" : r.foto());
        p.setVendedorEmail(r.vendedorEmail() == null || r.vendedorEmail().isBlank() ? null : r.vendedorEmail().trim());
        if (r.activa() != null) p.setActiva(r.activa());
        p.setOferta(r.ofertaOCero());
        p.setDesde(r.desdeOCero());
        p.setHasta(r.hastaOCero());
        boolean propia = r.ofertaOCero() == Promocion.PERSONALIZADA;
        p.setEtiqueta(propia && r.etiqueta() != null ? r.etiqueta().trim() : "");
        p.setPrecioOferta(propia && r.precioOferta() != null ? r.precioOferta() : 0);
    }

    // ---------- Canjes ----------

    public List<Canje> canjesDe(String email) {
        return canjes.findByEmailUsuarioIgnoreCaseOrderByFechaDesc(email.trim());
    }

    /** El canje es gratis y se paga en el local. Una vez por comprador y promoción; suma +3 al perfil. */
    @Transactional
    public Canje canjear(String email, Long promocionId) {
        Promocion p = buscar(promocionId);
        if (!p.isActiva()) throw new ResponseStatusException(HttpStatus.CONFLICT, "La promoción está pausada");
        String correo = email.trim(); // se guarda tal cual; las búsquedas no distinguen mayúsculas
        if (canjes.existsByEmailUsuarioIgnoreCaseAndLocalId(correo, promocionId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya canjeaste esta promoción");
        }
        Canje c = canjes.save(new Canje(correo, promocionId, System.currentTimeMillis()));
        sumarPuntos(correo, p.getCategoria(), 3);
        return c;
    }

    @Transactional
    public void anularCanje(Long id) {
        if (!canjes.existsById(id)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el canje " + id);
        canjes.deleteById(id);
    }

    /** Canjes de cada promoción de un local (para su panel). */
    public List<ConteoResponse> conteoDelLocal(String vendedor) {
        List<Long> ids = promociones.findByVendedorEmailIgnoreCaseOrderByIdDesc(vendedor.trim()).stream().map(Promocion::getId).toList();
        if (ids.isEmpty()) return List.of();
        return canjes.contarPorPromocion(ids).stream().map(c -> new ConteoResponse(c.getLocalId(), c.getTotal())).toList();
    }

    // ---------- Perfil de consumo ----------

    public List<Preferencia> preferenciasDe(String email) {
        return preferencias.findByEmailUsuarioIgnoreCaseOrderByPuntajeDesc(email.trim());
    }

    @Transactional
    public Preferencia sumarPuntos(String email, String categoria, int puntos) {
        String correo = email.trim(); // se guarda tal cual; las búsquedas no distinguen mayúsculas
        Preferencia pref = preferencias.findByEmailUsuarioIgnoreCaseAndCategoria(correo, categoria)
                .orElseGet(() -> new Preferencia(correo, categoria, 0));
        pref.sumar(puntos);
        return preferencias.save(pref);
    }
}
