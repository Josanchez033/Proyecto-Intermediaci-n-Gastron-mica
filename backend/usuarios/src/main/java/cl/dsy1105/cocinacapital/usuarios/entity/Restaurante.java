package cl.dsy1105.cocinacapital.usuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Perfil público de un restaurante. Lo crea el Admin junto con la cuenta del local; después lo edita el local. */
@Entity
@Table(name = "restaurantes")
public class Restaurante {

    @Id
    @Column(length = 120)
    private String emailDueno;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String comuna;

    @Column(nullable = false, length = 150)
    private String direccion;

    @Column(length = 1000)
    private String descripcion = "";

    @Column(length = 60)
    private String foto = "foto_salon";

    protected Restaurante() {
    }

    public Restaurante(String emailDueno, String nombre, String comuna, String direccion, String descripcion, String foto) {
        this.emailDueno = emailDueno;
        this.nombre = nombre;
        this.comuna = comuna;
        this.direccion = direccion;
        this.descripcion = descripcion;
        this.foto = foto;
    }

    public String getEmailDueno() { return emailDueno; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
}
