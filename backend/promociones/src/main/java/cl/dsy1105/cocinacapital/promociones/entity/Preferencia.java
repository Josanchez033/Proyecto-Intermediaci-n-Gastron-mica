package cl.dsy1105.cocinacapital.promociones.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Perfil de consumo: cuánto le interesa a cada comprador una categoría.
 * Sube +1 cuando revisa una promoción y +3 cuando canjea. Con esto se ordenan las propuestas.
 */
@Entity
@Table(name = "preferencias", uniqueConstraints = @UniqueConstraint(columnNames = {"emailUsuario", "categoria"}))
public class Preferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String emailUsuario;

    @Column(nullable = false, length = 40)
    private String categoria;

    private int puntaje;

    protected Preferencia() {
    }

    public Preferencia(String emailUsuario, String categoria, int puntaje) {
        this.emailUsuario = emailUsuario;
        this.categoria = categoria;
        this.puntaje = puntaje;
    }

    public Long getId() { return id; }
    public String getEmailUsuario() { return emailUsuario; }
    public String getCategoria() { return categoria; }
    public int getPuntaje() { return puntaje; }
    public void sumar(int puntos) { this.puntaje += puntos; }
}
