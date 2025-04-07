package com.binissa.particlize.showcase


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.binissa.particlize.R
import com.binissa.particlize.lib.compose.particlize
import com.binissa.particlize.lib.compose.rememberParticleEffect
import com.binissa.particlize.lib.config.EffectBuilder
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasStarShape
import com.binissa.particlize.lib.strategy.appearance.FadeStrategy
import com.binissa.particlize.lib.strategy.appearance.RotationStrategy
import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
import com.binissa.particlize.lib.strategy.emission.AssemblyEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.PatternEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RadialEmissionStrategy
import com.binissa.particlize.lib.strategy.physics.AssemblyPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.DriftPhysics
import com.binissa.particlize.lib.strategy.physics.ExplosionPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.VortexPhysics
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Base screen for all effect showcase screens.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectShowcaseScreen(
    navController: NavController,
    title: String,
    description: String,
    effects: List<NamedEffect>
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()
    var isPlaying by remember { mutableStateOf(false) }
    var currentEffectIndex by remember { mutableStateOf(0) }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hamburger),
                    contentDescription = "Effect Demo Image",
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .particlize(controller)
                        .clickable {
                            if (!isPlaying) {
                                isPlaying = true
                                scope.launch {
                                    controller.start(
                                        context = context,
                                        effect = effects[currentEffectIndex].effect
                                    ) {
                                        isPlaying = false
                                    }
                                }
                            }
                        },
                    contentScale = ContentScale.Crop
                )

            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Current Effect: ${effects[currentEffectIndex].name}",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                EffectSelector(
                    effects = effects.map { it.name },
                    selectedIndex = currentEffectIndex,
                    onEffectSelected = {
                        currentEffectIndex = it
                    }
                )
            }
        }
    }
}

/**
 * Component to select between different effect options.
 */
