package cl.dsy1105.cocinacapital.usuarios.config;

import cl.dsy1105.cocinacapital.usuarios.entity.Restaurante;
import cl.dsy1105.cocinacapital.usuarios.entity.Usuario;
import cl.dsy1105.cocinacapital.usuarios.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Cuentas de PRUEBA, con datos ficticios (Ley 21.719 / 19.628), iguales a las que siembra la app.
 * Se crean solo si no existen. Cambiar antes de producción.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioService servicio;

    public DataInitializer(UsuarioService servicio) {
        this.servicio = servicio;
    }

    @Override
    public void run(String... args) {
        servicio.sembrar("admin@correo.cl", "admin1234", Usuario.ADMIN, null);
        servicio.sembrar("trattoria@correo.cl", "trattoria123", Usuario.VENDEDOR,
                new Restaurante("trattoria@correo.cl", "La Trattoria Ascensor", "Valparaíso",
                        "Pasaje Gálvez 45, Cerro Concepción",
                        "Pasta hecha cada mañana junto al ascensor Concepción.", "foto_pasta"));
        servicio.sembrar("demo@correo.cl", "demo1234", Usuario.COMPRADOR, null);
    }
}
