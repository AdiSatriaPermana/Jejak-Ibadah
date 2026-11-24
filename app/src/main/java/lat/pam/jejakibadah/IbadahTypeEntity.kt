package lat.pam.jejakibadah.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ibadah_type",
    indices = [Index(value = ["kode"], unique = true)]
)
data class IbadahTypeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val kode: String,          // Contoh: "SALAT_SUBUH", "DZIKIR_PAGI"
    val nama: String,          // Contoh: "Salat Subuh"
    val kategori: String,      // "SALAT", "SUNNAH", "DZIKIR", "TILAWAH"
    val targetHarian: Int = 1, // Contoh: 1 kali, atau 2 halaman
    val satuan: String? = null,// "kali", "halaman"
    val aktif: Boolean = true,
    val urutan: Int = 0
)