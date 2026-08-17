package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.item
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.ui.CityViewModel
import com.educalab.atumanera.ui.MissionsViewModel
import com.educalab.atumanera.ui.components.CityGridCanvas
import com.educalab.atumanera.ui.components.ModuleStateBadge
import com.educalab.atumanera.ui.components.StatPill
import com.educalab.atumanera.ui.components.avatarRes
import com.educalab.atumanera.ui.components.categoryVisual
import com.educalab.atumanera.ui.components.moduleStateFor
import com.educalab.atumanera.ui.theme.BlueprintBlue
import com.educalab.atumanera.ui.theme.SkyBlueSoft
import com.educalab.atumanera.ui.theme.SunAmber
import com.educalab.atumanera.ui.theme.SurfaceCream
import com.educalab.atumanera.domain.model.MissionStatus

private val moduleCategories = listOf(
    InfraCategory.ROAD, InfraCategory.HOUSING, InfraCategory.EDUCATION, InfraCategory.HEALTH,
    InfraCategory.PARK, InfraCategory.WATER, InfraCategory.TRANSPORT
)

@Composable
fun HomeScreen(
    cityViewModel: CityViewModel,
    missionsViewModel: MissionsViewModel,
    onOpenBuild: (InfraCategory) -> Unit,
    onOpenMissions: () -> Unit,
    onOpenIndicators: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val state by cityViewModel.state.collectAsState()
    val missionsState by missionsViewModel.state.collectAsState()
    val nextMission = missionsState.items.firstOrNull { it.status == MissionStatus.AVAILABLE || it.status == MissionStatus.IN_PROGRESS }

    Surface(color = SurfaceCream, modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            item {
                HomeHeader(
                    aliasName = state.user?.alias ?: "Alcalde",
                    avatarCode = state.user?.avatarCode ?: "avatar_1",
                    onSettings = onOpenSettings
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val spent = state.latestMetric?.budgetSpent ?: 0
                    val total = state.city?.budgetTotal ?: 0
                    StatPill("Presupuesto", "${(total - spent).coerceAtLeast(0)} / $total", SunAmber, Modifier.weight(1f))
                    StatPill("Movilidad", "${state.latestMetric?.mobility ?: 0}%", BlueprintBlue, Modifier.weight(1f))
                    StatPill("Servicios", "${state.latestMetric?.servicesScore ?: 0}%", com.educalab.atumanera.ui.theme.GrassGreen, Modifier.weight(1f))
                }
            }

            item {
                Text(
                    "Mi Ciudad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SkyBlueSoft)
                ) {
                    if (state.isReady && state.city != null) {
                        CityGridCanvas(
                            tiles = state.tiles,
                            rows = state.city!!.rows,
                            cols = state.city!!.cols,
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            onTileTap = { _, _ -> onOpenIndicators() }
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                            Text("Preparando tu ciudad…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (nextMission != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clickable { onOpenMissions() },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(com.educalab.atumanera.R.drawable.mascot_guide), contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.size(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Siguiente misión", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(nextMission.mission.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    "Módulos de la ciudad",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            item {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((moduleCategories.size / 2 + moduleCategories.size % 2) * 108).dp)
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(moduleCategories) { category ->
                        val visual = categoryVisual(category)
                        val moduleState = moduleStateFor(category, state.tiles, state.latestMetric)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenBuild(category) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = visual.softColor)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Image(painter = painterResource(visual.moduleIconRes), contentDescription = visual.label, modifier = Modifier.size(44.dp))
                                Spacer(Modifier.size(6.dp))
                                Text(visual.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.size(4.dp))
                                ModuleStateBadge(moduleState)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HomeShortcutCard("Misiones", "${missionsState.missionsCompleted}/30 completadas", Modifier.weight(1f), onOpenMissions)
                    HomeShortcutCard("Indicadores", "Progreso y colección", Modifier.weight(1f), onOpenIndicators)
                }
            }
        }
    }
}

@Composable
private fun HomeHeader(aliasName: String, avatarCode: String, onSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BlueprintBlue)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(52.dp).clip(CircleShape).background(SkyBlueSoft),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(avatarRes(avatarCode)), contentDescription = null, modifier = Modifier.size(46.dp))
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Hola, $aliasName", color = androidx.compose.ui.graphics.Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            Text("Alcalde de tu ciudad", color = com.educalab.atumanera.ui.theme.SkyBlue, style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onSettings) {
            Icon(Icons.Filled.Settings, contentDescription = "Ajustes", tint = androidx.compose.ui.graphics.Color.White)
        }
    }
}

@Composable
private fun HomeShortcutCard(title: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.White)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
