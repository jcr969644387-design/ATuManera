package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.data.local.entity.InfrastructureTypeEntity
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.domain.model.MissionStatus
import com.educalab.atumanera.ui.CityEvent
import com.educalab.atumanera.ui.CityViewModel
import com.educalab.atumanera.ui.MissionsViewModel
import com.educalab.atumanera.ui.components.AnimatedProgressBar
import com.educalab.atumanera.ui.components.CityGridCanvas
import com.educalab.atumanera.ui.components.ScreenTopBar
import com.educalab.atumanera.ui.components.StatPill
import com.educalab.atumanera.util.SoundFeedback
import com.educalab.atumanera.ui.components.categoryVisual
import com.educalab.atumanera.ui.components.infraIconRes
import com.educalab.atumanera.util.AppPreferences
import kotlinx.coroutines.flow.collectLatest

private const val ZOOM_MIN = 1f
private const val ZOOM_MAX = 2.2f
private const val ZOOM_STEP = 0.3f

private fun categoryTip(category: InfraCategory): String = when (category) {
    InfraCategory.ROAD -> "Las calles deben tocarse entre sí para formar una red. Una casa solo tiene movilidad si está pegada a una calle conectada a esa red."
    InfraCategory.HOUSING -> "Coloca una calle junto a cada casa: sin acceso a la red de carreteras, la vivienda no cuenta para movilidad ni puede recibir servicios."
    InfraCategory.EDUCATION, InfraCategory.HEALTH, InfraCategory.WATER ->
        "Este servicio solo cubre casas que están conectadas por calles a la MISMA red que el edificio, y dentro de su radio de alcance (revisa la descripción de cada construcción)."
    InfraCategory.PARK -> "Los parques suman al puntaje verde por su cobertura y por la cantidad de parques respecto a tus casas."
    InfraCategory.TRANSPORT -> "El transporte público refuerza la movilidad de las casas que están dentro de su alcance."
}

