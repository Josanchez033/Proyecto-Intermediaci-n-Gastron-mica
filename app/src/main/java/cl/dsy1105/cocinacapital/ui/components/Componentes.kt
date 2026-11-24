package cl.dsy1105.cocinacapital.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.dsy1105.cocinacapital.R
import cl.dsy1105.cocinacapital.data.AppState
import cl.dsy1105.cocinacapital.data.pesos
import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.textoHorario
import cl.dsy1105.cocinacapital.data.textoVigencia
import cl.dsy1105.cocinacapital.ui.theme.Aqua100
import cl.dsy1105.cocinacapital.ui.theme.Aqua200
import cl.dsy1105.cocinacapital.ui.theme.AvisoFondo
import cl.dsy1105.cocinacapital.ui.theme.AvisoIcono
import cl.dsy1105.cocinacapital.ui.theme.AvisoTexto
import cl.dsy1105.cocinacapital.ui.theme.CodigoFondo
import cl.dsy1105.cocinacapital.ui.theme.Petroleo
import cl.dsy1105.cocinacapital.ui.theme.PetroleoProfundo
import cl.dsy1105.cocinacapital.ui.theme.Rojo
import cl.dsy1105.cocinacapital.ui.theme.Tinta
import cl.dsy1105.cocinacapital.ui.theme.TintaSuave
import cl.dsy1105.cocinacapital.ui.theme.VeloFoto
import cl.dsy1105.cocinacapital.ui.theme.Verde
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

// ---------- Fotos ----------

/** Recurso drawable de cada foto del catálogo (nombres guardados en Room). */
fun fotoRes(nombre: String): Int = when (nombre) {
    "foto_mariscos" -> R.drawable.foto_mariscos
    "foto_pasta" -> R.drawable.foto_pasta
    "foto_sushi" -> R.drawable.foto_sushi
    "foto_cafe" -> R.drawable.foto_cafe
    "foto_parrilla" -> R.drawable.foto_parrilla
    "foto_bowl" -> R.drawable.foto_bowl
    "foto_postre" -> R.drawable.foto_postre
    "foto_pizza" -> R.drawable.foto_pizza
    "foto_paella" -> R.drawable.foto_paella
    "foto_burger" -> R.drawable.foto_burger
    "foto_autor" -> R.drawable.foto_autor
    "foto_brunch" -> R.drawable.foto_brunch
    "foto_camarones" -> R.drawable.foto_camarones
    "foto_mesa" -> R.drawable.foto_mesa
    "foto_terraza" -> R.drawable.foto_terraza
    "foto_salon" -> R.drawable.foto_salon
    else -> R.drawable.foto_ensalada
}

@Composable
fun FotoLocal(nombre: String, descripcion: String?, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(fotoRes(nombre)),
        contentDescription = descripcion,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

// ---------- Botones ----------

enum class EstiloBoton { Primario, Secundario, Peligro }

/** Botón Material con un leve "apretón" de resorte al presionarlo. */
@Composable
fun PressButton(
    texto: String,
    modifier: Modifier = Modifier,
    icono: ImageVector? = null,
    estilo: EstiloBoton = EstiloBoton.Primario,
    habilitado: Boolean = true,
    onClick: () -> Unit
) {
    val fuente = remember { MutableInteractionSource() }
    val presionado by fuente.collectIsPressedAsState()
    val escala by animateFloatAsState(
        if (presionado) 0.96f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "escalaBoton"
    )
    val contenido: @Composable () -> Unit = {
        if (icono != null) {
            Icon(icono, null, Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
    val forma = RoundedCornerShape(16.dp)
    val mod = modifier.heightIn(min = 52.dp).scale(escala)
    when (estilo) {
        EstiloBoton.Primario -> Button(
            onClick, mod, enabled = habilitado, shape = forma, interactionSource = fuente,
            colors = ButtonDefaults.buttonColors(containerColor = Petroleo),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        ) { contenido() }
        EstiloBoton.Secundario -> OutlinedButton(
            onClick, mod, enabled = habilitado, shape = forma, interactionSource = fuente,
            border = androidx.compose.foundation.BorderStroke(1.dp, Aqua200),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Petroleo),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        ) { contenido() }
        EstiloBoton.Peligro -> OutlinedButton(
            onClick, mod, enabled = habilitado, shape = forma, interactionSource = fuente,
            border = androidx.compose.foundation.BorderStroke(1.dp, Rojo.copy(alpha = 0.35f)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White, contentColor = Rojo),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        ) { contenido() }
    }
}

// ---------- Animación de entrada ----------

/** El contenido entra con fundido + leve deslizamiento (con retraso opcional). */
@Composable
fun Aparecer(retrasoMs: Int = 0, contenido: @Composable () -> Unit) {
    val estado = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = estado,
        enter = fadeIn(tween(420, retrasoMs)) + slideInVertically(tween(420, retrasoMs)) { it / 6 }
    ) { contenido() }
}

// ---------- Encabezados ----------

/** Barra superior de cada pantalla: botón cuadrado redondeado + título. */
@Composable
fun BarraSuperior(
    titulo: String,
    subtitulo: String? = null,
    icono: ImageVector? = Icons.AutoMirrored.Filled.ArrowBack,
    descripcionIcono: String = "Volver",
    onIcono: (() -> Unit)? = null,
    accion: (@Composable () -> Unit)? = null
) {
    Row(
        Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null && onIcono != null) {
            FilledTonalIconButton(
                onClick = onIcono, modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Aqua100, contentColor = Petroleo)
            ) { Icon(icono, descripcionIcono) }
            Spacer(Modifier.width(12.dp))
        } else Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleLarge, color = Tinta, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitulo != null) Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
        }
        accion?.invoke()
    }
}

