package com.educalab.atumanera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.educalab.atumanera.ui.navigation.AppNavGraph
import com.educalab.atumanera.ui.theme.ATuManeraTheme
import com.educalab.atumanera.util.AppPreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as AtuManeraApplication
        val preferences = AppPreferences(this)

        setContent {
            ATuManeraTheme {
                AppNavGraph(repository = app.repository, preferences = preferences)
            }
        }
    }
}
