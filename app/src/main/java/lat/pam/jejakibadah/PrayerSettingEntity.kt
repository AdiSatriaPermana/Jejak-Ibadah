package lat.pam.jejakibadah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prayer_setting")
data class PrayerSettingEntity(
    @PrimaryKey
    val waktu: String,               // "SUBUH", "ZUHUR", "ASAR", "MAGRIB", "ISYA"
    val ihtiyatiMenit: Int = 0,      // Koreksi menit (+/-)
    val suara: String = "azan_umum", // Nama file di res/raw
    val modeNotifikasi: String = "AZAN", // "AZAN", "NADA", "GETAR", "SENYAP"
    val pengingatMenit: Int? = 10,   // Pengingat 10 menit sebelum azan
    val aktif: Boolean = true
)