package lat.pam.jejakibadah.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import lat.pam.jejakibadah.data.local.entity.*

@Dao
interface JejakIbadahDao {

    // --- 1. JENIS IBADAH ---
    @Query("SELECT * FROM ibadah_type WHERE aktif = 1 ORDER BY urutan ASC")
    fun getActiveIbadahTypes(): Flow<List<IbadahTypeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllIbadahTypes(types: List<IbadahTypeEntity>)

    // --- 2. LOG HARIAN, STREAK & HEATMAP ---
    @Query("SELECT * FROM ibadah_log WHERE tanggal = :tanggal")
    fun getLogsByDate(tanggal: String): Flow<List<IbadahLogEntity>>

    @Query("SELECT * FROM ibadah_log WHERE typeId = :typeId ORDER BY tanggal DESC")
    suspend fun getLogsForStreak(typeId: Int): List<IbadahLogEntity>

    @Query("SELECT * FROM ibadah_log WHERE tanggal LIKE :yearMonth || '%'")
    fun getLogsByMonth(yearMonth: String): Flow<List<IbadahLogEntity>>

    @Upsert
    suspend fun upsertIbadahLog(log: IbadahLogEntity)

    // --- 3. PENGATURAN AZAN ---
    @Query("SELECT * FROM prayer_setting")
    fun getAllPrayerSettings(): Flow<List<PrayerSettingEntity>>

    @Upsert
    suspend fun upsertPrayerSetting(setting: PrayerSettingEntity)

    // --- 4. DZIKIR & DOA ---
    @Query("SELECT * FROM dzikir WHERE kategori = :kategori")
    fun getDzikirByKategori(kategori: String): Flow<List<DzikirEntity>>

    @Query("UPDATE dzikir SET favorit = :isFavorit WHERE id = :id")
    suspend fun updateFavoritDzikir(id: Int, isFavorit: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllDzikir(list: List<DzikirEntity>)

    // --- 5. HAPUS SEMUA DATA ---
    @Query("DELETE FROM ibadah_log")
    suspend fun clearAllLogs()
}