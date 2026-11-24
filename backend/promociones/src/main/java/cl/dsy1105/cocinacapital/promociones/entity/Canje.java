package cl.dsy1105.cocinacapital.promociones.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Un beneficio canjeado por un comprador. Cada comprador canjea una vez cada promoción. */
@Entity
@Table(name = "canjes", uniqueConstraints = @UniqueConstraint(columnNames = {"emailUsuario", "localId"}))
public class Canje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String emailUsuario;

    /** id de la promoción canjeada (se llama localId como en la app). */
    @Column(nullable = false)
    private Long localId;

    private long fecha;

    protected Canje() {
    }

    public Canje(String emailUsuario, Long localId, long fecha) {
        this.emailUsuario = emailUsuario;
        this.localId = localId;
        this.fecha = fecha;
    }

    public Long getId() { return id; }
    public String getEmailUsuario() { return emailUsuario; }
    public Long getLocalId() { return localId; }
    public long getFecha() { return fecha; }
}
