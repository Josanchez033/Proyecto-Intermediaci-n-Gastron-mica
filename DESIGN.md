---
name: Cocina Capital
description: Propuestas gastronómicas de la Región de Valparaíso con % de match, sobre azul petróleo y aguamarina.
colors:
  Petroleo: "#0F4C5C"
  PetroleoProfundo: "#08313B"
  PetroleoMedio: "#1C6878"
  Aqua50: "#F2FAF9"
  Aqua100: "#E1F2F0"
  Aqua200: "#C4E5E2"
  Aqua300: "#9CD1CC"
  White: "#FFFFFF"
  Tinta: "#14282D"
  TintaSuave: "#4B6369"
  Linea: "#D5E4E3"
  outline: "#8FAAA9"
  AvisoFondo: "#FFF4C4"
  AvisoTexto: "#5E4700"
  AvisoIcono: "#B98A00"
  CodigoFondo: "#FFE27A"
  Verde: "#1B7F47"
  VerdeClaro: "#DDF3E5"
  Rojo: "#B3261E"
typography:
  headlineMedium:
    fontFamily: "Roboto, sans-serif"
    fontSize: "28sp"
    fontWeight: 700
    lineHeight: "36sp"
    letterSpacing: "-0.25sp"
  headlineSmall:
    fontFamily: "Roboto, sans-serif"
    fontSize: "24sp"
    fontWeight: 700
    lineHeight: "32sp"
  titleLarge:
    fontFamily: "Roboto, sans-serif"
    fontSize: "22sp"
    fontWeight: 700
    lineHeight: "28sp"
  titleMedium:
    fontFamily: "Roboto, sans-serif"
    fontSize: "16sp"
    fontWeight: 600
    lineHeight: "24sp"
    letterSpacing: "0.15sp"
  titleSmall:
    fontFamily: "Roboto, sans-serif"
    fontSize: "14sp"
    fontWeight: 600
    lineHeight: "20sp"
    letterSpacing: "0.1sp"
  bodyLarge:
    fontFamily: "Roboto, sans-serif"
    fontSize: "16sp"
    fontWeight: 400
    lineHeight: "24sp"
    letterSpacing: "0.2sp"
  bodyMedium:
    fontFamily: "Roboto, sans-serif"
    fontSize: "14sp"
    fontWeight: 400
    lineHeight: "20sp"
    letterSpacing: "0.25sp"
  bodySmall:
    fontFamily: "Roboto, sans-serif"
    fontSize: "12sp"
    fontWeight: 400
    lineHeight: "16sp"
    letterSpacing: "0.4sp"
  labelLarge:
    fontFamily: "Roboto, sans-serif"
    fontSize: "14sp"
    fontWeight: 600
    lineHeight: "20sp"
    letterSpacing: "0.1sp"
  labelMedium:
    fontFamily: "Roboto, sans-serif"
    fontSize: "12sp"
    fontWeight: 500
    lineHeight: "16sp"
    letterSpacing: "0.5sp"
rounded:
  extraSmall: "8dp"
  etiqueta: "10dp"
  small: "12dp"
  campo: "14dp"
  medium: "16dp"
  aviso: "18dp"
  panel: "22dp"
  large: "24dp"
  pill: "50%"
spacing:
  xxs: "4dp"
  xs: "6dp"
  sm: "8dp"
  sm2: "10dp"
  md: "12dp"
  md2: "14dp"
  lg: "16dp"
  xl: "20dp"
  xxl: "24dp"
