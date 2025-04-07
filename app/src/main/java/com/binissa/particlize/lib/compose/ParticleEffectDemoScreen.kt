package com.binissa.particlize.lib.compose


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.binissa.particlize.R
import com.binissa.particlize.lib.config.EffectBuilder
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasStarShape
import com.binissa.particlize.lib.strategy.appearance.FadeStrategy
import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
import com.binissa.particlize.lib.strategy.emission.AssemblyEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RandomEmissionStrategy
import com.binissa.particlize.lib.strategy.physics.AssemblyPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.DriftPhysics
import com.binissa.particlize.lib.strategy.physics.RandomFirePhysicsStrategy
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ParticleEffectDemoScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = rememberParticleEffect()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hamburger),
                contentDescription = "Exploding image",
                modifier = Modifier
                    .size(300.dp)
                    .particlize(controller)
                    .clickable {

                        val emissionStrategy = AssemblyEmissionStrategy(
                            startPosition = AssemblyEmissionStrategy.StartPosition.SPIRAL_IN,
                            assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.UNIFORM,
                            scatterFactor = 1.0f,  // Reduced scatter to avoid extreme positions
                            staggering = 0.4f      // Good amount of arrival variation
                        )
                        // Create physics strategy
                        val physicsStrategy = AssemblyPhysicsStrategy(
                            speed = 1f,          // Slightly slower for better visibility
                            easingType = AssemblyPhysicsStrategy.EasingType.SMOOTH // Bouncy arrival
                        )
                        // Create appearance strategies
                        val fadeStrategy = FadeStrategy(
                            fadeMode = FadeStrategy.FadeMode.FADE_IN,
                        )
                        // Create the effect
                        val assemblyEffect = ParticleEffect(
                            id = "assembly_effect_${System.currentTimeMillis()}",
                            name = "Assembly Effect",
                            duration = 3000.milliseconds,  // 3 seconds total animation
                            emissionStrategy = emissionStrategy,
                            physicsStrategy = physicsStrategy,
                            appearanceStrategies = listOf(),
                            particleMinLifetime = 3500.milliseconds,
                            particleMaxLifetime = 4500.milliseconds,
                            maxEmissionsPerFrame = 300,     // Adjust based on performance
                            particleDensity = 8,          // Lower = more particles
                            contentDisappearanceMode = ContentDisappearanceMode.ASSEMBLY
                        )
// Apply the effect

                        // Create a custom vortex effect
                        val vortexEffect = EffectBuilder().withDuration(5000.milliseconds)
                            .withParticleShape(CanvasStarShape()).withEmissionStrategy(
                                RandomEmissionStrategy()
                            ).withPhysicsStrategy(
                                DriftPhysics(
                                    windStrength = 0.5f,
                                    windDirection = 200f,
                                    turbulenceStrength = 0.4f,
                                    turbulenceScale = 0.3f,
                                )
                            ).withAppearanceStrategy(
                                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
                            ).withAppearanceStrategy(
                                ScaleStrategy(
                                    scaleMode = ScaleStrategy.ScaleMode.GROW,
                                    minScale = 0.5f,
                                    maxScale = 2.5f,
                                    scaleRate = 5f
                                )
                            )
                                .withParticleLifetime(1000.milliseconds, 2200.milliseconds)
                            .withParticleDensity(8).withMaxEmissionsPerFrame(20)
                            .withContentDisappearanceMode(ContentDisappearanceMode.NONE).build()
                        // Apply the effect using the controller
                        scope.launch {
                            controller.start(context, vortexEffect)
                        }
                    })
        }
    }
}