@Composable
fun TituloSeccion(titulo: String, detalle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(titulo, style = MaterialTheme.typography.titleMedium, color = Tinta)
        if (detalle != null) Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TintaSuave)
    }
}

// ---------- Avisos, etiquetas, chips ----------

/** Aviso amarillo pálido con ícono (información de contexto, no errores). */
@Composable
fun AvisoBanner(
    icono: ImageVector, titulo: String, texto: String? = null, modifier: Modifier = Modifier,
    accion: (@Composable () -> Unit)? = null
) {
    Row(
        modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(AvisoFondo).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = AvisoIcono, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleSmall, color = AvisoTexto)
            if (texto != null) Text(texto, style = MaterialTheme.typography.bodySmall, color = AvisoTexto)
        }
        accion?.invoke()
    }
}

@Composable
fun EtiquetaDescuento(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto,
        modifier.clip(RoundedCornerShape(10.dp)).background(Verde).padding(horizontal = 10.dp, vertical = 5.dp),
        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp
    )
}

@Composable
fun ChipCodigo(codigo: String, modifier: Modifier = Modifier) {
    Text(
        codigo,
        modifier.clip(RoundedCornerShape(8.dp)).background(CodigoFondo).padding(horizontal = 10.dp, vertical = 4.dp),
        color = Tinta, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.8.sp
    )
}

/** Chip pequeño de "por qué te lo sugerimos". */
@Composable
fun ChipRazon(texto: String, destacado: Boolean = false) {
    Text(
        texto,
        Modifier.clip(RoundedCornerShape(50)).background(if (destacado) Aqua200 else Aqua100)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        style = MaterialTheme.typography.labelMedium,
        color = PetroleoProfundo
    )
}

/** Precio normal tachado + precio con beneficio. */
@Composable
fun BloquePrecio(local: LocalEntity, grande: Boolean = false) {
    Column {
        Text(
            "Normal ${pesos(local.precioNormal)}",
            style = MaterialTheme.typography.bodySmall, color = TintaSuave,
            textDecoration = TextDecoration.LineThrough
        )
        Text(
            pesos(local.precioPromo),
            style = if (grande) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
            color = Petroleo
        )
    }
}

// ---------- Anillo de % de match (movimiento firma) ----------

@Composable
fun AnilloMatch(pct: Int, tamano: Dp = 56.dp, modifier: Modifier = Modifier) {
    val animacion = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(pct) {
        animacion.animateTo(pct / 100f, tween(900, easing = androidx.compose.animation.core.FastOutSlowInEasing))
    }
    val avance = animacion.value
    Box(
        modifier.size(tamano).semantics { contentDescription = "$pct por ciento de match" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val grosor = size.minDimension * 0.11f
            val estilo = Stroke(grosor, cap = StrokeCap.Round)
            val inset = grosor / 2
            drawArc(Aqua200, 0f, 360f, false, topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(size.width - grosor, size.height - grosor), style = estilo)
            drawArc(Petroleo, -90f, 360f * avance, false, topLeft = androidx.compose.ui.geometry.Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(size.width - grosor, size.height - grosor), style = estilo)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$pct%", fontWeight = FontWeight.Bold, fontSize = (tamano.value * 0.26f).sp, color = PetroleoProfundo, lineHeight = (tamano.value * 0.28f).sp)
            Text("match", fontSize = (tamano.value * 0.18f).coerceAtLeast(10f).sp, color = TintaSuave, lineHeight = (tamano.value * 0.17f).sp)
        }
    }
}