@Composable
fun EffectSelector(
    effects: List<String>,
    selectedIndex: Int,
    onEffectSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Text(
            text = "Select an Effect:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            effects.forEachIndexed { index, effectName ->
                androidx.compose.material3.FilterChip(
                    selected = index == selectedIndex,
                    onClick = { onEffectSelected(index) },
                    label = { Text(effectName) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

/**
 * Data class to hold effect name and implementation.
 */
data class NamedEffect(
    val name: String,
    val effect: ParticleEffect
)

/**
 * Directional effects showcase screen.
 */
@Composable
fun DirectionalEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Left to Right",
            effect = EffectBuilder()
                .withName("Left to Right")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    DirectionalEmissionStrategy(
                        direction = DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT,
                        emissionDelayMs = 50
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.5f,
                        verticalStrength = 0.2f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Right to Left",
            effect = EffectBuilder()
                .withName("Right to Left")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    DirectionalEmissionStrategy(
                        direction = DirectionalEmissionStrategy.Direction.RIGHT_TO_LEFT,
                        emissionDelayMs = 50
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.5f,
                        verticalStrength = 0.2f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Top to Bottom",
            effect = EffectBuilder()
                .withName("Top to Bottom")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    DirectionalEmissionStrategy(
                        direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                        emissionDelayMs = 50
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.2f,
                        verticalStrength = 0.7f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Bottom to Top",
            effect = EffectBuilder()
                .withName("Bottom to Top")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    DirectionalEmissionStrategy(
                        direction = DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP,
                        emissionDelayMs = 50
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.2f,
                        verticalStrength = 0.7f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Directional Effects",
        description = "Particles emit in a specific direction, creating flowing movement patterns",
        effects = effects
    )
}

/**
 * Radial effects showcase screen.
 */
@Composable
fun RadialEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Center Out",
            effect = EffectBuilder()
                .withName("Center Out")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.5f,
                        verticalStrength = 0.5f,
                        directionVariance = 60f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Edge In",
            effect = EffectBuilder()
                .withName("Edge In")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.EDGE_IN
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.4f,
                        verticalStrength = 0.4f,
                        directionVariance = 40f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Implosion",
            effect = EffectBuilder()
                .withName("Implosion")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.IMPLOSION
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.7f,
                        verticalStrength = 0.7f,
                        directionAngle = 180f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    ScaleStrategy(
                        scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                        minScale = 0.1f
                    )
                )
                .withParticleLifetime(800.milliseconds, 1600.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Radial Effects",
        description = "Particles emit radially from center to edge or edge to center",
        effects = effects
    )
}

/**
 * Explosion effects showcase screen.
 */
@Composable
fun ExplosionEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Standard",
            effect = EffectBuilder()
                .withName("Standard Explosion")
                .withDuration(1500.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    ExplosionPhysicsStrategy(
                        initialVelocityMin = 4f,
                        initialVelocityMax = 8f,
                        gravityY = 0.1f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    ScaleStrategy(
                        scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                        minScale = 0.2f
                    )
                )
                .withParticleLifetime(800.milliseconds, 1500.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
                .build()
        ),
        NamedEffect(
            name = "Fireworks",
            effect = EffectBuilder()
                .withName("Fireworks")
                .withDuration(1500.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    ExplosionPhysicsStrategy(
                        initialVelocityMin = 5f,
                        initialVelocityMax = 10f,
                        gravityY = 0.2f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 180f,
                        randomDirection = true
                    )
                )
                .withParticleShape(CanvasStarShape())
                .withParticleLifetime(800.milliseconds, 1500.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
                .build()
        ),
        NamedEffect(
            name = "Subtle",
            effect = EffectBuilder()
                .withName("Subtle Explosion")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    ExplosionPhysicsStrategy(
                        initialVelocityMin = 2f,
                        initialVelocityMax = 4f,
                        gravityY = 0f,
                        randomness = 0.2f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(1200.milliseconds, 1800.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Explosion Effects",
        description = "Particles explode outward from the center",
        effects = effects
    )
}

/**
 * Vortex effects showcase screen.
 */
@Composable
fun VortexEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Spiral",
            effect = EffectBuilder()
                .withName("Spiral")
                .withDuration(2500.milliseconds)
                .withEmissionStrategy(
                    PatternEmissionStrategy(
                        pattern = PatternEmissionStrategy.Pattern.SPIRAL,
                        clockwise = true
                    )
                )
                .withPhysicsStrategy(
                    VortexPhysics(
                        rotationSpeed = 1.5f,
                        pullStrength = 0.2f,
                        clockwise = true
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(1200.milliseconds, 2000.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Vortex",
            effect = EffectBuilder()
                .withName("Vortex")
                .withDuration(2500.milliseconds)
                .withEmissionStrategy(
                    PatternEmissionStrategy(
                        pattern = PatternEmissionStrategy.Pattern.VORTEX,
                        clockwise = true
                    )
                )
                .withPhysicsStrategy(
                    VortexPhysics(
                        rotationSpeed = 2.0f,
                        pullStrength = 0.3f,
                        turbulence = 0.2f,
                        clockwise = true
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(1200.milliseconds, 2000.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Counter-Clockwise",
            effect = EffectBuilder()
                .withName("Counter-Clockwise")
                .withDuration(2500.milliseconds)
                .withEmissionStrategy(
                    PatternEmissionStrategy(
                        pattern = PatternEmissionStrategy.Pattern.SPIRAL,
                        clockwise = false
                    )
                )
                .withPhysicsStrategy(
                    VortexPhysics(
                        rotationSpeed = 1.5f,
                        acceleration = 1.0f,
                        pullStrength = 0.2f,
                        clockwise = false
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withParticleLifetime(1200.milliseconds, 2000.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Vortex Effects",
        description = "Particles swirl in spiral patterns",
        effects = effects
    )
}

/**
 * Assembly effects showcase screen.
 */
@Composable
fun AssemblyEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Random Assembly",
            effect = EffectBuilder()
                .withName("Random Assembly")
                .withDuration(3000.milliseconds)
                .withEmissionStrategy(
                    AssemblyEmissionStrategy(
                        startPosition = AssemblyEmissionStrategy.StartPosition.RANDOM_OFFSCREEN,
                        assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.UNIFORM,
                        scatterFactor = 2.0f,
                        staggering = 0.3f
                    )
                )
                .withPhysicsStrategy(
                    AssemblyPhysicsStrategy(
                        speed = 1.0f,
                        jitter = 0.3f,
                        easingType = AssemblyPhysicsStrategy.EasingType.ELASTIC
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
                )
                .withAppearanceStrategy(
                    ScaleStrategy(
                        scaleMode = ScaleStrategy.ScaleMode.GROW,
                        minScale = 0.3f,
                        maxScale = 1.0f
                    )
                )
                .withParticleLifetime(2500.milliseconds, 3500.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY)
                .build()
        ),
        NamedEffect(
            name = "From Bottom",
            effect = EffectBuilder()
                .withName("From Bottom")
                .withDuration(3000.milliseconds)
                .withEmissionStrategy(
                    AssemblyEmissionStrategy(
                        startPosition = AssemblyEmissionStrategy.StartPosition.FROM_BOTTOM,
                        assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.BOTTOM_TO_TOP,
                        scatterFactor = 1.5f,
                        staggering = 0.4f
                    )
                )
                .withPhysicsStrategy(
                    AssemblyPhysicsStrategy(
                        speed = 1.2f,
                        jitter = 0.2f,
                        easingType = AssemblyPhysicsStrategy.EasingType.SMOOTH
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
                )
                .withParticleLifetime(2500.milliseconds, 3500.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY)
                .build()
        ),
        NamedEffect(
            name = "Spiral In",
            effect = EffectBuilder()
                .withName("Spiral In")
                .withDuration(3000.milliseconds)
                .withEmissionStrategy(
                    AssemblyEmissionStrategy(
                        startPosition = AssemblyEmissionStrategy.StartPosition.SPIRAL_IN,
                        assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.OUTSIDE_IN,
                        scatterFactor = 2.0f,
                        staggering = 0.3f
                    )
                )
                .withPhysicsStrategy(
                    AssemblyPhysicsStrategy(
                        speed = 1.0f,
                        jitter = 0.2f,
                        easingType = AssemblyPhysicsStrategy.EasingType.ELASTIC
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 120f,
                        randomDirection = false
                    )
                )
                .withParticleLifetime(2500.milliseconds, 3500.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Assembly Effects",
        description = "Particles assemble to form the original image",
        effects = effects
    )
}

/**
 * Confetti effects showcase screen.
 */
@Composable
fun ConfettiEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Party Confetti",
            effect = EffectBuilder()
                .withName("Party Confetti")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.5f,
                        verticalStrength = 1.2f,
                        directionAngle = 90f, // Down
                        directionVariance = 45f,
                        gravity = 0.2f,
                        turbulenceStrength = 0.3f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 180f,
                        randomDirection = true
                    )
                )
                .withParticleShape(CanvasCircleShape())
                .withParticleLifetime(1000.milliseconds, 1800.milliseconds)
                .withParticleDensity(4)
                .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
                .build()
        ),
        NamedEffect(
            name = "Celebration",
            effect = EffectBuilder()
                .withName("Celebration")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    ExplosionPhysicsStrategy(
                        initialVelocityMin = 4f,
                        initialVelocityMax = 8f,
                        gravityY = 0.15f,
                        randomness = 0.5f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.PULSE)
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 180f,
                        randomDirection = true
                    )
                )
                .withParticleShape(CanvasStarShape())
                .withParticleLifetime(1200.milliseconds, 1800.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT)
                .build()
        ),
        NamedEffect(
            name = "Rain Down",
            effect = EffectBuilder()
                .withName("Rain Down")
                .withDuration(2500.milliseconds)
                .withEmissionStrategy(
                    DirectionalEmissionStrategy(
                        direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                        emissionRate = 4,
                        emissionDelayMs = 50
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.3f,
                        verticalStrength = 1.5f,
                        directionAngle = 90f, // Down
                        directionVariance = 20f,
                        gravity = 0.25f,
                        windStrength = 0.2f,
                        windGustiness = 0.5f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 90f,
                        randomDirection = true
                    )
                )
                .withParticleLifetime(1500.milliseconds, 2000.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Confetti Effects",
        description = "Celebratory particle effects",
        effects = effects
    )
}

/**
 * Custom effects showcase screen.
 */
@Composable
fun CustomEffectsScreen(navController: NavController) {
    val effects = listOf(
        NamedEffect(
            name = "Magic Sparkle",
            effect = EffectBuilder()
                .withName("Magic Sparkle")
                .withDuration(2000.milliseconds)
                .withEmissionStrategy(
                    PatternEmissionStrategy(
                        pattern = PatternEmissionStrategy.Pattern.SPIRAL,
                        clockwise = true,
                        randomVariation = 0.2f
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.5f,
                        verticalStrength = 0.5f,
                        directionAngle = -30f, // Up and right
                        gravity = -0.05f, // Slight upward drift
                        turbulenceStrength = 0.2f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.PULSE)
                )
                .withAppearanceStrategy(
                    ScaleStrategy(
                        scaleMode = ScaleStrategy.ScaleMode.PULSE,
                        minScale = 0.5f,
                        maxScale = 1.5f
                    )
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 90f,
                        randomDirection = true
                    )
                )
                .withParticleShape(CanvasStarShape())
                .withParticleLifetime(1200.milliseconds, 1800.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Disintegration",
            effect = EffectBuilder()
                .withName("Disintegration")
                .withDuration(2500.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                    )
                )
                .withPhysicsStrategy(
                    DriftPhysics(
                        horizontalStrength = 0.7f,
                        verticalStrength = 0.7f,
                        directionAngle = -45f, // Up-right drift
                        directionVariance = 30f,
                        gravity = 0.1f,
                        turbulenceStrength = 0.3f
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    ScaleStrategy(
                        scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                        minScale = 0.3f,
                        maxScale = 1.0f
                    )
                )
                .withParticleLifetime(1200.milliseconds, 1800.milliseconds)
                .withParticleDensity(6)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        ),
        NamedEffect(
            name = "Black Hole",
            effect = EffectBuilder()
                .withName("Black Hole")
                .withDuration(2500.milliseconds)
                .withEmissionStrategy(
                    RadialEmissionStrategy(
                        radiationMode = RadialEmissionStrategy.RadiationMode.EDGE_IN
                    )
                )
                .withPhysicsStrategy(
                    VortexPhysics(
                        rotationSpeed = 2.0f,
                        pullStrength = 0.5f,
                        turbulence = 0.1f,
                        clockwise = true
                    )
                )
                .withAppearanceStrategy(
                    FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                )
                .withAppearanceStrategy(
                    ScaleStrategy(
                        scaleMode = ScaleStrategy.ScaleMode.SHRINK,
                        minScale = 0.1f,
                        maxScale = 1.0f
                    )
                )
                .withAppearanceStrategy(
                    RotationStrategy(
                        rotationSpeed = 90f,
                        randomDirection = false,
                        rotationAcceleration = 2.0f
                    )
                )
                .withParticleLifetime(1500.milliseconds, 2000.milliseconds)
                .withParticleDensity(5)
                .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE)
                .build()
        )
    )

    EffectShowcaseScreen(
        navController = navController,
        title = "Custom Effects",
        description = "Complex combined particle effects",
        effects = effects
    )
}