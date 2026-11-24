# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users
- **Comprador / Consumidor (principal):** persona de la Región de Valparaíso que busca dónde comer o vivir una experiencia culinaria. Usa la app en el teléfono, a menudo en la calle o planificando una salida (día, hora, cuántas personas, qué tipo de comida, cuán lejos).
- **Vendedor / Oferente (secundario):** dueño o encargado de un local gastronómico que publica promociones para llenar sus horarios o temporadas de baja demanda.
- **Cliente del proyecto:** COCINA CAPITAL (PYME intermediadora). Evalúa el prototipo mediante un video demo de máximo 120 s.

## Product Purpose
MVP Android de COCINA CAPITAL que conecta consumidores con la oferta gastronómica regional mediante promociones dirigidas según perfil de consumo y ubicación, para dar visibilidad a los locales y rentabilizar sus períodos de baja demanda. Éxito: más interacción consumidor–local, alta exposición de promociones y beneficios efectivamente canjeados en horas de baja demanda.

## Positioning
La app no es un buscador estático: aprende un perfil de consumo (+1 al revisar un local, +3 al canjear) y lo combina con cercanía y descuento para ordenar propuestas. La búsqueda específica (día, hora, radio, preferencia, cantidad de personas) devuelve varias propuestas que hacen match con ese perfil, cada una con su % de match, y el consumidor elige entre ellas.

## Operating Context
- Proyecto académico DSY1105 (Duoc UC) con cliente real; equipo de 2 (Joaquín Sánchez y Antonella Guzmán).
- Entregables: app Kotlin + Jetpack Compose + Material 3, MVVM, Room, Retrofit contra microservicios Spring Boot, API externa, pruebas unitarias ≥80 %, APK firmado, GitHub + Trello.
- Demo para el cliente: 60 s de introducción + 60 s del flujo de búsqueda específica y elección de propuesta.

## Capabilities and Constraints
- Roles: Comprador y Vendedor (solo el Vendedor publica). Sin rol administrador.
- Comprador: login/registro, Explorar "Para ti" (puntaje = afinidad×2 − km×0,3 + descuento/10), búsqueda y filtro por categoría, búsqueda específica con % de match, detalle de local, canje gratuito una vez por local con voucher (código + QR), Cerca (por distancia, permiso de ubicación), perfil de consumo con barras, notificación local predictiva.
- Vendedor: publicar, ver, editar y eliminar sus promociones.
- Cada promoción muestra precio normal y precio con beneficio; **no hay pago ni billetera dentro de la app** (fuera del MVP, PB-24).
- Datos guardados en Room; catálogo ficticio precargado; sincronización con backend propio.
- Privacidad: Ley 21.719 y 19.628; pruebas solo con datos ficticios y anonimizados; consentimiento informado antes de pedir ubicación y notificaciones.

## Brand Commitments
- Nombre: Cocina Capital. Lema: "Descubre, ahorra y saborea la región".
- El cliente pidió que la gráfica se base en la del Equipo_07_S3 (skin vista en su video demo: azul petróleo, fondos aguamarina, tarjetas blancas con fotos de comida, avisos amarillo pálido, etiquetas verdes de descuento, inicio con fotos circulares en torno al logo, vouchers con QR). Basarse, no copiar: sin reutilizar sus imágenes ni textos.
- Idioma: español de Chile.

## Evidence on Hand
- Catálogo de locales ficticio en `app/src/main/java/cl/dsy1105/cocinacapital/data/DatosEjemplo.kt`.
- Fotos de comida: de licencia libre (Unsplash/Pexels), incluidas como recursos de la app.
- No existen clientes, reseñas, cifras de negocio ni precios reales: todo precio y valoración es ficticio y de demostración.

## Product Principles
1. La lógica de recomendación es el diferencial: debe verse (puntaje, % de match, "por qué te lo sugerimos").
2. El consumidor siempre decide entre varias propuestas; la app sugiere, no impone.
3. Cada beneficio debe sentirse tangible: voucher con código y QR listo para mostrar en el local.
4. El vendedor llena horas vacías con pocos toques.
5. Datos ficticios y consentimiento explícito; nada personal real.

## Accessibility & Inclusion
Áreas táctiles de 48 dp, descripciones de contenido en íconos, contraste WCAG AA, textos que respeten el tamaño de fuente del sistema.
