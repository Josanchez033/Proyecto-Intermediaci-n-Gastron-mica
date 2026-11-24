package cl.dsy1105.cocinacapital.promociones.repository;

import cl.dsy1105.cocinacapital.promociones.entity.Canje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CanjeRepository extends JpaRepository<Canje, Long> {

    List<Canje> findByEmailUsuarioIgnoreCaseOrderByFechaDesc(String emailUsuario);

    boolean existsByEmailUsuarioIgnoreCaseAndLocalId(String emailUsuario, Long localId);

    /** Cuántos canjes tuvo cada promoción (panel del local). */
    @Query("select c.localId as localId, count(c) as total from Canje c where c.localId in :ids group by c.localId")
    List<ConteoCanjes> contarPorPromocion(Collection<Long> ids);

    interface ConteoCanjes {
        Long getLocalId();
        Long getTotal();
    }
}
