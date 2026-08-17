package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.domain.model.MissionStatus
import com.educalab.atumanera.ui.MissionUiItem
import com.educalab.atumanera.ui.MissionsViewModel
import com.educalab.atumanera.ui.components.AnimatedProgressBar
import com.educalab.atumanera.ui.components.ScreenTopBar
import com.educalab.atumanera.ui.components.StatPill
import com.educalab.atumanera.ui.theme.SuccessGreen
import com.educalab.atumanera.ui.theme.SunAmber
import com.educalab.atumanera.ui.theme.SurfaceCream

@Composable
fun MissionsScreen(viewModel: MissionsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()

    Surface(color = SurfaceCream, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTopBar(title = "Misiones", subtitle = "Cada misión ayuda a tu ciudad a crecer", onBack = onBack)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                StatPill("Experiencia", "${state.totalXp} XP", SunAmber, Modifier.weight(1f))
                StatPill("Completadas", "${state.missionsCompleted}/30", SuccessGreen, Modifier.weight(1f))
            }
            Spacer(Modifier.size(8.dp))
            LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                items(state.items, key = { it.mission.id }) { item ->
                    MissionRow(item)
                }
            }
        }
    }
}

@Composable
private fun MissionRow(item: MissionUiItem) {
    val (icon, tint) = when (item.status) {
        MissionStatus.COMPLETED -> Icons.Filled.CheckCircle to SuccessGreen
        MissionStatus.LOCKED -> Icons.Filled.Lock to Color(0xFF9AA5AF)
        else -> Icons.Filled.PlayArrow to SunAmber
    }
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(28.dp))
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.mission.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(item.mission.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (item.status != MissionStatus.LOCKED && item.status != MissionStatus.COMPLETED) {
                    Spacer(Modifier.size(6.dp))
                    AnimatedProgressBar(progress = item.progressPercent / 100f, modifier = Modifier.fillMaxWidth())
                }
                Text("+${item.mission.rewardXp} XP", style = MaterialTheme.typography.labelMedium, color = SunAmber, fontWeight = FontWeight.Bold)
            }
        }
    }
}
