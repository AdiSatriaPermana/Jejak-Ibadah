package lat.pam.jejakibadah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lat.pam.jejakibadah.ui.theme.*

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
        color = if (isDark) DarkCard.copy(alpha = 0.96f) else Color.White.copy(alpha = 0.96f),
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