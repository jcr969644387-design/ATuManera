package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lightbulb
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

private fun missionHint(code: String): String? = when (code) {
    "M01" -> "Ve al módulo Carreteras y coloca una calle en cualquier casilla libre."
    "M02" -> "Ve a Vivienda y coloca una Casa pequeña junto a una calle."
    "M03" -> "Construye 3 casas pequeñas y únelas con calles que se toquen entre sí formando una sola red."
    "M04" -> "Ve a Vivienda y elige 'Bloque de viviendas' en vez de una casa pequeña."
    "M05" -> "Ve a Educación y coloca una Escuela primaria conectada por calle."
    "M06" -> "Conecta por calle al menos 3 casas al mismo tramo donde está tu escuela, dentro de su radio de alcance (6 casillas)."
    "M07" -> "Ve a Salud y coloca un Centro de salud conectado por calle."
    "M08" -> "Conecta 3 casas por calle al centro de salud, dentro de su radio de 6 casillas."
    "M09" -> "Ve a Parques y coloca una Plaza verde en cualquier casilla libre."
    "M10" -> "Construye más parques: el puntaje verde combina cobertura de parques y tener ~1 parque por cada 4 casas."
    "M11" -> "Ve a Agua y servicios y coloca una Torre de agua conectada por calle."
    "M12" -> "Conecta 3 casas por calle a la torre de agua, dentro de su radio de 7 casillas."
    "M13" -> "Ve a Transporte y coloca una Parada de autobús."
    "M14" -> "Construye 8 calles que se toquen entre sí formando una sola red continua (no en tramos separados)."
    "M15" -> "Coloca una calle junto a cada casa; revisa el % de Movilidad en Indicadores para ver tu avance."
    "M16" -> "Construye más escuelas o bibliotecas y conecta más casas por calle dentro de su radio."
    "M17" -> "Construye más centros de salud u hospitales y acerca las casas con calles."
    "M18" -> "Ve a Educación y coloca una Biblioteca."
    "M19" -> "Ve a Salud y coloca un Hospital (tiene mayor radio de cobertura que el centro de salud)."
    "M20" -> "Ve a Parques y coloca un Parque grande."
    "M21" -> "Añade más torres de agua o plantas potabilizadoras y conecta más casas por calle."
    "M22" -> "Ve a Transporte y coloca una Estación de tren."
    "M23" -> "Logra 60 o más en Servicios gastando menos del 70% del presupuesto: prioriza construcciones económicas y bien conectadas."
    "M24" -> "Amplía tu red de calles conectadas hasta que tenga 15 casillas, todas tocándose entre sí."
    "M25" -> "Sigue conectando casas con calles hasta llegar al 80% de Movilidad."
    "M26" -> "Amplía salud (hospitales) y sus conexiones hasta el 80% de cobertura sanitaria."
    "M27" -> "Amplía educación (escuelas/bibliotecas) y sus conexiones hasta el 80% de cobertura educativa."
    "M28" -> "Agrega más parques hasta una puntuación verde de 70 o más."
    "M29" -> "Sube educación, salud y agua a la vez: Servicios es el promedio de los tres, necesitas 70 o más."
    "M30" -> "Combina 85% de movilidad, 75% de servicios y 60% de puntuación verde: una ciudad bien conectada, con servicios cerca de las casas y suficientes parques."
    "M31" -> "Construye 5 casas pequeñas y conéctalas todas a una misma red de al menos 12 calles."
    "M32" -> "Amplía tu red de calles y escuelas hasta que 6 casas queden dentro del radio educativo."
    "M33" -> "Amplía tu red de calles y centros de salud hasta que 6 casas queden dentro del radio sanitario."
    "M34" -> "Amplía tu red de calles y torres/plantas de agua hasta que 6 casas queden cubiertas."
    "M35" -> "Reparte parques cerca de tus casas hasta que 6 de ellas tengan uno dentro de su radio."
    "M36" -> "Sigue pegando calles a cada casa nueva hasta llegar al 45% de movilidad."
    "M37" -> "Combina educación, salud y agua para subir el promedio de Servicios al 40%."
    "M38" -> "Agrega más parques y cuida la proporción de 1 parque por cada 4 casas para llegar a 45 de verde."
    "M39" -> "Coloca una escuela primaria Y una biblioteca conectadas por calle a las mismas casas."
    "M40" -> "Coloca un centro de salud Y un hospital conectados por calle a las mismas casas."
    "M41" -> "Coloca una torre de agua Y una planta potabilizadora conectadas por calle a las mismas casas."
    "M42" -> "Reparte 2 paradas de autobús y 1 estación de tren por distintas zonas de tu ciudad."
    "M43" -> "Los bloques de viviendas también necesitan calle al lado: construye 3 y conserva buena movilidad."
    "M44" -> "Sigue uniendo calles sin dejar tramos sueltos hasta llegar a 20 casillas conectadas."
    "M45" -> "Combina bloques y casas pequeñas, todos conectados, para llegar al 50% de movilidad."
    "M46" -> "Elige construcciones económicas y bien ubicadas: sube Servicios a 35% gastando máximo 60% del presupuesto."
    "M47" -> "Distribuye 2 plazas y 1 parque grande cerca de tus casas para llegar a 55 de verde."
    "M48" -> "Repasa movilidad, servicios y verde a la vez: este nivel se cierra combinando los tres."
    "M49" -> "Construye muchas calles conectadas, pero elige rutas eficientes para no gastar de más."
    "M50" -> "Agrega más escuelas/bibliotecas y casas conectadas hasta cubrir 10 hogares con 65% de cobertura."
    "M51" -> "Agrega más centros de salud/hospitales y casas conectadas hasta cubrir 10 hogares con 65% de cobertura."
    "M52" -> "Agrega más torres/plantas de agua y casas conectadas hasta cubrir 10 hogares con 65% de cobertura."
    "M53" -> "Piensa qué construcciones dan más cobertura por su costo: sube Servicios a 55% gastando máximo 55%."
    "M54" -> "Una red grande y bien pensada de 25 calles te acerca al 75% de movilidad."
    "M55" -> "Dos parques grandes bien ubicados ayudan a llegar a 65 de puntuación verde."
    "M56" -> "Combina varias paradas de autobús y estaciones de tren para reforzar la movilidad al 70%."
    "M57" -> "Un barrio de 5 bloques necesita una red de calles sólida para mantener movilidad y servicios altos."
    "M58" -> "Busca el equilibrio: 70% movilidad y 60% servicios gastando como máximo 70% del presupuesto."
    "M59" -> "El reto de nivel 3: combina movilidad, servicios, verde y eficiencia de presupuesto a la vez."
    "M60" -> "Revisa cada indicador en la pantalla de Indicadores y refuerza el que esté más bajo."
    "M61" -> "Lleva la cobertura educativa al límite: más escuelas, bibliotecas y casas bien conectadas."
    "M62" -> "Lleva la cobertura sanitaria al límite: más centros de salud, hospitales y casas bien conectadas."
    "M63" -> "Lleva la cobertura de agua al límite: más torres, plantas y casas bien conectadas."
    "M64" -> "La decisión clave: ¿construcciones baratas y muchas, o pocas y potentes? Encuentra el balance para gastar máximo 45%."
    "M65" -> "Una red vial de 45 calles sin huecos es la base de una movilidad casi perfecta."
    "M66" -> "Una ciudad verde de verdad combina parques grandes y pequeños repartidos por todo el mapa."
    "M67" -> "El reto final: decide con cuidado cada construcción para lograr todo a la vez, sin desperdiciar presupuesto."
    else -> null
}

