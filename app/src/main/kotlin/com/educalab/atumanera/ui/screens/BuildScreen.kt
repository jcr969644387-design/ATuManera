package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.data.local.entity.InfrastructureTypeEntity
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.ui.CityEvent
import com.educalab.atumanera.ui.CityViewModel
import com.educalab.atumanera.ui.components.CityGridCanvas
import com.educalab.atumanera.ui.components.ScreenTopBar
import com.educalab.atumanera.ui.components.StatPill
import com.educalab.atumanera.ui.components.categoryVisual
import com.educalab.atumanera.ui.components.infraIconRes
import com.educalab.atumanera.util.AppPreferences
import kotlinx.coroutines.flow.collectLatest

@Composable
fun BuildScreen(
    category: InfraCategory,
    viewModel: CityViewModel,
    preferences: AppPreferences,
    onBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val visual = categoryVisual(category)
    val context = LocalContext.current
    val vibrator = remember { androidx.core.content.ContextCompat.getSystemService(context, android.os.Vibrator::class.java) }

    var selectedInfraId by remember(category) { mutableStateOf<Long?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }

    val catalogForCategory = state.catalog.filter { it.category == category.name }
    LaunchedEffect(catalogForCategory) {
        if (selectedInfraId == null && catalogForCategory.isNotEmpty()) selectedInfraId = catalogForCategory.first().id
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            feedback = when (event) {
                is CityEvent.MissionsCompleted -> "¡Misión completada! Sigue así."
                is CityEvent.BadgesEarned -> "¡Nueva insignia desbloqueada!"
                is CityEvent.Rejected -> event.reason
            }
            if (preferences.hapticsEnabled) {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        }
    }

    Surface(color = visual.softColor, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTopBar(title = visual.label, subtitle = "Toca una casilla libre para construir", onBack = onBack)

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val spent = state.latestMetric?.budgetSpent ?: 0
                val total = state.city?.budgetTotal ?: 0
                StatPill("Presupuesto disponible", "${(total - spent).coerceAtLeast(0)}", visual.color, Modifier.weight(1f))
            }

            Text(
                "Elige qué construir",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp)
            ) {
                items(catalogForCategory) { infra ->
                    InfraOptionCard(
                        infra = infra,
                        selected = infra.id == selectedInfraId,
                        color = visual.color,
                        onClick = { selectedInfraId = infra.id }
                    )
                }
            }

            feedback?.let { msg ->
                Snackbar(modifier = Modifier.padding(12.dp), action = {}) { Text(msg) }
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(2200)
                    feedback = null
                }
            }

            if (state.isReady && state.city != null) {
                CityGridCanvas(
                    tiles = state.tiles,
                    rows = state.city!!.rows,
                    cols = state.city!!.cols,
                    highlightCategory = category,
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    onTileTap = { row, col ->
                        val existing = state.tiles.firstOrNull { it.tile.row == row && it.tile.col == col }
                        if (existing?.infraType != null) {
                            if (existing.infraType?.category == category.name) {
                                viewModel.remove(row, col)
                            } else {
                                feedback = "Esa casilla ya tiene otra construcción (${existing.infraType?.name})."
                            }
                        } else {
                            val infraId = selectedInfraId
                            if (infraId != null) viewModel.place(row, col, infraId)
                        }
                    }
                )
            }

            Text(
                "Consejo: coloca calles primero para conectar tus casas con los servicios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun InfraOptionCard(infra: InfrastructureTypeEntity, selected: Boolean, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
            .border(width = if (selected) 3.dp else 0.dp, color = color, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(infraIconRes(infra.code)), contentDescription = infra.name, modifier = Modifier.size(52.dp))
            Spacer(Modifier.size(4.dp))
            Text(infra.name, style = MaterialTheme.typography.labelLarge, textAlign = androidx.compose.ui.text.style.TextAlign.Center, maxLines = 2)
            Text("${infra.cost} monedas", style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
