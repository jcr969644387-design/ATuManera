package com.educalab.atumanera.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AtuManeraColorScheme = lightColorScheme(
    primary = BlueprintBlue,
    onPrimary = Color.White,
    primaryContainer = SkyBlueSoft,
    onPrimaryContainer = BlueprintBlue,
    secondary = SunAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE6BF),
    onSecondaryContainer = SunAmberDeep,
    tertiary = GrassGreen,
    onTertiary = Color.White,
    background = SurfaceCream,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = ConcreteGray,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = Color.White
)

val AtuManeraTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, lineHeight = 18.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp)
)

@Composable
fun ATuManeraTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AtuManeraColorScheme,
        typography = AtuManeraTypography,
        content = content
    )
}
