package cl.dsy1105.cocinacapital.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import cl.dsy1105.cocinacapital.utils.errorClaveNueva
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.Usuario
import cl.dsy1105.cocinacapital.data.room.RestauranteEntity
import cl.dsy1105.cocinacapital.ui.components.AvisoBanner
import cl.dsy1105.cocinacapital.ui.components.BarraSuperior
import cl.dsy1105.cocinacapital.ui.components.CampoTexto
import cl.dsy1105.cocinacapital.ui.components.EstadoVacio
import cl.dsy1105.cocinacapital.ui.components.FilaFlexible
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
import cl.dsy1105.cocinacapital.utils.validarNuevoLocal

private val COMUNAS = listOf("Valparaíso", "Viña del Mar", "Concón", "Quilpué", "Villa Alemana")

// Panel del Admin de Cocina Capital: es el único que crea cuentas de Local,
// y puede desactivarlas (la cuenta no ingresa y sus promociones se ocultan).
@Composable
fun AdminScreen(navController: NavController, appState: AppState) {
    val cuentas = appState.cuentasLocales()
    val activas = cuentas.count { it.first.activo }
    var porDesactivar by remember { mutableStateOf<Pair<Usuario, RestauranteEntity?>?>(null) }
    var claveDe by remember { mutableStateOf<Pair<Usuario, RestauranteEntity?>?>(null) }

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        LazyColumn(
            Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                BarraSuperior(
                    "Administración", "Cuentas de restaurantes verificados", icono = null,
                    accion = {
                        IconButton(onClick = {
                            appState.logout()
                            navController.navigate("login") { popUpTo("admin") { inclusive = true } }
                        }) { Icon(Icons.AutoMirrored.Filled.Logout, "Cerrar sesión", tint = Petroleo) }
                    }
                )
            }
            item {
                Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Cifra("Locales activos", activas.toString(), Modifier.weight(1f))
                    Cifra("Desactivados", (cuentas.size - activas).toString(), Modifier.weight(1f))
                }
            }
            item {
                AvisoBanner(
                    Icons.Filled.VerifiedUser, "Solo el Admin crea cuentas de local",
                    "Así cada restaurante queda verificado y nadie puede publicar promociones falsas.",
                    Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                PressButton(
                    "Crear cuenta de local", Modifier.fillMaxWidth().padding(horizontal = 16.dp), icono = Icons.Filled.AddBusiness
                ) { navController.navigate("admin/nuevo") }
            }
            item { TituloSeccion("Restaurantes", modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp)) }
            if (cuentas.isEmpty()) {
                item {
                    EstadoVacio(Icons.Filled.Storefront, "Aún no hay locales", "Crea la primera cuenta de restaurante con el botón de arriba.")
                }
            }
            items(cuentas, key = { it.first.email }) { (usuario, restaurante) ->
                TarjetaCuentaLocal(
                    usuario, restaurante, appState.promocionesDe(usuario.email), Modifier.padding(horizontal = 16.dp),
                    onClave = { claveDe = usuario to restaurante }
                ) { activar ->
                    if (activar) appState.cambiarActivo(usuario.email, true)
                    else porDesactivar = usuario to restaurante
                }
            }
        }
    }

    claveDe?.let { (usuario, restaurante) ->
        DialogoClaveLocal(usuario, restaurante, appState) { claveDe = null }
    }

    porDesactivar?.let { (usuario, restaurante) ->
        AlertDialog(
            onDismissRequest = { porDesactivar = null },
            title = { Text("¿Desactivar ${restaurante?.nombre ?: usuario.email}?") },
            text = { Text("El local no podrá ingresar y sus promociones dejarán de verse para los compradores. Puedes reactivarlo cuando quieras.") },
            confirmButton = {
                TextButton(onClick = { appState.cambiarActivo(usuario.email, false); porDesactivar = null }) {
                    Text("Desactivar", color = Rojo)
                }
            },
            dismissButton = { TextButton(onClick = { porDesactivar = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun Cifra(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Surface(modifier, shape = RoundedCornerShape(20.dp), color = Color.White, shadowElevation = 1.dp) {
        Column(Modifier.padding(16.dp)) {
            Text(valor, style = MaterialTheme.typography.headlineMedium, color = PetroleoProfundo)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = TintaSuave)
        }
    }
}

@Composable
private fun TarjetaCuentaLocal(
    usuario: Usuario, restaurante: RestauranteEntity?, promociones: Int,
    modifier: Modifier = Modifier, onClave: () -> Unit, onCambiar: (Boolean) -> Unit
) {
    Panel(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(if (usuario.activo) Aqua200 else Aqua100),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Storefront, null, tint = if (usuario.activo) PetroleoProfundo else TintaSuave) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    restaurante?.nombre ?: "Local sin perfil", style = MaterialTheme.typography.titleMedium,
                    color = if (usuario.activo) Tinta else TintaSuave, maxLines = 1, overflow = TextOverflow.Ellipsis
                )
                Text(usuario.email, style = MaterialTheme.typography.bodySmall, color = TintaSuave, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Switch(
                checked = usuario.activo, onCheckedChange = onCambiar,
                colors = SwitchDefaults.colors(checkedTrackColor = Verde)
            )
        }
        if (restaurante != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Place, null, tint = TintaSuave, modifier = Modifier.size(16.dp))
                Text(" ${restaurante.direccion}, ${restaurante.comuna}", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
            }
        }
        Text(
            (if (usuario.activo) "Activo" else "Desactivado") + " · " +
                    if (promociones == 1) "1 promoción" else "$promociones promociones",
            style = MaterialTheme.typography.labelLarge, color = if (usuario.activo) Verde else Rojo
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (usuario.claveTemporal) {
                Text(
                    "Contraseña temporal sin cambiar", Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall, color = TintaSuave
                )
            } else Spacer(Modifier.weight(1f))
            TextButton(onClick = onClave) {
                Icon(Icons.Filled.LockReset, null, Modifier.size(18.dp)); Text(" Restablecer contraseña")
            }
        }
    }
}

/** Contraseña temporal legible: 8 caracteres sin letras ni números que se confundan, con al menos un número. */
private fun generarClaveTemporal(): String {
    val letras = "ABCDEFGHJKMNPQRSTUVWXYZ"; val numeros = "23456789"
    val base = (1..6).map { (letras + numeros).random() } + numeros.random() + numeros.random()
    return base.shuffled().joinToString("")
}

/**
 * El Admin restablece la contraseña de un Local que la olvidó o necesita cambiarla.
 * Se propone una temporal (editable); el Local debe reemplazarla al ingresar.
 */
@Composable
private fun DialogoClaveLocal(
    usuario: Usuario, restaurante: RestauranteEntity?, appState: AppState, onCerrar: () -> Unit
) {
    var clave by remember { mutableStateOf(generarClaveTemporal()) }
    var lista by remember { mutableStateOf(false) }
    val error = errorClaveNueva(clave)
    val nombre = restaurante?.nombre ?: usuario.email

    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(if (lista) Icons.Filled.CheckCircle else Icons.Filled.LockReset, null, tint = if (lista) Verde else Petroleo) },
        title = { Text(if (lista) "Contraseña restablecida" else "Restablecer contraseña") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!lista) {
                    Text("Crea una contraseña temporal para $nombre. Al ingresar, el local deberá reemplazarla por una propia.")
                    CampoTexto(
                        clave, { clave = it.trim() }, "Contraseña temporal", Icons.Filled.Key, error,
                        valido = error == null, ayuda = "Puedes usar la sugerida o escribir otra"
                    )
                    TextButton(onClick = { clave = generarClaveTemporal() }) {
                        Icon(Icons.Filled.Refresh, null, Modifier.size(18.dp)); Text(" Sugerir otra")
                    }
                } else {
                    Text("Entrégale estos datos a $nombre por un canal seguro:")
                    Surface(shape = RoundedCornerShape(14.dp), color = Aqua100) {
                        Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(usuario.email, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
                            Text(clave, style = MaterialTheme.typography.headlineSmall, color = PetroleoProfundo)
                        }
                    }
                    Text("Cuando ingrese, la app le pedirá crear su propia contraseña.", style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                }
            }
        },
        confirmButton = {
            if (!lista) {
                TextButton(enabled = error == null, onClick = {
                    if (appState.restablecerClaveLocal(usuario.email, clave)) lista = true
                }) { Text("Guardar") }
            } else TextButton(onClick = onCerrar) { Text("Listo") }
        },
        dismissButton = { if (!lista) TextButton(onClick = onCerrar) { Text("Cancelar") } },
        containerColor = Color.White
    )
}

