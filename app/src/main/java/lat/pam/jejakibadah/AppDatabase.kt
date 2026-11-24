package lat.pam.jejakibadah.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lat.pam.jejakibadah.data.local.dao.JejakIbadahDao
import lat.pam.jejakibadah.data.local.entity.*

@Database(
    entities = [
        IbadahTypeEntity::class,
        IbadahLogEntity::class,
        PrayerSettingEntity::class,
        DzikirEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun jejakIbadahDao(): JejakIbadahDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jejak_ibadah_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    isiDataAwal(database.jejakIbadahDao())
                }
            }
        }

        suspend fun isiDataAwal(dao: JejakIbadahDao) {
            // 1. Seed Jenis Ibadah Bawaan
            val defaultIbadah = listOf(
                IbadahTypeEntity(kode = "SALAT_SUBUH", nama = "Salat Subuh", kategori = "SALAT", urutan = 1),
                IbadahTypeEntity(kode = "DZIKIR_PAGI", nama = "Dzikir Pagi", kategori = "DZIKIR", urutan = 2),
                IbadahTypeEntity(kode = "SALAT_ZUHUR", nama = "Salat Zuhur", kategori = "SALAT", urutan = 3),
                IbadahTypeEntity(kode = "SALAT_ASAR", nama = "Salat Asar", kategori = "SALAT", urutan = 4),
                IbadahTypeEntity(kode = "DZIKIR_PETANG", nama = "Dzikir Petang", kategori = "DZIKIR", urutan = 5),
                IbadahTypeEntity(kode = "SALAT_MAGRIB", nama = "Salat Magrib", kategori = "SALAT", urutan = 6),
                IbadahTypeEntity(kode = "SALAT_ISYA", nama = "Salat Isya", kategori = "SALAT", urutan = 7),
                IbadahTypeEntity(kode = "TILAWAH", nama = "Tilawah Harian", kategori = "TILAWAH", targetHarian = 2, satuan = "halaman", urutan = 8)
            )
            dao.insertAllIbadahTypes(defaultIbadah)

            // 2. Seed Pengaturan 5 Waktu Salat
            val waktuSalat = listOf("SUBUH", "ZUHUR", "ASAR", "MAGRIB", "ISYA")
            waktuSalat.forEach { waktu ->
                dao.upsertPrayerSetting(
                    PrayerSettingEntity(
                        waktu = waktu,
                        suara = if (waktu == "SUBUH") "azan_subuh" else "azan_umum"
                    )
                )
            }
        }
    }
}