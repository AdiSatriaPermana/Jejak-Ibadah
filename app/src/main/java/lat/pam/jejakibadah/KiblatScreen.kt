package lat.pam.jejakibadah

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

data class HijriDayCellData(
    val dayName: String,
    val gregorianDay: Int,
    val hijriDay: String,
    val isAyyamulBidh: Boolean = false,
    val isHolidayHighlight: Boolean = false,
    val isWeekend: Boolean = false
)

@Composable
fun KiblatScreen(
    onNavigateTab: (Int) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val haptic = LocalHapticFeedback.current

    // Segmented Mode: 0 = Semua (Kiblat + Kalender), 1 = Fokus Kalender Hijriah
    var selectedTabMode by remember { mutableIntStateOf(0) }

    // State Kompas & Kalibrasi
    var qiblaDegree by remember { mutableFloatStateOf(295f) }
    var hapticEnabled by remember { mutableStateOf(true) }
    var isCalibrating by remember { mutableStateOf(false) }

    // Animasi rotasi jarum kompas (295° = -65° dari arah Utara atas)
    val needleAngle by animateFloatAsState(
        targetValue = if (isCalibrating) -25f else (qiblaDegree - 360f),
        animationSpec = tween(durationMillis = 600),
        finishedListener = {
            if (isCalibrating) {
                isCalibrating = false
                if (hapticEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
        },
        label = "qibla_needle_rotation"
    )

    val weekHijriCells = remember {
        listOf(
            HijriDayCellData("Min", 22, "12 H"),
            HijriDayCellData("Sen", 23, "13 H", isAyyamulBidh = true),
            HijriDayCellData("Sel", 24, "14 H", isAyyamulBidh = true),
            HijriDayCellData("Rab", 25, "15 H", isAyyamulBidh = true),
            HijriDayCellData("Kam", 26, "16 H"),
            HijriDayCellData("Jum", 27, "17 H", isHolidayHighlight = true),
            HijriDayCellData("Sab", 28, "18 H", isWeekend = true)
        )
    }

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
                bottom = 104.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Header Bar & Accuracy Badge
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "AKURASI SENSOR",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = if (isDark) BrandCyan else BrandBlue
                            )
                            Text(
                                text = "Arah Kiblat & Kalender",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        // Badge Status Akurasi Sensor
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isDark) DarkCard else EmeraldBgLight)
                                .border(
                                    1.dp,
                                    if (isDark) DarkBorder else EmeraldLight.copy(alpha = 0.35f),
                                    CircleShape
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isDark) EmeraldDark else EmeraldLight)
                                )
                                Text(
                                    text = if (isCalibrating) "Mengkalibrasi..." else "Kompas Presisi",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) BrandCyan else EmeraldLight
                                )
                            }
                        }
                    }

                    Text(
                        text = "Presisi arah salat & penanggalan hijriah",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Top Segmented Switcher Pill (Arah Kiblat vs Kalender Hijriah)
                    Surface(
                        shape = CircleShape,
                        color = if (isDark) DarkCard else Color(0xFFEEF2F6),
                        border = if (isDark) BorderStroke(1.dp, DarkBorder) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            val tabs = listOf(
                                Pair("Arah Kiblat", Icons.Outlined.Place),
                                Pair("Kalender Hijriah", Icons.Default.DateRange)
                            )
                            tabs.forEachIndexed { index, (label, icon) ->
                                val active = selectedTabMode == index
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(CircleShape)
                                        .background(if (active) BrandBlue else Color.Transparent)
                                        .clickable { selectedTabMode = index }
                                        .padding(vertical = 8.dp, horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = label,
                                        tint = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Primary Card: Kompas Kiblat Minimalis (Tampil pada mode Arah Kiblat)
            if (selectedTabMode == 0) {
                item {
                    QiblaCompassCard(
                        degree = qiblaDegree.toInt(),
                        needleAngle = needleAngle,
                        isDark = isDark,
                        hapticEnabled = hapticEnabled,
                        onToggleHaptic = {
                            hapticEnabled = !hapticEnabled
                            if (hapticEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        onRecalibrate = {
                            isCalibrating = true
                            qiblaDegree = 295f
                        }
                    )
                }
            }

            // 3. Lower Card: Kalender Hijriah & Agenda Sunnah
            item {
                HijriCalendarCard(
                    cells = weekHijriCells,
                    isDark = isDark
                )
            }
        }

        // 4. Floating Pill Navbar Konsisten (Index 3 = Kiblat aktif)
        AppBottomNavbar(
            selectedIndex = 3,
            isDark = isDark,
            onSelectTab = onNavigateTab,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun QiblaCompassCard(
    degree: Int,
    needleAngle: Float,
    isDark: Boolean,
    hapticEnabled: Boolean,
    onToggleHaptic: () -> Unit,
    onRecalibrate: () -> Unit
) {
    val activeAccent = if (isDark) BrandCyan else BrandBlue
    val outerRingColor = if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val innerRingColor = if (isDark) Color(0xFF334155).copy(alpha = 0.5f) else Color(0xFFCBD5E1).copy(alpha = 0.7f)

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dial Kompas 220dp
            Box(
                modifier = Modifier.size(224.dp),
                contentAlignment = Alignment.Center
            ) {
                // Canvas Lingkaran Kalibrasi & Tick Marks
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val outerRadius = size.minDimension / 2f - 8.dp.toPx()
                    val innerRadius = outerRadius - 16.dp.toPx()

                    // Lingkaran luar putus-putus
                    drawCircle(
                        color = outerRingColor,
                        radius = outerRadius,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 12f), 0f)
                        )
                    )

                    // Lingkaran dalam tipis
                    drawCircle(
                        color = innerRingColor,
                        radius = innerRadius,
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 4 Garis Mata Angin (U, T, S, B)
                    for (i in 0 until 4) {
                        val angleRad = Math.toRadians((i * 90 - 90).toDouble())
                        val start = Offset(
                            center.x + (outerRadius - 10.dp.toPx()) * cos(angleRad).toFloat(),
                            center.y + (outerRadius - 10.dp.toPx()) * sin(angleRad).toFloat()
                        )
                        val end = Offset(
                            center.x + outerRadius * cos(angleRad).toFloat(),
                            center.y + outerRadius * sin(angleRad).toFloat()
                        )
                        drawLine(
                            color = if (i == 0) activeAccent else innerRingColor,
                            start = start,
                            end = end,
                            strokeWidth = if (i == 0) 2.5.dp.toPx() else 2.dp.toPx()
                        )
                    }
                }

                // Label Mata Angin
                Text(
                    text = "U",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeAccent,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 14.dp)
                )
                Text(
                    text = "T",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
                )
                Text(
                    text = "S",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp)
                )
                Text(
                    text = "B",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp)
                )

                // Penanda Target Ka'bah di Barat Laut (~295°)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 26.dp, top = 26.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(9.dp))
                            .background(BrandBlue)
                            .border(2.dp, activeAccent.copy(alpha = 0.4f), RoundedCornerShape(9.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ka'bah",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = "KA'BAH",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = activeAccent,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Jarum Kompas Berputar ke 295° NW
                Canvas(
                    modifier = Modifier
                        .size(156.dp)
                        .rotate(needleAngle)
                ) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Ujung Utara / Kiblat (Biru / Cyan)
                    val northPath = Path().apply {
                        moveTo(cx, 8.dp.toPx())
                        lineTo(cx - 7.dp.toPx(), cy)
                        lineTo(cx + 7.dp.toPx(), cy)
                        close()
                    }
                    drawPath(path = northPath, color = activeAccent)

                    // Ujung Selatan (Abu-abu netral)
                    val southPath = Path().apply {
                        moveTo(cx, size.height - 12.dp.toPx())
                        lineTo(cx - 6.dp.toPx(), cy)
                        lineTo(cx + 6.dp.toPx(), cy)
                        close()
                    }
                    drawPath(
                        path = southPath,
                        color = if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
                    )
                }

                // Titik Tengah Kompas
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkCanvas else Color(0xFFF1F5F9))
                        .border(2.dp, activeAccent.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(BrandBlue)
                    )
                }
            }

            // Derajat & Arah Utama
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "$degree°",
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 36.sp
                )
                Text(
                    text = "NW",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = activeAccent,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Text(
                text = "Arah Kiblat: Barat Laut",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Badge Status Alignment
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isDark) EmeraldBgDark.copy(alpha = 0.65f) else EmeraldBgLight)
                    .border(
                        1.dp,
                        if (isDark) EmeraldDark.copy(alpha = 0.4f) else EmeraldLight.copy(alpha = 0.35f),
                        CircleShape
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isDark) EmeraldDark else EmeraldLight,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Tepat Menghadap Kiblat",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) EmeraldDark else EmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Jarak & Koordinat Bandung
            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Jarak ke Ka'bah: ",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "7.912 km",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = "Bandung, Jawa Barat (6.9175° S, 107.6191° E)",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol Haptic & Kalibrasi Ulang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = onToggleHaptic,
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    color = if (isDark) DarkSubCard else Color.White,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "≈",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeAccent
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hapticEnabled) "Getar Aktif" else "Getar Mati",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    onClick = onRecalibrate,
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    color = if (isDark) DarkSubCard else Color.White,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Kalibrasi",
                            tint = BrandBlue,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Kalibrasi Ulang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HijriCalendarCard(
    cells: List<HijriDayCellData>,
    isDark: Boolean
) {
    val activeAccent = if (isDark) BrandCyan else BrandBlue

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Penanggalan Islam
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PENANGGALAN ISLAM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        color = activeAccent
                    )
                    Text(
                        text = "Rajab – Sya'ban 1448 H",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "November 2026",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        onClick = {},
                        shape = CircleShape,
                        color = if (isDark) DarkSubCard else Color(0xFFF1F5F9),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Sebelumnya",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Surface(
                        onClick = {},
                        shape = CircleShape,
                        color = if (isDark) DarkSubCard else Color(0xFFF1F5F9),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Berikutnya",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid 7 Hari Dual-Date (Masehi + Hijriah)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                cells.forEach { cell ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = cell.dayName.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = if (cell.isWeekend) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (cell.isWeekend) activeAccent else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val cellBg = when {
                            cell.isHolidayHighlight -> BrandBlue
                            cell.isAyyamulBidh && isDark -> Color(0xFF1E293B)
                            cell.isAyyamulBidh -> Color(0xFFE0F2FE)
                            isDark -> DarkCanvas
                            else -> Color(0xFFF8FAFC)
                        }

                        val cellBorder = when {
                            cell.isAyyamulBidh && isDark -> BrandCyan.copy(alpha = 0.3f)
                            cell.isAyyamulBidh -> BrandBlue.copy(alpha = 0.25f)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(cellBg)
                                .border(1.dp, cellBorder, RoundedCornerShape(12.dp))
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${cell.gregorianDay}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        cell.isHolidayHighlight -> Color.White
                                        cell.isAyyamulBidh -> activeAccent
                                        else -> MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = cell.hijriDay,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = when {
                                        cell.isHolidayHighlight -> Color(0xFFDBEAFE)
                                        cell.isAyyamulBidh -> activeAccent.copy(alpha = 0.85f)
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daftar Agenda Ibadah & Hari Besar
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AgendaItemRow(
                    emoji = "⭐",
                    title = "Isra Mi'raj 1448 H",
                    subtitle = "27 Nov • 17 Rajab 1448 H",
                    badge = "Libur",
                    isPrimaryBadge = true,
                    isDark = isDark
                )
                AgendaItemRow(
                    emoji = "✨",
                    title = "Puasa Ayyamul Bidh",
                    subtitle = "23–25 Nov • 13, 14, 15 Rajab",
                    badge = "Sunnah",
                    isPrimaryBadge = false,
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun AgendaItemRow(
    emoji: String,
    title: String,
    subtitle: String,
    badge: String,
    isPrimaryBadge: Boolean,
    isDark: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) DarkCanvas else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(BrandBlue.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 12.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        if (isPrimaryBadge) BrandCyan.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline,
                        CircleShape
                    )
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrimaryBadge) (if (isDark) BrandCyan else BrandBlue) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}