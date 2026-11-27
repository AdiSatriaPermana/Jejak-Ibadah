package lat.pam.jejakibadah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

@Composable
fun SettingsScreen(
    selectedThemeMode: Int = 2,
    onThemeModeChange: (Int) -> Unit = {},
    onOpenLogin: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val isDark = when (selectedThemeMode) {
        0 -> false
        1 -> true
        else -> isSystemInDarkTheme()
    }

    // State Interaktif Pengaturan
    var selectedCity by remember { mutableStateOf("Bandung, Jawa Barat") }
    var gpsAuto by remember { mutableStateOf(true) }
    var calculationMethod by remember { mutableStateOf("Kemenag RI") }
    var ihtiyatiMinutes by remember { mutableIntStateOf(2) }
    var azanMode by remember { mutableStateOf("Azan Penuh") }
    var hijriCorrection by remember { mutableIntStateOf(0) }
    var sunnahFastingReminder by remember { mutableStateOf(true) }

    // State Dialog Konfirmasi & Info
    var showResetDialog by remember { mutableStateOf(false) }
    var showAntiDelayDialog by remember { mutableStateOf(false) }
    var statusToastMessage by remember { mutableStateOf<String?>(null) }

    val activeAccent = if (isDark) BrandCyan else BrandBlue
    val iconContainerBg = if (isDark) Color(0xFF0C2340) else Color(0xFFEFF6FF)
    val iconContainerBorder = if (isDark) BrandCyan.copy(alpha = 0.28f) else Color(0xFFDBEAFE)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            onClick = onNavigateBack,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Kembali",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = "Pengaturan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Badge Versi v1.0.0 (Sesuai PRD v1.0)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDark) Color(0xFF0C2340) else Color(0xFFEFF6FF))
                            .border(
                                1.dp,
                                if (isDark) BrandCyan.copy(alpha = 0.35f) else Color(0xFFDBEAFE),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "v1.0.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = activeAccent,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Kartu Pengaturan Awal (Membuka ulang OnboardingScreen / LoginScreen)
            item {
                Surface(
                    onClick = onOpenLogin,
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Person,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Column {
                                Text(
                                    text = "Preferensi Awal & Target Ibadah",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Atur ulang bahasa, lokasi, & ibadah sunnah",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(BrandBlue)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Atur",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            // Banner feedback singkat jika ada aksi yang ditekan
            if (statusToastMessage != null) {
                item {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) EmeraldBgDark else EmeraldBgLight,
                        border = BorderStroke(
                            1.dp,
                            if (isDark) EmeraldDark.copy(alpha = 0.4f) else EmeraldLight.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = statusToastMessage ?: "",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) EmeraldDark else EmeraldLight,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Tutup",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = activeAccent,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier
                                    .clickable { statusToastMessage = null }
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================
            // SECTION 1: JADWAL & AZAN
            // =========================================================
            item {
                SettingsSectionBlock(title = "JADWAL & AZAN") {
                    SettingsNavigationRow(
                        icon = Icons.Default.LocationOn,
                        title = "Kota & Koordinat",
                        subtitle = selectedCity,
                        badgeText = if (gpsAuto) "Otomatis (GPS)" else "Manual",
                        isPillBadge = true,
                        isDark = isDark,
                        iconBg = iconContainerBg,
                        iconBorder = iconContainerBorder,
                        onClick = {
                            gpsAuto = !gpsAuto
                            selectedCity = if (gpsAuto) "Bandung, Jawa Barat" else "Jakarta Pusat, DKI"
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    SettingsNavigationRow(
                        icon = Icons.Default.Settings,
                        title = "Metode Perhitungan",
                        subtitle = null,
                        badgeText = calculationMethod,
                        isPillBadge = false,
                        isDark = isDark,
                        iconBg = iconContainerBg,
                        iconBorder = iconContainerBorder,
                        onClick = {
                            calculationMethod = when (calculationMethod) {
                                "Kemenag RI" -> "MWL (Rabithah)"
                                "MWL (Rabithah)" -> "Umm al-Qura"
                                else -> "Kemenag RI"
                            }
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    // Koreksi Ihtiyati (+2 Menit stepper)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.CheckCircle,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Text(
                                text = "Koreksi Ihtiyati",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDark) DarkCanvas else Color(0xFFF1F5F9))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "-",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { if (ihtiyatiMinutes > 0) ihtiyatiMinutes-- }
                                    .padding(horizontal = 4.dp)
                            )
                            Text(
                                text = "+$ihtiyatiMinutes Menit",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = "+",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { if (ihtiyatiMinutes < 5) ihtiyatiMinutes++ }
                                    .padding(horizontal = 4.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    SettingsNavigationRow(
                        icon = Icons.Default.Notifications,
                        title = "Suara & Mode Azan",
                        subtitle = "Subuh, Zuhur, Asar, Magrib, Isya",
                        badgeText = azanMode,
                        isPillBadge = false,
                        isDark = isDark,
                        iconBg = iconContainerBg,
                        iconBorder = iconContainerBorder,
                        onClick = {
                            azanMode = when (azanMode) {
                                "Azan Penuh" -> "Takbir Singkat"
                                "Takbir Singkat" -> "Getar Saja"
                                else -> "Azan Penuh"
                            }
                        }
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    // Panduan Anti-Delay Notifikasi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) Color(0xFF281C0E).copy(alpha = 0.45f) else Color(0xFFFFFBEB))
                            .clickable { showAntiDelayDialog = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Warning,
                                tint = if (isDark) AmberDark else AmberLight,
                                bg = if (isDark) Color(0xFF451A03).copy(alpha = 0.65f) else Color(0xFFFEF3C7),
                                border = if (isDark) AmberDark.copy(alpha = 0.35f) else Color(0xFFFDE68A)
                            )
                            Column {
                                Text(
                                    text = "Panduan Anti-Delay Notifikasi",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Xiaomi, Oppo, Vivo, Samsung",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF451A03) else Color(0xFFFEF3C7))
                                    .border(
                                        1.dp,
                                        if (isDark) AmberDark.copy(alpha = 0.4f) else Color(0xFFFCD34D),
                                        CircleShape
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Info Penting",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) AmberDark else Color(0xFFB45309),
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================
            // SECTION 2: TAMPILAN & KALENDER
            // =========================================================
            item {
                SettingsSectionBlock(title = "TAMPILAN & KALENDER") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Star,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Text(
                                text = "Tema Aplikasi",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) DarkCanvas else Color(0xFFF1F5F9))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val themeOptions = listOf("Terang", "Gelap", "Sistem")
                            themeOptions.forEachIndexed { index, label ->
                                val active = selectedThemeMode == index
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(9.dp))
                                        .background(if (active) BrandBlue else Color.Transparent)
                                        .clickable { onThemeModeChange(index) }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                        color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    // Koreksi Tanggal Hijriah (-1 / 0 Hari / +1)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.DateRange,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Text(
                                text = "Koreksi Tanggal Hijriah",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(if (isDark) DarkCanvas else Color(0xFFF1F5F9))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(9.dp))
                                .padding(3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "-1",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { hijriCorrection = -1 }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isDark) DarkSubCard else Color.White)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${if (hijriCorrection > 0) "+$hijriCorrection" else hijriCorrection} Hari",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            Text(
                                text = "+1",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clickable { hijriCorrection = 1 }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    // Pengingat Puasa Sunnah
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Notifications,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Column {
                                Text(
                                    text = "Pengingat Puasa Sunnah",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Senin-Kamis & Ayyamul Bidh",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Switch(
                            checked = sunnahFastingReminder,
                            onCheckedChange = { sunnahFastingReminder = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrandBlue
                            )
                        )
                    }
                }
            }

            // =========================================================
            // SECTION 3: DATA & PRIVASI (100% OFFLINE)
            // =========================================================
            item {
                SettingsSectionBlock(title = "DATA & PRIVASI (100% OFFLINE)") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                statusToastMessage = "✓ Riwayat ibadah siap diekspor ke format CSV lokal."
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Share,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Text(
                                text = "Ekspor Riwayat Ibadah",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isDark) DarkCanvas else Color(0xFFF1F5F9))
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "CSV / Excel",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                statusToastMessage = "Jejak Ibadah v1.0.0 — Aplikasi catatan ibadah 100% offline."
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Info,
                                tint = BrandBlue,
                                bg = iconContainerBg,
                                border = iconContainerBorder
                            )
                            Column {
                                Text(
                                    text = "Tentang Jejak Ibadah",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Versi 1.0.0 • Bebas Iklan & Pelacak",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            SettingsLeadingIcon(
                                icon = Icons.Default.Delete,
                                tint = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626),
                                bg = if (isDark) Color(0xFF450A0A).copy(alpha = 0.65f) else Color(0xFFFEF2F2),
                                border = if (isDark) Color(0xFF991B1B).copy(alpha = 0.5f) else Color(0xFFFECACA)
                            )
                            Column {
                                Text(
                                    text = "Hapus Semua Data",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) Color(0xFFF87171) else Color(0xFFDC2626)
                                )
                                Text(
                                    text = "Reset total semua histori ibadah lokal",
                                    fontSize = 10.sp,
                                    color = if (isDark) Color(0xFFFCA5A5).copy(alpha = 0.7f) else Color(0xFFEF4444)
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFF87171) else Color(0xFFEF4444),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // =========================================================
            // FOOTER OFFLINE GUARANTEE
            // =========================================================
            item {
                Text(
                    text = "🔒 100% Offline & Privat • Data tersimpan aman di perangkat Anda",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }

    if (showAntiDelayDialog) {
        AlertDialog(
            onDismissRequest = { showAntiDelayDialog = false },
            title = { Text("Panduan Anti-Delay Notifikasi", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Agar pengingat adzan tidak tertunda pada perangkat Xiaomi, Oppo, Vivo, atau Samsung:\n\n" +
                            "1. Buka Pengaturan Baterai aplikasi Jejak Ibadah.\n" +
                            "2. Pilih 'Tidak Dibatasi' (Unrestricted).\n" +
                            "3. Aktifkan izin 'Mulai Otomatis' (Autostart) dan 'Alarm & Pengingat'."
                )
            },
            confirmButton = {
                TextButton(onClick = { showAntiDelayDialog = false }) {
                    Text("Mengerti", color = BrandBlue, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Hapus Semua Data?", fontWeight = FontWeight.Bold) },
            text = {
                Text("Seluruh catatan salat harian, statistik streak, dan progres dzikir lokal akan dihapus permanen.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        statusToastMessage = "✓ Semua data ibadah lokal berhasil direset."
                    }
                ) {
                    Text("Hapus Permanen", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionBlock(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsLeadingIcon(
    icon: ImageVector,
    tint: Color,
    bg: Color,
    border: Color
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(9.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    badgeText: String,
    isPillBadge: Boolean,
    isDark: Boolean,
    iconBg: Color,
    iconBorder: Color,
    onClick: () -> Unit
) {
    val activeAccent = if (isDark) BrandCyan else BrandBlue

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        ) {
            SettingsLeadingIcon(
                icon = icon,
                tint = BrandBlue,
                bg = iconBg,
                border = iconBorder
            )
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (isPillBadge) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF0C2340) else Color(0xFFEFF6FF))
                        .border(
                            1.dp,
                            if (isDark) BrandCyan.copy(alpha = 0.35f) else Color(0xFFDBEAFE),
                            CircleShape
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = activeAccent,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            } else {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = activeAccent,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}