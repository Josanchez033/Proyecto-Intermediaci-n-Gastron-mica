package cl.dsy1105.cocinacapital.usuarios.repository;

import cl.dsy1105.cocinacapital.usuarios.entity.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, String> {
}
