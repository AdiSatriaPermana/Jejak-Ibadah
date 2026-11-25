package lat.pam.jejakibadah

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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
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
import lat.pam.jejakibadah.ui.theme.*

// --- Dummy Data Models untuk Preview & Prototyping ---
data class PrayerSlot(
    val name: String,
    val time: String,
    val isDone: Boolean = false,
    val isNext: Boolean = false
)

data class DailyIbadahItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val streakDays: Int,
    val isCompleted: Boolean,
    val statusLabel: String,
    val icon: ImageVector,
    val iconTint: Color
)

@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit = {},
    onNavigateTab: (Int) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    var selectedTab by remember { mutableIntStateOf(0) }

    // State Dummy Jadwal Salat
    val prayerSlots = remember {
        listOf(
            PrayerSlot("Subuh", "04:08", isDone = true),
            PrayerSlot("Zuhur", "11:44", isDone = true),
            PrayerSlot("Asar", "15:12", isNext = true),
            PrayerSlot("Magrib", "17:52"),
            PrayerSlot("Isya", "19:04")
        )
    }

    // State Dummy Checklist Ibadah (Bisa diklik langsung!)
    var ibadahList by remember {
        mutableStateOf(
            listOf(
                DailyIbadahItem(1, "Salat Subuh", "04:08 WIB • Berjamaah", 14, true, "Tepat Waktu", Icons.Outlined.WbTwilight, EmeraldLight),
                DailyIbadahItem(2, "Salat Zuhur", "11:44 WIB • Tepat Waktu", 12, true, "Tepat Waktu", Icons.Outlined.WbSunny, AmberLight),
                DailyIbadahItem(3, "Salat Asar", "15:12 WIB • Menanti", 9, false, "Belum", Icons.Outlined.Schedule, BrandBlue),
                DailyIbadahItem(4, "Dzikir Pagi", "Al-Ma'tsurat Selesai", 7, true, "Selesai", Icons.Outlined.AutoAwesome, Color(0xFF0D9488)),
                DailyIbadahItem(5, "Tilawah Al-Qur'an", "Target 1 Juz (Juz 14)", 4, false, "Lanjutkan", Icons.Outlined.MenuBook, Color(0xFF4F46E5))
            )
        )
    }

    val completedCount = ibadahList.count { it.isCompleted }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // Konten Utama Scrollable
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 12.dp,
                bottom = 104.dp // Ruang aman agar tidak tertutup floating navbar
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Bar
            item {
                DashboardTopBar(
                    city = "Bandung, ID",
                    dateText = "12 Jumadil Awal 1448 H • 22 Nov 2026",
                    isDark = isDark,
                    onSettingsClick = onOpenSettings
                )
            }

            // 2. Hero Card (Menuju Asar)
            item {
                NextPrayerHeroCard(isDark = isDark)
            }

            // 3. Prayer Strip (Imsak, Duha & 5 Waktu Salat)
            item {
                PrayerTimesStrip(prayers = prayerSlots, isDark = isDark)
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
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = if (isDark) BrandCyan else BrandBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "IBADAH HARI INI",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = if (isDark) BrandBlue.copy(alpha = 0.2f) else BrandBlue.copy(alpha = 0.1f),
                        border = BorderStroke(
                            1.dp,
                            if (isDark) BrandCyan.copy(alpha = 0.3f) else BrandBlue.copy(alpha = 0.2f)
                        )
                    ) {
                        Text(
                            text = "$completedCount/${ibadahList.size} Selesai",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) BrandCyan else BrandBlue,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 5. List Checklist Ibadah Harian
            items(ibadahList, key = { it.id }) { item ->
                IbadahChecklistCard(
                    item = item,
                    isDark = isDark,
                    onToggle = {
                        ibadahList = ibadahList.map {
                            if (it.id == item.id) {
                                val nextDone = !it.isCompleted
                                it.copy(
                                    isCompleted = nextDone,
                                    statusLabel = if (nextDone) "Tepat Waktu" else "Belum",
                                    streakDays = if (nextDone) it.streakDays + 1 else (it.streakDays - 1).coerceAtLeast(0)
                                )
                            } else it
                        }
                    }
                )
            }
        }

        // 6. Floating Pop-up Pill Bottom Navigation Bar
        FloatingPillNavbar(
            selectedIndex = selectedTab,
            isDark = isDark,
            onSelectTab = { index ->
                selectedTab = index
                onNavigateTab(index)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        )
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
        Column {
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
                    color = if (isDark) BrandCyan else BrandBlue
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dateText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun NextPrayerHeroCard(isDark: Boolean) {
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
            // Top Row
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
                        text = "MENUJU ASAR",
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
                        text = "-01:24:10",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Middle Row: Time & Direction
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "15:12",
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
                    Text(
                        text = "Arah 295° NW",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.28f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.68f)
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
                    text = "68% terlewati",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun PrayerTimesStrip(prayers: List<PrayerSlot>, isDark: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Imsak & Duha Auxiliary Row
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

        // 5-Column Prayer Grid
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
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = slot.time,
                            fontSize = 11.sp,
                            fontWeight = if (slot.isNext) FontWeight.ExtraBold else FontWeight.Bold,
                            color = if (slot.isNext) Color.White else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
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
    onToggle: () -> Unit
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Icon + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
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

                Column {
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
                        fontSize = 10.sp,
                        color = if (!item.isCompleted && item.id == 3) {
                            if (isDark) BrandCyan else BrandBlue
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Right: Streak Badge + Action Chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Streak Pill
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
                        color = if (isDark) AmberDark else AmberLight
                    )
                }

                // Status Button
                if (item.isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isDark) EmeraldBgDark.copy(alpha = 0.75f) else EmeraldBgLight)
                            .border(
                                1.dp,
                                if (isDark) EmeraldDark.copy(alpha = 0.4f) else EmeraldLight.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "✓ ${item.statusLabel}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) EmeraldDark else EmeraldLight
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingPillNavbar(
    selectedIndex: Int,
    isDark: Boolean,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        Pair("Beranda", Icons.Filled.Home),
        Pair("Jejak", Icons.Outlined.CalendarMonth),
        Pair("Dzikir", Icons.Outlined.AutoAwesome),
        Pair("Kiblat", Icons.Outlined.Explore)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 16.dp else 12.dp,
                shape = CircleShape,
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.55f else 0.16f),
                spotColor = Color.Black.copy(alpha = if (isDark) 0.55f else 0.16f)
            ),
        shape = CircleShape,
        color = if (isDark) DarkCard.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.96f),
        border = BorderStroke(
            1.dp,
            if (isDark) Color(0xFF334155).copy(alpha = 0.7f) else LightBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, (label, icon) ->
                val isSelected = selectedIndex == index
                val pillBg = if (isSelected) BrandBlue else Color.Transparent
                val contentColor = when {
                    isSelected -> Color.White
                    isDark -> DarkTextSecondary
                    else -> LightTextSecondary
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(pillBg)
                        .clickable { onSelectTab(index) }
                        .padding(
                            horizontal = if (isSelected) 14.dp else 10.dp,
                            vertical = 8.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    if (isSelected) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}