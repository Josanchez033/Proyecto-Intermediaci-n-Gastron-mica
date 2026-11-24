package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.room.LocalEntity
import cl.dsy1105.cocinacapital.data.room.Oferta

// Catálogo FICTICIO de prueba (locales, direcciones, precios y valoraciones
// inventados). Se carga en Room la primera vez que se abre la app.
val LOCALES_EJEMPLO = listOf(
    LocalEntity(1, "Caleta del Puerto", "Mariscos", "Valparaíso", "Muelle Prat 12",
        "2x1 en ceviche y pisco sour", "Dos ceviches mixtos y dos pisco sour de la casa",
        "Mariscos frescos de la caleta con vista a la bahía.", 50, 18900, "1234", 15, 19, 6, 4.7,
        -33.0393, -71.6270, "foto_mariscos"),
    LocalEntity(2, "La Trattoria Ascensor", "Italiana", "Valparaíso", "Pasaje Gálvez 45, Cerro Concepción",
        "30% en pastas artesanales", "Plato de pasta fresca a elección y una copa de vino",
        "Pasta hecha cada mañana junto al ascensor Concepción.", 30, 14900, "234", 12, 17, 8, 4.5,
        -33.0421, -71.6285, "foto_pasta", vendedorEmail = LOCAL_DEMO_EMAIL),
    LocalEntity(3, "Sushi Marejada", "Sushi", "Viña del Mar", "Av. San Martín 820",
        "Roll premium + bebida gratis", "Tabla de 20 piezas nikkei y una bebida",
        "Fusión nikkei con pescados de la región.", 25, 16000, "123", 15, 21, 4, 4.6,
        -33.0245, -71.5518, "foto_sushi"),
    LocalEntity(4, "Café Brisa Celeste", "Café", "Viña del Mar", "5 Norte 310",
        "Combo desayuno a 40% menos", "Café de especialidad, jugo natural y croissant",
        "Café de especialidad y pastelería de autor.", 40, 8900, "1234567", 8, 12, 4, 4.8,
        -33.0153, -71.5500, "foto_cafe"),
    LocalEntity(5, "Fuego & Parrilla", "Carnes", "Concón", "Av. Borgoño 21500",
        "Parrillada para 2 con 35% dto.", "Costillar, longaniza, ensalada chilena y dos bebidas",
        "Cortes a las brasas frente al mar.", 35, 32000, "1234", 13, 18, 10, 4.4,
        -32.9306, -71.5190, "foto_parrilla"),
    LocalEntity(6, "Verde Raíz", "Vegana", "Quilpué", "Los Carrera 1450",
        "Bowl + jugo natural al 20%", "Bowl de temporada y jugo prensado en frío",
        "Cocina vegana con productos de huertas locales.", 20, 9900, "12345", 12, 16, 6, 4.3,
        -33.0475, -71.4425, "foto_bowl"),
    LocalEntity(7, "Dulce Costa", "Postres", "Valparaíso", "Almirante Montt 77, Cerro Alegre",
        "Segundo postre gratis", "Dos copas de postre a elección y dos cafés",
        "Pastelería chilena reinventada en el cerro.", 50, 7800, "234", 16, 20, 6, 4.6,
        -33.0460, -71.6190, "foto_postre"),
    LocalEntity(8, "Pizzería Cumbre", "Italiana", "Villa Alemana", "Av. Valparaíso 980",
        "Pizza familiar con 28% dto.", "Pizza familiar de masa madre y bebida de 1,5 L",
        "Masa madre y horno de piedra.", 28, 15900, "12367", 18, 22, 8, 4.2,
        -33.0422, -71.3736, "foto_pizza"),
    LocalEntity(9, "Mar de Fondo", "Mariscos", "Viña del Mar", "Av. Perú 450",
        "Paella marinera para 2 al 30%", "Paella marinera para dos y una jarra de clery",
        "Arroces y mariscos frente a la costanera.", 30, 29900, "123456", 15, 19, 6, 4.5,
        -33.0170, -71.5600, "foto_paella"),
    LocalEntity(10, "Burger Brava", "Rápida", "Valparaíso", "Av. Pedro Montt 2010",
        "Combo doble + papas al 35%", "Hamburguesa doble, papas rústicas y bebida",
        "Hamburguesas a la plancha con pan de masa madre.", 35, 9900, "123456", 15, 19, 8, 4.3,
        -33.0485, -71.6080, "foto_burger"),
    LocalEntity(11, "Mesa de Autor", "Chilena", "Valparaíso", "Templeman 210, Cerro Alegre",
        "Menú degustación 3 tiempos -25%", "Entrada, fondo y postre de cocina chilena de autor",
        "Cocina chilena de temporada en una casona del cerro.", 25, 24000, "234", 13, 17, 4, 4.8,
        -33.0432, -71.6262, "foto_autor"),
    LocalEntity(12, "Brunch del Cerro", "Café", "Valparaíso", "Pasaje Templeman 5",
        "Brunch para 2 con 30% dto.", "Tabla de brunch para dos con huevos, panes y café",
        "Brunch largo y luminoso con vista a los cerros.", 30, 19800, "1234567", 10, 14, 6, 4.6,
        -33.0428, -71.6275, "foto_brunch"),
    LocalEntity(13, "Picada Las Gaviotas", "Mariscos", "Valparaíso", "Caleta Portales s/n",
        "Camarones al pil pil al 40%", "Camarones al pil pil, pan amasado y bebida",
        "Picada de caleta con recetas de la abuela.", 40, 13900, "34567", 15, 20, 4, 4.4,
        -33.0315, -71.5985, "foto_camarones"),
    // Ids 101+ para no chocar con las promociones que crean los vendedores.
    LocalEntity(101, "Nikkei Barón", "Sushi", "Valparaíso", "Av. Francia 1340",
        "Tabla nikkei para 2 al 30%", "Tabla de 30 piezas y dos bebidas",
        "Sushi y ceviche nikkei a pasos del Congreso.", 30, 21900, "1234567", 16, 22, 6, 4.5,
        -33.0440, -71.6110, "foto_sushi"),
    LocalEntity(102, "Parrilla del Almendral", "Carnes", "Valparaíso", "Av. Pedro Montt 1650",
        "Lomo a lo pobre con 30% dto.", "Lomo a lo pobre, ensalada y bebida",
        "Parrilla de barrio con cortes nacionales.", 30, 15900, "1234567", 15, 21, 8, 4.3,
        -33.0500, -71.6060, "foto_parrilla"),
    LocalEntity(103, "Huerta Alegre", "Vegana", "Valparaíso", "Lautaro Rosas 410, Cerro Alegre",
        "Menú vegano del día al 25%", "Crema de temporada, bowl y agua de hierbas",
        "Cocina de huerta en una terraza del cerro.", 25, 10900, "1234567", 12, 18, 6, 4.6,
        -33.0445, -71.6240, "foto_ensalada"),
    LocalEntity(104, "Heladería Bellavista", "Postres", "Valparaíso", "Subida Ecuador 95",
        "Dos copas de helado al 30%", "Dos copas de helado artesanal de tres sabores",
        "Helados artesanales con frutas de la zona.", 30, 6900, "1234567", 15, 22, 6, 4.5,
        -33.0470, -71.6150, "foto_postre"),
    LocalEntity(105, "Osteria Recreo", "Italiana", "Viña del Mar", "Av. Borgoño 14200, Recreo",
        "Lasaña de la casa con 25% dto.", "Lasaña bolognesa, pan de ajo y copa de vino",
        "Cocina italiana casera con vista al mar.", 25, 16900, "1234567", 17, 23, 8, 4.4,
        -33.0300, -71.5850, "foto_pizza"),
    LocalEntity(106, "Cocinería del Mercado", "Chilena", "Valparaíso", "Mercado Puerto, local 12",
        "Cazuela + sopaipillas al 30%", "Cazuela de vacuno, sopaipillas y jugo natural",
        "Cocina chilena casera en el barrio Puerto.", 30, 8900, "1234567", 12, 19, 8, 4.4,
        -33.0380, -71.6230, "foto_mesa"),
    LocalEntity(107, "Completos Argentina", "Rápida", "Valparaíso", "Av. Argentina 560",
        "Dos completos + bebidas al 30%", "Dos completos italianos y dos bebidas",
        "Completos y churrascos de la avenida.", 30, 6900, "1234567", 12, 23, 6, 4.2,
        -33.0505, -71.6030, "foto_burger"),
    LocalEntity(108, "Café Subida Ecuador", "Café", "Valparaíso", "Subida Ecuador 210",
        "Once para 2 con 35% dto.", "Dos cafés, sándwich de ave palta y kuchen",
        "Café de barrio con once a la chilena.", 35, 9900, "1234567", 15, 21, 4, 4.7,
        -33.0455, -71.6160, "foto_terraza"),
    // Promociones extra del Local de demostración (una pausada, para su historial).
    LocalEntity(109, "La Trattoria Ascensor", "Italiana", "Valparaíso", "Pasaje Gálvez 45, Cerro Concepción",
        "Tiramisú + café al 40%", "Tiramisú de la casa y café de grano",
        "Pasta hecha cada mañana junto al ascensor Concepción.", 40, 7900, "12345", 16, 19, 6, 4.5,
        -33.0421, -71.6285, "foto_postre", vendedorEmail = LOCAL_DEMO_EMAIL),
    LocalEntity(110, "La Trattoria Ascensor", "Italiana", "Valparaíso", "Pasaje Gálvez 45, Cerro Concepción",
        "Lasaña del lunes al 35%", "Lasaña de la casa y bebida",
        "Pasta hecha cada mañana junto al ascensor Concepción.", 35, 12900, "1", 15, 18, 6, 4.5,
        -33.0421, -71.6285, "foto_pasta", vendedorEmail = LOCAL_DEMO_EMAIL, activa = false),
    // Promoción PROGRAMADA: 2x1 todos los viernes durante un mes desde que se instala la app.
    // id alto: los ids bajos pueden estar usados por promociones que publicaron los locales.
    LocalEntity(900, "La Trattoria Ascensor", "Italiana", "Valparaíso", "Pasaje Gálvez 45, Cerro Concepción",
        "2x1 en pizzas los viernes", "Dos pizzas medianas a elección, pagas una",
        "Pasta hecha cada mañana junto al ascensor Concepción.", 50, 11900, "5", 19, 23, 8, 4.5,
        -33.0421, -71.6285, "foto_pizza", vendedorEmail = LOCAL_DEMO_EMAIL,
        oferta = Oferta.DOS_POR_UNO, hasta = cl.dsy1105.cocinacapital.utils.inicioDelDia(30))
)

