package cl.dsy1105.cocinacapital.usuarios.repository;

import cl.dsy1105.cocinacapital.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    /** El correo se compara sin mayúsculas: "Ana@Correo.cl" y "ana@correo.cl" son la misma cuenta. */
    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Usuario> findByRolOrderByEmail(String rol);
}
