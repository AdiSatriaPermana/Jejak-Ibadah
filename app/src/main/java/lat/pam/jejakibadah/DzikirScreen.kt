package lat.pam.jejakibadah

import android.content.Intent
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

data class DzikirTemplateItem(
    val id: Int,
    val category: String,
    val title: String,
    val repeatCount: Int,
    val repeatBadge: String,
    val transliteration: String,
    val translation: String,
    val reference: String,
    val isBookmarked: Boolean = false
)

@Composable
fun DzikirScreen(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    onNavigateTab: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // Mode Segmented Switcher: 0 = Bacaan Dzikir, 1 = Tasbih Digital
    var selectedMode by remember { mutableIntStateOf(0) }

    // Kategori Sub-tabs
    val categories = listOf("Pagi", "Petang", "Setelah Salat", "Doa Harian")
    var selectedCategory by remember { mutableStateOf("Pagi") }

    // Skala Ukuran Huruf (Aa button)
    var isLargeText by remember { mutableStateOf(false) }
    val bodyFontSize = if (isLargeText) 15.sp else 13.sp

    // Data Template Bacaan Dzikir
    var dzikirTemplates by remember {
        mutableStateOf(
            listOf(
                DzikirTemplateItem(
                    id = 1,
                    category = "Pagi",
                    title = "Sayyidul Istighfar",
                    repeatCount = 1,
                    repeatBadge = "Dibaca 1x",
                    transliteration = "\"Allahumma anta rabbi la ilaha illa anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika ma-statha'tu...\"",
                    translation = "\"Ya Allah, Engkau adalah Tuhanku, tidak ada tuhan selain Engkau. Engkaulah yang menciptakanku dan aku adalah hamba-Mu...\"",
                    reference = "HR. Bukhari no. 6306"
                ),
                DzikirTemplateItem(
                    id = 2,
                    category = "Pagi",
                    title = "Dzikir Perlindungan Pagi",
                    repeatCount = 3,
                    repeatBadge = "Dibaca 3x",
                    transliteration = "\"Bismillahilladzi la yadhurru ma'asmihi syai'un fil ardhi wa la fis sama'i wa huwas sami'ul 'alim.\"",
                    translation = "\"Dengan menyebut nama Allah yang dengan sebab nama-Nya tidak ada sesuatu pun di bumi maupun di langit yang dapat membahayakan.\"",
                    reference = "HR. Abu Dawud & Tirmidzi"
                ),
                DzikirTemplateItem(
                    id = 3,
                    category = "Petang",
                    title = "Dzikir Petang Ketenteraman",
                    repeatCount = 1,
                    repeatBadge = "Dibaca 1x",
                    transliteration = "\"Amsaina wa amsal mulku lillah, walhamdulillah, la ilaha illallahu wahdahu la syarika lah...\"",
                    translation = "\"Kami telah memasuki waktu petang dan kerajaan hanya milik Allah, segala puji bagi Allah.\"",
                    reference = "HR. Muslim no. 2723"
                ),
                DzikirTemplateItem(
                    id = 4,
                    category = "Setelah Salat",
                    title = "Tasbih, Tahmid & Takbir",
                    repeatCount = 33,
                    repeatBadge = "Dibaca 33x",
                    transliteration = "\"Subhanallah (33x), Alhamdulillah (33x), Allahu Akbar (33x).\"",
                    translation = "\"Maha Suci Allah, Segala Puji bagi Allah, Allah Maha Besar.\"",
                    reference = "HR. Muslim no. 597"
                ),
                DzikirTemplateItem(
                    id = 5,
                    category = "Doa Harian",
                    title = "Doa Kebaikan Dunia & Akhirat",
                    repeatCount = 1,
                    repeatBadge = "Dibaca 1x",
                    transliteration = "\"Rabbana atina fid-dunya hasanah wa fil-akhirati hasanah wa qina 'adzaban-nar.\"",
                    translation = "\"Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat, serta lindungilah kami dari siksa neraka.\"",
                    reference = "QS. Al-Baqarah: 201"
                )
            )
        )
    }

    // State Tasbih Digital
    var tasbihTitle by remember { mutableStateOf("Subhanallah wa bihamdihi") }
    var tasbihCount by remember { mutableIntStateOf(24) }
    var tasbihTarget by remember { mutableIntStateOf(33) } // 33, 99, atau 0 (∞)
    var hapticEnabled by remember { mutableStateOf(true) }
    var savedMessage by remember { mutableStateOf(false) }

    val filteredTemplates = dzikirTemplates.filter { it.category == selectedCategory }

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
                bottom = BottomNavSafePadding + 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Mobile Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                        Text(
                            text = "Dzikir & Tasbih",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Lafalkan dengan penuh ketenteraman hati",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tombol Aa (Ubah Ukuran Teks)
                        Surface(
                            onClick = { isLargeText = !isLargeText },
                            shape = CircleShape,
                            color = if (isLargeText) BrandBlue else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Aa",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isLargeText) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Tombol Toggle Haptic
                        Surface(
                            onClick = { hapticEnabled = !hapticEnabled },
                            shape = CircleShape,
                            color = if (hapticEnabled) BrandBlue.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (hapticEnabled) BrandBlue.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "≈",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hapticEnabled) (if (isDarkTheme) BrandCyan else BrandBlue) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // 2. Segmented Mode Pill Switcher (Bacaan Dzikir vs Tasbih Digital)
            item {
                Surface(
                    shape = CircleShape,
                    color = if (isDarkTheme) DarkCard else Color(0xFFEEF2F6),
                    border = if (isDarkTheme) BorderStroke(1.dp, DarkBorder) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        val modes = listOf("Bacaan Dzikir", "Tasbih Digital")
                        modes.forEachIndexed { index, title ->
                            val active = selectedMode == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            active && isDarkTheme -> BrandBlue
                                            active -> Color.White
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { selectedMode = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                    color = when {
                                        active && isDarkTheme -> Color.White
                                        active -> BrandBlue
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }

            // 3. Sub-tabs Category Filter Pills (Muncul pada mode Bacaan Dzikir)
            if (selectedMode == 0) {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = selectedCategory == category
                            Surface(
                                onClick = { selectedCategory = category },
                                shape = CircleShape,
                                color = if (isSelected) BrandBlue else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // 4. Kartu Bacaan Dzikir Template
                items(filteredTemplates, key = { it.id }) { item ->
                    DzikirTemplateCard(
                        item = item,
                        bodyFontSize = bodyFontSize,
                        isDark = isDarkTheme,
                        onPracticeTasbih = {
                            tasbihTitle = item.title
                            tasbihTarget = if (item.repeatCount > 1) item.repeatCount else 33
                            tasbihCount = 0
                            savedMessage = false
                            selectedMode = 1
                        },
                        onShare = {
                            val shareText = "${item.title} (${item.repeatBadge})\n\n${item.transliteration}\n\nArtinya: ${item.translation}\n(${item.reference})\n\n— Jejak Ibadah"
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Bagikan Dzikir"))
                        },
                        onToggleBookmark = {
                            dzikirTemplates = dzikirTemplates.map {
                                if (it.id == item.id) it.copy(isBookmarked = !it.isBookmarked) else it
                            }
                        }
                    )
                }
            }

            // 5. Kartu Tasbih Digital
            item {
                DigitalTasbihCard(
                    title = tasbihTitle,
                    count = tasbihCount,
                    target = tasbihTarget,
                    isDark = isDarkTheme,
                    hapticEnabled = hapticEnabled,
                    savedMessage = savedMessage,
                    onIncrement = {
                        if (hapticEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                        savedMessage = false
                        tasbihCount = if (tasbihTarget > 0 && tasbihCount >= tasbihTarget) {
                            if (hapticEnabled) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                            1
                        } else {
                            tasbihCount + 1
                        }
                    },
                    onReset = {
                        tasbihCount = 0
                        savedMessage = false
                    },
                    onSelectTarget = { newTarget ->
                        tasbihTarget = newTarget
                        if (newTarget > 0 && tasbihCount > newTarget) {
                            tasbihCount = 0
                        }
                    },
                    onToggleHaptic = { hapticEnabled = !hapticEnabled },
                    onSaveLog = { savedMessage = true }
                )
            }
        }

        // 6. Floating Pill Navbar Konsisten (Index 2 = Dzikir aktif)
        AppBottomNavbar(
            selectedIndex = 2,
            isDark = isDarkTheme,
            onSelectTab = onNavigateTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun DzikirTemplateCard(
    item: DzikirTemplateItem,
    bodyFontSize: TextUnit,
    isDark: Boolean,
    onPracticeTasbih: () -> Unit,
    onShare: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Row: Title + Repeat Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isDark) BrandCyan else BrandBlue)
                    )
                    Text(
                        text = item.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDark) BrandBlue.copy(alpha = 0.22f) else Color(0xFFF0F9FF))
                        .border(
                            1.dp,
                            if (isDark) BrandCyan.copy(alpha = 0.35f) else Color(0xFFBAE6FD),
                            CircleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = item.repeatBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) BrandCyan else BrandBlue,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Template Lafaz Latin
            Text(
                text = item.transliteration,
                fontSize = bodyFontSize,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                lineHeight = (bodyFontSize.value + 7).sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Terjemahan / Makna Template
            Text(
                text = item.translation,
                fontSize = (bodyFontSize.value - 1).sp,
                lineHeight = (bodyFontSize.value + 5).sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Reference & Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.reference,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallCircleAction(
                        icon = Icons.Default.PlayArrow,
                        contentDesc = "Hitung di Tasbih",
                        isDark = isDark,
                        onClick = onPracticeTasbih
                    )
                    SmallCircleAction(
                        icon = Icons.Default.Share,
                        contentDesc = "Bagikan",
                        isDark = isDark,
                        onClick = onShare
                    )
                    SmallCircleAction(
                        icon = if (item.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDesc = "Simpan",
                        isDark = isDark,
                        tint = if (item.isBookmarked) BrandBlue else null,
                        onClick = onToggleBookmark
                    )
                }
            }
        }
    }
}

@Composable
private fun SmallCircleAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDesc: String,
    isDark: Boolean,
    tint: Color? = null,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (isDark) DarkSubCard else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        modifier = Modifier.size(30.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = tint ?: MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun DigitalTasbihCard(
    title: String,
    count: Int,
    target: Int,
    isDark: Boolean,
    hapticEnabled: Boolean,
    savedMessage: Boolean,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onSelectTarget: (Int) -> Unit,
    onToggleHaptic: () -> Unit,
    onSaveLog: () -> Unit
) {
    val progress = when {
        target <= 0 -> (count % 100) / 100f
        else -> (count.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    }

    val ringActiveColor = if (isDark) BrandCyan else BrandBlue
    val ringTrackColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header + Target Pill Selector (33, 99, ∞)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .border(2.5.dp, ringActiveColor, CircleShape)
                    )
                    Text(
                        text = "Tasbih Digital",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Target Selector Pills
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDark) DarkCanvas else Color(0xFFF1F5F9))
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val targets = listOf(Pair(33, "33"), Pair(99, "99"), Pair(0, "∞"))
                    targets.forEach { (value, label) ->
                        val active = target == value
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (active) BrandBlue else Color.Transparent)
                                .clickable { onSelectTarget(value) }
                                .padding(horizontal = 9.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Centralized Circular Progress Ring Counter
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(152.dp)
                        .clip(CircleShape)
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                        val strokeWidth = 9.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(
                            (size.width - diameter) / 2f,
                            (size.height - diameter) / 2f
                        )
                        val arcSize = Size(diameter, diameter)

                        drawArc(
                            color = ringTrackColor,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        drawArc(
                            color = ringActiveColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$count",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 40.sp
                        )
                        Text(
                            text = if (target > 0) "/ $target" else "/ ∞",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Ketuk lingkaran di atas untuk menghitung",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (isDark) BrandBlue.copy(alpha = 0.2f) else Color(0xFFF0F9FF))
                        .border(
                            1.dp,
                            if (isDark) BrandCyan.copy(alpha = 0.3f) else Color(0xFFBAE6FD),
                            CircleShape
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (savedMessage) "✓ Tersimpan ke catatan dzikir hari ini"
                        else if (hapticEnabled) "• Getar haptic aktif"
                        else "• Mode senyap (tanpa getar)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) BrandCyan else BrandBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(10.dp))

            // Control Action Buttons (Reset, Haptic, Simpan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onReset() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Reset",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onToggleHaptic() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = if (hapticEnabled) "Haptic Aktif" else "Haptic Nonaktif",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (hapticEnabled) (if (isDark) BrandCyan else BrandBlue) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSaveLog() }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Simpan",
                        tint = if (isDark) EmeraldDark else EmeraldLight,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Simpan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) EmeraldDark else EmeraldLight
                    )
                }
            }
        }
    }
}