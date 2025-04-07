package com.binissa.particlize.showcase

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.binissa.particlize.R
import com.binissa.particlize.lib.compose.particlize
import com.binissa.particlize.lib.compose.rememberParticleEffect
import com.binissa.particlize.lib.core.model.ParticleEffect
import kotlinx.coroutines.launch

/**
 * Main showcase screen that displays tabs for different strategy types
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StrategyShowcaseScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    
    val tabs = listOf(
        "Emission Strategies",
        "Physics Strategies",
        "Appearance",
        "Assembly & Special"
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Particlize Showcase") },
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index }
                    )
                }
            }
            
            when (selectedTabIndex) {
                0 -> EmissionStrategyShowcase()
                1 -> PhysicsStrategyShowcase()
                2 -> AppearanceShowcase()
                3 -> AssemblyStrategiesShowcase()
            }
        }
    }
}

/**
 * Showcase for Emission Strategies
 */
@Composable
fun EmissionStrategyShowcase() {
    var selectedStrategy by remember { mutableStateOf<String?>(null) }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Upper section with strategy selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StrategyButton(
                name = "Directional",
                isSelected = selectedStrategy == "Directional",
                onClick = { selectedStrategy = "Directional" }
            )
            
            StrategyButton(
                name = "Radial",
                isSelected = selectedStrategy == "Radial",
                onClick = { selectedStrategy = "Radial" }
            )
            
            StrategyButton(
                name = "Pattern",
                isSelected = selectedStrategy == "Pattern",
                onClick = { selectedStrategy = "Pattern" }
            )
            
            StrategyButton(
                name = "Random",
                isSelected = selectedStrategy == "Random",
                onClick = { selectedStrategy = "Random" }
            )
            
            StrategyButton(
                name = "Instant",
                isSelected = selectedStrategy == "Instant",
                onClick = { selectedStrategy = "Instant" }
            )
        }
        
        // Lower section with variants of the selected strategy
        when (selectedStrategy) {
            "Directional" -> DirectionalShowcaseGrid()
            "Radial" -> RadialShowcaseGrid()
            "Pattern" -> PatternShowcaseGrid()
            "Random" -> RandomShowcaseGrid()
            "Instant" -> InstantShowcaseGrid()
            else -> {
                // Default placeholder
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Select a strategy type to see its variants",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * Showcase for Physics Strategies
 */
@Composable
fun PhysicsStrategyShowcase() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(physicsShowcaseItems) { item ->
            PhysicsShowcaseItemCompose(
                title = item.title,
                effect = item.effect,
                description = item.description
            )
        }
    }
}

/**
 * Showcase for Assembly and special combined effects
 */
@Composable
fun AssemblyStrategiesShowcase() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Assembly Strategies",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Assembly effects combine special emission and physics strategies to reassemble particles into the original image.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(assemblyShowcaseItems) { item ->
                AssemblyShowcaseItemCompose(
                    title = item.title,
                    effect = item.effect,
                    description = item.description
                )
            }
        }
    }
}

/**
 * Showcase for Appearance Strategies
 */
@Composable
fun AppearanceShowcase() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Fade", "Scale", "Rotation", "Color", "Shapes")
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }
        
        when (selectedTab) {
            0 -> FadeStrategyShowcase()
            1 -> ScaleStrategyShowcase()
            2 -> RotationStrategyShowcase()
            3 -> ColorStrategyShowcase()
            4 -> ShapesShowcase()
        }
    }
}

/**
 * Directional Emission Strategy showcase grid
 */
@Composable
fun DirectionalShowcaseGrid() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Directional Emission Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Particles emit in a specific direction, creating a progressive reveal effect.",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(directionalShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Radial Emission Strategy showcase grid
 */
@Composable
fun RadialShowcaseGrid() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Radial Emission Strategy",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Particles emit based on their distance from the center point.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(radialShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Pattern Emission Strategy showcase grid
 */
