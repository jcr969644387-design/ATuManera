package com.educalab.atumanera.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.R
import com.educalab.atumanera.ui.theme.BlueprintBlue
import com.educalab.atumanera.ui.theme.SkyBlueSoft
import com.educalab.atumanera.ui.theme.SunAmber

private data class OnboardingPage(val title: String, val body: String, val iconRes: Int)

private val pages = listOf(
    OnboardingPage(
        "Bienvenido a A Tu Manera",
        "Vas a diseñar y hacer crecer tu propia ciudad: calles, casas, escuelas, hospitales, parques y mucho más.",
        R.drawable.logo_city
    ),
    OnboardingPage(
        "Conoce a tu ayudante",
        "La Grúa te acompañará en tus misiones, te dará pistas y celebrará contigo cada logro.",
        R.drawable.mascot_guide
    ),
    OnboardingPage(
        "Construye y avanza",
        "Cada construcción cuesta presupuesto. Completa misiones, gana experiencia y desbloquea insignias y monumentos.",
        R.drawable.ic_badge_missions10
    ),
    OnboardingPage(
        "Tu ciudad, sin conexión",
        "Todo lo que hagas se guarda en tu dispositivo. No pedimos tu nombre real ni datos personales: elige un alias y un avatar.",
        R.drawable.ic_infra_water_tower
    )
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var pageIndex by remember { mutableIntStateOf(0) }
    val page = pages[pageIndex]

    Surface(color = SkyBlueSoft, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onFinished) { Text("Saltar") }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(painter = painterResource(page.iconRes), contentDescription = null, modifier = Modifier.size(140.dp))
                }
                Spacer(Modifier.height(24.dp))
                Text(page.title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, color = BlueprintBlue, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(12.dp))
                Text(page.body, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pages.indices.forEach { i ->
                        val active = i == pageIndex
                        Box(
                            modifier = Modifier
                                .size(if (active) 10.dp else 8.dp)
                                .clip(CircleShape)
                                .then(Modifier)
                        ) {
                            Surface(color = if (active) SunAmber else Color(0xFFC9D6E0), shape = CircleShape, modifier = Modifier.fillMaxSize()) {}
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (pageIndex < pages.lastIndex) pageIndex++ else onFinished()
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (pageIndex < pages.lastIndex) "Siguiente" else "¡Empezar!", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