components:
  button-primario:
    backgroundColor: "{colors.Petroleo}"
    textColor: "{colors.White}"
    typography: "{typography.labelLarge}"
    rounded: "{rounded.medium}"
    padding: "14dp 20dp"
    height: "52dp"
  button-secundario:
    backgroundColor: "{colors.White}"
    textColor: "{colors.Petroleo}"
    typography: "{typography.labelLarge}"
    rounded: "{rounded.medium}"
    padding: "14dp 20dp"
    height: "52dp"
  button-peligro:
    backgroundColor: "{colors.White}"
    textColor: "{colors.Rojo}"
    typography: "{typography.labelLarge}"
    rounded: "{rounded.medium}"
    padding: "14dp 20dp"
    height: "52dp"
  campo-texto:
    backgroundColor: "{colors.White}"
    textColor: "{colors.Tinta}"
    typography: "{typography.bodyLarge}"
    rounded: "{rounded.campo}"
  chip-filtro:
    backgroundColor: "{colors.White}"
    textColor: "{colors.Tinta}"
    typography: "{typography.labelLarge}"
    rounded: "{rounded.extraSmall}"
  chip-filtro-seleccionado:
    backgroundColor: "{colors.Petroleo}"
    textColor: "{colors.White}"
  chip-razon:
    backgroundColor: "{colors.Aqua100}"
    textColor: "{colors.PetroleoProfundo}"
    typography: "{typography.labelMedium}"
    rounded: "{rounded.pill}"
    padding: "5dp 10dp"
  chip-razon-destacado:
    backgroundColor: "{colors.Aqua200}"
    textColor: "{colors.PetroleoProfundo}"
  etiqueta-descuento:
    backgroundColor: "{colors.Verde}"
    textColor: "{colors.White}"
    rounded: "{rounded.etiqueta}"
    padding: "5dp 10dp"
  chip-codigo:
    backgroundColor: "{colors.CodigoFondo}"
    textColor: "{colors.Tinta}"
    rounded: "{rounded.extraSmall}"
    padding: "4dp 10dp"
  aviso-banner:
    backgroundColor: "{colors.AvisoFondo}"
    textColor: "{colors.AvisoTexto}"
    typography: "{typography.titleSmall}"
    rounded: "{rounded.aviso}"
    padding: "14dp"
  panel:
    backgroundColor: "{colors.White}"
    rounded: "{rounded.panel}"
    padding: "16dp"
  tarjeta-promo:
    backgroundColor: "{colors.White}"
    rounded: "{rounded.large}"
    padding: "16dp"
  navegacion-inferior:
    backgroundColor: "{colors.White}"
    textColor: "{colors.TintaSuave}"
    typography: "{typography.labelMedium}"
  navegacion-inferior-activa:
    backgroundColor: "{colors.Aqua200}"
    textColor: "{colors.PetroleoProfundo}"
---

# Design System: Cocina Capital

## Overview

**Creative North Star: "La mesa servida"**

Cocina Capital es una app Android nativa (Jetpack Compose, Material 3) que convierte una salida a comer en un puñado de propuestas. El mundo visual es una piel pedida por el cliente: azul petróleo como voz de marca, un fondo aguamarina claro que baja en degradado, tarjetas blancas con foto de comida a sangre, y tres acentos funcionales que nunca se mezclan (amarillo pálido para avisos, verde para el descuento, amarillo vivo para el código de canje). Todo descansa sobre Material 3 con esquema de color propio y sin color dinámico, para que la identidad no dependa del fondo de pantalla del usuario.

La comida es la protagonista. Las fotos (Unsplash, procedencia en `FOTOS_ORIGEN.txt`) aparecen recortadas a sangre en tarjetas, en círculos con borde blanco en la órbita del inicio y bajo un velo petróleo cuando llevan texto encima. La interfaz alrededor es tranquila: superficies blancas, texto en Tinta, y el petróleo reservado para la acción y la marca.

Tres movimientos firman el mundo: la órbita de seis platos con mesa ahora, que gira lentamente (60 s por vuelta) alrededor de un reloj con las mesas con descuento disponibles; la "marea de horas", cuyas barras suben al entrar al Inicio, y el anillo de % de match que se llena en cada propuesta. El resto del movimiento es Material: fundidos cortos, un leve apretón de resorte al presionar botones y entradas escalonadas de tarjetas.

**Key Characteristics:**
- Petróleo + aguamarina como base; blanco para toda superficie de contenido.
- Fotos de comida a sangre, nunca ilustraciones ni íconos en su lugar.
- Acentos funcionales con un solo trabajo cada uno: aviso, descuento, código.
- Material 3 visible y sin disfraz: barra de navegación inferior, FilterChip, OutlinedTextField, AlertDialog.
- Esquinas generosas (16 a 24dp) y círculos para todo lo que es foto o marca.

## Colors

Una paleta fría de petróleo y aguamarina, cortada por tres acentos cálidos o verdes que solo aparecen cuando tienen algo que decir.