private fun missionLevel(orderIndex: Int): Int = when {
    orderIndex <= 30 -> 1
    orderIndex <= 48 -> 2
    orderIndex <= 59 -> 3
    else -> 4
}

private fun levelInfo(level: Int): Pair<String, String> = when (level) {
    1 -> "Nivel 1 · Fundamentos" to "¿Qué debo construir?"
    2 -> "Nivel 2 · Conexiones" to "¿Dónde debo construir?"
    3 -> "Nivel 3 · Retos" to "¿Qué problema debo solucionar?"
    else -> "Nivel 4 · Maestría" to "¿Qué decisión es mejor y por qué?"
}

@Composable
fun MissionsScreen(viewModel: MissionsViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    var showCompleted by remember { mutableStateOf(false) }

    val pending = state.items.filter { it.status != MissionStatus.COMPLETED }
    val completed = state.items.filter { it.status == MissionStatus.COMPLETED }

    Surface(color = SurfaceCream, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenTopBar(title = "Misiones", subtitle = "Cada misión ayuda a tu ciudad a crecer", onBack = onBack)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                StatPill("Experiencia", "${state.totalXp} XP", SunAmber, Modifier.weight(1f))
                StatPill("Completadas", "${state.missionsCompleted}/${state.items.size}", SuccessGreen, Modifier.weight(1f))
            }
            Spacer(Modifier.size(8.dp))
            LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                if (completed.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCompleted = !showCompleted }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Completadas (${completed.size})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (showCompleted) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = if (showCompleted) "Ocultar completadas" else "Ver completadas",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (showCompleted) {
                        items(completed, key = { it.mission.id }) { item ->
                            MissionRow(item)
                        }
                    }
                }

                if (pending.isEmpty() && state.items.isNotEmpty()) {
                    item {
                        Text(
                            "¡Completaste todas las misiones disponibles! Sigue construyendo para desbloquear más.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }

                val pendingByLevel = pending.groupBy { missionLevel(it.mission.orderIndex) }
                pendingByLevel.forEach { (level, itemsInLevel) ->
                    item {
                        val (title, question) = levelInfo(level)
                        Column(modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)) {
                            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                            Text(
                                question,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    items(itemsInLevel, key = { it.mission.id }) { item ->
                        MissionRow(item)
                    }
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
                    missionHint(item.mission.code)?.let { hint ->
                        Spacer(Modifier.size(6.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = SunAmber, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.size(4.dp))
                            Text(
                                hint,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(Modifier.size(4.dp))
                Text("+${item.mission.rewardXp} XP", style = MaterialTheme.typography.labelMedium, color = SunAmber, fontWeight = FontWeight.Bold)
            }
        }
    }
}