@Composable
fun BuildScreen(
    category: InfraCategory,
    viewModel: CityViewModel,
    missionsViewModel: MissionsViewModel,
    preferences: AppPreferences,
    onBack: () -> Unit,
    onOpenMissions: () -> Unit,
    freeMode: Boolean = false
) {
    val state by viewModel.state.collectAsState()
    val missionsState by missionsViewModel.state.collectAsState()
    val nextMission = if (freeMode) null else missionsState.items.firstOrNull { it.status == MissionStatus.AVAILABLE || it.status == MissionStatus.IN_PROGRESS }
    var levelCelebration by remember { mutableStateOf<Int?>(null) }
    var currentCategory by remember { mutableStateOf(category) }
    val visual = categoryVisual(currentCategory)
    val context = LocalContext.current
    val vibrator = remember { androidx.core.content.ContextCompat.getSystemService(context, android.os.Vibrator::class.java) }

    var selectedInfraId by remember(currentCategory) { mutableStateOf<Long?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var pendingDelete by remember { mutableStateOf<Triple<Int, Int, String>?>(null) }
    var confirmClearAll by remember { mutableStateOf(false) }
    var confirmClearCategory by remember { mutableStateOf(false) }
    var zoom by remember { mutableFloatStateOf(ZOOM_MIN) }

    val catalogForCategory = state.catalog.filter { it.category == currentCategory.name }
    val selectedInfra = catalogForCategory.firstOrNull { it.id == selectedInfraId }
    LaunchedEffect(catalogForCategory) {
        if (selectedInfraId == null && catalogForCategory.isNotEmpty()) selectedInfraId = catalogForCategory.first().id
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CityEvent.MissionsCompleted -> feedback = "¡Misión completada! Sigue así."
                is CityEvent.BadgesEarned -> feedback = "¡Nueva insignia desbloqueada!"
                is CityEvent.LevelCompleted -> levelCelebration = event.level
                is CityEvent.Rejected -> feedback = event.reason
                CityEvent.Placed, CityEvent.Removed -> Unit
            }

            if (preferences.hapticsEnabled) {
                val pattern = if (event is CityEvent.Rejected) 80L else 35L
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern)
            }
            if (preferences.soundEnabled) {
                when (event) {
                    is CityEvent.Rejected -> SoundFeedback.playReject()
                    is CityEvent.MissionsCompleted, is CityEvent.BadgesEarned, is CityEvent.LevelCompleted -> SoundFeedback.playSuccess()
                    else -> SoundFeedback.playBuild()
                }
            }
        }
    }

    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val boardBaseWidth = (screenWidthDp - 32.dp).coerceAtLeast(200.dp)

    Surface(color = visual.softColor, modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                ScreenTopBar(
                    title = if (freeMode) "${visual.label} · Modo Libre" else visual.label,
                    subtitle = if (freeMode) "Construye lo que quieras, sin límite de presupuesto" else "Elige un módulo y toca una casilla libre para construir",
                    onBack = onBack
                )

                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (freeMode) {
                        StatPill("Presupuesto", "Ilimitado ∞", visual.color, Modifier.weight(1f))
                    } else {
                        val spent = state.latestMetric?.budgetSpent ?: 0
                        val total = state.city?.budgetTotal ?: 0
                        StatPill("Presupuesto disponible", "${(total - spent).coerceAtLeast(0)}", visual.color, Modifier.weight(1f))
                    }
                }

                if (nextMission != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onOpenMissions() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(com.educalab.atumanera.R.drawable.mascot_guide),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(Modifier.size(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Misión pendiente", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(nextMission.mission.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.size(4.dp))
                                AnimatedProgressBar(progress = nextMission.progressPercent / 100f, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(InfraCategory.values().toList()) { cat ->
                        CategoryChip(
                            category = cat,
                            selected = cat == currentCategory,
                            onClick = { currentCategory = cat }
                        )
                    }
                }
                Text(
                    "Más módulos para elegir →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!freeMode) {
                        Text(
                            "Elige qué construir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    IconButton(onClick = { confirmClearCategory = true }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Eliminar solo ${visual.label}")
                    }
                }

                // En Modo Libre se oculta la tarjeta de selección (imagen +
                // nombre + descripción) para dejar más espacio al mapa: la
                // categoría ya elegida arriba basta, y se construye con la
                // primera opción de esa categoría automáticamente.
                if (!freeMode) {
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

                    if (selectedInfra != null) {
                        Text(
                            selectedInfra.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }

                if (state.isReady && state.city != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Zoom del mapa",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { zoom = (zoom - ZOOM_STEP).coerceAtLeast(ZOOM_MIN) }, enabled = zoom > ZOOM_MIN) {
                            Icon(Icons.Filled.ZoomOut, contentDescription = "Alejar mapa")
                        }
                        IconButton(onClick = { zoom = (zoom + ZOOM_STEP).coerceAtMost(ZOOM_MAX) }, enabled = zoom < ZOOM_MAX) {
                            Icon(Icons.Filled.ZoomIn, contentDescription = "Acercar mapa")
                        }
                        IconButton(onClick = { confirmClearAll = true }) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = "Eliminar todas las construcciones")
                        }
                    }

                    if (freeMode) {
                        val gridSize = state.city!!.rows
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Tamaño del mapa: $gridSize x $gridSize",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.resizeFreeGrid(gridSize - 1) }, enabled = gridSize > 5) {
                                Icon(Icons.Filled.Remove, contentDescription = "Achicar el mapa")
                            }
                            IconButton(onClick = { viewModel.resizeFreeGrid(gridSize + 1) }, enabled = gridSize < 20) {
                                Icon(Icons.Filled.Add, contentDescription = "Agrandar el mapa")
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 420.dp)
                            .padding(horizontal = 16.dp)
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                    ) {
                        CityGridCanvas(
                            tiles = state.tiles,
                            rows = state.city!!.rows,
                            cols = state.city!!.cols,
                            highlightCategory = currentCategory,
                            modifier = Modifier.width(boardBaseWidth * zoom).padding(vertical = 8.dp),
                            onTileTap = { row, col ->
                                val existing = state.tiles.firstOrNull { it.tile.row == row && it.tile.col == col }
                                if (existing?.infraType != null) {
                                    pendingDelete = Triple(row, col, existing.infraType?.name ?: "esta construcción")
                                } else {
                                    val infraId = selectedInfraId
                                    if (infraId != null) viewModel.place(row, col, infraId)
                                }
                            },
                            onLineDrag = if (freeMode) {
                                { rowStart: Int, colStart: Int, rowEnd: Int, colEnd: Int ->
                                    selectedInfraId?.let { infraId -> viewModel.placeLine(rowStart, colStart, rowEnd, colEnd, infraId) }
                                }
                            } else null
                        )
                    }
                }

                if (!freeMode) {
                    Text(
                        categoryTip(currentCategory),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            feedback?.let { msg ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {}
                ) { Text(msg) }
                LaunchedEffect(msg) {
                    kotlinx.coroutines.delay(2200)
                    feedback = null
                }
            }
        }
    }

    pendingDelete?.let { (row, col, name) ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("¿Eliminar construcción?") },
            text = { Text("Vas a eliminar \"$name\". Recuperarás el presupuesto invertido y podrás construir algo nuevo en esta casilla.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.remove(row, col)
                    pendingDelete = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") }
            }
        )
    }

    if (confirmClearCategory) {
        AlertDialog(
            onDismissRequest = { confirmClearCategory = false },
            icon = { Icon(Icons.Filled.DeleteSweep, contentDescription = null) },
            title = { Text("¿Eliminar todo de ${visual.label}?") },
            text = { Text("Vas a borrar solo las construcciones de ${visual.label} de tu ciudad. Recuperarás el presupuesto invertido en ellas, pero esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCategory(currentCategory)
                    confirmClearCategory = false
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearCategory = false }) { Text("Cancelar") }
            }
        )
    }

    if (confirmClearAll) {
        AlertDialog(
            onDismissRequest = { confirmClearAll = false },
            icon = { Icon(Icons.Filled.DeleteSweep, contentDescription = null) },
            title = { Text("¿Eliminar TODAS las construcciones?") },
            text = { Text("Vas a borrar todo lo que has construido en tu ciudad: calles, casas y servicios. Recuperarás todo el presupuesto invertido, pero esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearCity()
                    confirmClearAll = false
                }) { Text("Eliminar todo") }
            },
            dismissButton = {
                TextButton(onClick = { confirmClearAll = false }) { Text("Cancelar") }
            }
        )
    }

    levelCelebration?.let { level ->
        AlertDialog(
            onDismissRequest = { levelCelebration = null },
            icon = {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(level) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = com.educalab.atumanera.ui.theme.SunAmber, modifier = Modifier.size(28.dp))
                    }
                }
            },
            title = { Text(if (level < 4) "¡Nivel $level completado!" else "¡Completaste los 4 niveles!") },
            text = {
                Text(
                    when (level) {
                        1 -> "¡Muy bien! Dominaste los fundamentos de tu ciudad. Avanzas al Nivel 2: ahora tendrás que pensar dónde construir cada cosa."
                        2 -> "¡Excelente! Aprendiste a conectar tu ciudad. Avanzas al Nivel 3, con retos de verdad. Además, acabas de desbloquear el Modo Libre: podrás construir lo que quieras sin presupuesto desde el mapa principal."
                        3 -> "¡Impresionante! Resolviste los problemas del Nivel 3. Avanzas al Nivel 4, el más difícil: ahí tendrás que decidir qué opción es mejor y por qué."
                        else -> "¡Felicidades! Completaste los 4 niveles de misiones. Ganaste la insignia \"Alcalde de Mérito\": eres el mejor alcalde de la ciudad."
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { levelCelebration = null }) { Text("¡Genial!") }
            }
        )
    }
}

@Composable
private fun CategoryChip(category: InfraCategory, selected: Boolean, onClick: () -> Unit) {
    val visual = categoryVisual(category)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(if (selected) visual.color else visual.softColor)
                .border(width = if (selected) 2.dp else 0.dp, color = visual.color, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(painter = painterResource(visual.moduleIconRes), contentDescription = visual.label, modifier = Modifier.size(30.dp))
        }
        Text(
            visual.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) visual.color else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp).width(64.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun InfraOptionCard(infra: InfrastructureTypeEntity, selected: Boolean, color: Color, showPrice: Boolean = true, onClick: () -> Unit) {
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
            if (showPrice) {
                Text("${infra.cost} monedas", style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}
