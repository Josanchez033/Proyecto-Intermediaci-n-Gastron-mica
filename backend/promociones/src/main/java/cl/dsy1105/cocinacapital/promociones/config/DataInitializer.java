package cl.dsy1105.cocinacapital.promociones.config;

import cl.dsy1105.cocinacapital.promociones.entity.Promocion;
import cl.dsy1105.cocinacapital.promociones.repository.PromocionRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

/**
 * Carga el catálogo de ejemplo (catalogo.json, datos ficticios) la primera vez que arranca.
 * Es el mismo catálogo que trae la app, con los mismos ids.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    /** Promoción programada de demostración: 2x1 los viernes durante un mes. */
    private static final long ID_PROGRAMADA = 900L;

    private final PromocionRepository repositorio;

    public DataInitializer(PromocionRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repositorio.count() > 0) return;
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try (InputStream in = new ClassPathResource("catalogo.json").getInputStream()) {
            List<Promocion> catalogo = Arrays.asList(mapper.readValue(in, Promocion[].class));
            long enUnMes = LocalDate.now(ZoneId.of("America/Santiago")).plusDays(30)
                    .atStartOfDay(ZoneId.of("America/Santiago")).toInstant().toEpochMilli();
            catalogo.stream().filter(p -> p.getId() == ID_PROGRAMADA).forEach(p -> p.setHasta(enUnMes));
            repositorio.saveAll(catalogo);
            log.info("Catálogo de ejemplo cargado: {} promociones", catalogo.size());
        }
    }
}
