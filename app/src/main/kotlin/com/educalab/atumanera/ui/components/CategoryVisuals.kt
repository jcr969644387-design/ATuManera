package com.educalab.atumanera.ui.components

import androidx.compose.ui.graphics.Color
import com.educalab.atumanera.R
import com.educalab.atumanera.domain.model.InfraCategory

data class CategoryVisual(
    val label: String,
    val color: Color,
    val softColor: Color,
    val moduleIconRes: Int
)

fun categoryVisual(category: InfraCategory): CategoryVisual = when (category) {
    InfraCategory.ROAD -> CategoryVisual("Carreteras", Color(0xFF3A4650), Color(0xFFD9DEE2), R.drawable.ic_module_road)
    InfraCategory.HOUSING -> CategoryVisual("Vivienda", Color(0xFFEF6F6C), Color(0xFFFBE1E0), R.drawable.ic_module_housing)
    InfraCategory.EDUCATION -> CategoryVisual("Educación", Color(0xFF8B6FE0), Color(0xFFE7E1FA), R.drawable.ic_module_education)
    InfraCategory.HEALTH -> CategoryVisual("Salud", Color(0xFFE0554F), Color(0xFFFAE0DF), R.drawable.ic_module_health)
    InfraCategory.PARK -> CategoryVisual("Parques", Color(0xFF57B15F), Color(0xFFDFF3E1), R.drawable.ic_module_park)
    InfraCategory.WATER -> CategoryVisual("Agua y servicios", Color(0xFF3AA6D6), Color(0xFFDFF3FB), R.drawable.ic_module_water)
    InfraCategory.TRANSPORT -> CategoryVisual("Transporte", Color(0xFFE08A3C), Color(0xFFFCE8D3), R.drawable.ic_module_transport)
}

/** Qué es cada módulo y cómo se conecta con los demás, para mostrar antes de construir. */
fun moduleInfo(category: InfraCategory): String = when (category) {
    InfraCategory.ROAD -> "Las calles son la base de tu ciudad: sin ellas, ninguna casa ni servicio puede conectarse entre sí. Constrúyelas una junto a otra para formar una sola red. Todas las viviendas y servicios necesitan una calle justo al lado para funcionar."
    InfraCategory.HOUSING -> "Aquí viven los habitantes de tu ciudad. Cada casa necesita una calle junto a ella para tener movilidad y poder recibir educación, salud y agua. Sin conexión a la red de calles, la casa no cuenta para ningún indicador."
    InfraCategory.EDUCATION -> "Escuelas y bibliotecas dan cobertura educativa a las casas cercanas. Para que funcionen, deben estar conectadas por calle a la MISMA red que las viviendas, y dentro de su radio de alcance."
    InfraCategory.HEALTH -> "Centros de salud y hospitales cuidan a tus vecinos. Igual que educación, necesitan estar conectados por calle a las casas, dentro de su radio de cobertura."
    InfraCategory.PARK -> "Plazas y parques mejoran la puntuación verde de tu ciudad. Cuantas más casas tengas, más parques conviene construir: lo ideal es 1 parque por cada 4 casas."
    InfraCategory.WATER -> "Torres de agua y plantas potabilizadoras dan cobertura de agua potable a las casas conectadas por calle, dentro de su radio de alcance."
    InfraCategory.TRANSPORT -> "Paradas de autobús y estaciones de tren refuerzan la movilidad de las casas cercanas, como complemento a la red de calles."
}

/** Icono de infraestructura concreta a partir de su código de catálogo. */
fun infraIconRes(code: String): Int = when (code) {
    "ROAD_BASIC" -> R.drawable.ic_infra_road
    "HOUSE_SMALL" -> R.drawable.ic_infra_house_small
    "HOUSE_BLOCK" -> R.drawable.ic_infra_house_block
    "SCHOOL_PRIMARY" -> R.drawable.ic_infra_school
    "SCHOOL_LIBRARY" -> R.drawable.ic_infra_library
    "HEALTH_CENTER" -> R.drawable.ic_infra_health_center
    "HOSPITAL" -> R.drawable.ic_infra_hospital
    "PARK_SMALL" -> R.drawable.ic_infra_park_small
    "PARK_LARGE" -> R.drawable.ic_infra_park_large
    "WATER_TOWER" -> R.drawable.ic_infra_water_tower
    "WATER_TREATMENT" -> R.drawable.ic_infra_water_plant
    "BUS_STOP" -> R.drawable.ic_infra_bus
    "TRAIN_STATION" -> R.drawable.ic_infra_train
    else -> R.drawable.ic_module_road
}

fun avatarRes(code: String): Int = when (code) {
    "avatar_1" -> R.drawable.avatar_1
    "avatar_2" -> R.drawable.avatar_2
    else -> R.drawable.avatar_1
}

/** Nombre del rol representado por cada avatar, para mostrarlo al elegirlo. */
fun avatarLabel(code: String): String = when (code) {
    "avatar_1" -> "Constructor"
    "avatar_2" -> "Constructora"
    else -> "Constructor"
}

fun badgeIconRes(code: String): Int = when (code) {
    "BADGE_FIRST_ROAD" -> R.drawable.ic_badge_road
    "BADGE_FIRST_HOUSE" -> R.drawable.ic_badge_house
    "BADGE_FIRST_SCHOOL" -> R.drawable.ic_badge_school
    "BADGE_MOBILITY_MASTER" -> R.drawable.ic_badge_mobility
    "BADGE_GREEN_CITY" -> R.drawable.ic_badge_green
    "BADGE_HEALTH_HERO" -> R.drawable.ic_badge_health
    "BADGE_WATER_HERO" -> R.drawable.ic_badge_water
    "BADGE_EDUCATION_HERO" -> R.drawable.ic_badge_education
    "BADGE_BUDGET_WIZARD" -> R.drawable.ic_badge_budget
    "BADGE_MISSION_10" -> R.drawable.ic_badge_missions10
    "BADGE_MISSION_20" -> R.drawable.ic_badge_missions20
    "BADGE_XP_500" -> R.drawable.ic_badge_xp
    else -> R.drawable.ic_badge_xp
}

fun decorationIconRes(code: String): Int = when (code) {
    "DECO_FOUNTAIN" -> R.drawable.ic_deco_fountain
    "DECO_CLOCK_TOWER" -> R.drawable.ic_deco_clock
    "DECO_STATUE" -> R.drawable.ic_deco_statue
    "DECO_GARDEN" -> R.drawable.ic_deco_garden
    "DECO_BRIDGE" -> R.drawable.ic_deco_bridge
    "DECO_OBELISK" -> R.drawable.ic_deco_obelisk
    "DECO_BANDSTAND" -> R.drawable.ic_deco_bandstand
    "DECO_LIGHTHOUSE" -> R.drawable.ic_deco_lighthouse
    else -> R.drawable.ic_deco_fountain
}
