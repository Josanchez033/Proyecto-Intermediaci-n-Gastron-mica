package cl.dsy1105.cocinacapital.usuarios.dto;

import cl.dsy1105.cocinacapital.usuarios.entity.Restaurante;
import cl.dsy1105.cocinacapital.usuarios.entity.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Lo que entra y sale de la API. La contraseña solo entra; nunca se devuelve. */
public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    /** Misma regla que la app: 6+ caracteres y al menos un número. */
    private static final String CLAVE = "^(?=.*\\d).{6,}$";
    private static final String MENSAJE_CLAVE = "La contraseña debe tener al menos 6 caracteres y un número";

    public record RegistroRequest(
            @NotBlank @Email(message = "El formato del correo no es válido") String email,
            @NotBlank @Pattern(regexp = CLAVE, message = MENSAJE_CLAVE) String password,
            @Size(max = 80) String nombre) {
    }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {
    }

    /** El Admin crea la cuenta del local y su restaurante en un solo paso. */
    public record NuevoLocalRequest(
            @NotBlank @Email(message = "El formato del correo no es válido") String email,
            @NotBlank @Pattern(regexp = CLAVE, message = MENSAJE_CLAVE) String password,
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 60) String comuna,
            @NotBlank @Size(max = 150) String direccion) {
    }

    public record PerfilRequest(@Size(max = 80) String nombre, @Size(max = 300) String foto) {
    }

    /** "actual" puede venir vacía solo cuando la cuenta tiene una contraseña temporal del Admin. */
    public record CambioClaveRequest(String actual,
                                     @NotBlank @Pattern(regexp = CLAVE, message = MENSAJE_CLAVE) String nueva) {
    }

    /** El Admin restablece la contraseña de un local con una temporal. */
    public record ClaveTemporalRequest(@NotBlank @Pattern(regexp = CLAVE, message = MENSAJE_CLAVE) String temporal) {
    }

    public record ActivoRequest(@NotNull Boolean activo) {
    }

    public record RestauranteRequest(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 60) String comuna,
            @NotBlank @Size(max = 150) String direccion,
            @Size(max = 1000) String descripcion,
            @Size(max = 60) String foto) {
    }

    public record UsuarioResponse(String email, String rol, boolean activo, String nombre, String foto, boolean claveTemporal) {
        public static UsuarioResponse de(Usuario u) {
            return new UsuarioResponse(u.getEmail(), u.getRol(), u.isActivo(), u.getNombre(), u.getFoto(), u.isClaveTemporal());
        }
    }

    public record RestauranteResponse(String emailDueno, String nombre, String comuna, String direccion,
                                      String descripcion, String foto) {
        public static RestauranteResponse de(Restaurante r) {
            return new RestauranteResponse(r.getEmailDueno(), r.getNombre(), r.getComuna(), r.getDireccion(),
                    r.getDescripcion(), r.getFoto());
        }
    }

    /** Una cuenta de local con su restaurante (lista del panel del Admin). */
    public record LocalResponse(UsuarioResponse usuario, RestauranteResponse restaurante) {
    }
}
