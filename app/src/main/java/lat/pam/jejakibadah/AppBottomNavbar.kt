package lat.pam.jejakibadah

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

// Standar padding bawah untuk LazyColumn/Column di semua layar agar tidak tertutup navbar
val BottomNavSafePadding = 96.dp

@Composable
fun AppBottomNavbar(
    selectedIndex: Int,
    isDark: Boolean,
    onSelectTab: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        Pair("Beranda", Icons.Default.Home),
        Pair("Jejak", Icons.Default.DateRange),
        Pair("Dzikir", Icons.Default.Favorite),
        Pair("Kiblat", Icons.Outlined.Place)
    )
    val haptic = LocalHapticFeedback.current

    // RESPONSIF: BoxWithConstraints agar bar tahu lebar yang tersedia
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            // Latar solid: konten yang di-scroll tidak tembus di balik bar
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Label di semua tab hanya jika layar lebar DAN font tidak diperbesar,
        // supaya label tidak terpotong ("Ki...") saat ukuran font besar
        val fontScale = LocalDensity.current.fontScale
        val showAllLabels = maxWidth >= 480.dp && fontScale <= 1.15f

        Surface(
            modifier = Modifier
                // Di layar lebar bar tidak melebar penuh
                .widthIn(max = 560.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = if (isDark) 16.dp else 12.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = if (isDark) 0.55f else 0.16f),
                    spotColor = Color.Black.copy(alpha = if (isDark) 0.55f else 0.16f)
                ),
            shape = CircleShape,
            color = if (isDark) DarkCard else Color.White,
            border = BorderStroke(
                1.dp,
                if (isDark) DarkBorder else LightBorder
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
                    val showLabel = isSelected || showAllLabels

                    // INTERAKTIF: efek tekan, warna beranimasi, getar halus
                    val interaction = remember { MutableInteractionSource() }
                    val pressed by interaction.collectIsPressedAsState()
                    val scale by animateFloatAsState(
                        targetValue = if (pressed) 0.94f else 1f,
                        label = "tabScale"
                    )
                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) BrandBlue else Color.Transparent,
                        label = "tabBg"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = when {
                            isSelected -> Color.White
                            isDark -> DarkTextSecondary
                            else -> LightTextSecondary
                        },
                        label = "tabContent"
                    )

                    Row(
                        modifier = Modifier
                            .scale(scale)
                            // Area sentuh minimal 48dp
                            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                            .clip(CircleShape)
                            .background(pillBg)
                            // Terbaca sebagai "tab, terpilih" oleh TalkBack
                            .selectable(
                                selected = isSelected,
                                interactionSource = interaction,
                                indication = null,
                                role = Role.Tab,
                                onClick = {
                                    if (!isSelected) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    onSelectTab(index)
                                }
                            )
                            .animateContentSize()
                            .padding(
                                horizontal = if (showLabel) 16.dp else 12.dp,
                                vertical = 9.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            // Label sudah terlihat: jangan dibaca dua kali
                            contentDescription = if (showLabel) null else label,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                        if (showLabel) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = contentColor,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}