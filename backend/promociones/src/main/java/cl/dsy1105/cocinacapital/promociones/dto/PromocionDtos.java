package cl.dsy1105.cocinacapital.promociones.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Lo que entra a la API. Las respuestas devuelven la entidad tal cual (mismos campos que la app). */
public final class PromocionDtos {

    private PromocionDtos() {
    }

    /** Crear o editar una promoción. Las mismas reglas que valida la app en vivo. */
    public record PromocionRequest(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 40) String categoria,
            @NotBlank @Size(max = 60) String comuna,
            @NotBlank @Size(max = 150) String direccion,
            @NotBlank(message = "Describe la promoción") @Size(max = 120) String promocion,
            @Size(max = 300) String incluye,
            @Size(max = 1000) String descripcion,
            @Min(value = 1, message = "El descuento va de 1% a 95%") @Max(value = 95, message = "El descuento va de 1% a 95%") int descuento,
            @Min(value = 1, message = "El precio debe ser mayor que cero") @Max(9_999_999) int precioNormal,
            @NotBlank @Pattern(regexp = "^[1-7]{1,7}$", message = "Días: dígitos 1 (lunes) a 7 (domingo)") String dias,
            @Min(0) @Max(23) int horaInicio,
            @Min(1) @Max(24) int horaFin,
            @Min(value = 1, message = "El cupo mínimo es 1 persona") @Max(100) int capacidad,
            Double rating,
            Double latitud,
            Double longitud,
            @Size(max = 60) String foto,
            @Size(max = 120) String vendedorEmail,
            Boolean activa,
            @Min(0) @Max(9) Integer oferta,
            @Min(0) Long desde,
            @Min(0) Long hasta,
            @Size(max = 20) String etiqueta,
            @Min(0) Integer precioOferta) {

        // Los campos opcionales que no vienen en el JSON se toman como 0 (sin vigencia, oferta en %).
        public int ofertaOCero() { return oferta == null ? 0 : oferta; }
        public long desdeOCero() { return desde == null ? 0 : desde; }
        public long hastaOCero() { return hasta == null ? 0 : hasta; }

        @AssertTrue(message = "La hora de término debe ser posterior al inicio")
        public boolean isHorarioValido() {
            return horaFin > horaInicio;
        }

        @AssertTrue(message = "La fecha de término debe ser después del inicio")
        public boolean isVigenciaValida() {
            return hastaOCero() == 0 || desdeOCero() == 0 || hastaOCero() >= desdeOCero();
        }
    }

    public record EstadoRequest(@NotNull Boolean activa) {
    }

    public record CanjeRequest(@NotBlank String emailUsuario, @NotNull Long localId) {
    }

    /** Suma puntos al perfil de consumo: +1 al revisar, +3 al canjear. */
    public record PuntosRequest(@NotBlank String emailUsuario, @NotBlank String categoria,
                                @Min(1) @Max(10) int puntos) {
    }

    public record ConteoResponse(Long localId, Long total) {
    }
}
