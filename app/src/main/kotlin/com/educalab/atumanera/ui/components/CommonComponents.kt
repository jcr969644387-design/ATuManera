package com.educalab.atumanera.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.domain.model.ModuleState
import com.educalab.atumanera.ui.theme.SuccessGreen
import com.educalab.atumanera.ui.theme.SunAmber
import androidx.compose.foundation.Image

@Composable
fun ScreenTopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        trailing()
    }
}

@Composable
fun AnimatedProgressBar(progress: Float, modifier: Modifier = Modifier, color: Color = SunAmber) {
    val animated by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(500), label = "progress")
    Box(
        modifier = modifier
            .height(14.dp)
            .clip(RoundedCornerShape(50))
            .background(Color(0x22143355))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animated)
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
    }
}

@Composable
fun StatPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = color, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BadgeChip(iconRes: Int, name: String, earned: Boolean, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(if (earned) Color(0xFFFFF6E6) else Color(0xFFF0F1F3))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = name,
                modifier = Modifier.size(56.dp).alpha(if (earned) 1f else 0.35f)
            )
            if (!earned) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF57626D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = "Bloqueado", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
        Text(
            name,
            style = MaterialTheme.typography.labelMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ModuleStateBadge(state: ModuleState, modifier: Modifier = Modifier) {
    val (text, color) = when (state) {
        ModuleState.LOCKED -> "Bloqueado" to Color(0xFF9AA5AF)
        ModuleState.AVAILABLE -> "Disponible" to SunAmber
        ModuleState.STARTED -> "En marcha" to Color(0xFF3AA6D6)
        ModuleState.COMPLETED -> "Completado" to SuccessGreen
        ModuleState.MASTERED -> "Dominado" to Color(0xFF8B6FE0)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
    }
}