### Primary
- **Azul Petróleo** (`Petroleo`): color de marca y de acción. Botón primario, barra elegida de la marea de horas, chip de filtro seleccionado, borde de campo enfocado, arco del anillo de match, precio con beneficio y el pseudo botón "Ver promo" de la tarjeta.
- **Petróleo Profundo** (`PetroleoProfundo`): el tono más oscuro de la marca. Texto sobre aguamarina (chips de razón, ítem activo de la navegación), cabeceras oscuras (perfil, resumen del voucher), velo sobre fotos y módulos del código QR.
- **Petróleo Medio** (`PetroleoMedio`): `secondary` del esquema; píldora de rol dentro de la cabecera oscura del perfil.

### Secondary
- **Aguamarina** (`Aqua50`, `Aqua100`, `Aqua200`, `Aqua300`): la escala de fondo y de tono. `Aqua50` a `Aqua100` forman el degradado `FondoApp` de cada pantalla; `Aqua100` rellena chips de razón, el contador y el botón de volver; `Aqua200` es el borde de reposo de campos, chips y botón secundario, el indicador de la navegación, el chip de razón destacado y la pista del anillo de match; `Aqua300` dibuja la órbita punteada del Inicio y las barras de la marea de horas (las horas sin mesas van en `Aqua200`).

### Tertiary
- **Verde Descuento** (`Verde`): pestaña de porcentaje de descuento sobre la foto. `tertiary` del esquema.
- **Verde Claro** (`VerdeClaro`): fondo de confirmaciones de éxito (`tertiaryContainer`).
- **Amarillo Aviso** (`AvisoFondo`, `AvisoTexto`, `AvisoIcono`): avisos de contexto (horario de baja demanda, permisos). `AvisoIcono` también tiñe la estrella de valoración.
- **Amarillo Código** (`CodigoFondo`): fondo exclusivo del chip de código de canje.

### Neutral
- **Blanco** (`White`): toda superficie de contenido (tarjetas, paneles, campos, barra inferior, diálogos). En Compose es `Color.White`.
- **Tinta** (`Tinta`): texto principal y títulos.
- **Tinta Suave** (`TintaSuave`): texto secundario, horarios, precio normal tachado, íconos y etiquetas inactivas. Contraste AA sobre blanco.
- **Línea** (`Linea`): `outlineVariant`, divisores.
- **Contorno** (`outline`): `outline` del esquema, solo por defecto en componentes Material.
- **Rojo** (`Rojo`): errores de campo y botón de acción destructiva.

### Named Rules
**The Un Acento, Un Trabajo Rule.** Verde es solo descuento o éxito, `CodigoFondo` es solo el código de canje, el amarillo pálido es solo aviso. Ninguno decora ni se usa como color de marca.

**The Superficie Blanca Rule.** El contenido vive en blanco sobre el degradado aguamarina. El aguamarina rellena controles y chips; nunca reemplaza al blanco como fondo de tarjeta.

**The Velo Petróleo Rule.** Texto blanco sobre foto siempre lleva velo: `VeloFoto` en tarjetas (transparente arriba, petróleo profundo al 85% abajo) o un degradado de `PetroleoProfundo` del 55% al 92% en cabeceras a pantalla completa.

## Typography

**Display Font:** Roboto (fuente del sistema Android)
**Body Font:** Roboto
**Label/Mono Font:** Roboto

**Character:** La escala tipográfica de Material 3 con Roboto, tal como pidió el cliente, endurecida: titulares y títulos en negrita (700), títulos chicos y etiquetas en seminegrita (600), y un ligero tracking negativo en los tamaños grandes. La voz viene del peso, no de una familia nueva.

### Hierarchy
- **Headline Medium** (700, 28sp, 36sp): nombre de marca en la cabecera del login, inicial del avatar.
- **Headline Small** (700, 24sp, 32sp): saludo "Hola, …" del Inicio, precio grande en detalle, código de canje del voucher (con tracking de 2sp).
- **Title Large** (700, 22sp, 28sp): título de `BarraSuperior`, nombre del local sobre la foto, precio con beneficio en tarjeta.
- **Title Medium** (600, 16sp, 24sp): títulos de sección (`TituloSeccion`), nombre de la promoción, valor del contador.
- **Title Small** (600, 14sp, 20sp): título de aviso, subtítulos dentro de paneles ("¿Qué incluye?").
- **Body Large** (400, 16sp, 24sp, 0.2sp): texto de lectura principal (detalle del beneficio) y texto ingresado en campos.
- **Body Medium** (400, 14sp, 20sp): subtítulos y textos de apoyo; el estilo más usado.
- **Body Small** (400, 12sp, 16sp): horarios, ubicación sobre foto, precio tachado, errores de campo.
- **Label Large** (600, 14sp, 20sp): texto de botones, chip de comuna.
- **Label Medium** (500, 12sp, 16sp): chips de razón, valoración, horas de la marea.

