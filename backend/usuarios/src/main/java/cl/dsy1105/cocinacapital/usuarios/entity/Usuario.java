package cl.dsy1105.cocinacapital.usuarios.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Cuenta de la app. El email es la clave: no puede haber dos cuentas con el mismo correo. */
@Entity
@Table(name = "usuarios")
public class Usuario {

    public static final String COMPRADOR = "Comprador";
    public static final String VENDEDOR = "Vendedor";
    public static final String ADMIN = "Admin";

    @Id
    @Column(length = 120)
    private String email;

    /** Contraseña cifrada con BCrypt (hash con sal). Nunca se guarda ni se devuelve en texto plano. */
    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String rol;

    /** Un local desactivado por el Admin no puede ingresar. */
    private boolean activo = true;

    @Column(length = 80)
    private String nombre = "";

    @Column(length = 300)
    private String foto;

    /** true cuando el Admin entregó una contraseña temporal: la app pide cambiarla al ingresar. */
    private boolean claveTemporal = false;

    protected Usuario() {
    }

    public Usuario(String email, String passwordHash, String rol) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol;
    }

    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public boolean isClaveTemporal() { return claveTemporal; }
    public void setClaveTemporal(boolean claveTemporal) { this.claveTemporal = claveTemporal; }
}
