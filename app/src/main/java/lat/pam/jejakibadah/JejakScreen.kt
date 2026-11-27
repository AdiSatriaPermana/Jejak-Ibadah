package lat.pam.jejakibadah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

// Status harian pada Heatmap
enum class DayHeatmapStatus {
    TEPAT_WAKTU,
    TERLAMBAT,
    FREEZE,
    JEDA,
    BELUM
}

@Composable
fun JejakScreen(
    onOpenSettings: () -> Unit = {},
    onNavigateTab: (Int) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()

    // Filter jenis ibadah
    val worshipFilters = listOf("Semua Salat", "Subuh", "Dzikir Pagi", "Tilawah", "Puasa")
    var selectedFilter by remember { mutableStateOf("Semua Salat") }

    // State Streak & Kuota Freeze
    var currentStreak by remember { mutableIntStateOf(14) }
    val longestStreak = 28
    var freezeQuota by remember { mutableIntStateOf(2) }
    var isFreezeTodayActive by remember { mutableStateOf(false) }
    var isJedaTodayActive by remember { mutableStateOf(false) }

    // Data kalender November 2026 (Tanggal 1..30)
    var novemberStatuses by remember {
        mutableStateOf(
            (1..30).associateWith { day ->
                when (day) {
                    8, 14 -> DayHeatmapStatus.TERLAMBAT
                    in 1..21 -> DayHeatmapStatus.TEPAT_WAKTU
                    else -> DayHeatmapStatus.BELUM
                }
            }
        )
    }

    // Dialog koreksi tanggal (H-1 s.d. H-3 -> tanggal 19, 20, 21) & konfirmasi Freeze
    var editingDay by remember { mutableStateOf<Int?>(null) }
    var showFreezeDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 12.dp,
                bottom = BottomNavSafePadding + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Judul & Tombol Pengaturan
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Jejak Konsistensi",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Pantau kebiasaan ibadah dengan tenang",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        onClick = onOpenSettings,
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
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }
                }
            }

            // 2. Filter Jenis Ibadah (Horizontal Pills)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(worshipFilters) { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            onClick = { selectedFilter = filter },
                            shape = CircleShape,
                            color = if (isSelected) BrandBlue else MaterialTheme.colorScheme.surface,
                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = filter,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // 3. Streak Summary Row (Streak Aktif & Rekor Terpanjang)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Streak Aktif
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Streak Aktif",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) AmberBgDark else AmberBgLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🔥", fontSize = 11.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$currentStreak",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isDark) BrandCyan else BrandBlue
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Hari",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = "Konsisten sejak 8 Nov",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Card 2: Rekor Terpanjang
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rekor Terpanjang",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(BrandBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = BrandBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$longestStreak",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Hari",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            Text(
                                text = "Pernah dicapai Okt 2026",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            // 4. Gentle Action Bar (Perlindungan Ketenangan - Non-judgmental)
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isDark) Color(0xFF101B2E) else Color(0xFFEFF6FF),
                    border = BorderStroke(
                        1.dp,
                        if (isDark) DarkBorder else Color(0xFFDBEAFE)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PERLINDUNGAN KETENANGAN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = "Tanpa Beban Bersalah",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) BrandCyan else BrandBlue,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Tombol Jeda Hari Ini (Netral, tanpa tanya alasan)
                            Surface(
                                onClick = {
                                    isJedaTodayActive = !isJedaTodayActive
                                    if (isJedaTodayActive && isFreezeTodayActive) {
                                        isFreezeTodayActive = false
                                        freezeQuota += 1
                                    }
                                    novemberStatuses = novemberStatuses.toMutableMap().apply {
                                        this[22] = if (isJedaTodayActive) DayHeatmapStatus.JEDA else DayHeatmapStatus.BELUM
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isJedaTodayActive) BrandBlue else MaterialTheme.colorScheme.outline
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "⏸",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isJedaTodayActive) "Jeda Aktif" else "Jeda Hari Ini",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Istirahat tenang",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            // Tombol Streak Freeze (Kuota 2/2)
                            Surface(
                                onClick = {
                                    if (isFreezeTodayActive) {
                                        isFreezeTodayActive = false
                                        freezeQuota += 1
                                        novemberStatuses = novemberStatuses.toMutableMap().apply {
                                            this[22] = DayHeatmapStatus.BELUM
                                        }
                                    } else if (freezeQuota > 0) {
                                        showFreezeDialog = true
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(
                                    1.dp,
                                    if (isFreezeTodayActive) BrandCyan else BrandCyan.copy(alpha = 0.45f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(BrandCyan.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "❄",
                                            fontSize = 13.sp,
                                            color = BrandCyan
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Freeze ($freezeQuota/2)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandCyan,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = if (isFreezeTodayActive) "Terpasang hari ini" else "Pelindung aktif",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 5. Calendar Heatmap Card (November 2026)
            item {
                CalendarHeatmapCard(
                    statuses = novemberStatuses,
                    isDark = isDark,
                    onDayClick = { day ->
                        // BR-06: Koreksi hanya diizinkan untuk H-1 s.d. H-3 (Tanggal 19, 20, 21)
                        if (day in 19..21) {
                            editingDay = day
                        }
                    }
                )
            }
        }

        // 6. Floating Pill Navbar (Padding luar sudah ditangani di dalam AppBottomNavbar)
        AppBottomNavbar(
            selectedIndex = 1,
            isDark = isDark,
            onSelectTab = onNavigateTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Dialog Konfirmasi Pakai Streak Freeze
        if (showFreezeDialog) {
            AlertDialog(
                onDismissRequest = { showFreezeDialog = false },
                title = {
                    Text(
                        text = "Gunakan Streak Freeze?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Streak aktif kamu akan tetap terlindungi hari ini tanpa mengurangi hitungan beruntun. Sisa kuota bulan ini akan menjadi ${freezeQuota - 1}/2.",
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        freezeQuota -= 1
                        isFreezeTodayActive = true
                        isJedaTodayActive = false
                        novemberStatuses = novemberStatuses.toMutableMap().apply {
                            this[22] = DayHeatmapStatus.FREEZE
                        }
                        showFreezeDialog = false
                    }) {
                        Text("Aktifkan Freeze", color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showFreezeDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Dialog Koreksi Data Mundur (H-1 s.d. H-3)
        editingDay?.let { day ->
            AlertDialog(
                onDismissRequest = { editingDay = null },
                title = {
                    Text(
                        text = "Koreksi Tanggal $day Nov 2026",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Pilih status pencatatan ibadah untuk tanggal ini. Nilai streak akan dihitung ulang otomatis dari log.",
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        novemberStatuses = novemberStatuses.toMutableMap().apply {
                            this[day] = DayHeatmapStatus.TEPAT_WAKTU
                        }
                        editingDay = null
                    }) {
                        Text("Tepat Waktu", color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Row {
                        TextButton(onClick = {
                            novemberStatuses = novemberStatuses.toMutableMap().apply {
                                this[day] = DayHeatmapStatus.TERLAMBAT
                            }
                            editingDay = null
                        }) {
                            Text("Terlambat")
                        }
                        TextButton(onClick = {
                            novemberStatuses = novemberStatuses.toMutableMap().apply {
                                this[day] = DayHeatmapStatus.JEDA
                            }
                            editingDay = null
                        }) {
                            Text("Jeda")
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun CalendarHeatmapCard(
    statuses: Map<Int, DayHeatmapStatus>,
    isDark: Boolean,
    onDayClick: (Int) -> Unit
) {
    val daysOfWeek = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Ahd")
    val leadingEmptySlots = 6
    val totalCells = leadingEmptySlots + 30
    val rowsCount = (totalCells + 6) / 7

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bulan & Badge Persentase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "November 2026",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Bulan Sebelumnya",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Bulan Berikutnya",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDark) EmeraldBgDark.copy(alpha = 0.7f) else EmeraldBgLight)
                        .border(
                            1.dp,
                            if (isDark) EmeraldDark.copy(alpha = 0.4f) else EmeraldLight.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "92% Terpenuhi",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) EmeraldDark else EmeraldLight,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nama Hari (Sen - Ahd)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                daysOfWeek.forEach { dayName ->
                    Text(
                        text = dayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid Tanggal Heatmap
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (row in 0 until rowsCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val dayNumber = cellIndex - leadingEmptySlots + 1

                            if (dayNumber in 1..30) {
                                val status = statuses[dayNumber] ?: DayHeatmapStatus.BELUM
                                HeatmapDayCell(
                                    day = dayNumber,
                                    status = status,
                                    isDark = isDark,
                                    isEditable = dayNumber in 19..21,
                                    onClick = { onDayClick(dayNumber) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f).height(34.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Legend Warna Heatmap
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = BrandBlue, label = "Tepat Waktu")
                LegendItem(color = Color(0xFF7CC4FA), label = "Terlambat")
                LegendItem(color = BrandCyan, label = "Freeze", isDashed = true)
                LegendItem(
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    label = "Belum"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Catatan Koreksi H-1 s.d. H-3
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Ketuk tanggal H-1 s.d. H-3 (19–21 Nov) untuk koreksi data.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeatmapDayCell(
    day: Int,
    status: DayHeatmapStatus,
    isDark: Boolean,
    isEditable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when (status) {
        DayHeatmapStatus.TEPAT_WAKTU -> BrandBlue
        DayHeatmapStatus.TERLAMBAT -> Color(0xFF7CC4FA)
        DayHeatmapStatus.FREEZE, DayHeatmapStatus.JEDA ->
            if (isDark) Color(0xFF0C4A6E).copy(alpha = 0.4f) else Color(0xFFF0F9FF)
        DayHeatmapStatus.BELUM ->
            if (isDark) Color(0xFF1E293B).copy(alpha = 0.55f) else Color(0xFFF1F5F9)
    }

    val textColor = when (status) {
        DayHeatmapStatus.TEPAT_WAKTU -> Color.White
        DayHeatmapStatus.TERLAMBAT -> Color(0xFF0F172A)
        DayHeatmapStatus.FREEZE, DayHeatmapStatus.JEDA -> BrandCyan
        DayHeatmapStatus.BELUM ->
            if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
    }

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (isEditable) Modifier.clickable { onClick() } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (status == DayHeatmapStatus.FREEZE || status == DayHeatmapStatus.JEDA) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = BrandCyan,
                    style = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                    ),
                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$day",
                fontSize = if (status == DayHeatmapStatus.FREEZE || status == DayHeatmapStatus.JEDA) 9.sp else 11.sp,
                fontWeight = if (status == DayHeatmapStatus.BELUM) FontWeight.Normal else FontWeight.Bold,
                color = textColor,
                lineHeight = 10.sp
            )
            if (status == DayHeatmapStatus.FREEZE) {
                Text(text = "❄", fontSize = 8.sp, color = BrandCyan, lineHeight = 8.sp)
            } else if (status == DayHeatmapStatus.JEDA) {
                Text(text = "⏸", fontSize = 8.sp, color = BrandCyan, lineHeight = 8.sp)
            }
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String,
    isDashed: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isDashed) color.copy(alpha = 0.2f) else color)
                .then(
                    if (isDashed) Modifier.border(1.dp, color, RoundedCornerShape(3.dp)) else Modifier
                )
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            softWrap = false
        )
    }
}