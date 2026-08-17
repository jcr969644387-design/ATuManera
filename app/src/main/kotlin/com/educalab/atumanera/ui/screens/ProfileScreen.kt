package com.educalab.atumanera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.educalab.atumanera.ui.CityViewModel
import com.educalab.atumanera.ui.components.avatarRes
import com.educalab.atumanera.ui.theme.SkyBlueSoft
import com.educalab.atumanera.ui.theme.SunAmber

private val avatarCodes = (1..8).map { "avatar_$it" }

@Composable
fun ProfileScreen(viewModel: CityViewModel, onDone: () -> Unit) {
    val state by viewModel.state.collectAsState()
    var alias by remember { mutableStateOf(state.user?.alias ?: "") }
    var selectedAvatar by remember { mutableStateOf(state.user?.avatarCode ?: avatarCodes.first()) }

    Surface(color = SkyBlueSoft, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            Text("Elige tu alias y tu avatar", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
            Text(
                "No necesitas tu nombre real: usa un alias divertido que te represente en tu ciudad.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = alias,
                onValueChange = { if (it.length <= 16) alias = it },
                label = { Text("Alias") },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            )

            Text("Avatar", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(avatarCodes) { code ->
                    val selected = code == selectedAvatar
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(width = if (selected) 3.dp else 0.dp, color = SunAmber, shape = CircleShape)
                            .clickable { selectedAvatar = code },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(painter = painterResource(avatarRes(code)), contentDescription = code, modifier = Modifier.size(64.dp))
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.updateProfile(alias, selectedAvatar)
                    onDone()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Entrar a mi ciudad", fontWeight = FontWeight.Bold)
            }
        }
    }
}
