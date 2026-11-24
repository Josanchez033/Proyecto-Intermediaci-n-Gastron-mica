package cl.dsy1105.cocinacapital.ui.views

import android.graphics.BitmapFactory
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.Recuperacion
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.CampoTexto
import cl.dsy1105.cocinacapital.ui.components.EstiloBoton
import cl.dsy1105.cocinacapital.ui.components.Panel
import cl.dsy1105.cocinacapital.ui.components.PressButton
import cl.dsy1105.cocinacapital.ui.components.TituloSeccion
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.FondoApp
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Rojo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.ui.theme.Verde
import cl.dsy1105.cocinacapital.utils.Campo
import cl.dsy1105.cocinacapital.utils.borrarFotoPerfil
import cl.dsy1105.cocinacapital.utils.esEmailValido
import cl.dsy1105.cocinacapital.utils.guardarFotoPerfil
import cl.dsy1105.cocinacapital.utils.validarClaveNueva
import kotlinx.coroutines.launch

/** Foto de perfil circular; sin foto muestra la inicial (o el ícono indicado). */
@Composable
fun AvatarCuenta(
    foto: String?, nombre: String, tamano: Dp, modifier: Modifier = Modifier,
    fondo: Color = Aqua200, iconoSinFoto: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    val imagen = remember(foto) { foto?.let { runCatching { BitmapFactory.decodeFile(it)?.asImageBitmap() }.getOrNull() } }
    Box(modifier.size(tamano).clip(CircleShape).background(fondo), contentAlignment = Alignment.Center) {
        when {
            imagen != null -> Image(imagen, "Foto de perfil", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            iconoSinFoto != null -> Icon(iconoSinFoto, null, tint = PetroleoProfundo, modifier = Modifier.size(tamano * 0.46f))
            else -> Text(
                nombre.take(1).uppercase(), color = PetroleoProfundo,
                style = if (tamano >= 80.dp) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Recuperar contraseña (desde el login)
// ---------------------------------------------------------------------------

@Composable
fun RecuperarClaveScreen(navController: NavController, appState: AppState) {
    var paso by rememberSaveable { mutableStateOf(1) }
    var email by rememberSaveable { mutableStateOf("") }
    var codigoEnviado by rememberSaveable { mutableStateOf("") }
    var codigo by rememberSaveable { mutableStateOf("") }
    var nueva by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var tocados by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intento by rememberSaveable { mutableStateOf(false) }
    var noExiste by rememberSaveable { mutableStateOf(false) }
    var gestionada by rememberSaveable { mutableStateOf(false) }

    val erroresCorreo = buildMap {
        if (email.isBlank()) put(Campo.EMAIL, "Ingresa el correo de tu cuenta")
        else if (!esEmailValido(email)) put(Campo.EMAIL, "El formato del correo no es válido")
        else if (noExiste) put(Campo.EMAIL, "No hay una cuenta con ese correo")
    }
    val erroresClave = validarClaveNueva(nueva, confirmar) + buildMap {
        if (codigo.length < 6) put(Campo.CODIGO, "El código tiene 6 dígitos")
        else if (!appState.codigoValido(email, codigo)) put(Campo.CODIGO, "El código no coincide")
    }
    fun error(m: Map<String, String>, c: String) = if (intento || c in tocados) m[c] else null

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior("Recuperar contraseña", "Paso $paso de 3", onIcono = { navController.popBackStack() })
            Column(
                Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (paso) {
                    1 -> Panel {
                        TituloSeccion("¿Cuál es tu correo?", "Te enviaremos un código de 6 dígitos para crear una contraseña nueva.")
                        CampoTexto(
                            email, { email = it; noExiste = false; gestionada = false; tocados = tocados + Campo.EMAIL },
                            "Correo electrónico", Icons.Filled.Email, error(erroresCorreo, Campo.EMAIL),
                            teclado = KeyboardType.Email, valido = esEmailValido(email) && !noExiste
                        )
                        if (gestionada) {
                            AvisoBanner(
                                Icons.Filled.Storefront, "Esta cuenta la administra Cocina Capital",
                                "Las cuentas de restaurante se recuperan con el administrador: pídele que restablezca tu contraseña."
                            )
                        }
                        PressButton("Enviar código", Modifier.fillMaxWidth(), icono = Icons.Filled.MarkEmailRead) {
                            intento = true
                            if (erroresCorreo.isEmpty()) {
                                when (val r = appState.solicitarRecuperacion(email)) {
                                    is Recuperacion.CodigoEnviado -> { codigoEnviado = r.codigo; paso = 2; intento = false; tocados = emptySet() }
                                    Recuperacion.NoExiste -> noExiste = true
                                    Recuperacion.CuentaGestionada -> gestionada = true
                                }
                            }
                        }
                    }
                    2 -> {
                        // Prototipo: no hay servidor de correo, así que el código se muestra aquí.
                        AvisoBanner(
                            Icons.Filled.MarkEmailRead, "Código enviado a ${email.trim()}",
                            "Prototipo sin servidor de correo: tu código es $codigoEnviado"
                        )
                        Panel {
                            TituloSeccion("Crea tu nueva contraseña")
                            CampoTexto(
                                codigo, { codigo = it.filter(Char::isDigit).take(6); tocados = tocados + Campo.CODIGO },
                                "Código de 6 dígitos", Icons.Filled.Pin, error(erroresClave, Campo.CODIGO),
                                teclado = KeyboardType.NumberPassword, valido = Campo.CODIGO !in erroresClave
                            )
                            CampoTexto(
                                nueva, { nueva = it; tocados = tocados + Campo.PASSWORD }, "Nueva contraseña", Icons.Filled.Lock,
                                error(erroresClave, Campo.PASSWORD), esPassword = true, valido = Campo.PASSWORD !in erroresClave,
                                ayuda = if (Campo.PASSWORD !in erroresClave) "Contraseña válida" else "Mínimo 6 caracteres, con al menos un número"
                            )
                            CampoTexto(
                                confirmar, { confirmar = it; tocados = tocados + Campo.CONFIRMAR }, "Repite la contraseña", Icons.Filled.LockReset,
                                error(erroresClave, Campo.CONFIRMAR), esPassword = true, valido = Campo.CONFIRMAR !in erroresClave,
                                ayuda = if (Campo.CONFIRMAR !in erroresClave) "Las contraseñas coinciden" else null
                            )
                            PressButton("Guardar contraseña", Modifier.fillMaxWidth(), icono = Icons.Filled.Key) {
                                intento = true
                                if (erroresClave.isEmpty() && appState.restablecerConCodigo(email, codigo, nueva)) paso = 3
                            }
                            TextButton(onClick = {
                                (appState.solicitarRecuperacion(email) as? Recuperacion.CodigoEnviado)?.let { codigoEnviado = it.codigo; codigo = "" }
                            }) { Text("Enviar otro código") }
                        }
                    }
                    else -> Panel {
                        Column(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, null, tint = Verde, modifier = Modifier.size(56.dp))
                            Spacer(Modifier.height(10.dp))
                            Text("Contraseña actualizada", style = MaterialTheme.typography.titleLarge, color = Tinta)
                            Text("Ya puedes ingresar con tu nueva contraseña.", style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
                        }
                        PressButton("Volver a iniciar sesión", Modifier.fillMaxWidth()) { navController.popBackStack() }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reemplazar la contraseña temporal entregada por el Admin
// ---------------------------------------------------------------------------

@Composable
fun NuevaClaveScreen(navController: NavController, appState: AppState) {
    var nueva by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var tocados by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intento by rememberSaveable { mutableStateOf(false) }
    val errores = validarClaveNueva(nueva, confirmar)
    fun error(c: String) = if (intento || c in tocados) errores[c] else null
    fun salir() {
        appState.logout()
        navController.navigate("login") { popUpTo(0) }
    }
    // No se puede saltar este paso: volver atrás cierra la sesión.
    BackHandler { salir() }

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().imePadding().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BarraSuperior("Crea tu contraseña", appState.usuarioActual?.email, icono = null)
            AvisoBanner(
                Icons.Filled.LockReset, "Estás usando una contraseña temporal",
                "El administrador de Cocina Capital la creó para ti. Reemplázala por una que solo tú conozcas."
            )
            Panel {
                CampoTexto(
                    nueva, { nueva = it; tocados = tocados + Campo.PASSWORD }, "Nueva contraseña", Icons.Filled.Lock,
                    error(Campo.PASSWORD), esPassword = true, valido = Campo.PASSWORD !in errores,
                    ayuda = if (Campo.PASSWORD !in errores) "Contraseña válida" else "Mínimo 6 caracteres, con al menos un número"
                )
                CampoTexto(
                    confirmar, { confirmar = it; tocados = tocados + Campo.CONFIRMAR }, "Repite la contraseña", Icons.Filled.LockReset,
                    error(Campo.CONFIRMAR), esPassword = true, valido = Campo.CONFIRMAR !in errores,
                    ayuda = if (Campo.CONFIRMAR !in errores) "Las contraseñas coinciden" else null
                )
                PressButton("Guardar y entrar", Modifier.fillMaxWidth(), icono = Icons.Filled.Key) {
                    intento = true
                    if (errores.isEmpty() && appState.cambiarClave(null, nueva)) {
                        navController.navigate("principal") { popUpTo(0) }
                    }
                }
                TextButton(onClick = { salir() }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Cerrar sesión") }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Mi cuenta: foto, nombre, correo y contraseña (comprador y Local)
// ---------------------------------------------------------------------------

@Composable
fun MiCuentaScreen(navController: NavController, appState: AppState) {
    val usuario = appState.usuarioActual ?: return
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val corrutinas = rememberCoroutineScope()
    fun avisar(texto: String) { corrutinas.launch { snackbar.showSnackbar(texto) } }

    // Foto
    val elegirFoto = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val ruta = guardarFotoPerfil(context, uri, usuario.email)
            if (ruta != null) { appState.actualizarPerfil(usuario.nombre, ruta); avisar("Foto actualizada") }
            else avisar("No pudimos leer esa imagen. Prueba con otra.")
        }
    }

    // Nombre
    var nombre by rememberSaveable { mutableStateOf(usuario.nombre) }
    val errorNombre = if (nombre.length > 40) "Máximo 40 caracteres" else null

    // Correo
    var correoNuevo by rememberSaveable { mutableStateOf("") }
    var claveCorreo by rememberSaveable { mutableStateOf("") }
    var tocadosCorreo by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intentoCorreo by rememberSaveable { mutableStateOf(false) }
    var claveCorreoMala by rememberSaveable { mutableStateOf(false) }
    val erroresCorreo = buildMap {
        if (correoNuevo.isBlank()) put(Campo.EMAIL, "Escribe el correo nuevo")
        else if (!esEmailValido(correoNuevo)) put(Campo.EMAIL, "El formato del correo no es válido")
        else if (correoNuevo.trim().equals(usuario.email, true)) put(Campo.EMAIL, "Es tu correo actual")
        else if (appState.existeCorreo(correoNuevo)) put(Campo.EMAIL, "Ya existe una cuenta con ese correo")
        if (claveCorreo.isBlank()) put(Campo.ACTUAL, "Confirma con tu contraseña actual")
        else if (claveCorreoMala) put(Campo.ACTUAL, "La contraseña no es correcta")
    }
    fun errorCorreo(c: String) = if (intentoCorreo || c in tocadosCorreo) erroresCorreo[c] else null

    // Contraseña
    var actual by rememberSaveable { mutableStateOf("") }
    var nueva by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }
    var tocadosClave by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intentoClave by rememberSaveable { mutableStateOf(false) }
    var actualMala by rememberSaveable { mutableStateOf(false) }
    val erroresClave = validarClaveNueva(nueva, confirmar) + buildMap {
        if (actual.isBlank()) put(Campo.ACTUAL, "Escribe tu contraseña actual")
        else if (actualMala) put(Campo.ACTUAL, "La contraseña actual no es correcta")
        else if (nueva.isNotBlank() && nueva == actual) put(Campo.PASSWORD, "Debe ser distinta de la actual")
    }
    fun errorClave(c: String) = if (intentoClave || c in tocadosClave) erroresClave[c] else null

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior("Mi cuenta", "Tus datos de acceso y cómo te ven", onIcono = { navController.popBackStack() })
            Column(
                Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
                    .navigationBarsPadding().padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Panel {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AvatarCuenta(
                            usuario.foto, usuario.nombre.ifBlank { appState.nombreCorto }, 88.dp,
                            Modifier.border(3.dp, Aqua100, CircleShape)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            PressButton("Cambiar foto", icono = Icons.Filled.AddAPhoto, estilo = EstiloBoton.Secundario) {
                                elegirFoto.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                            if (usuario.foto != null) {
                                TextButton(onClick = {
                                    borrarFotoPerfil(usuario.foto); appState.actualizarPerfil(usuario.nombre, null); avisar("Foto quitada")
                                }) { Text("Quitar foto", color = Rojo) }
                            }
                        }
                    }
                    CampoTexto(
                        nombre, { nombre = it }, if (appState.esVendedor) "Tu nombre (encargado del local)" else "Tu nombre",
                        Icons.Filled.Badge, errorNombre, ayuda = "Así te saludamos en la app"
                    )
                    PressButton(
                        "Guardar nombre", Modifier.fillMaxWidth(),
                        habilitado = errorNombre == null && nombre.trim() != usuario.nombre
                    ) { appState.actualizarPerfil(nombre, usuario.foto); avisar("Nombre guardado") }
                    if (appState.esVendedor) {
                        Text(
                            "El nombre, la foto y la dirección del restaurante se editan en \"Mi restaurante\".",
                            style = MaterialTheme.typography.bodySmall, color = TintaSuave
                        )
                    }
                }

                Panel {
                    TituloSeccion("Correo de acceso", "Hoy ingresas con ${usuario.email}")
                    CampoTexto(
                        correoNuevo, { correoNuevo = it; tocadosCorreo = tocadosCorreo + Campo.EMAIL }, "Correo nuevo",
                        Icons.Filled.AlternateEmail, errorCorreo(Campo.EMAIL), teclado = KeyboardType.Email,
                        valido = Campo.EMAIL !in erroresCorreo
                    )
                    CampoTexto(
                        claveCorreo, { claveCorreo = it; claveCorreoMala = false; tocadosCorreo = tocadosCorreo + Campo.ACTUAL },
                        "Contraseña actual", Icons.Filled.Lock, errorCorreo(Campo.ACTUAL), esPassword = true
                    )
                    PressButton("Cambiar correo", Modifier.fillMaxWidth(), icono = Icons.Filled.AlternateEmail, estilo = EstiloBoton.Secundario) {
                        intentoCorreo = true
                        if (erroresCorreo.isEmpty()) {
                            if (!appState.claveCorrecta(claveCorreo)) claveCorreoMala = true
                            else if (appState.cambiarCorreo(correoNuevo, claveCorreo)) {
                                avisar("Listo: ahora ingresas con ${correoNuevo.trim()}")
                                correoNuevo = ""; claveCorreo = ""; intentoCorreo = false; tocadosCorreo = emptySet()
                            }
                        }
                    }
                }

                Panel {
                    TituloSeccion("Contraseña")
                    CampoTexto(
                        actual, { actual = it; actualMala = false; tocadosClave = tocadosClave + Campo.ACTUAL }, "Contraseña actual",
                        Icons.Filled.Lock, errorClave(Campo.ACTUAL), esPassword = true
                    )
                    CampoTexto(
                        nueva, { nueva = it; tocadosClave = tocadosClave + Campo.PASSWORD }, "Nueva contraseña", Icons.Filled.Password,
                        errorClave(Campo.PASSWORD), esPassword = true, valido = Campo.PASSWORD !in erroresClave,
                        ayuda = if (Campo.PASSWORD !in erroresClave) "Contraseña válida" else "Mínimo 6 caracteres, con al menos un número"
                    )
                    CampoTexto(
                        confirmar, { confirmar = it; tocadosClave = tocadosClave + Campo.CONFIRMAR }, "Repite la nueva contraseña",
                        Icons.Filled.LockReset, errorClave(Campo.CONFIRMAR), esPassword = true, valido = Campo.CONFIRMAR !in erroresClave,
                        ayuda = if (Campo.CONFIRMAR !in erroresClave) "Las contraseñas coinciden" else null
                    )
                    PressButton("Cambiar contraseña", Modifier.fillMaxWidth(), icono = Icons.Filled.Key, estilo = EstiloBoton.Secundario) {
                        intentoClave = true
                        if (erroresClave.isEmpty()) {
                            if (!appState.claveCorrecta(actual)) actualMala = true
                            else if (appState.cambiarClave(actual, nueva)) {
                                avisar("Contraseña cambiada")
                                actual = ""; nueva = ""; confirmar = ""; intentoClave = false; tocadosClave = emptySet()
                            }
                        }
                    }
                }
            }
        }
        SnackbarHost(snackbar, Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(16.dp))
    }
}
