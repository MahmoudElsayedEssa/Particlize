package com.binissa.particlize.showcase

import android.graphics.Color
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.binissa.particlize.R
import com.binissa.particlize.lib.compose.particlize
import com.binissa.particlize.lib.compose.rememberParticleEffect
import com.binissa.particlize.lib.config.EffectBuilder
import com.binissa.particlize.lib.config.presets.ParticlePresets
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasRectangleShape
import com.binissa.particlize.lib.strategy.appearance.ColorTransformStrategy
import com.binissa.particlize.lib.strategy.appearance.FadeStrategy
import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.InstantEmissionStrategy
import com.binissa.particlize.lib.strategy.physics.DriftPhysics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ParticlizeDemo() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController, startDestination = "welcome"
        ) {
            composable("welcome") {
                WelcomeScreen(navController)
            }
            composable("directional") {
                DirectionalEffectScreen(navController)
            }
            composable("assembly") {
                AssemblyEffectScreen(navController)
            }
            composable("explosion") {
                ExplosionEffectScreen(navController)
            }
            composable("confetti") {
                ConfettiEffectScreen(navController)
            }
            composable("other") {
                OtherEffectScreen(navController)
            }
            composable("meme") {
                MemeEffectScreen(navController)
            }
        }
    }
}

@Composable
fun WelcomeScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title effect
    val titleController = rememberParticleEffect()

    // Controller for button effect
    val buttonController = rememberParticleEffect()

    // Track if animation is playing
    var isAnimating by remember { mutableStateOf(false) }

    // Effect visibility
    var showText by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Animate elements in sequentially
    LaunchedEffect(Unit) {
        delay(300)
        showText = true
        delay(800)
        showButton = true
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(visible = showText, enter = fadeIn()) {
                Text(
                    "Welcome to Particlize",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .particlize(titleController)
                        .clickable {
                            if (!isAnimating) {
                                isAnimating = true
                                scope.launch {
                                    titleController.start(
                                        context = context, effect = ParticlePresets.disintegrationText()
                                    )
                                    isAnimating = false
                                }
                            }
                        })
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = showText, enter = fadeIn()) {
                Text(
                    "Touch any text or button to see particle effects in action",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(visible = showButton, enter = fadeIn()) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context,
                                    effect = EffectBuilder().withDuration(2000.milliseconds)
                                        .withEmissionStrategy(
                                            InstantEmissionStrategy()
                                        ).withPhysicsStrategy(
                                            DriftPhysics(
                                                windStrength = 10f,
                                                windGustiness = 2f,
                                                turbulenceStrength = 0.8f
                                            )
                                        ).withParticleShape(CanvasCircleShape()).withParticleLifetime(
                                            10000.milliseconds, 12000.milliseconds
                                        ).withParticleDensity(4).withMaxEmissionsPerFrame(300)
                                        .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
                                        .build()
                                ) {
                                    // Navigate when effect completes
                                    navController.navigate("directional") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Start Demo")
                }
            }
        }
    }
}

@Composable
fun DirectionalEffectScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title and description
    val textController = rememberParticleEffect()

    // Controller for button
    val buttonController = rememberParticleEffect()

    // Track if animations are complete
    var isAnimating by remember { mutableStateOf(false) }
    var showElements by remember { mutableStateOf(false) }

    // Auto-animate the text with directional assembly effect when screen appears
    LaunchedEffect(Unit) {
        showElements = true
        delay(300)
        textController.start(
            context = context, effect = ParticlePresets.directionalAssembly(
                direction = "left-to-right"
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.particlize(textController),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Directional Effects",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Particles flow in specific directions, creating seamless transitions",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(directionalShowcaseItems) { item ->
                    EffectShowcaseItem(
                        title = item.title, effect = item.effect
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context, effect = ParticlePresets.blackHole()
                                ) {
                                    // Navigate when effect completes
                                    navController.navigate("assembly") {
                                        popUpTo("directional") { inclusive = true }
                                    }
                                    isAnimating = false
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Next: Assembly Effects")
                }
            }
        }
    }
}

