package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.item
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.data.local.entity.CityMetricEntity
import com.educalab.atumanera.ui.CityViewModel
import com.educalab.atumanera.ui.CollectionViewModel
import com.educalab.atumanera.ui.components.BadgeChip
import com.educalab.atumanera.ui.components.ScreenTopBar
import com.educalab.atumanera.ui.components.StatPill
import com.educalab.atumanera.ui.components.badgeIconRes
import com.educalab.atumanera.ui.components.decorationIconRes
import com.educalab.atumanera.ui.theme.BlueprintBlue
import com.educalab.atumanera.ui.theme.GrassGreen
import com.educalab.atumanera.ui.theme.SunAmber
import com.educalab.atumanera.ui.theme.SurfaceCream
import com.educalab.atumanera.ui.theme.WaterBlue

@Composable
fun IndicatorsCollectionScreen(
    cityViewModel: CityViewModel,
    collectionViewModel: CollectionViewModel,
    onBack: () -> Unit
) {
    val cityState by cityViewModel.state.collectAsState()
    val collectionState by collectionViewModel.state.collectAsState()
    val metric = cityState.latestMetric

    Surface(color = SurfaceCream, modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
            item { ScreenTopBar(title = "Indicadores y colección", subtitle = "El progreso real de tu ciudad", onBack = onBack) }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().height(140.dp).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        listOf(
                            Triple("Movilidad", metric?.mobility ?: 0, BlueprintBlue),
                            Triple("Servicios", metric?.servicesScore ?: 0, GrassGreen),
                            Triple("Áreas verdes", metric?.greenScore ?: 0, GrassGreen),
                            Triple("Educación", metric?.educationCoverage ?: 0, Color(0xFF8B6FE0)),
                            Triple("Salud", metric?.healthCoverage ?: 0, Color(0xFFE0554F)),
                            Triple("Agua", metric?.waterCoverage ?: 0, WaterBlue)
                        )
                    ) { (label, value, color) ->
                        StatPill(label, "$value%", color, Modifier.fillMaxWidth())
                    }
                }
            }

            item {
                Text(
                    "Evolución de tu ciudad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
                ) {
                    MetricHistoryChart(cityState.metricHistory, modifier = Modifier.fillMaxWidth().height(160.dp).padding(16.dp))
                }
            }

            item {
                Text(
                    "Insignias",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().height(((collectionState.badges.size / 4 + 1) * 110).dp).padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collectionState.badges) { item ->
                        BadgeChip(badgeIconRes(item.badge.code), item.badge.name, item.earned)
                    }
                }
            }

            item {
                Text(
                    "Monumentos de la ciudad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier.fillMaxWidth().height(((collectionState.decorations.size / 4 + 1) * 110).dp).padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(collectionState.decorations) { item ->
                        BadgeChip(decorationIconRes(item.decoration.code), item.decoration.name, item.unlocked)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricHistoryChart(history: List<CityMetricEntity>, modifier: Modifier = Modifier) {
    if (history.size < 2) {
        Text("Construye un poco más para ver la evolución de tu ciudad aquí.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
        return
    }
    val recent = history.takeLast(20)
    Canvas(modifier = modifier) {
        val stepX = size.width / (recent.size - 1).coerceAtLeast(1)
        fun points(selector: (CityMetricEntity) -> Int, color: Color) {
            val path = androidx.compose.ui.graphics.Path()
            recent.forEachIndexed { index, m ->
                val x = index * stepX
                val y = size.height - (selector(m) / 100f) * size.height
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(path, color = color, style = Stroke(width = 5f))
        }
        points({ it.mobility }, Color(0xFF14335A))
        points({ it.servicesScore }, Color(0xFF4CAF6D))
        points({ it.greenScore }, Color(0xFF57B15F))
    }
}
