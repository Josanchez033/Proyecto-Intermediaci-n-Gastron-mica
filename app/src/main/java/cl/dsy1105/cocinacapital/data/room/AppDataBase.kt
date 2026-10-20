package cl.dsy1105.cocinacapital.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// @Database le dice a Room qué entidades (tablas) existen y en qué versión
// de esquema estamos. Si en el futuro agregas o cambias una columna, debes
// subir la versión y definir una migración (por ahora no es necesario).
@Database(
    entities = [UsuarioEntity::class, LocalEntity::class,
        PreferenciaEntity::class, CanjeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun localDao(): LocalDao
    abstract fun preferenciaDao(): PreferenciaDao
    abstract fun canjeDao(): CanjeDao

    companion object {
        // Volatile + synchronized: nos aseguramos de crear una ÚNICA instancia
        // de la base de datos en toda la app (patrón Singleton clásico).
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "cocinacapital_db" // nombre del archivo .db en el dispositivo
                ).build().also { INSTANCE = it }
            }
        }
    }
}
