package cl.dsy1105.cocinacapital.promociones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de promociones de Cocina Capital: el catálogo de ofertas que publican los
 * locales, los canjes de beneficios y el perfil de consumo de cada comprador.
 */
@SpringBootApplication
public class PromocionesApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromocionesApplication.class, args);
    }
}
