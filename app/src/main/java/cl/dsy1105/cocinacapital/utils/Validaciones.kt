package cl.dsy1105.cocinacapital.utils

// Validaciones de formularios, separadas de la interfaz (IE 2.2.1).
// Cada función devuelve un mapa campo -> mensaje; vacío = formulario válido.

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun esEmailValido(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

object Campo {
    const val EMAIL = "email"
    const val PASSWORD = "password"
    const val CONFIRMAR = "confirmar"
    const val NOMBRE = "nombre"
    const val PROMOCION = "promocion"
    const val PRECIO = "precio"
    const val DESCUENTO = "descuento"
    const val DIAS = "dias"
    const val HORARIO = "horario"
    const val COMUNA = "comuna"
    const val ACTUAL = "actual"
    const val CODIGO = "codigo"
    const val DIRECCION = "direccion"
    const val VIGENCIA = "vigencia"
    const val ETIQUETA = "etiqueta"
    const val PRECIO_PROMO = "precioPromo"
}

fun validarLogin(email: String, password: String): Map<String, String> = buildMap {
    if (email.isBlank()) put(Campo.EMAIL, "Ingresa tu correo")
    else if (!esEmailValido(email)) put(Campo.EMAIL, "El formato del correo no es válido")
    if (password.isBlank()) put(Campo.PASSWORD, "Ingresa tu contraseña")
}

/** Regla única para toda contraseña nueva: 6+ caracteres y al menos un número. null = válida. */
fun errorClaveNueva(password: String): String? = when {
    password.isBlank() -> "La contraseña es obligatoria"
    password.length < 6 -> "Debe tener al menos 6 caracteres"
    password.none { it.isDigit() } -> "Agrega al menos un número"
    else -> null
}

fun validarRegistro(email: String, password: String, confirmar: String): Map<String, String> = buildMap {
    if (email.isBlank()) put(Campo.EMAIL, "El correo es obligatorio")
    else if (!esEmailValido(email)) put(Campo.EMAIL, "El formato del correo no es válido")
    errorClaveNueva(password)?.let { put(Campo.PASSWORD, it) }
    if (confirmar.isBlank()) put(Campo.CONFIRMAR, "Repite la contraseña")
    else if (password != confirmar) put(Campo.CONFIRMAR, "Las contraseñas no coinciden")
}

/** Nueva contraseña + confirmación (recuperar, cambiar o reemplazar una temporal). */
fun validarClaveNueva(password: String, confirmar: String): Map<String, String> = buildMap {
    errorClaveNueva(password)?.let { put(Campo.PASSWORD, it) }
    if (confirmar.isBlank()) put(Campo.CONFIRMAR, "Repite la contraseña")
    else if (password != confirmar) put(Campo.CONFIRMAR, "Las contraseñas no coinciden")
}

fun validarPromocion(
    nombre: String, promocion: String, precio: String, descuento: String,
    dias: String, horaInicio: Int, horaFin: Int
): Map<String, String> = buildMap {
    if (nombre.isBlank()) put(Campo.NOMBRE, "El nombre del local es obligatorio")
    if (promocion.isBlank()) put(Campo.PROMOCION, "Describe la promoción")
    val p = precio.toIntOrNull()
    if (p == null || p <= 0) put(Campo.PRECIO, "Ingresa un precio en pesos, solo números")
    val d = descuento.toIntOrNull()
    if (d == null || d !in 5..90) put(Campo.DESCUENTO, "El descuento va de 5% a 90%")
    if (dias.isBlank()) put(Campo.DIAS, "Elige al menos un día")
    if (horaFin <= horaInicio) put(Campo.HORARIO, "La hora de término debe ser posterior al inicio")
}

/** Cuenta de Local que crea el Admin: datos del restaurante + correo y contraseña inicial. */
fun validarNuevoLocal(
    nombre: String, comuna: String, direccion: String, email: String, password: String, correoExiste: Boolean
): Map<String, String> = buildMap {
    if (nombre.isBlank()) put(Campo.NOMBRE, "El nombre del restaurante es obligatorio")
    if (comuna.isBlank()) put(Campo.COMUNA, "Indica la comuna")
    if (direccion.isBlank()) put(Campo.DIRECCION, "Indica la dirección del local")
    if (email.isBlank()) put(Campo.EMAIL, "El correo es obligatorio")
    else if (!esEmailValido(email)) put(Campo.EMAIL, "El formato del correo no es válido")
    else if (correoExiste) put(Campo.EMAIL, "Ya existe una cuenta con ese correo")
    if (password.length < 6) put(Campo.PASSWORD, "La contraseña inicial debe tener al menos 6 caracteres")
}
