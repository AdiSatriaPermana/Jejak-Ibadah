package lat.pam.jejakibadah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import lat.pam.jejakibadah.ui.theme.JejakIbadahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by remember { mutableIntStateOf(2) } // 0=Terang, 1=Gelap, 2=Sistem
            var currentTab by remember { mutableIntStateOf(0) }
            var showSettings by remember { mutableStateOf(false) }
            var showLogin by remember { mutableStateOf(true) } // Tampil pertama kali saat buka app

            val useDarkTheme = when (themeMode) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }

            JejakIbadahTheme(darkTheme = useDarkTheme) {
                when {
                    showLogin -> {
                        LoginScreen(
                            isDarkTheme = useDarkTheme,
                            onToggleTheme = {
                                themeMode = if (useDarkTheme) 0 else 1
                            },
                            onLoginSuccess = { showLogin = false },
                            onContinueOffline = { showLogin = false },
                            onNavigateBack = { showLogin = false }
                        )
                    }
                    showSettings -> {
                        SettingsScreen(
                            selectedThemeMode = themeMode,
                            onThemeModeChange = { themeMode = it },
                            onOpenLogin = { showLogin = true },
                            onNavigateBack = { showSettings = false }
                        )
                    }
                    else -> {
                        when (currentTab) {
                            0 -> HomeScreen(
                                onNavigateTab = { currentTab = it },
                                onOpenSettings = { showSettings = true }
                            )
                            1 -> JejakScreen(onNavigateTab = { currentTab = it })
                            2 -> DzikirScreen(onNavigateTab = { currentTab = it })
                            3 -> KiblatScreen(onNavigateTab = { currentTab = it })
                            else -> HomeScreen(
                                onNavigateTab = { currentTab = it },
                                onOpenSettings = { showSettings = true }
                            )
                        }
                    }
                }
            }
        }
    }
}