// Formulario del Admin para crear una cuenta de Local con su restaurante.
@Composable
fun CrearLocalScreen(navController: NavController, appState: AppState) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var comuna by rememberSaveable { mutableStateOf("") }
    var direccion by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    // Un campo muestra su error en cuanto el usuario escribió en él (o al intentar crear).
    var tocados by rememberSaveable { mutableStateOf(setOf<String>()) }
    var intento by rememberSaveable { mutableStateOf(false) }
    var creada by remember { mutableStateOf(false) }
    var creando by remember { mutableStateOf(false) }
    val alcance = rememberCoroutineScope()

    val errores = validarNuevoLocal(nombre, comuna, direccion, email, password, appState.existeCorreo(email))
    fun error(campo: String) = if (intento || campo in tocados) errores[campo] else null

    Box(Modifier.fillMaxSize().background(FondoApp)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            BarraSuperior("Nueva cuenta de local", "El restaurante recibirá estos datos para ingresar", onIcono = { navController.popBackStack() })
            Column(
                Modifier.weight(1f).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Panel {
                    TituloSeccion("Restaurante")
                    CampoTexto(nombre, { nombre = it; tocados = tocados + Campo.NOMBRE }, "Nombre del restaurante", Icons.Filled.Storefront, error(Campo.NOMBRE))
                    CampoTexto(direccion, { direccion = it; tocados = tocados + Campo.DIRECCION }, "Dirección", Icons.Filled.Place, error(Campo.DIRECCION))
                    Text("Comuna", style = MaterialTheme.typography.titleSmall, color = Tinta)
                    FilaFlexible(COMUNAS) { _, c ->
                        FilterChip(
                            selected = comuna == c, onClick = { comuna = c; tocados = tocados + Campo.COMUNA }, label = { Text(c) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Petroleo, selectedLabelColor = Color.White, containerColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(true, comuna == c, borderColor = Aqua200)
                        )
                    }
                    error(Campo.COMUNA)?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = Rojo) }
                }
                Panel {
                    TituloSeccion("Acceso del local", "Entrégale estos datos al restaurante")
                    CampoTexto(email, { email = it; tocados = tocados + Campo.EMAIL }, "Correo del local", Icons.Filled.Email,
                        error(Campo.EMAIL), teclado = KeyboardType.Email, valido = Campo.EMAIL !in errores)
                    CampoTexto(password, { password = it; tocados = tocados + Campo.PASSWORD }, "Contraseña inicial", Icons.Filled.Lock,
                        error(Campo.PASSWORD), esPassword = true, valido = Campo.PASSWORD !in errores,
                        ayuda = "Mínimo 6 caracteres")
                }
            }
            Surface(color = Color.White, shadowElevation = 12.dp) {
                PressButton(
                    if (creando) "Creando cuenta…" else "Crear cuenta de local", Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp),
                    icono = Icons.Filled.AddBusiness, habilitado = !creando
                ) {
                    intento = true
                    if (errores.isEmpty()) alcance.launch {
                        creando = true
                        creada = appState.crearCuentaLocal(email, password, nombre, comuna, direccion)
                        creando = false
                    }
                }
            }
        }
    }

    if (creada) {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Filled.AdminPanelSettings, null, tint = Verde) },
            title = { Text("Cuenta creada") },
            text = { Text("$nombre ya puede ingresar con ${email.trim()} y la contraseña inicial que definiste.") },
            confirmButton = { TextButton(onClick = { creada = false; navController.popBackStack() }) { Text("Listo") } }
        )
    }
}