@Composable
fun AssemblyEffectScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title and description
    val textController = rememberParticleEffect()

    // Controller for button
    val buttonController = rememberParticleEffect()

    // Track if animations are complete
    var isAnimating by remember { mutableStateOf(false) }
    var showElements by remember { mutableStateOf(false) }

    // Auto-animate the text with assembly effect when screen appears
    LaunchedEffect(Unit) {
        showElements = true
        delay(300)
        textController.start(
            context = context, effect = ParticlePresets.directionalAssembly(
                direction = "top-to-bottom"
            )
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.particlize(textController),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Assembly Effects",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Particles assemble from various starting positions to form the content",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxHeight(0.8f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(assemblyShowcaseItems) { item ->
                    AssemblyShowcaseItemCompose(
                        title = item.title, effect = item.effect, description = item.description
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context, effect = ParticlePresets.disintegration()
                                ) {
                                    // Navigate when effect completes
                                    navController.navigate("explosion") {
                                        popUpTo("assembly") { inclusive = true }
                                    }
                                    isAnimating = false
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Next: Explosion Effects")
                }
            }
        }
    }
}

@Composable
fun ExplosionEffectScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title and description
    val textController = rememberParticleEffect()

    // Controller for button
    val buttonController = rememberParticleEffect()

    // Track if animations are complete
    var isAnimating by remember { mutableStateOf(false) }
    var showElements by remember { mutableStateOf(false) }

    // Added missing LaunchedEffect for consistency with other screens
    LaunchedEffect(Unit) {
        showElements = true
        delay(300)
        textController.start(
            context = context, effect = ParticlePresets.directionalAssembly()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.particlize(textController),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Explosion Effects",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Particles with dynamic physics and explosive animations",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(physicsShowcaseItems) { item ->
                    EffectShowcaseItem(
                        title = item.title, effect = item.effect
                    )
                }
            }

            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context, effect = ParticlePresets.explosion()
                                ) {
                                    // Navigate when effect completes
                                    navController.navigate("confetti") {
                                        popUpTo("explosion") { inclusive = true }
                                    }
                                    isAnimating = false
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Next: Confetti Effects")
                }
            }
        }
    }
}

@Composable
fun ConfettiEffectScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title and description
    val textController = rememberParticleEffect()

    // Controller for button
    val buttonController = rememberParticleEffect()

    // Track if animations are complete
    var isAnimating by remember { mutableStateOf(false) }
    var showElements by remember { mutableStateOf(false) }

    // Auto-animate the text with assembly effect when screen appears
    LaunchedEffect(Unit) {
        showElements = true
        delay(300)
        textController.start(
            context = context, effect = ParticlePresets.directionalAssembly()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.particlize(textController),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Confetti Effects",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Perfect for celebrations and special moments in your app",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(confettiShowcaseItems) { item ->
                    EffectShowcaseItem(
                        title = item.title, effect = item.effect
                    )
                }
            }

            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context, effect = ParticlePresets.confetti()
                                ) {
                                    // Navigate to other effects screen
                                    navController.navigate("other") {
                                        popUpTo("confetti") { inclusive = true }
                                    }
                                    isAnimating = false
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Next: Other Effects")
                }
            }
        }
    }
}

@Composable
fun OtherEffectScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title and description
    val textController = rememberParticleEffect()

    // Controller for button
    val buttonController = rememberParticleEffect()

    // Track if animations are complete
    var isAnimating by remember { mutableStateOf(false) }
    var showElements by remember { mutableStateOf(false) }

    // Auto-animate the text with assembly effect when screen appears
    LaunchedEffect(Unit) {
        showElements = true
        delay(300)
        textController.start(
            context = context, effect = ParticlePresets.directionalAssembly()
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Column(
                    modifier = Modifier.particlize(textController),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Fixed incorrect text (was "Confetti Effects")
                    Text(
                        "Special Effects",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fixed incorrect description
                    Text(
                        "Discover more unique and creative particle animations",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxHeight(0.8f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(otherShowcaseItems) { item ->
                    EffectShowcaseItem(
                        title = item.title, effect = item.effect
                    )
                }
            }

            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context, effect = ParticlePresets.confetti()
                                ) {
                                    // Navigate to meme screen
                                    navController.navigate("meme") {
                                        popUpTo("other") { inclusive = true }
                                    }
                                    isAnimating = false
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Next: Meme Demo")
                }
            }
        }
    }
}

