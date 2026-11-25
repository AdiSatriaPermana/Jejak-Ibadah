package lat.pam.jejakibadah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import lat.pam.jejakibadah.ui.theme.JejakIbadahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JejakIbadahTheme {
                var currentTab by remember { mutableIntStateOf(0) }

                when (currentTab) {
                    0 -> HomeScreen(
                        onNavigateTab = { currentTab = it }
                    )
                    1 -> JejakScreen(
                        onNavigateTab = { currentTab = it }
                    )
                    else -> HomeScreen(
                        onNavigateTab = { currentTab = it }
                    )
                }
            }
        }
    }
}