package com.binissa.particlize.showcase

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.binissa.particlize.R
import com.binissa.particlize.lib.compose.ParticlizeComposeController
import com.binissa.particlize.lib.compose.particlize
import com.binissa.particlize.lib.compose.rememberParticleEffect
import com.binissa.particlize.lib.config.EffectBuilder
import com.binissa.particlize.lib.config.presets.ParticlePresets
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasStarShape
import com.binissa.particlize.lib.strategy.appearance.RotationStrategy
import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
import com.binissa.particlize.lib.strategy.physics.RandomFirePhysicsStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Example 1: Simple click-to-explode effect.
 * This shows the most basic usage pattern.
 */
@Composable
fun SimpleExplodeExample() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hamburger),
            contentDescription = "Clickable Image",
            modifier = Modifier
                .size(200.dp)
                .particlize(controller)
                .clickable {
                    scope.launch {
                        // Use the explosion preset when clicked
                        controller.start(
                            context = context, effect = ParticlePresets.explosion()
                        )
                    }
                })
    }
}

/**
 * Example 2: Automatic sequence of effects.
 * This demonstrates transitioning between multiple effects.
 */
@Composable
fun SequentialEffectsExample() {
    val context = LocalContext.current
    val controller = rememberParticleEffect()
    val scope = rememberCoroutineScope()

    // Track whether we've started the sequence
    var sequenceStarted by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hamburger),
            contentDescription = "Sequential Effects Image",
            modifier = Modifier
                .size(200.dp)
                .particlize(controller)
                .clickable {
                    if (!sequenceStarted) {
                        sequenceStarted = true
                        scope.launch {
                            playEffectSequence(context, controller)
                            sequenceStarted = false
                        }
                    }
                })
    }
}

/**
 * Plays a sequence of effects one after another.
 */
private suspend fun playEffectSequence(
    context: Context, controller: ParticlizeComposeController
) {
    // First effect: Vortex
    controller.start(
        context = context, effect = ParticlePresets.vortex(duration = 2000)
    )

    // Small delay between effects
    delay(200)

    // Second effect: Explosion
    controller.start(
        context = context, effect = ParticlePresets.explosion(duration = 1500)
    )

    // Small delay between effects
    delay(200)

    // Final effect: Reassembly
    controller.start(
        context = context, effect = ParticlePresets.reassembly(duration = 2500)
    )
}

/**
 * Example 3: Automatic effect on component appearance.
 * This demonstrates triggering an effect when a component appears.
 */
@Composable
fun AutoEffectOnAppearExample() {
    val context = LocalContext.current
    val controller = rememberParticleEffect()

    // Trigger effect on first composition
    LaunchedEffect(key1 = Unit) {
        controller.start(
            context = context, effect = ParticlePresets.magicSparkle(duration = 2500)
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hamburger),
            contentDescription = "Auto Effect Image",
            modifier = Modifier
                .size(200.dp)
                .particlize(controller)
        )
    }
}

/**
 * Example 4: Creating a custom effect with specific parameters.
 * This demonstrates how to create custom effects without using presets.
 */
@Composable
fun CustomEffectExample() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.hamburger),
            contentDescription = "Custom Effect Image",
            modifier = Modifier
                .size(200.dp)
                .particlize(controller)
                .clickable {
                    scope.launch {
                        // Create a custom effect using the builder pattern
                        val customEffect = EffectBuilder()
                            .withName("Custom Confetti Rain").withDuration(3000.milliseconds)
                            .withEmissionStrategy(
                                DirectionalEmissionStrategy(
                                    direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                                    emissionRate = 5,
                                    emissionDelay = 50.milliseconds
                                )
                            ).withPhysicsStrategy(
                                RandomFirePhysicsStrategy(
                                    velocityMin = 1f,
                                    velocityMax = 4f,
                                    gravityMin = 0.1f,
                                    gravityMax = 0.3f,
                                    turbulence = 0.5f
                                )
                            ).withAppearanceStrategy(
                                RotationStrategy(
                                    rotationSpeed = 120f, randomDirection = true
                                )
                            ).withAppearanceStrategy(
                                ScaleStrategy(
                                    scaleMode = ScaleStrategy.ScaleMode.PULSE,
                                    minScale = 0.7f,
                                    maxScale = 1.4f
                                )
                            ).withParticleShape(
                                CanvasStarShape()
                            ).withParticleLifetime(
                                min = 1500.milliseconds, max = 2500.milliseconds
                            ).withParticleDensity(5).withMaxEmissionsPerFrame(200)
                            .withContentDisappearanceMode(
                                ContentDisappearanceMode.PROGRESSIVE
                            ).build()

                        // Apply the custom effect
                        controller.start(
                            context = context, effect = customEffect
                        )
                    }
                })
    }
}

/**
 * Example 5: Button with confetti celebration effect.
 * Shows how to use effects for UI feedback.
 */
@Composable
fun CelebrationButtonExample() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                scope.launch {
                    controller.start(
                        context = context, effect = ParticlePresets.confetti(duration = 2000)
                    )
                }
            }, modifier = Modifier.particlize(controller)
        ) {
            Text("Celebrate!")
        }
    }
}