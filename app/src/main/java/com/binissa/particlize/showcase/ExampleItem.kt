package com.binissa.particlize.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

data class ExampleItem(
    val title: String,
    val description: String,
    val icon: @Composable () -> Unit,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LargeTopAppBar(
                title = {
                    Column {
                        Text("Particlize")
                        Text(
                            "Elegant Particle Effects",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            )
            

            Text(
                "Select an example to explore:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val examples = listOf(
                ExampleItem(
                    title = "Directional",
                    description = "Particles flowing in directions",
                    icon = { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null) },
                    route = "directional"
                ),
                ExampleItem(
                    title = "Radial",
                    description = "Particles radiating from center",
                    icon = { Icon(circle, contentDescription = null) },
                    route = "radial"
                ),
                ExampleItem(
                    title = "Explosion",
                    description = "Explosive particle bursts",
                    icon = { Icon(Icons.Default.Star, contentDescription = null) },
                    route = "explosion"
                ),
                ExampleItem(
                    title = "Vortex",
                    description = "Swirling particle effects",
                    icon = { Icon(Icons.Default.Refresh, contentDescription = null) },
                    route = "vortex"
                ),
                ExampleItem(
                    title = "Assembly",
                    description = "Particles forming images",
                    icon = { Icon(puzzle, contentDescription = null) },
                    route = "assembly"
                ),
                ExampleItem(
                    title = "Confetti",
                    description = "Celebration effects",
                    icon = { Icon(Icons.Outlined.Star, contentDescription = null) },
                    route = "confetti"
                ),
                ExampleItem(
                    title = "Custom",
                    description = "Build your own effects",
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    route = "custom"
                )
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(examples) { example ->
                    ExampleCard(
                        title = example.title,
                        description = example.description,
                        icon = example.icon,
                        onClick = { navController.navigate(example.route) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExampleCard(
    title: String,
    description: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}