package cl.dsy1105.cocinacapital.promociones.repository;

import cl.dsy1105.cocinacapital.promociones.entity.Preferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreferenciaRepository extends JpaRepository<Preferencia, Long> {

    List<Preferencia> findByEmailUsuarioIgnoreCaseOrderByPuntajeDesc(String emailUsuario);

    Optional<Preferencia> findByEmailUsuarioIgnoreCaseAndCategoria(String emailUsuario, String categoria);
}