@Composable
fun PatternShowcaseGrid() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Pattern Emission Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Creates visually distinctive patterns for particle emission.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(patternShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Random Emission Strategy showcase grid
 */
@Composable
fun RandomShowcaseGrid() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Random Emission Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Particles emit in random order with different grouping patterns.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(randomShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Instant Emission Strategy showcase grid
 */
@Composable
fun InstantShowcaseGrid() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Instant Emission Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "All particles appear simultaneously with different sorting patterns.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(instantShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Fade Strategy showcase grid
 */
@Composable
fun FadeStrategyShowcase() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Fade Appearance Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Controls how particles fade in, out, or pulse over time.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(fadeShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Scale Strategy showcase grid
 */
@Composable
fun ScaleStrategyShowcase() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Scale Appearance Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Controls how particles change size over time.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(scaleShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Rotation Strategy showcase grid
 */
@Composable
fun RotationStrategyShowcase() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Rotation Appearance Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Controls how particles rotate over time.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(rotationShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Color Strategy showcase grid
 */
@Composable
fun ColorStrategyShowcase() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Color Transform Strategy",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Controls how particles change color over time.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(colorShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Shape showcase grid
 */
@Composable
fun ShapesShowcase() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "Particle Shapes",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
        
        Text(
            "Different shapes that can be used for particles.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shapeShowcaseItems) { item ->
                EffectShowcaseItem(
                    title = item.title,
                    effect = item.effect
                )
            }
        }
    }
}

/**
 * Button for selecting strategy type
 */
@Composable
fun StrategyButton(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
//            backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ),
//        elevation = ButtonDefaults.elevation(
//            defaultElevation = if (isSelected) 8.dp else 2.dp
//        )
    ) {
        Text(name, style  = MaterialTheme.typography.labelSmall )
    }
}

/**
 * Card item for showing a strategy variant with an effect
 */
@Composable
fun EffectShowcaseItem(
    title: String,
    effect: ParticleEffect
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()
    var isPlaying by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.hamburger),
                contentDescription = "Demo Image for $title",
                modifier = Modifier
                    .size(150.dp)
                    .particlize(controller)
                    .clickable {
                        if (!isPlaying) {
                            isPlaying = true
                            scope.launch {
                                controller.start(context, effect) {
                                    isPlaying = false
                                }
                            }
                        }
                    },
                contentScale = ContentScale.Crop
            )

        }
    }
}

/**
 * Card item for showing a physics strategy with description
 */
@Composable
fun PhysicsShowcaseItemCompose(
    title: String,
    effect: ParticleEffect,
    description: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()
    var isPlaying by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
//        elevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hamburger),
                    contentDescription = "Demo Image for $title",
                    modifier = Modifier
                        .size(120.dp)
                        .particlize(controller)
                        .clickable {
                            if (!isPlaying) {
                                isPlaying = true
                                scope.launch {
                                    controller.start(context, effect) {
                                        isPlaying = false
                                    }
                                }
                            }
                        },
                    contentScale = ContentScale.Crop
                )
                
                if (!isPlaying) {
                    IconButton(
                        onClick = {
                            if (!isPlaying) {
                                isPlaying = true
                                scope.launch {
                                    controller.start(context, effect) {
                                        isPlaying = false
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Effect",
                            tint = Color.White,
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Card item for showcasing assembly effects
 */
@Composable
fun AssemblyShowcaseItemCompose(
    title: String,
    effect: ParticleEffect,
    description: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()
    var isPlaying by remember { mutableStateOf(false) }
    
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hamburger),
                    contentDescription = "Demo Image for $title",
                    modifier = Modifier
                        .size(140.dp)
                        .particlize(controller)
                        .clickable {
                            if (!isPlaying) {
                                isPlaying = true
                                scope.launch {
                                    controller.start(context, effect) {
                                        isPlaying = false
                                    }
                                }
                            }
                        },
                    contentScale = ContentScale.Crop
                )
                
            }
    }
}