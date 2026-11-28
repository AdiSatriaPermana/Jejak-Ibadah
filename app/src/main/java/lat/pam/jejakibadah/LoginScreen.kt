package lat.pam.jejakibadah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

@Composable
fun LoginScreen(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onToggleTheme: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onContinueOffline: () -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    // State Preferensi Awal (PRD FR-84 - 100% Offline)
    var selectedLanguage by remember { mutableStateOf("ID") }
    var useGps by remember { mutableStateOf(true) }
    var selectedCity by remember { mutableStateOf("Bandung, Jawa Barat") }
    var showCityDialog by remember { mutableStateOf(false) }

    val availableCities = listOf(
        "Bandung, Jawa Barat",
        "Jakarta Pusat, DKI Jakarta",
        "Surabaya, Jawa Timur",
        "Yogyakarta, DIY",
        "Medan, Sumatera Utara",
        "Makassar, Sulawesi Selatan"
    )

    val sunnahOptions = listOf(
        "Dzikir Pagi & Petang",
        "Tilawah Al-Qur'an",
        "Salat Duha",
        "Salat Tahajud",
        "Puasa Senin-Kamis & Ayyamul Bidh"
    )
    var selectedSunnah by remember {
        mutableStateOf(setOf("Dzikir Pagi & Petang", "Tilawah Al-Qur'an"))
    }

    // Palet Adaptif Light (#F8FAFC) & Night Mode (#0B1120)
    val screenBg = if (isDarkTheme) DarkCanvas else LightCanvas
    val cardBg = if (isDarkTheme) DarkCard else Color.White
    val subCardBg = if (isDarkTheme) DarkSubCard else Color(0xFFF1F5F9)
    val borderColor = if (isDarkTheme) DarkBorder else LightBorder
    val primaryText = if (isDarkTheme) DarkTextPrimary else LightTextPrimary
    val secondaryText = if (isDarkTheme) DarkTextSecondary else LightTextSecondary
    val accentBlue = if (isDarkTheme) BrandCyan else BrandBlue

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = screenBg,
        topBar = {
            // 1. Top App Bar (Badge 100% Lokal & Aman + Toggle Night Mode)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(cardBg)
                        .border(1.dp, borderColor, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = accentBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "100% LOKAL & TANPA AKUN",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = secondaryText,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Surface(
                    onClick = onToggleTheme,
                    shape = CircleShape,
                    color = cardBg,
                    border = BorderStroke(1.dp, borderColor),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = if (isDarkTheme) "☀️" else "🌙",
                            fontSize = 15.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Tombol Mulai Beribadah aman di atas gesture/navigation bar HP
            Surface(
                color = screenBg,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 22.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onContinueOffline,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Mulai Beribadah",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "Semua data ibadah tersimpan aman di perangkat lokal.",
                        fontSize = 11.sp,
                        color = secondaryText,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2. Hero Brand Identity
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(BrandBlue, BrandCyan)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Logo Jejak Ibadah",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = 4.dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(cardBg)
                            .border(1.dp, borderColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = accentBlue,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Jejak Ibadah",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
                Text(
                    text = "TEMAN SETIA IBADAH HARIAN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = accentBlue,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "Atur bahasa, lokasi jadwal salat, dan amalan harianmu sebelum memulai.",
                    fontSize = 12.sp,
                    color = secondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .widthIn(max = 300.dp)
                )
            }

            // 3. Langkah 1: Bahasa Aplikasi
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "1. BAHASA APLIKASI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = accentBlue
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .background(subCardBg)
                            .padding(4.dp)
                    ) {
                        val languages = listOf("ID" to "Bahasa Indonesia", "EN" to "English")
                        languages.forEach { (code, label) ->
                            val active = selectedLanguage == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(if (active) BrandBlue else Color.Transparent)
                                    .clickable { selectedLanguage = code }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                    color = if (active) Color.White else secondaryText,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            // 4. Langkah 2: Lokasi & Metode Jadwal Salat (Default Kemenag RI)
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "2. LOKASI & JADWAL SALAT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = accentBlue,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(accentBlue.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Kemenag RI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentBlue,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Lokasi Otomatis (GPS)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryText
                            )
                            Text(
                                text = "Untuk akurasi waktu salat & arah kiblat",
                                fontSize = 11.sp,
                                color = secondaryText
                            )
                        }
                        Switch(
                            checked = useGps,
                            onCheckedChange = { useGps = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = BrandBlue)
                        )
                    }

                    Surface(
                        onClick = { showCityDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = subCardBg,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = accentBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (useGps) "Kota Cadangan (Offline)" else "Kota Terpilih",
                                    fontSize = 10.sp,
                                    color = secondaryText
                                )
                                Text(
                                    text = selectedCity,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Ubah",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentBlue
                            )
                        }
                    }
                }
            }

            // 5. Langkah 3: Pilih Target Ibadah Awal
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = cardBg,
                border = BorderStroke(1.dp, borderColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "3. IBADAH HARIAN YANG DILACAK",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = accentBlue
                    )
                    Text(
                        text = "Salat 5 Waktu otomatis aktif. Pilih amalan sunnah yang ingin dilacak:",
                        fontSize = 11.sp,
                        color = secondaryText
                    )

                    sunnahOptions.forEach { habit ->
                        val isChecked = habit in selectedSunnah
                        Surface(
                            onClick = {
                                selectedSunnah = if (isChecked) {
                                    selectedSunnah - habit
                                } else {
                                    selectedSunnah + habit
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isChecked) accentBlue.copy(alpha = 0.1f) else subCardBg,
                            border = BorderStroke(
                                1.dp,
                                if (isChecked) accentBlue else borderColor
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = habit,
                                    fontSize = 13.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = primaryText,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(if (isChecked) BrandBlue else Color.Transparent)
                                        .border(
                                            1.5.dp,
                                            if (isChecked) BrandBlue else secondaryText.copy(alpha = 0.4f),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialog Pilih Kota
        if (showCityDialog) {
            AlertDialog(
                onDismissRequest = { showCityDialog = false },
                title = {
                    Text(
                        text = "Pilih Kota",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        availableCities.forEach { city ->
                            TextButton(
                                onClick = {
                                    selectedCity = city
                                    showCityDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = city,
                                    color = if (city == selectedCity) accentBlue else primaryText,
                                    fontWeight = if (city == selectedCity) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showCityDialog = false }) {
                        Text("Tutup")
                    }
                }
            )
        }
    }
}