// ---------- Tarjeta de promoción con foto ----------

@Composable
fun TarjetaPromo(
    appState: AppState,
    local: LocalEntity,
    indice: Int,
    modifier: Modifier = Modifier,
    match: Int? = null,
    razones: List<String> = emptyList(),
    textoBoton: String = "Ver promo",
    ajuste: String? = null,
    onClick: () -> Unit
) {
    Aparecer(retrasoMs = (indice * 60).coerceAtMost(360)) {
        Card(
            modifier = modifier.fillMaxWidth().clickable(onClickLabel = "Ver ${local.nombre}") { onClick() },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(Modifier.fillMaxWidth().height(156.dp)) {
                FotoLocal(local.foto, null, Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(VeloFoto))
                EtiquetaDescuento(local.etiquetaOferta, Modifier.align(Alignment.TopStart).padding(12.dp))
                Row(
                    Modifier.align(Alignment.TopEnd).padding(12.dp).clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.92f)).padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Star, null, tint = AvisoIcono, modifier = Modifier.size(14.dp))
                    Text(" ${local.rating}", style = MaterialTheme.typography.labelMedium, color = Tinta)
                }
                Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                    Text(local.nombre, style = MaterialTheme.typography.titleLarge, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Place, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Text(
                            " ${local.categoria} · ${local.comuna} · " + "%.1f km".format(appState.distanciaKm(local)).replace(".", ","),
                            style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.92f)
                        )
                    }
                }
            }
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(local.promocion, style = MaterialTheme.typography.titleMedium, color = Tinta)
                        Text(textoHorario(local), style = MaterialTheme.typography.bodySmall, color = TintaSuave)
                        textoVigencia(local)?.let {
                            Text(it, style = MaterialTheme.typography.bodySmall, color = Petroleo)
                        }
                    }
                    if (match != null) {
                        Spacer(Modifier.width(12.dp))
                        AnilloMatch(match)
                    }
                }
                if (ajuste != null) {
                    Text(
                        "No calza exacto · $ajuste",
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AvisoFondo)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge, color = AvisoTexto
                    )
                }
                if (razones.isNotEmpty()) {
                    // La última razón es el cupo (filtro duro elegido por el usuario): siempre visible.
                    val visibles = if (razones.size > 4) razones.take(3) + razones.last() else razones
                    FilaFlexible(visibles) { i, r -> ChipRazon(r, destacado = i == 0) }
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Box(Modifier.weight(1f)) { BloquePrecio(local) }
                    Text(
                        textoBoton,
                        Modifier.clip(RoundedCornerShape(14.dp)).background(Petroleo).padding(horizontal = 16.dp, vertical = 10.dp),
                        color = Color.White, style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

/** Fila que hace salto de línea (chips de razones, días, categorías). */
@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun <T> FilaFlexible(items: List<T>, espacio: Dp = 6.dp, contenido: @Composable (Int, T) -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(espacio),
        verticalArrangement = Arrangement.spacedBy(espacio)
    ) { items.forEachIndexed { i, it -> contenido(i, it) } }
}

// ---------- Campos de formulario con error por campo ----------

@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    error: String? = null,
    modifier: Modifier = Modifier,
    esPassword: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    lineas: Int = 1,
    sufijo: String? = null,
    valido: Boolean = false,   // muestra un check verde cuando el valor ya es correcto
    ayuda: String? = null      // pista bajo el campo mientras no haya error
) {
    var visible by remember { mutableStateOf(false) }
    // Validación en tiempo real: el error aparece cuando el usuario hace una pausa al
    // escribir (para no regañar a mitad de palabra) y desaparece apenas se corrige.
    var errorVisible by remember { mutableStateOf(error) }
    var ultimoError by remember { mutableStateOf(error ?: "") }
    LaunchedEffect(error, valor) {
        if (error == null) errorVisible = null
        else {
            if (errorVisible == null) kotlinx.coroutines.delay(450)
            errorVisible = error
            ultimoError = error
        }
    }
    val correcto = valido && error == null
    Column(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            label = { Text(etiqueta) },
            leadingIcon = { Icon(icono, null) },
            trailingIcon = when {
                esPassword -> {
                    {
                        IconButton(onClick = { visible = !visible }) {
                            Icon(
                                if (visible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                if (visible) "Ocultar contraseña" else "Mostrar contraseña"
                            )
                        }
                    }
                }
                errorVisible != null -> { { Icon(Icons.Filled.ErrorOutline, "Error", tint = Rojo) } }
                correcto -> { { Icon(Icons.Filled.CheckCircle, "Correcto", tint = Verde) } }
                else -> null
            },
            suffix = sufijo?.let { { Text(it) } },
            isError = errorVisible != null,
            singleLine = lineas == 1,
            minLines = lineas,
            visualTransformation = if (esPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = if (esPassword) KeyboardType.Password else teclado),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                focusedBorderColor = if (correcto) Verde else Petroleo,
                unfocusedBorderColor = if (correcto) Verde else Aqua200,
                focusedLeadingIconColor = Petroleo, unfocusedLeadingIconColor = TintaSuave
            ),
            modifier = Modifier.fillMaxWidth()
        )
        AnimatedVisibility(errorVisible != null, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Row(Modifier.padding(start = 6.dp, top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ErrorOutline, null, tint = Rojo, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(errorVisible ?: ultimoError, color = Rojo, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (errorVisible == null && ayuda != null) {
            Text(ayuda, Modifier.padding(start = 6.dp, top = 4.dp), color = if (correcto) Verde else TintaSuave, style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ---------- Tarjeta blanca de sección ----------

@Composable
fun Panel(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Surface(modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Color.White, shadowElevation = 1.dp) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = contenido)
    }
}

// ---------- Estado vacío ----------

@Composable
fun EstadoVacio(icono: ImageVector, titulo: String, texto: String, accion: (@Composable () -> Unit)? = null) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(Modifier.size(72.dp).clip(CircleShape).background(Aqua100), contentAlignment = Alignment.Center) {
            Icon(icono, null, tint = Petroleo, modifier = Modifier.size(34.dp))
        }
        Text(titulo, style = MaterialTheme.typography.titleMedium, color = Tinta)
        Text(texto, style = MaterialTheme.typography.bodyMedium, color = TintaSuave, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        accion?.invoke()
    }
}

// ---------- Diálogo de consentimiento informado (RF-27, Ley 21.719) ----------

@Composable
fun DialogoConsentimiento(
    icono: ImageVector, titulo: String, texto: String, onAceptar: () -> Unit, onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = { Icon(icono, null, tint = Petroleo) },
        title = { Text(titulo) },
        text = { Text(texto) },
        confirmButton = { TextButton(onClick = onAceptar) { Text("Continuar") } },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Ahora no") } },
        containerColor = Color.White
    )
}

// ---------- Código QR del voucher ----------

@Composable
fun CodigoQR(texto: String, tamano: Dp = 180.dp) {
    val bitmap = remember(texto) {
        val lado = 256
        val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, lado, lado)
        val bmp = Bitmap.createBitmap(lado, lado, Bitmap.Config.ARGB_8888)
        val oscuro = android.graphics.Color.rgb(8, 49, 59)
        for (x in 0 until lado) for (y in 0 until lado) {
            bmp.setPixel(x, y, if (matriz[x, y]) oscuro else android.graphics.Color.WHITE)
        }
        bmp.asImageBitmap()
    }
    Image(
        bitmap, "Código QR del voucher $texto",
        Modifier.size(tamano).clip(RoundedCornerShape(12.dp)).border(1.dp, Aqua200, RoundedCornerShape(12.dp)),
        filterQuality = FilterQuality.None
    )
}

// ---------- Selector numérico (personas, capacidad) ----------

@Composable
fun Contador(valor: Int, onCambio: (Int) -> Unit, minimo: Int, maximo: Int, unidad: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Aqua100).padding(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = { if (valor > minimo) onCambio(valor - 1) }, enabled = valor > minimo,
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White, contentColor = Petroleo),
            modifier = Modifier.size(48.dp)
        ) { Icon(Icons.Filled.Remove, "Menos") }
        Text(
            if (unidad.startsWith(":")) "$valor$unidad" else "$valor $unidad", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium,
            color = Tinta, textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        FilledTonalIconButton(
            onClick = { if (valor < maximo) onCambio(valor + 1) }, enabled = valor < maximo,
            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = Color.White, contentColor = Petroleo),
            modifier = Modifier.size(48.dp)
        ) { Icon(Icons.Filled.Add, "Más") }
    }
}
