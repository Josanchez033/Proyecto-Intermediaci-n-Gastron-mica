package cl.dsy1105.cocinacapital.promociones.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Una promoción de un local en sus horas de baja demanda.
 * Los nombres de los campos son los mismos que LocalEntity en la app, así Retrofit los lee directo.
 */
@Entity
@Table(name = "promociones")
public class Promocion {

    /** Tipos de oferta (iguales a la app). */
    public static final int PORCENTAJE = 0;
    public static final int DOS_POR_UNO = 2;
    public static final int TRES_POR_DOS = 3;
    public static final int PERSONALIZADA = 9;

    @Id
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;
    @Column(nullable = false, length = 40)
    private String categoria;
    @Column(nullable = false, length = 60)
    private String comuna;
    @Column(nullable = false, length = 150)
    private String direccion;
    @Column(nullable = false, length = 120)
    private String promocion;
    @Column(length = 300)
    private String incluye = "";
    @Column(length = 1000)
    private String descripcion = "";

    private int descuento;
    private int precioNormal;
    /** Días en que aplica: dígitos 1 (lunes) a 7 (domingo), ej. "1234". */
    @Column(nullable = false, length = 7)
    private String dias;
    private int horaInicio;
    private int horaFin;
    private int capacidad;
    private double rating = 5.0;
    private double latitud;
    private double longitud;
    @Column(length = 60)
    private String foto;

    /** Correo del local dueño; null en el catálogo base de Cocina Capital. */
    @Column(length = 120)
    private String vendedorEmail;
    /** El local puede pausarla: deja de verse, pero queda en su historial. */
    private boolean activa = true;
    /** Fecha de publicación (ms). */
    private long creada;

    private int oferta = PORCENTAJE;
    /** Vigencia programada (medianoche local, en ms). 0 = sin límite. */
    private long desde;
    private long hasta;
    /** Nombre corto de una oferta propia ("Combo"); solo con PERSONALIZADA. */
    @Column(length = 20)
    private String etiqueta = "";
    /** Precio exacto de una oferta propia (0 = se calcula con el descuento). */
    private int precioOferta;

    public Promocion() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getComuna() { return comuna; }
    public void setComuna(String comuna) { this.comuna = comuna; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getPromocion() { return promocion; }
    public void setPromocion(String promocion) { this.promocion = promocion; }
    public String getIncluye() { return incluye; }
    public void setIncluye(String incluye) { this.incluye = incluye; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getDescuento() { return descuento; }
    public void setDescuento(int descuento) { this.descuento = descuento; }
    public int getPrecioNormal() { return precioNormal; }
    public void setPrecioNormal(int precioNormal) { this.precioNormal = precioNormal; }
    public String getDias() { return dias; }
    public void setDias(String dias) { this.dias = dias; }
    public int getHoraInicio() { return horaInicio; }
    public void setHoraInicio(int horaInicio) { this.horaInicio = horaInicio; }
    public int getHoraFin() { return horaFin; }
    public void setHoraFin(int horaFin) { this.horaFin = horaFin; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public String getVendedorEmail() { return vendedorEmail; }
    public void setVendedorEmail(String vendedorEmail) { this.vendedorEmail = vendedorEmail; }
    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }
    public long getCreada() { return creada; }
    public void setCreada(long creada) { this.creada = creada; }
    public int getOferta() { return oferta; }
    public void setOferta(int oferta) { this.oferta = oferta; }
    public long getDesde() { return desde; }
    public void setDesde(long desde) { this.desde = desde; }
    public long getHasta() { return hasta; }
    public void setHasta(long hasta) { this.hasta = hasta; }
    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }
    public int getPrecioOferta() { return precioOferta; }
    public void setPrecioOferta(int precioOferta) { this.precioOferta = precioOferta; }
}
