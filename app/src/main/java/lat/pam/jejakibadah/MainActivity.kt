package lat.pam.jejakibadah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import lat.pam.jejakibadah.ui.theme.JejakIbadahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 0 = Terang, 1 = Gelap, 2 = Sistem (nantinya dibaca dari DataStore UserPreferences)
            var themeMode by rememberSaveable { mutableIntStateOf(2) }
            var currentTab by rememberSaveable { mutableIntStateOf(0) }
            var showSettings by rememberSaveable { mutableStateOf(false) }
            var isOnboardingCompleted by rememberSaveable { mutableStateOf(false) }
            var userAccountEmail by rememberSaveable { mutableStateOf<String?>(null) }

            val useDarkTheme = when (themeMode) {
                0 -> false
                1 -> true
                else -> isSystemInDarkTheme()
            }

            // System bar (status bar & nav bar) mengikuti tema APLIKASI, bukan tema sistem
            DisposableEffect(useDarkTheme) {
                this@MainActivity.enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { useDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                    ) { useDarkTheme }
                )
                onDispose { }
            }

            // Menyimpan state tiap tab (posisi scroll, rememberSaveable) saat pindah tab
            val saveableStateHolder = rememberSaveableStateHolder()

            // Menangani tombol Back sistem agar tidak langsung keluar aplikasi
            BackHandler(enabled = showSettings || currentTab != 0) {
                if (showSettings) {
                    showSettings = false
                } else if (currentTab != 0) {
                    currentTab = 0
                }
            }

            JejakIbadahTheme(darkTheme = useDarkTheme) {
                val screenRouteKey = when {
                    !isOnboardingCompleted -> "onboarding"
                    showSettings -> "settings"
                    else -> "tab_$currentTab"
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AnimatedContent(
                        targetState = screenRouteKey,
                        transitionSpec = {
                            if (targetState == "settings" || initialState == "settings") {
                                (fadeIn(tween(220)) + slideInVertically(tween(240)) { it / 18 })
                                    .togetherWith(fadeOut(tween(180)) + slideOutVertically(tween(200)) { it / 18 })
                            } else {
                                fadeIn(tween(180)) togetherWith fadeOut(tween(140))
                            }
                        },
                        label = "screen_transition"
                    ) { route ->
                        when (route) {
                            "onboarding" -> {
                                LoginScreen(
                                    isDarkTheme = useDarkTheme,
                                    onToggleTheme = {
                                        themeMode = if (useDarkTheme) 0 else 1
                                    },
                                    onLoginSuccess = {
                                        userAccountEmail = "hamba.allah@domain.id"
                                        isOnboardingCompleted = true
                                    },
                                    onContinueOffline = {
                                        userAccountEmail = null
                                        isOnboardingCompleted = true
                                    },
                                    onNavigateBack = {
                                        isOnboardingCompleted = true
                                    }
                                )
                            }
                            "settings" -> {
                                SettingsScreen(
                                    selectedThemeMode = themeMode,
                                    userEmail = userAccountEmail,
                                    onThemeModeChange = { themeMode = it },
                                    onOpenLogin = {
                                        showSettings = false
                                        isOnboardingCompleted = false
                                    },
                                    onLogout = {
                                        userAccountEmail = null
                                    },
                                    onNavigateBack = { showSettings = false }
                                )
                            }
                            else -> {
                                val tabIndex = route.removePrefix("tab_").toIntOrNull() ?: 0
                                saveableStateHolder.SaveableStateProvider(tabIndex) {
                                    when (tabIndex) {
                                        0 -> HomeScreen(
                                            isDarkTheme = useDarkTheme,
                                            onNavigateTab = { currentTab = it },
                                            onOpenSettings = { showSettings = true }
                                        )
                                        1 -> JejakScreen(
                                            isDarkTheme = useDarkTheme,
                                            onOpenSettings = { showSettings = true },
                                            onNavigateTab = { currentTab = it }
                                        )
                                        2 -> DzikirScreen(
                                            isDarkTheme = useDarkTheme,
                                            onNavigateTab = { currentTab = it }
                                        )
                                        3 -> KiblatScreen(
                                            isDarkTheme = useDarkTheme,
                                            onNavigateTab = { currentTab = it }
                                        )
                                        else -> HomeScreen(
                                            isDarkTheme = useDarkTheme,
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
        }
    }
}