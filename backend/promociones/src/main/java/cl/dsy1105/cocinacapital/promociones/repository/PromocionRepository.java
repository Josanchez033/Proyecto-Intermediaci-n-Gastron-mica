package cl.dsy1105.cocinacapital.promociones.repository;

import cl.dsy1105.cocinacapital.promociones.entity.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {

    List<Promocion> findByVendedorEmailIgnoreCaseOrderByIdDesc(String vendedorEmail);

    List<Promocion> findByActivaTrueOrderById();

    /** El id siguiente se calcula aquí, así los ids del catálogo base calzan con los de la app. */
    @Query("select coalesce(max(p.id), 0) from Promocion p")
    long maxId();
}
