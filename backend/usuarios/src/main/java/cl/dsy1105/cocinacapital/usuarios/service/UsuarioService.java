package cl.dsy1105.cocinacapital.usuarios.service;

import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.CambioClaveRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.LocalResponse;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.NuevoLocalRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.PerfilRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.RegistroRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.RestauranteRequest;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.RestauranteResponse;
import cl.dsy1105.cocinacapital.usuarios.dto.UsuarioDtos.UsuarioResponse;
import cl.dsy1105.cocinacapital.usuarios.entity.Restaurante;
import cl.dsy1105.cocinacapital.usuarios.entity.Usuario;
import cl.dsy1105.cocinacapital.usuarios.repository.RestauranteRepository;
import cl.dsy1105.cocinacapital.usuarios.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final RestauranteRepository restaurantes;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarios, RestauranteRepository restaurantes) {
        this.usuarios = usuarios;
        this.restaurantes = restaurantes;
    }

    private static String normalizar(String email) {
        return email.trim().toLowerCase();
    }

    private Usuario buscar(String email) {
        return usuarios.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe una cuenta con ese correo"));
    }

    public boolean existe(String email) {
        return usuarios.existsByEmailIgnoreCase(email.trim());
    }

    // ---------- Registro y login ----------

    /** El registro público solo crea compradores. Las cuentas de local las crea el Admin. */
    @Transactional
    public UsuarioResponse registrarComprador(RegistroRequest req) {
        if (existe(req.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya tiene una cuenta");
        Usuario u = new Usuario(normalizar(req.email()), encoder.encode(req.password()), Usuario.COMPRADOR);
        if (req.nombre() != null) u.setNombre(req.nombre().trim());
        return UsuarioResponse.de(usuarios.save(u));
    }

    /** 401 si el correo o la contraseña no calzan; 403 si es un local desactivado por el Admin. */
    public UsuarioResponse login(String email, String password) {
        Usuario u = usuarios.findByEmailIgnoreCase(email.trim())
                .filter(x -> encoder.matches(password, x.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        if (!u.isActivo()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta cuenta de local está desactivada");
        return UsuarioResponse.de(u);
    }

    // ---------- Cuentas ----------

    public List<UsuarioResponse> listar(String rol) {
        List<Usuario> lista = rol == null ? usuarios.findAll() : usuarios.findByRolOrderByEmail(rol);
        return lista.stream().map(UsuarioResponse::de).toList();
    }

    public UsuarioResponse obtener(String email) {
        return UsuarioResponse.de(buscar(email));
    }

    @Transactional
    public UsuarioResponse actualizarPerfil(String email, PerfilRequest req) {
        Usuario u = buscar(email);
        if (req.nombre() != null) u.setNombre(req.nombre().trim());
        if (req.foto() != null) u.setFoto(req.foto().isBlank() ? null : req.foto());
        return UsuarioResponse.de(usuarios.save(u));
    }

    /** Pide la contraseña actual, salvo que la cuenta tenga una temporal entregada por el Admin. */
    @Transactional
    public void cambiarClave(String email, CambioClaveRequest req) {
        Usuario u = buscar(email);
        if (!u.isClaveTemporal() && (req.actual() == null || !encoder.matches(req.actual(), u.getPasswordHash()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual no es correcta");
        }
        u.setPasswordHash(encoder.encode(req.nueva()));
        u.setClaveTemporal(false);
        usuarios.save(u);
    }

    /** Recuperar contraseña: solo compradores (los locales la recuperan con el Admin). */
    @Transactional
    public void restablecerComprador(String email, String nueva) {
        Usuario u = buscar(email);
        if (!Usuario.COMPRADOR.equals(u.getRol())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta cuenta la recupera el administrador");
        }
        u.setPasswordHash(encoder.encode(nueva));
        u.setClaveTemporal(false);
        usuarios.save(u);
    }

    @Transactional
    public void eliminar(String email) {
        Usuario u = buscar(email);
        restaurantes.deleteById(u.getEmail());
        usuarios.delete(u);
    }

    // ---------- Locales (solo el Admin) ----------

    @Transactional
    public LocalResponse crearLocal(NuevoLocalRequest req) {
        if (existe(req.email())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Ese correo ya tiene una cuenta");
        String email = normalizar(req.email());
        Usuario u = usuarios.save(new Usuario(email, encoder.encode(req.password()), Usuario.VENDEDOR));
        Restaurante r = restaurantes.save(new Restaurante(email, req.nombre().trim(), req.comuna().trim(),
                req.direccion().trim(), "", "foto_salon"));
        return new LocalResponse(UsuarioResponse.de(u), RestauranteResponse.de(r));
    }

    public List<LocalResponse> listarLocales() {
        return usuarios.findByRolOrderByEmail(Usuario.VENDEDOR).stream()
                .map(u -> new LocalResponse(UsuarioResponse.de(u),
                        restaurantes.findById(u.getEmail()).map(RestauranteResponse::de).orElse(null)))
                .toList();
    }

    @Transactional
    public UsuarioResponse cambiarActivo(String email, boolean activo) {
        Usuario u = buscar(email);
        if (!Usuario.VENDEDOR.equals(u.getRol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden activar o desactivar cuentas de local");
        }
        u.setActivo(activo);
        return UsuarioResponse.de(usuarios.save(u));
    }

    /** El Admin entrega una contraseña temporal; el local deberá cambiarla al ingresar. */
    @Transactional
    public UsuarioResponse restablecerClave(String email, String temporal) {
        Usuario u = buscar(email);
        if (!Usuario.VENDEDOR.equals(u.getRol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se restablecen contraseñas de locales");
        }
        u.setPasswordHash(encoder.encode(temporal));
        u.setClaveTemporal(true);
        return UsuarioResponse.de(usuarios.save(u));
    }

    // ---------- Perfil del restaurante ----------

    public List<RestauranteResponse> listarRestaurantes() {
        return restaurantes.findAll().stream().map(RestauranteResponse::de).toList();
    }

    public RestauranteResponse obtenerRestaurante(String email) {
        return restaurantes.findById(normalizar(email)).map(RestauranteResponse::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ese local no tiene perfil de restaurante"));
    }

    @Transactional
    public RestauranteResponse actualizarRestaurante(String email, RestauranteRequest req) {
        Restaurante r = restaurantes.findById(normalizar(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ese local no tiene perfil de restaurante"));
        r.setNombre(req.nombre().trim());
        r.setComuna(req.comuna().trim());
        r.setDireccion(req.direccion().trim());
        if (req.descripcion() != null) r.setDescripcion(req.descripcion().trim());
        if (req.foto() != null && !req.foto().isBlank()) r.setFoto(req.foto());
        return RestauranteResponse.de(restaurantes.save(r));
    }

    // ---------- Datos de prueba ----------

    /** Crea una cuenta solo si no existe (la usa el DataInitializer). */
    @Transactional
    public void sembrar(String email, String password, String rol, Restaurante restaurante) {
        if (existe(email)) return;
        usuarios.save(new Usuario(normalizar(email), encoder.encode(password), rol));
        if (restaurante != null) restaurantes.save(restaurante);
    }
}
