package cl.dsy1105.cocinacapital.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de usuarios de Cocina Capital.
 * Maneja las cuentas (comprador, local y admin), el login y el perfil de cada restaurante.
 */
@SpringBootApplication
public class UsuariosApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsuariosApplication.class, args);
    }
}
