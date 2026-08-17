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
    "avatar_3" -> R.drawable.avatar_3
    "avatar_4" -> R.drawable.avatar_4
    "avatar_5" -> R.drawable.avatar_5
    "avatar_6" -> R.drawable.avatar_6
    "avatar_7" -> R.drawable.avatar_7
    "avatar_8" -> R.drawable.avatar_8
    else -> R.drawable.avatar_1
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