@Composable
fun MemeEffectScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Controller for title and description
    val textController = rememberParticleEffect()

    // Controller for button
    val buttonController = rememberParticleEffect()
    val memeController = rememberParticleEffect()

    // Track if animations are complete
    var isAnimating by remember { mutableStateOf(false) }
    var showElements by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "That Meme",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.particlize(textController)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.meme),
                contentDescription = "Demo Image for meme",
                modifier = Modifier
                    .size(300.dp)
                    .particlize(memeController)
                    .clickable {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                memeController.start(
                                    context,
                                    EffectBuilder().withName("Disintegration")
                                        .withDuration(3000.milliseconds)
                                        .withEmissionStrategy(
                                            DirectionalEmissionStrategy(
                                                direction = DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP,
                                                emissionDelayMs = 50
                                            )
                                        ).withPhysicsStrategy(
                                            DriftPhysics(
                                                horizontalStrength = 0.7f,
                                                verticalStrength = 0.7f,
                                                directionAngle = -135f, // Up-right drift
                                                directionVariance = 30f,
                                                turbulenceStrength = 0.4f
                                            )
                                        ).withAppearanceStrategy(
                                            FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                                        ).withAppearanceStrategy(
                                            ScaleStrategy(
                                                scaleMode = ScaleStrategy.ScaleMode.GROW,
                                                minScale = 0.1f,
                                                maxScale = 1.5f,
                                                scaleRate = 3f
                                            )
                                        )
                                        .withParticleShape(CanvasRectangleShape())
                                        .withParticleLifetime(
                                            (3000 * 0.6).milliseconds, (3000 * 0.9).milliseconds
                                        )
                                        .withParticleDensity(5)
                                        .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                                        .build()
                                )
                                isAnimating = false
                            }
                        }
                    },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showElements, enter = fadeIn()
            ) {
                Button(
                    onClick = {
                        if (!isAnimating) {
                            isAnimating = true
                            scope.launch {
                                buttonController.start(
                                    context = context, effect = ParticlePresets.confetti()
                                ) {
                                    navController.navigate("welcome") {
                                        popUpTo("welcome") { inclusive = true }
                                    }
                                    isAnimating = false
                                }
                            }
                        }
                    }, modifier = Modifier.particlize(buttonController)
                ) {
                    Text("Restart Demo")
                }
            }
        }
    }
}
//package com.binissa.particlize.showcase
//
//import android.graphics.Color
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.binissa.particlize.R
//import com.binissa.particlize.lib.compose.particlize
//import com.binissa.particlize.lib.compose.rememberParticleEffect
//import com.binissa.particlize.lib.config.EffectBuilder
//import com.binissa.particlize.lib.config.presets.ParticlePresets
//import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
//import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
//import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasRectangleShape
//import com.binissa.particlize.lib.strategy.appearance.ColorTransformStrategy
//import com.binissa.particlize.lib.strategy.appearance.FadeStrategy
//import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
//import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
//import com.binissa.particlize.lib.strategy.emission.InstantEmissionStrategy
//import com.binissa.particlize.lib.strategy.physics.DriftPhysics
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlin.time.Duration.Companion.milliseconds
//
//@Composable
//fun ParticlizeDemo() {
//    val navController = rememberNavController()
//
//    Surface(
//        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
//    ) {
//        NavHost(
//            navController = navController, startDestination = "welcome"
//        ) {
//            composable("welcome") {
//                WelcomeScreen(navController)
//            }
//            composable("directional") {
//                DirectionalEffectScreen(navController)
//            }
//            composable("assembly") {
//                AssemblyEffectScreen(navController)
//            }
//            composable("explosion") {
//                ExplosionEffectScreen(navController)
//            }
//            composable("confetti") {
//                ConfettiEffectScreen(navController)
//            }
//            composable("other") {
//                OtherEffectScreen(navController)
//            }
//
//            composable("meme") {
//                MemeEffectScreen(navController)
//            }
//        }
//    }
//}
//
//@Composable
//fun WelcomeScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title effect
//    val titleController = rememberParticleEffect()
//
//    // Controller for button effect
//    val buttonController = rememberParticleEffect()
//
//    // Track if animation is playing
//    var isAnimating by remember { mutableStateOf(false) }
//
//    // Effect visibility
//    var showText by remember { mutableStateOf(false) }
//    var showButton by remember { mutableStateOf(false) }
//
//    // Animate elements in sequentially
//    LaunchedEffect(Unit) {
//        delay(300)
//        showText = true
//        delay(800)
//        showButton = true
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth(0.8f)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                "Welcome to Particlize",
//                style = MaterialTheme.typography.headlineMedium,
//                textAlign = TextAlign.Center,
//                modifier = Modifier
//                    .particlize(titleController)
//                    .clickable {
//                        if (!isAnimating) {
//                            isAnimating = true
//                            scope.launch {
//                                titleController.start(
//                                    context = context, effect = ParticlePresets.disintegrationText()
//                                )
//                            }
//                        }
//                    })
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                "Touch any text or button to see particle effects in action",
//                style = MaterialTheme.typography.bodyLarge,
//                textAlign = TextAlign.Center,
//            )
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            Button(
//                onClick = {
//                    isAnimating = true
//                    scope.launch {
//                        buttonController.start(
//                            context = context,
//                            effect = EffectBuilder().withDuration(2000.milliseconds)
//                                .withEmissionStrategy(
//                                    InstantEmissionStrategy()
//                                ).withPhysicsStrategy(
//                                    DriftPhysics(
//                                        windStrength = 10f,
//                                        windGustiness = 2f,
//                                        turbulenceStrength = 0.8f
//                                    )
//                                ).withParticleShape(CanvasCircleShape()).withParticleLifetime(
//                                    10000.milliseconds, 12000.milliseconds
//                                ).withParticleDensity(4).withMaxEmissionsPerFrame(300)
//                                .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
//                                .build()
//                        ) {
//                            // Navigate when effect completes
//                            navController.navigate("directional") {
//                                popUpTo("welcome") { inclusive = true }
//                            }
//                        }
//                    }
//                }, modifier = Modifier.particlize(buttonController)
//            ) {
//                Text("Start Demo")
//            }
//        }
//    }
//}
//
//@Composable
//fun DirectionalEffectScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title and description
//    val textController = rememberParticleEffect()
//
//    // Controller for button
//    val buttonController = rememberParticleEffect()
//
//    // Track if animations are complete
//    var isAnimating by remember { mutableStateOf(false) }
//    var showElements by remember { mutableStateOf(false) }
//
//    // Auto-animate the text with directional assembly effect when screen appears
//    LaunchedEffect(Unit) {
//        showElements = true
//        delay(300)
//        textController.start(
//            context = context, effect = ParticlePresets.directionalAssembly(
//                direction = "left-to-right"
//            )
//        )
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Column(
//                    modifier = Modifier.particlize(textController),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "Directional Effects",
//                        style = MaterialTheme.typography.headlineMedium,
//                        textAlign = TextAlign.Center
//                    )
//
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        "Particles flow in specific directions, creating seamless transitions",
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(directionalShowcaseItems) { item ->
//                    EffectShowcaseItem(
//                        title = item.title, effect = item.effect
//                    )
//                }
//            }
//
//
//            Spacer(modifier = Modifier.height(40.dp))
//
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Button(
//                    onClick = {
//                        if (!isAnimating) {
//                            isAnimating = true
//                            scope.launch {
//                                buttonController.start(
//                                    context = context, effect = ParticlePresets.blackHole(
////                                        direction = "left-to-right"
//                                    )
//                                ) {
//                                    // Navigate when effect completes
//                                    navController.navigate("assembly") {
//                                        popUpTo("directional") { inclusive = true }
//                                    }
//                                }
//                            }
//                        }
//                    }, modifier = Modifier.particlize(buttonController)
//                ) {
//                    Text("Next: Assembly Effects")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AssemblyEffectScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title and description
//    val textController = rememberParticleEffect()
//
//    // Controller for button
//    val buttonController = rememberParticleEffect()
//
//    // Track if animations are complete
//    var isAnimating by remember { mutableStateOf(false) }
//    var showElements by remember { mutableStateOf(false) }
//
//    // Auto-animate the text with assembly effect when screen appears
//    LaunchedEffect(Unit) {
//        showElements = true
//        delay(300)
//        textController.start(
//            context = context, effect = ParticlePresets.directionalAssembly(
//                direction = "top-to-bottom"
//            )
//        )
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Column(
//                    modifier = Modifier.particlize(textController),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "Assembly Effects",
//                        style = MaterialTheme.typography.headlineMedium,
//                        textAlign = TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        "Particles assemble from various starting positions to form the content",
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                modifier = Modifier.fillMaxHeight(0.8f),
//                contentPadding = PaddingValues(16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(assemblyShowcaseItems) { item ->
//                    AssemblyShowcaseItemCompose(
//                        title = item.title, effect = item.effect, description = item.description
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Button(
//                    onClick = {
//                        if (!isAnimating) {
//                            isAnimating = true
//                            scope.launch {
//                                buttonController.start(
//                                    context = context, effect = ParticlePresets.disintegration()
//                                ) {
//                                    // Navigate when effect completes
//                                    navController.navigate("explosion") {
//                                        popUpTo("assembly") { inclusive = true }
//                                    }
//                                }
//                            }
//                        }
//                    }, modifier = Modifier.particlize(buttonController)
//                ) {
//                    Text("Next: Explosion Effects")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ExplosionEffectScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title and description
//    val textController = rememberParticleEffect()
//
//    // Controller for button
//    val buttonController = rememberParticleEffect()
//
//    // Track if animations are complete
//    var isAnimating by remember { mutableStateOf(false) }
//    var showElements by remember { mutableStateOf(false) }
//
//    // Auto-animate the text with assembly effect when screen appears
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Column(
//                modifier = Modifier
//                    .particlize(textController)
//                    .clickable {
//                        scope.launch {
//
//                            textController.start(
//                                context = context, effect = ParticlePresets.blackHole()
//                            )
//                        }
//
//                    }, horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text(
//                    "Particles Effects",
//                    style = MaterialTheme.typography.headlineMedium,
//                    textAlign = TextAlign.Center
//                )
//
//                Spacer(modifier = Modifier.height(12.dp))
//
//                Text(
//                    "it comes with many particles physics and it all configurable ",
//                    style = MaterialTheme.typography.bodyLarge,
//                    textAlign = TextAlign.Center
//                )
//            }
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                modifier = Modifier.fillMaxHeight(0.8f),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(physicsShowcaseItems) { item ->
//                    EffectShowcaseItem(
//                        title = item.title, effect = item.effect
//                    )
//                }
//            }
//
//            Button(
//                onClick = {
//                    if (!isAnimating) {
//                        isAnimating = true
//                        scope.launch {
//                            buttonController.start(
//                                context = context, effect = ParticlePresets.explosion()
//                            ) {
//                                // Navigate when effect completes
//                                navController.navigate("confetti") {
//                                    popUpTo("explosion") { inclusive = true }
//                                }
//                            }
//                        }
//                    }
//                }, modifier = Modifier.particlize(buttonController)
//            ) {
//                Text("Next: Vortex Effects")
//            }
//        }
//    }
//}
//
//
//@Composable
//fun ConfettiEffectScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title and description
//    val textController = rememberParticleEffect()
//
//    // Controller for button
//    val buttonController = rememberParticleEffect()
//
//    // Track if animations are complete
//    var isAnimating by remember { mutableStateOf(false) }
//    var showElements by remember { mutableStateOf(false) }
//
//    // Auto-animate the text with assembly effect when screen appears
//    LaunchedEffect(Unit) {
//        showElements = true
//        delay(300)
//        textController.start(
//            context = context, effect = ParticlePresets.directionalAssembly()
//        )
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Column(
//                    modifier = Modifier.particlize(textController),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "Confetti Effects",
//                        style = MaterialTheme.typography.headlineMedium,
//                        textAlign = TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        "Perfect for celebrations and special moments in your app",
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                modifier = Modifier.fillMaxHeight(0.8f),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(confettiShowcaseItems) { item ->
//                    EffectShowcaseItem(
//                        title = item.title, effect = item.effect
//                    )
//                }
//            }
//
//
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Button(
//                    onClick = {
//                        if (!isAnimating) {
//                            isAnimating = true
//                            scope.launch {
//                                buttonController.start(
//                                    context = context, effect = ParticlePresets.confetti()
//                                ) {
//                                    // Navigate back to start
//                                    navController.navigate("other") {
//                                        popUpTo("welcome") { inclusive = true }
//                                    }
//                                }
//                            }
//                        }
//                    }, modifier = Modifier.particlize(buttonController)
//                ) {
//                    Text("Restart Demo")
//                }
//            }
//        }
//    }
//}
//
//
//@Composable
//fun OtherEffectScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title and description
//    val textController = rememberParticleEffect()
//
//    // Controller for button
//    val buttonController = rememberParticleEffect()
//
//    // Track if animations are complete
//    var isAnimating by remember { mutableStateOf(false) }
//    var showElements by remember { mutableStateOf(false) }
//
//    // Auto-animate the text with assembly effect when screen appears
//    LaunchedEffect(Unit) {
//        showElements = true
//        delay(300)
//        textController.start(
//            context = context, effect = ParticlePresets.directionalAssembly()
//        )
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Column(
//                    modifier = Modifier.particlize(textController),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "Confetti Effects",
//                        style = MaterialTheme.typography.headlineMedium,
//                        textAlign = TextAlign.Center
//                    )
//
//                    Spacer(modifier = Modifier.height(12.dp))
//
//                    Text(
//                        "Perfect for celebrations and special moments in your app",
//                        style = MaterialTheme.typography.bodyLarge,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }
//
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(16.dp),
//                modifier = Modifier.fillMaxHeight(0.8f),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(otherShowcaseItems) { item ->
//                    EffectShowcaseItem(
//                        title = item.title, effect = item.effect
//                    )
//                }
//            }
//
//
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Button(
//                    onClick = {
//                        if (!isAnimating) {
//                            isAnimating = true
//                            scope.launch {
//                                buttonController.start(
//                                    context = context, effect = ParticlePresets.confetti()
//                                ) {
//                                    // Navigate back to start
//                                    navController.navigate("meme") {
//                                        popUpTo("welcome") { inclusive = true }
//                                    }
//                                }
//                            }
//                        }
//                    }, modifier = Modifier.particlize(buttonController)
//                ) {
//                    Text("meme Demo")
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun MemeEffectScreen(navController: NavHostController) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    // Controller for title and description
//    val textController = rememberParticleEffect()
//
//    // Controller for button
//    val buttonController = rememberParticleEffect()
//    val memeController = rememberParticleEffect()
//
//    // Track if animations are complete
//    var isAnimating by remember { mutableStateOf(false) }
//    var showElements by remember { mutableStateOf(false) }
//
//    // Auto-animate the text with assembly effect when screen appears
//    LaunchedEffect(Unit) {
//        showElements = true
//        delay(300)
//        textController.start(
//            context = context, effect = ParticlePresets.reassembly()
//        )
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                "That meme",
//                style = MaterialTheme.typography.headlineMedium,
//                textAlign = TextAlign.Center
//            )
//
//            Image(
//                painter = painterResource(id = R.drawable.meme),
//                contentDescription = "Demo Image for meme",
//                modifier = Modifier
//                    .size(300.dp)
//                    .particlize(memeController)
//                    .clickable {
//                        scope.launch {
//                            memeController.start(
//                                context,
//                                EffectBuilder().withName("Disintegration")
//                                    .withDuration(3000.milliseconds).withEmissionStrategy(
//                                        DirectionalEmissionStrategy(
//                                            direction = DirectionalEmissionStrategy.Direction. BOTTOM_TO_TOP,
//                                            emissionDelayMs = 50
//                                        )
//
////                                        InstantEmissionStrategy(
////                                            sortMode = InstantEmissionStrategy.SortMode.BOTTOM_TO_TOP
////                                        )
//                                    ).withPhysicsStrategy(
//                                        DriftPhysics(
//                                            horizontalStrength = 0.7f,
//                                            verticalStrength = 0.7f,
//                                            directionAngle = -135f, // Up-right drift
//                                            directionVariance = 30f,
//                                            turbulenceStrength = 0.4f
//                                        )
//                                    ).withAppearanceStrategy(
//                                        FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
//                                    ).withAppearanceStrategy(
//                                        ScaleStrategy(
//                                            scaleMode = ScaleStrategy.ScaleMode.GROW,
//                                            minScale = 0.1f,
//                                            maxScale = 1.5f,
//                                            scaleRate = 3f
//                                        )
//                                    )
//                                    .withParticleShape(CanvasRectangleShape())
//                                    .withParticleLifetime(
//                                        (3000 * 0.6).milliseconds, (3000 * 0.9).milliseconds
//                                    )
//                                    .withParticleDensity(5)
//                                    .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
//                                    .build()
//                            )
//                        }
//                    },
//                contentScale = ContentScale.Crop
//            )
//
//            AnimatedVisibility(
//                visible = showElements, enter = fadeIn()
//            ) {
//                Button(
//                    onClick = {
//                        if (!isAnimating) {
//                            isAnimating = true
//                            scope.launch {
//                                buttonController.start(
//                                    context = context, effect = ParticlePresets.confetti()
//                                ) {
//                                    navController.navigate("welcome") {
//                                        popUpTo("welcome") { inclusive = true }
//                                    }
//                                }
//                            }
//                        }
//                    }, modifier = Modifier.particlize(buttonController)
//                ) {
//                    Text("Restart Demo")
//                }
//            }
//        }
//    }
//}