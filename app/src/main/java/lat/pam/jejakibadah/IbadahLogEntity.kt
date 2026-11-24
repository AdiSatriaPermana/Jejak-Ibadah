package lat.pam.jejakibadah.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ibadah_log",
    foreignKeys = [
        ForeignKey(
            entity = IbadahTypeEntity::class,
            parentColumns = ["id"],
            childColumns = ["typeId"],
            onDelete = ForeignKey.CASCADE // Jika jenis ibadah dihapus, log-nya ikut terhapus
        )
    ],
    indices = [
        Index(value = ["typeId", "tanggal"], unique = true),
        Index(value = ["tanggal"]) // Mempercepat query heatmap bulanan
    ]
)
data class IbadahLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val typeId: Int,
    val tanggal: String,       // Format "yyyy-MM-dd" (dihitung mulai Subuh)
    val status: String,        // "SELESAI", "TERLAMBAT", "JEDA", "BELUM"
    val jumlah: Int? = null,   // Untuk tilawah (misal: 2 halaman)
    val waktuCatat: Long = System.currentTimeMillis()
)