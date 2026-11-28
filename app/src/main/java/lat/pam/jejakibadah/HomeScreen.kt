package lat.pam.jejakibadah

import androidx.compose.ui.graphics.luminance
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay
import lat.pam.jejakibadah.ui.theme.*

data class PrayerSlot(
    val name: String,
    val time: String,
    val isDone: Boolean = false,
    val isNext: Boolean = false
)

enum class IbadahCategory {
    SALAT, DZIKIR, TILAWAH
}

data class DailyIbadahItem(
    val id: Int,
    val prayerKey: String? = null,
    val title: String,
    val subtitle: String,
    val streakDays: Int,
    val isCompleted: Boolean,
    val statusLabel: String,
    val category: IbadahCategory,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun HomeScreen(
    isDarkTheme: Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f,
    onNavigateTab: (Int) -> Unit = {},
    onOpenSettings: () -> Unit = {}
) {
    // Tanggal Masehi hari ini otomatis
    val todayDateText = remember {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))
        "12 Jumadil Awal 1448 H • ${LocalDate.now().format(formatter)}"
    }

    // Hitung mundur berdetak setiap 1 detik di Hero Card
    var remainingSeconds by remember { mutableIntStateOf(5050) } // 01:24:10
    LaunchedEffect(Unit) {
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
        }
    }
    val formattedCountdown = remember(remainingSeconds) {
        val h = remainingSeconds / 3600
        val m = (remainingSeconds % 3600) / 60
        val s = remainingSeconds % 60
        String.format(Locale.US, "-%02d:%02d:%02d", h, m, s)
    }

    var ibadahList by remember {
        mutableStateOf(
            listOf(
                DailyIbadahItem(1, "Subuh", "Salat Subuh", "04:08 WIB • Berjamaah", 14, true, "Tepat Waktu", IbadahCategory.SALAT, Icons.Outlined.WbTwilight, EmeraldLight),
                DailyIbadahItem(2, "Zuhur", "Salat Zuhur", "11:44 WIB • Tepat Waktu", 12, true, "Tepat Waktu", IbadahCategory.SALAT, Icons.Outlined.WbSunny, AmberLight),
                DailyIbadahItem(3, "Asar", "Salat Asar", "15:12 WIB • Menanti", 9, false, "Belum", IbadahCategory.SALAT, Icons.Outlined.Schedule, BrandBlue),
                DailyIbadahItem(4, null, "Dzikir Pagi", "Al-Ma'tsurat Selesai", 7, true, "Selesai", IbadahCategory.DZIKIR, Icons.Outlined.AutoAwesome, Color(0xFF0D9488)),
                DailyIbadahItem(5, null, "Tilawah Al-Qur'an", "Target 1 Juz (Juz 14)", 4, false, "Lanjutkan", IbadahCategory.TILAWAH, Icons.Outlined.MenuBook, Color(0xFF4F46E5))
            )
        )
    }

    // Sinkronisasi otomatis antara checklist salat & strip 5 waktu di atas
    val completedPrayers = ibadahList
        .filter { it.prayerKey != null && it.isCompleted }
        .mapNotNull { it.prayerKey }
        .toSet()

    val baseSchedule = listOf(
        "Subuh" to "04:08",
        "Zuhur" to "11:44",
        "Asar" to "15:12",
        "Magrib" to "17:52",
        "Isya" to "19:04"
    )
    val nextUnfinishedPrayer = baseSchedule.firstOrNull { it.first !in completedPrayers }?.first

    val prayerSlots = baseSchedule.map { (name, time) ->
        PrayerSlot(
            name = name,
            time = time,
            isDone = name in completedPrayers,
            isNext = name == nextUnfinishedPrayer
        )
    }

    // State Dialog Interaktif untuk Pencatatan Detail
    var selectedSalatForDialog by remember { mutableStateOf<DailyIbadahItem?>(null) }
    var isBerjamaahChecked by remember { mutableStateOf(true) }
    var tilawahDialogItem by remember { mutableStateOf<DailyIbadahItem?>(null) }
    var tilawahProgressInput by remember { mutableStateOf("Juz 14 • Halaman 270") }

    val completedCount = ibadahList.count { it.isCompleted }

    val haptic = LocalHapticFeedback.current
    var lastCompleted by remember { mutableIntStateOf(completedCount) }
    LaunchedEffect(completedCount) {
        if (completedCount > lastCompleted) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
        lastCompleted = completedCount
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 12.dp,
                bottom = BottomNavSafePadding + 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Bar
            item {
                DashboardTopBar(
                    city = "Bandung, ID",
                    dateText = todayDateText,
                    isDark = isDarkTheme,
                    onSettingsClick = onOpenSettings
                )
            }

            // 2. Hero Card (Menuju Salat Berikutnya + Pintasan Kiblat)
            item {
                val nextSlot = prayerSlots.firstOrNull { it.isNext }
                NextPrayerHeroCard(
                    isDark = isDarkTheme,
                    nextPrayerName = nextSlot?.name ?: "Subuh",
                    nextPrayerTime = nextSlot?.time ?: "04:08",
                    countdownText = formattedCountdown,
                    allDoneToday = nextSlot == null,
                    onOpenQibla = { onNavigateTab(3) }
                )
            }

            // 3. Prayer Strip (Imsak, Duha & 5 Waktu Salat tersinkron)
            item {
                PrayerTimesStrip(
                    prayers = prayerSlots,
                    isDark = isDarkTheme,
                    onPrayerClick = { name ->
                        ibadahList.firstOrNull { it.prayerKey == name }?.let { salat ->
                            isBerjamaahChecked = salat.subtitle.contains("Berjamaah", ignoreCase = true)
                            selectedSalatForDialog = salat
                        }
                    }
                )
            }

            // 4. Header Section Ibadah Hari Ini
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (isDarkTheme) BrandCyan else BrandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "IBADAH HARI INI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (isDarkTheme) BrandBlue.copy(alpha = 0.2f) else BrandBlue.copy(alpha = 0.1f),
                        border = BorderStroke(
                            1.dp,
                            if (isDarkTheme) BrandCyan.copy(alpha = 0.3f) else BrandBlue.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "$completedCount/${ibadahList.size} Selesai",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDarkTheme) BrandCyan else BrandBlue,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 5. List Checklist Ibadah Harian
            items(ibadahList, key = { it.id }) { item ->
                IbadahChecklistCard(
                    item = item,
                    isDark = isDarkTheme,
                    onCardClick = {
                        when (item.category) {
                            IbadahCategory.SALAT -> {
                                isBerjamaahChecked = item.subtitle.contains("Berjamaah", ignoreCase = true)
                                selectedSalatForDialog = item
                            }
                            IbadahCategory.TILAWAH -> {
                                tilawahProgressInput = item.subtitle
                                tilawahDialogItem = item
                            }
                            IbadahCategory.DZIKIR -> {
                                ibadahList = ibadahList.map {
                                    if (it.id == item.id) {
                                        val nextDone = !it.isCompleted
                                        it.copy(
                                            isCompleted = nextDone,
                                            statusLabel = if (nextDone) "Selesai" else "Belum",
                                            subtitle = if (nextDone) "Al-Ma'tsurat Selesai" else "Bacaan Dzikir Pagi",
                                            streakDays = if (nextDone) it.streakDays + 1 else (it.streakDays - 1).coerceAtLeast(0)
                                        )
                                    } else it
                                }
                            }
                        }
                    }
                )
            }
        }

        // 6. Floating Pill Bottom Navigation Bar
        AppBottomNavbar(
            selectedIndex = 0,
            isDark = isDarkTheme,
            onSelectTab = onNavigateTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Dialog Pencatatan Salat (Tepat Waktu / Terlambat / Berjamaah)
        selectedSalatForDialog?.let { salat ->
            val timePrefix = salat.subtitle.substringBefore("•").trim()
            AlertDialog(
                onDismissRequest = { selectedSalatForDialog = null },
                title = {
                    Text(
                        text = "Catat ${salat.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Pilih waktu pelaksanaan salat:",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isBerjamaahChecked = !isBerjamaahChecked }
                        ) {
                            Checkbox(
                                checked = isBerjamaahChecked,
                                onCheckedChange = { isBerjamaahChecked = it },
                                colors = CheckboxDefaults.colors(checkedColor = BrandBlue)
                            )
                            Text(
                                text = "Salat Berjamaah",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val suffix = if (isBerjamaahChecked) "Berjamaah" else "Tepat Waktu"
                        ibadahList = ibadahList.map {
                            if (it.id == salat.id) {
                                it.copy(
                                    isCompleted = true,
                                    statusLabel = "Tepat Waktu",
                                    subtitle = "$timePrefix • $suffix",
                                    streakDays = if (!it.isCompleted) it.streakDays + 1 else it.streakDays
                                )
                            } else it
                        }
                        selectedSalatForDialog = null
                    }) {
                        Text("Tepat Waktu", color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Row {
                        TextButton(onClick = {
                            ibadahList = ibadahList.map {
                                if (it.id == salat.id) {
                                    it.copy(
                                        isCompleted = true,
                                        statusLabel = "Terlambat",
                                        subtitle = "$timePrefix • Terlambat",
                                        streakDays = if (!it.isCompleted) it.streakDays + 1 else it.streakDays
                                    )
                                } else it
                            }
                            selectedSalatForDialog = null
                        }) {
                            Text("Terlambat")
                        }
                        if (salat.isCompleted) {
                            TextButton(onClick = {
                                ibadahList = ibadahList.map {
                                    if (it.id == salat.id) {
                                        it.copy(
                                            isCompleted = false,
                                            statusLabel = "Belum",
                                            subtitle = "$timePrefix • Menanti",
                                            streakDays = (it.streakDays - 1).coerceAtLeast(0)
                                        )
                                    } else it
                                }
                                selectedSalatForDialog = null
                            }) {
                                Text("Reset", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            )
        }

        // Dialog Progres Tilawah Al-Qur'an
        tilawahDialogItem?.let { tilawah ->
            AlertDialog(
                onDismissRequest = { tilawahDialogItem = null },
                title = {
                    Text(
                        text = "Progres Tilawah Hari Ini",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Catat batas bacaan terakhir atau tandai target hari ini selesai:",
                            fontSize = 13.sp
                        )
                        OutlinedTextField(
                            value = tilawahProgressInput,
                            onValueChange = { tilawahProgressInput = it },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        ibadahList = ibadahList.map {
                            if (it.id == tilawah.id) {
                                it.copy(
                                    isCompleted = true,
                                    statusLabel = "Selesai",
                                    subtitle = tilawahProgressInput,
                                    streakDays = if (!it.isCompleted) it.streakDays + 1 else it.streakDays
                                )
                            } else it
                        }
                        tilawahDialogItem = null
                    }) {
                        Text("Tandai Selesai", color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        ibadahList = ibadahList.map {
                            if (it.id == tilawah.id) {
                                it.copy(subtitle = tilawahProgressInput)
                            } else it
                        }
                        tilawahDialogItem = null
                    }) {
                        Text("Simpan Progres")
                    }
                }
            )
        }
    }
}

@Composable
private fun DashboardTopBar(
    city: String,
    dateText: String,
    isDark: Boolean,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Lokasi",
                    tint = if (isDark) BrandCyan else BrandBlue,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = city,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) BrandCyan else BrandBlue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dateText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Surface(
            onClick = onSettingsClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.size(38.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Pengaturan",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun NextPrayerHeroCard(
    isDark: Boolean,
    nextPrayerName: String,
    nextPrayerTime: String,
    countdownText: String,
    allDoneToday: Boolean,
    onOpenQibla: () -> Unit
) {
    val heroProgress by animateFloatAsState(
        targetValue = if (allDoneToday) 1f else 0.68f,
        label = "heroProgress"
    )
    val gradientColors = if (isDark) {
        listOf(BrandBlue, Color(0xFF004F8B))
    } else {
        listOf(BrandBlue, BrandDarkBlue)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 12.dp else 10.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = BrandBlue.copy(alpha = 0.35f),
                spotColor = BrandBlue.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(gradientColors))
            .border(
                width = 1.dp,
                color = if (isDark) BrandCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF34D399))
                    )
                    Text(
                        text = if (allDoneToday) "ALHAMDULILLAH • TUNTAS" else "MENUJU ${nextPrayerName.uppercase()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.28f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (allDoneToday) "Selesai" else countdownText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = nextPrayerTime,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 36.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WIB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Kemenag RI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.92f)
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onOpenQibla() }
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Buka Kompas Kiblat",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Arah 295° NW",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.28f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(if (allDoneToday) 1f else 0.68f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF67E8F9) else Color.White)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Zuhur 11:44",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = if (allDoneToday) "100% terlewati" else "68% terlewati",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PrayerTimesStrip(
    prayers: List<PrayerSlot>,
    isDark: Boolean,
    onPrayerClick: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.NightsStay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Imsak: ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "03:58",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.WbSunny,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Duha: ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "05:52",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                prayers.forEach { slot ->
                    val slotBg = when {
                        slot.isNext -> BrandBlue
                        isDark -> DarkSubCard
                        else -> Color(0xFFF8FAFC)
                    }
                    val slotBorder = when {
                        slot.isNext -> BrandCyan.copy(alpha = 0.4f)
                        slot.isDone -> MaterialTheme.colorScheme.outline
                        else -> Color.Transparent
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(13.dp))
                            .background(slotBg)
                            .clickable { onPrayerClick(slot.name) }
                            .border(1.dp, slotBorder, RoundedCornerShape(13.dp))
                            .padding(vertical = 8.dp, horizontal = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = slot.name,
                            fontSize = 10.sp,
                            fontWeight = if (slot.isNext) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (slot.isNext) Color(0xFFDBEAFE) else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            softWrap = false
                        )
                        Text(
                            text = slot.time,
                            fontSize = 11.sp,
                            fontWeight = if (slot.isNext) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (slot.isNext) Color.White else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            softWrap = false
                        )
                        when {
                            slot.isDone -> Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selesai",
                                tint = if (isDark) EmeraldDark else EmeraldLight,
                                modifier = Modifier.size(14.dp)
                            )
                            slot.isNext -> Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Berikutnya",
                                tint = Color(0xFFDBEAFE),
                                modifier = Modifier.size(14.dp)
                            )
                            else -> Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "Menanti",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IbadahChecklistCard(
    item: DailyIbadahItem,
    isDark: Boolean,
    onCardClick: () -> Unit
) {
    Surface(
        onClick = onCardClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(item.iconTint.copy(alpha = if (isDark) 0.18f else 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = item.subtitle,
                        fontSize = 11.sp,
                        color = if (!item.isCompleted && item.category == IbadahCategory.SALAT) {
                            if (isDark) BrandCyan else BrandBlue
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) AmberBgDark.copy(alpha = 0.7f) else AmberBgLight)
                        .border(
                            1.dp,
                            if (isDark) AmberDark.copy(alpha = 0.25f) else AmberLight.copy(alpha = 0.2f),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "🔥 ${item.streakDays} hr",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) AmberDark else AmberLight,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when {
                    item.isCompleted -> {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) EmeraldBgDark.copy(alpha = 0.75f) else EmeraldBgLight)
                                .border(
                                    1.dp,
                                    if (isDark) EmeraldDark.copy(alpha = 0.4f) else EmeraldLight.copy(alpha = 0.3f),
                                    CircleShape
                                )
                                .padding(horizontal = 11.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "✓ ${item.statusLabel}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) EmeraldDark else EmeraldLight,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                    item.category == IbadahCategory.TILAWAH -> {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.background)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Lanjutkan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) BrandBlue.copy(alpha = 0.16f) else BrandBlue.copy(alpha = 0.08f))
                                .border(1.dp, BrandBlue.copy(alpha = 0.35f), CircleShape)
                                .padding(horizontal = 11.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Catat Ibadah",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) BrandCyan else BrandBlue,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }
    }
}