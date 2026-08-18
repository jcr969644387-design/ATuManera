package com.educalab.atumanera

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.educalab.atumanera.ui.navigation.AppNavGraph
import com.educalab.atumanera.ui.theme.ATuManeraTheme
import com.educalab.atumanera.util.AppPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Estilo fijo (no SystemBarStyle.auto): la apariencia de la barra de
        // estado/navegación no debe cambiar según el modo claro/oscuro del
        // teléfono, ya que la app siempre usa su propia paleta clara.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT)
        )
        hideNavigationBar()

        val app = application as AtuManeraApplication
        val preferences = AppPreferences(this)

        setContent {
            ATuManeraTheme {
                AppNavGraph(repository = app.repository, preferences = preferences)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideNavigationBar()
    }

    /**
     * Oculta la barra de navegación del sistema como en un juego: solo
     * reaparece brevemente si el usuario desliza desde el borde inferior
     * (o superior, para notificaciones), sin quedarse fija tapando la app.
     */
    private fun hideNavigationBar() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }
}
