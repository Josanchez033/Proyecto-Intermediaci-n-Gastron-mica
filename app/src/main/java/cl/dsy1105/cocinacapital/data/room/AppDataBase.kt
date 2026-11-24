package cl.dsy1105.cocinacapital.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// @Database le dice a Room qué entidades (tablas) existen y en qué versión
// de esquema estamos. Si en el futuro agregas o cambias una columna, debes
// subir la versión. En el prototipo se recrea la base (datos ficticios).
@Database(
    entities = [UsuarioEntity::class, LocalEntity::class,
        PreferenciaEntity::class, CanjeEntity::class, RestauranteEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun localDao(): LocalDao
    abstract fun preferenciaDao(): PreferenciaDao
    abstract fun canjeDao(): CanjeDao
    abstract fun restauranteDao(): RestauranteDao

    companion object {
        // Volatile + synchronized: nos aseguramos de crear una ÚNICA instancia
        // de la base de datos en toda la app (patrón Singleton clásico).
        // v2 -> v3: cuentas activables por el Admin y perfil de restaurante.
        // Migración real: así no se borran las cuentas ni los vouchers ya creados.
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN activo INTEGER NOT NULL DEFAULT 1")
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `restaurantes` (`emailDueno` TEXT NOT NULL, `nombre` TEXT NOT NULL, " +
                        "`comuna` TEXT NOT NULL, `direccion` TEXT NOT NULL, `descripcion` TEXT NOT NULL, " +
                        "`foto` TEXT NOT NULL, PRIMARY KEY(`emailDueno`))"
                )
            }
        }

        // v3 -> v4: promociones pausables (historial) con fecha de publicación.
        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE locales ADD COLUMN activa INTEGER NOT NULL DEFAULT 1")
                db.execSQL("ALTER TABLE locales ADD COLUMN creada INTEGER NOT NULL DEFAULT 0")
            }
        }

        // v4 -> v5: datos editables de la cuenta y contraseña temporal entregada por el Admin.
        private val MIGRACION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE usuarios ADD COLUMN nombre TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE usuarios ADD COLUMN foto TEXT")
                db.execSQL("ALTER TABLE usuarios ADD COLUMN claveTemporal INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Promociones programadas: tipo de oferta (2x1, 3x2) y vigencia desde/hasta.
        private val MIGRACION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE locales ADD COLUMN oferta INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE locales ADD COLUMN desde INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE locales ADD COLUMN hasta INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Ofertas propias del Local (combos, menús): su nombre corto.
        private val MIGRACION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE locales ADD COLUMN etiqueta TEXT NOT NULL DEFAULT ''")
            }
        }

        // Precio exacto de las ofertas propias.
        private val MIGRACION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE locales ADD COLUMN precioOferta INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "cocinacapital_db" // nombre del archivo .db en el dispositivo
                ).addMigrations(MIGRACION_2_3, MIGRACION_3_4, MIGRACION_4_5, MIGRACION_5_6, MIGRACION_6_7, MIGRACION_7_8)
                    .fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