// Local de DEMOSTRACIÓN (como si el Admin ya lo hubiera creado) para mostrar la vista del restaurante.
const val LOCAL_DEMO_EMAIL = "trattoria@correo.cl"
const val LOCAL_DEMO_PASSWORD_PRUEBA = "trattoria123"
val RESTAURANTE_DEMO = cl.dsy1105.cocinacapital.data.room.RestauranteEntity(
    LOCAL_DEMO_EMAIL, "La Trattoria Ascensor", "Valparaíso", "Pasaje Gálvez 45, Cerro Concepción",
    "Pasta hecha cada mañana junto al ascensor Concepción.", "foto_pasta"
)

// Fotos disponibles para que el Vendedor ilustre su promoción.
val FOTOS_DISPONIBLES = listOf(
    "foto_mariscos", "foto_pasta", "foto_sushi", "foto_cafe", "foto_parrilla", "foto_bowl",
    "foto_postre", "foto_pizza", "foto_paella", "foto_burger", "foto_autor", "foto_brunch",
    "foto_camarones", "foto_ensalada"
)

val CATEGORIAS_BASE = listOf(
    "Mariscos", "Italiana", "Sushi", "Café", "Carnes", "Vegana", "Postres", "Rápida", "Chilena"
)

// Cuenta Admin de PRUEBA, sembrada al instalar. Es la única que puede crear
// cuentas de Local (evita perfiles de restaurante falsos). Cambiar antes de producción.
const val ADMIN_EMAIL = "admin@correo.cl"
const val ADMIN_PASSWORD_PRUEBA = "admin1234"

object Rol {
    const val COMPRADOR = "Comprador"
    const val VENDEDOR = "Vendedor"
    const val ADMIN = "Admin"
}
