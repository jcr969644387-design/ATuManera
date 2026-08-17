package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.ui.components.ScreenTopBar
import com.educalab.atumanera.ui.theme.SurfaceCream
import com.educalab.atumanera.util.AppPreferences

@Composable
fun SettingsScreen(preferences: AppPreferences, onBack: () -> Unit) {
    var sound by remember { mutableStateOf(preferences.soundEnabled) }
    var haptics by remember { mutableStateOf(preferences.hapticsEnabled) }

    Surface(color = SurfaceCream, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTopBar(title = "Ajustes", onBack = onBack)
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    SettingSwitch("Sonido", "Efectos de construcción y logros", sound) {
                        sound = it
                        preferences.soundEnabled = it
                    }
                    androidx.compose.material3.Divider(modifier = Modifier.padding(vertical = 12.dp))
                    SettingSwitch("Vibración", "Aviso táctil al construir o completar misiones", haptics) {
                        haptics = it
                        preferences.hapticsEnabled = it
                    }
                }
            }
            Text(
                "Tus datos se guardan solo en este dispositivo. A Tu Manera no usa Internet, cuentas ni anuncios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun SettingSwitch(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