### Named Rules
**The Peso Manda Rule.** La jerarquía se marca con peso y tamaño de la escala Material; no se agregan familias, mayúsculas sostenidas ni tracking abierto salvo en el código de canje.

## Layout

Columna única de teléfono. El margen lateral es 16dp en todas las pantallas (20dp en el saludo del Inicio); las listas (`LazyColumn`) separan bloques con 14dp y terminan con 24dp de aire inferior. Dentro de paneles y tarjetas el relleno es 16dp y los elementos se separan con 10 a 12dp; las filas de chips usan 6 a 8dp y hacen salto de línea (`FilaFlexible`).

El Inicio se ordena de arriba abajo: saludo con chip de comuna, la órbita de platos (300dp de alto, radio 112dp) con el reloj al centro, la tarjeta "¿A qué hora sales?" con la marea de horas y el botón "Armar mi salida", y luego "Para ti" con búsqueda, carrusel horizontal de chips y tarjetas. Las pantallas de detalle abren con una foto a sangre de 300dp y fijan la acción principal en una barra blanca inferior. El login pone una foto con velo de 330dp y monta la tarjeta del formulario encima, solapando desde los 250dp.

Navegación: `NavigationBar` de Material abajo, con cuatro o cinco pestañas (el Vendedor suma "Mi local"). Los objetivos táctiles miden al menos 48dp; los botones de texto, 52dp de alto.

## Elevation & Depth

Híbrido: sombras Material suaves sobre un fondo tonal. El degradado aguamarina da el plano base; el blanco eleva el contenido y una sombra corta lo separa. Las sombras son ambientales y nunca de color ni desplazadas.

### Shadow Vocabulary
- **Reposo** (`shadowElevation = 1.dp`): `Panel`, chip de comuna, paneles de detalle y voucher.
- **Tarjeta** (`cardElevation 3.dp`): `TarjetaPromo`, la unidad que se toca.
- **Formulario flotante** (`shadowElevation = 6.dp`): tarjeta del login sobre la foto.
- **Barra de acción** (`shadowElevation = 12.dp`): barra inferior fija con la acción principal (detalle, búsqueda, publicar).
- **Órbita** (`shadow 6.dp / 8.dp / 10.dp, CircleShape`): fotos circulares y reloj central, en orden creciente de importancia.

### Named Rules
**The Profundidad Por Importancia Rule.** Cuanto más importante la acción, más alta la sombra; el contenido informativo se queda en 1dp. `NavigationBar` va sin elevación tonal (0dp) y en blanco puro.

## Shapes

Esquinas generosas y redondeadas, todas `RoundedCornerShape`; nada recto, nada biselado. El tamaño del radio sigue al tamaño del objeto: etiquetas pequeñas 8 a 10dp, campos 14dp, botones y contador 16dp, avisos 18dp, paneles 22dp, tarjetas con foto y cabeceras oscuras 24dp. Las píldoras (`RoundedCornerShape(50)`) son para chips de razón, valoración y chip de comuna.

Lo circular es lenguaje de marca: fotos de la órbita con borde blanco de 3dp, reloj central, avatar, ícono de estado vacío y el anillo de match con remates redondos. El QR se recorta a 12dp con borde `Aqua200` de 1dp.

## Components

### Buttons
Táctiles y seguros; un leve apretón de resorte al presionar (escala 0,96, `DampingRatioMediumBouncy`).
- **Shape:** esquinas suaves (16dp), alto mínimo 52dp, relleno 14dp × 20dp, ícono opcional de 20dp a 8dp del texto.
- **Primario:** relleno `Petroleo`, texto blanco en Label Large.
- **Secundario:** blanco con borde `Aqua200` de 1dp, texto `Petroleo`.
- **Peligro:** blanco con borde `Rojo` al 35%, texto `Rojo`. Solo para eliminar o cerrar sesión.
- **Armar mi salida:** botón primario a todo el ancho bajo la marea de horas; nombra la hora elegida ("Armar mi salida · hoy 16:00") y abre la búsqueda con esa hora.
- **Botón de volver:** `FilledTonalIconButton` de 48dp, `Aqua100` con ícono `Petroleo` (blanco cuando va sobre foto).

