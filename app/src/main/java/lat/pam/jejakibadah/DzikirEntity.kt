package lat.pam.jejakibadah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dzikir")
data class DzikirEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kategori: String,   // "PAGI", "PETANG", "SETELAH_SALAT", "DOA_HARIAN"
    val judul: String,
    val arab: String,
    val latin: String,
    val terjemahan: String,
    val jumlahBaca: Int = 1,
    val sumber: String,     // Wajib dicantumkan sesuai PRD (misal: "Hisnul Muslim")
    val favorit: Boolean = false
)