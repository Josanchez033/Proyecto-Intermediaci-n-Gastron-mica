package cl.dsy1105.cocinacapital.data

import cl.dsy1105.cocinacapital.data.room.LocalEntity

// Catálogo FICTICIO de prueba (solo datos inventados, sin información real).
// Se carga una sola vez en Room la primera vez que se abre la app.
val LOCALES_EJEMPLO = listOf(
    LocalEntity(1, "Caleta del Puerto", "Mariscos", "Valparaíso", "2x1 en ceviche y pisco sour", "Mariscos frescos con vista a la bahía.", 50, "Lun–Jue 15:00–19:00", 4.7, -33.0393, -71.6270, "🦐"),
    LocalEntity(2, "La Trattoria Ascensor", "Italiana", "Valparaíso", "30% en pastas artesanales", "Pasta fresca hecha cada mañana en Cerro Alegre.", 30, "Mar–Jue 12:00–16:00", 4.5, -33.0421, -71.6285, "🍝"),
    LocalEntity(3, "Sushi Marejada", "Sushi", "Viña del Mar", "Roll premium + bebida gratis", "Fusión nikkei con ingredientes de la región.", 25, "Lun–Mié 18:00–21:00", 4.6, -33.0245, -71.5518, "🍣"),
    LocalEntity(4, "Café Brisa Celeste", "Café", "Viña del Mar", "Combo desayuno a 40% menos", "Café de especialidad y pastelería de autor.", 40, "Todos los días 08:00–11:00", 4.8, -33.0153, -71.5500, "☕"),
    LocalEntity(5, "Fuego & Parrilla", "Carnes", "Concón", "Parrillada para 2 con 35% dto.", "Cortes premium a las brasas frente al mar.", 35, "Lun–Jue 13:00–17:00", 4.4, -32.9306, -71.5190, "🥩"),
    LocalEntity(6, "Verde Raíz", "Vegano", "Quilpué", "Bowl + jugo natural al 20%", "Cocina vegana de temporada con productos locales.", 20, "Lun–Vie 12:00–15:00", 4.3, -33.0475, -71.4425, "🥗"),
    LocalEntity(7, "Dulce Costa", "Postres", "Valparaíso", "Segundo postre gratis", "Pastelería chilena reinventada.", 50, "Mar–Jue 16:00–19:00", 4.6, -33.0460, -71.6190, "🍰"),
    LocalEntity(8, "Pizzería Cumbre", "Italiana", "Villa Alemana", "Pizza familiar con 28% dto.", "Masa madre y horno de piedra.", 28, "Lun–Mié 19:00–22:00", 4.2, -33.0422, -71.3736, "🍕")
)