### Chips
- **Filtro (`FilterChip`):** blanco con borde `Aqua200`; seleccionado, relleno `Petroleo` y texto blanco. Categorías, días, horas, radios.
- **Razón (`ChipRazon`):** píldora `Aqua100` con texto `PetroleoProfundo` en Label Medium; la primera razón va destacada en `Aqua200`.
- **Descuento (`EtiquetaDescuento`):** "-N%" en blanco negrita 14sp sobre `Verde`, 10dp, en la esquina superior izquierda de la foto.
- **Código (`ChipCodigo`):** código en negrita 13sp con tracking 0,8sp sobre `CodigoFondo`, 8dp.

### Cards / Containers
- **TarjetaPromo:** 24dp, blanco, elevación 3dp. Foto a sangre de 156dp con `VeloFoto`, descuento arriba a la izquierda, valoración en píldora blanca al 92% arriba a la derecha, nombre y ubicación en blanco abajo. Cuerpo con relleno 16dp: promoción y horario, anillo de match opcional, chips de razón (máximo cuatro, el cupo siempre visible), precio y pseudo botón "Ver promo" (`Petroleo`, 14dp). Toda la tarjeta es el objetivo táctil. Entra con fundido y deslizamiento escalonado de 60ms por índice (tope 360ms).
- **Panel:** 22dp, blanco, sombra 1dp, relleno 16dp, separación interna 12dp. El contenedor de sección por defecto.
- **Cabecera oscura:** `PetroleoProfundo`, 24 a 26dp, texto blanco; perfil y resumen del voucher.

### Inputs / Fields
- **Style (`CampoTexto`):** `OutlinedTextField` blanco, 14dp, borde `Aqua200` en reposo, ícono inicial `TintaSuave`.
- **Focus:** borde e ícono inicial pasan a `Petroleo`.
- **Error:** borde e ícono final `Rojo`; el mensaje aparece debajo con ícono de 16dp, expandiéndose verticalmente. La contraseña lleva conmutador de visibilidad.
- **Contador:** barra `Aqua100` de 16dp con botones tonales blancos de 48dp a los lados y el valor al centro en Title Medium.

### Navigation
- **NavigationBar:** blanco, sin elevación tonal. Ícono relleno si está activo, delineado si no. Activo: indicador `Aqua200`, ícono y texto `PetroleoProfundo`. Inactivo: `TintaSuave`. El cambio de pestaña es un fade through (entra 220ms tras 90ms con escala desde 0,96; sale en 90ms).
- **BarraSuperior:** botón tonal de 48dp, título Title Large en `Tinta` y subtítulo opcional en Body Medium `TintaSuave`.
- **Pestañas del login:** `SecondaryTabRow` con indicador `Petroleo` de 3dp.

### AnilloMatch (signature)
Anillo de 56dp con pista `Aqua200` y arco `Petroleo` de grosor 11% del diámetro y remates redondos; el arco arranca a las 12 y se llena en 900ms con `FastOutSlowInEasing`. Al centro, el porcentaje en negrita `PetroleoProfundo`. Anuncia "N por ciento de match" a lectores de pantalla.

### Órbita de platos (signature)
Seis fotos circulares (alternando 70 y 60dp, borde blanco 3dp, sombra 6dp) giran sobre un círculo punteado `Aqua300` alrededor de un reloj blanco de 128dp. Las fotos son los locales con mesa con descuento AHORA (luego los que abren más tarde hoy). El reloj muestra la hora, el número de mesas con descuento disponibles y, si no hay, desde qué hora vuelven. Una vuelta cada 60 s, lineal. Si la escala de animación del sistema es 0, queda quieta. Cada foto abre su local.

### Marea de horas (signature)
Componente `BarrasHoras`, compartido por el Inicio y "Arma tu salida". Una columna por hora elegible: si es hoy, desde la hora actual hasta las 22:00; otro día, de 08:00 a 22:00. Cada columna lleva su número encima (siempre visible), la barra, una línea base `Linea` y la hora en formato "16:00" debajo; la actual dice "ahora". La elegida va en `Petroleo` sobre fondo `Aqua100`, en negrita; las demás en `Aqua300` y las vacías en `Aqua200` con "0" atenuado. Columnas de 48dp (toque mínimo), anunciadas como "16:00, 9 mesas con descuento". Las barras suben desde cero en 700ms con `FastOutSlowInEasing` y cambian de alto y color con animación cuando cambia la búsqueda; si la hora elegida cambia desde fuera, la fila se desplaza para mostrarla.
- **En el Inicio** cuenta mesas con descuento y una frase lee la barra elegida ("A las 16:00 hay **9 mesas con descuento**."), con la pista "Cada barra es una hora desde ahora: más alta, más mesas".
- **En "Arma tu salida"** cuenta los locales que calzan con toda la búsqueda (día, personas, radio y antojo), así reacciona en vivo a cada cambio.

### Tira de fechas y calendario
En "Arma tu salida" el día se elige sobre fechas reales: una fila de celdas de 60dp (etiqueta "Hoy", "Mañana" o el día corto; número en Title Large; mes corto) para las próximas dos semanas. Nunca se muestran días pasados; si hoy ya no quedan horas, "Hoy" se ve al 40% y no se puede tocar. La elegida va en `Petroleo` con texto blanco. A la derecha del título, un botón tonal de calendario abre el `DatePickerDialog` de Material en español (lunes primero), con los días pasados deshabilitados y hasta 60 días adelante. Al cambiar de día, si la hora ya pasó, la búsqueda salta a la primera hora con locales. La distancia se elige con un `Slider` de cinco paradas (1, 3, 5, 10 y 20 km) y el antojo con una fila desplazable de chips. La barra inferior repite la búsqueda en una línea ("Sáb 28 nov · 16:00 · 4 personas · hasta 5 km · Italiana").

### Programación de promociones (Local)
En "Nueva promoción" el tipo de oferta se elige con chips (% descuento, 2x1, 3x2) y el período con dos filas de chips: "Empieza" (Hoy u Otra fecha) y "Termina" (Hasta que la pause, 1 semana, 1 mes, 3 meses u Otra fecha); los que abren calendario llevan ícono de calendario y, una vez elegidos, muestran la fecha ("El vie 27 nov"). Debajo, un resumen en `Aqua100` con ícono `EventRepeat` lee la programación en una frase. La etiqueta verde de descuento muestra el texto de la oferta ("-30%", "2x1", "3x2"); la vigencia aparece en las tarjetas en Body Small `Petroleo`. En el panel del Local el estado va en una píldora: Activa (`Verde`), Programada (`Petroleo`), Pausada y Terminó (`TintaSuave`).

### AvisoBanner
Fila `AvisoFondo` de 18dp con relleno 14dp, ícono `AvisoIcono` de 24dp, título Title Small y texto Body Small en `AvisoTexto`. Información de contexto, nunca errores.

### DialogoConsentimiento
`AlertDialog` blanco con ícono `Petroleo`, acciones de texto "Continuar" y "Ahora no". Precede a todo permiso o canje.

## Do's and Don'ts

### Do:
- **Do** usar `Petroleo` para la acción principal de cada pantalla y `PetroleoProfundo` para texto sobre aguamarina.
- **Do** poner el contenido sobre blanco y el blanco sobre `FondoApp`.
- **Do** mostrar comida real a sangre, recortada con `ContentScale.Crop`, y registrar su procedencia en `FOTOS_ORIGEN.txt`.
- **Do** cubrir con `VeloFoto` o un degradado de `PetroleoProfundo` toda foto que lleve texto blanco.
- **Do** usar componentes Material 3 (`NavigationBar`, `FilterChip`, `OutlinedTextField`, `AlertDialog`) con los colores del esquema propio.
- **Do** mantener objetivos táctiles de 48dp como mínimo y botones de 52dp de alto.
- **Do** respetar la escala de animación del sistema en todo movimiento continuo, como hace la órbita.

### Don't:
- **Don't** activar color dinámico (Material You): la paleta es fija.
- **Don't** usar el verde, el amarillo de aviso o el amarillo de código fuera de su único trabajo.
- **Don't** poner texto blanco directamente sobre una foto sin velo.
- **Don't** usar fondos aguamarina como fondo de tarjeta ni rojo fuera de errores y acciones destructivas.
- **Don't** usar esquinas rectas ni sombras de color o desplazadas.
- **Don't** volver a la lista plana de restaurantes con estrellas como forma principal: las propuestas llevan su anillo de match y sus razones.
