package com.binissa.particlize.showcase

import android.graphics.Color
import com.binissa.particlize.lib.config.EffectBuilder
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasRectangleShape
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasStarShape
import com.binissa.particlize.lib.strategy.appearance.ColorTransformStrategy
import com.binissa.particlize.lib.strategy.appearance.FadeStrategy
import com.binissa.particlize.lib.strategy.appearance.RotationStrategy
import com.binissa.particlize.lib.strategy.appearance.ScaleStrategy
import com.binissa.particlize.lib.strategy.emission.AssemblyEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.InstantEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.PatternEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RadialEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RandomEmissionStrategy
import com.binissa.particlize.lib.strategy.physics.AssemblyPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.DriftPhysics
import com.binissa.particlize.lib.strategy.physics.ExplosionPhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.GravityWellPhysics
import com.binissa.particlize.lib.strategy.physics.RandomFirePhysicsStrategy
import com.binissa.particlize.lib.strategy.physics.VortexPhysics
import kotlin.time.Duration.Companion.milliseconds

/**
 * Data models for showcase items
 */
data class ShowcaseItem(
    val title: String, val effect: ParticleEffect
)

data class PhysicsShowcaseItem(
    val title: String, val effect: ParticleEffect, val description: String
)

data class AssemblyShowcaseItem(
    val title: String, val effect: ParticleEffect, val description: String
)

// DirectionalEmissionStrategy showcase items
val directionalShowcaseItems = listOf(
    ShowcaseItem(
        title = "Left to Right",
        effect = EffectBuilder().withName("Left to Right").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT,
                    emissionRate = 3,
                    emissionDelay = 50.milliseconds
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.2f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleDensity(6).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Right to Left",
        effect = EffectBuilder().withName("Right to Left").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.RIGHT_TO_LEFT,
                    emissionRate = 3,
                    emissionDelay = 50.milliseconds

                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.2f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Top to Bottom",
        effect = EffectBuilder().withName("Top to Bottom").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                    emissionRate = 3,
                    emissionDelay = 50.milliseconds

                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.2f, verticalStrength = 0.7f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Bottom to Top",
        effect = EffectBuilder().withName("Bottom to Top").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP,
                    emissionRate = 3,
                    emissionDelay = 50.milliseconds

                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.2f, verticalStrength = 0.7f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)

// RadialEmissionStrategy showcase items
val radialShowcaseItems = listOf(
    ShowcaseItem(
        title = "Center Out",
        effect = EffectBuilder().withName("Center Out").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 2f, initialVelocityMax = 5f, gravityY = 0f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Edge In",
        effect = EffectBuilder().withName("Edge In").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.EDGE_IN
                )
            ).withPhysicsStrategy(
                GravityWellPhysics(
                    wells = listOf(
                        GravityWellPhysics.GravityWell(
                            strength = 1.5f, isRepulsive = false
                        )
                    )
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Explosion",
        effect = EffectBuilder().withName("Explosion").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.EXPLOSION
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 3f, initialVelocityMax = 7f, gravityY = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    ), ShowcaseItem(
        title = "Implosion",
        effect = EffectBuilder().withName("Implosion").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.IMPLOSION
                )
            ).withPhysicsStrategy(
                GravityWellPhysics(
                    wells = listOf(
                        GravityWellPhysics.GravityWell(
                            strength = 2.0f,
                            isRepulsive = false,
                            falloff = GravityWellPhysics.GravityWell.Falloff.INVERSE_SQUARE
                        )
                    ), initialVelocity = 0.3f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK, minScale = 0.1f, maxScale = 1.0f
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    )
)

// PatternEmissionStrategy showcase items
val patternShowcaseItems = listOf(
    ShowcaseItem(
        title = "Spiral",
        effect = EffectBuilder().withName("Spiral").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.SPIRAL, clockwise = true
                )
            ).withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 1.5f, pullStrength = 0.2f, clockwise = true
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Vortex",
        effect = EffectBuilder().withName("Vortex").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.VORTEX, clockwise = true
                )
            ).withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 2.0f, pullStrength = 0.3f, turbulence = 0.2f, clockwise = true
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Ripple",
        effect = EffectBuilder().withName("Ripple").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.RIPPLE
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.3f,
                    verticalStrength = 0.3f,
                    directionVariance = 60f,
                    turbulenceStrength = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Grid",
        effect = EffectBuilder().withName("Grid").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.GRID
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.3f, verticalStrength = 0.3f, directionVariance = 30f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Zigzag",
        effect = EffectBuilder().withName("Zigzag").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.ZIGZAG
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.3f, directionVariance = 45f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Heartbeat",
        effect = EffectBuilder().withName("Heartbeat").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.HEARTBEAT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.3f,
                    verticalStrength = 0.3f,
                    directionVariance = 60f,
                    gravity = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.PULSE)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Shatter",
        effect = EffectBuilder().withName("Shatter").withDuration(1800.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.SHATTER
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 2f, initialVelocityMax = 5f, gravityY = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleShape(CanvasRectangleShape())
            .withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    )
)

// RandomEmissionStrategy showcase items
val randomShowcaseItems = listOf(
    ShowcaseItem(
        title = "Pure Random",
        effect = EffectBuilder().withName("Pure Random").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RandomEmissionStrategy(
                    randomnessMode = RandomEmissionStrategy.RandomnessMode.PURE_RANDOM
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Clustered",
        effect = EffectBuilder().withName("Clustered").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RandomEmissionStrategy(
                    randomnessMode = RandomEmissionStrategy.RandomnessMode.CLUSTERED
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Chaotic",
        effect = EffectBuilder().withName("Chaotic").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RandomEmissionStrategy(
                    randomnessMode = RandomEmissionStrategy.RandomnessMode.CHAOTIC
                )
            ).withPhysicsStrategy(
                RandomFirePhysicsStrategy(
                    velocityMin = 1f, velocityMax = 4f, turbulence = 0.5f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Popcorn",
        effect = EffectBuilder().withName("Popcorn").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RandomEmissionStrategy(
                    randomnessMode = RandomEmissionStrategy.RandomnessMode.POPCORN, groupSize = 8
                )
            ).withPhysicsStrategy(
                RandomFirePhysicsStrategy(
                    velocityMin = 2f, velocityMax = 5f, gravityMin = 0.05f, gravityMax = 0.15f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)

// InstantEmissionStrategy showcase items
val instantShowcaseItems = listOf(
    ShowcaseItem(
        title = "Random Sort",
        effect = EffectBuilder().withName("Random Sort").withDuration(1500.milliseconds)
            .withEmissionStrategy(
                InstantEmissionStrategy(
                    burstDurationMs = 100, sortMode = InstantEmissionStrategy.SortMode.RANDOM
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 2f, initialVelocityMax = 5f, gravityY = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    ), ShowcaseItem(
        title = "Inside Out",
        effect = EffectBuilder().withName("Inside Out").withDuration(1500.milliseconds)
            .withEmissionStrategy(
                InstantEmissionStrategy(
                    burstDurationMs = 100, sortMode = InstantEmissionStrategy.SortMode.INSIDE_OUT
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 2f, initialVelocityMax = 5f, gravityY = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    ), ShowcaseItem(
        title = "Outside In",
        effect = EffectBuilder().withName("Outside In").withDuration(1500.milliseconds)
            .withEmissionStrategy(
                InstantEmissionStrategy(
                    burstDurationMs = 100, sortMode = InstantEmissionStrategy.SortMode.OUTSIDE_IN
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 2f, initialVelocityMax = 5f, gravityY = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    ), ShowcaseItem(
        title = "Left to Right",
        effect = EffectBuilder().withName("Left to Right").withDuration(1500.milliseconds)
            .withEmissionStrategy(
                InstantEmissionStrategy(
                    burstDurationMs = 100, sortMode = InstantEmissionStrategy.SortMode.LEFT_TO_RIGHT
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 2f, initialVelocityMax = 5f, gravityY = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    )
)

// Physics showcase items
val physicsShowcaseItems = listOf(
    PhysicsShowcaseItem(
        title = "Explosion Physics",
        effect = EffectBuilder().withName("Explosion Physics").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 3f,
                    initialVelocityMax = 7f,
                    velocityDecay = 0.96f,
                    gravityY = 0.12f,
                    randomness = 0.3f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build(),
        description = "Particles move outward with initial velocity, subject to gravity and decay"
    ), PhysicsShowcaseItem(
        title = "Random Fire Physics",
        effect = EffectBuilder().withName("Random Fire Physics").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                RandomFirePhysicsStrategy(
                    velocityMin = 2f,
                    velocityMax = 6f,
                    accelerationMin = -0.4f,
                    accelerationMax = 0.6f,
                    gravityMin = 0.05f,
                    gravityMax = 0.15f,
                    turbulence = 0.7f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleShape(CanvasStarShape())
            .withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build(),
        description = "Chaotic, unpredictable movement with random directional changes"
    ), PhysicsShowcaseItem(
        title = "Vortex Physics",
        effect = EffectBuilder().withName("Vortex Physics").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.SPIRAL
                )
            ).withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 2.0f,
                    acceleration = 0.5f,
                    pullStrength = 0.3f,
                    initialOutwardForce = 0.2f,
                    turbulence = 0.2f,
                    clockwise = true
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build(),
        description = "Swirling movement with rotation and inward/outward forces"
    ), PhysicsShowcaseItem(
        title = "Gravity Well Physics",
        effect = EffectBuilder().withName("Gravity Well Physics").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.EDGE_IN
                )
            ).withPhysicsStrategy(
                GravityWellPhysics(
                    wells = listOf(
                        GravityWellPhysics.GravityWell(
                            strength = 2.0f,
                            isRepulsive = false,
                            falloff = GravityWellPhysics.GravityWell.Falloff.INVERSE_SQUARE
                        )
                    ), damping = 0.98f, initialVelocity = 0.3f, turbulence = 0.1f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build(),
        description = "Attraction or repulsion forces that pull particles like a black hole"
    ), PhysicsShowcaseItem(
        title = "Drift Physics",
        effect = EffectBuilder().withName("Drift Physics").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                    emissionDelay = 50.milliseconds
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f,
                    verticalStrength = 1.0f,
                    directionAngle = 100f,
                    directionVariance = 30f,
                    horizontalBias = 0.2f,
                    verticalBias = 0.3f,
                    gravity = 0.15f,
                    windStrength = 0.3f,
                    windGustiness = 0.5f,
                    turbulenceStrength = 0.2f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build(),
        description = "Gentle drifting movement with gravity, wind, and turbulence effects"
    )
)

// Assembly strategy showcase items
val assemblyShowcaseItems = listOf(
    AssemblyShowcaseItem(
        title = "Random ",
        effect = EffectBuilder().withName("Random Assembly").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.RANDOM_OFFSCREEN,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.UNIFORM,
                    scatterFactor = 2.0f,
                    staggering = 0.3f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.0f,
                    jitter = 0.3f,
                    easingType = AssemblyPhysicsStrategy.EasingType.ELASTIC
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.GROW, minScale = 0.3f, maxScale = 1.0f
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(1000).withParticleDensity(4)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles start from random offscreen positions and assemble uniformly"
    ), AssemblyShowcaseItem(
        title = "From Bottom",
        effect = EffectBuilder().withName("From Bottom").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.FROM_BOTTOM,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.BOTTOM_TO_TOP,
                    scatterFactor = 1.5f,
                    staggering = 0.4f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.2f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.SMOOTH
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles rise from the bottom of the screen, assembling from bottom to top"
    ), AssemblyShowcaseItem(
        title = "From Top",
        effect = EffectBuilder().withName("From Top").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.FROM_TOP,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.TOP_TO_BOTTOM,
                    scatterFactor = 1.5f,
                    staggering = 0.4f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.2f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.BOUNCE
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles fall from the top of the screen, assembling from top to bottom"
    ), AssemblyShowcaseItem(
        title = "From Sides",
        effect = EffectBuilder().withName("From Sides").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.FROM_SIDES,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.LEFT_TO_RIGHT,
                    scatterFactor = 1.5f,
                    staggering = 0.4f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.2f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.ELASTIC
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles come in from left and right sides, assembling from left to right"
    ), AssemblyShowcaseItem(
        title = "Spiral In",
        effect = EffectBuilder().withName("Spiral In").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.SPIRAL_IN,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.OUTSIDE_IN,
                    scatterFactor = 2.0f,
                    staggering = 0.3f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.0f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.SMOOTH
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 120f, randomDirection = false
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles start in a spiral pattern and assemble from outside in"
    ), AssemblyShowcaseItem(
        title = "Inside Out",
        effect = EffectBuilder().withName("Inside Out").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.RANDOM_OFFSCREEN,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.INSIDE_OUT,
                    scatterFactor = 2.0f,
                    staggering = 0.4f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.1f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.ELASTIC
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles assemble starting from the center and moving outward"
    ), AssemblyShowcaseItem(
        title = "Outside In",
        effect = EffectBuilder().withName("Outside In").withDuration(3000.milliseconds)
            .withEmissionStrategy(
                AssemblyEmissionStrategy(
                    startPosition = AssemblyEmissionStrategy.StartPosition.RANDOM_OFFSCREEN,
                    assemblyOrder = AssemblyEmissionStrategy.AssemblyOrder.OUTSIDE_IN,
                    scatterFactor = 2.0f,
                    staggering = 0.4f
                )
            ).withPhysicsStrategy(
                AssemblyPhysicsStrategy(
                    speed = 1.1f,
                    jitter = 0.2f,
                    easingType = AssemblyPhysicsStrategy.EasingType.BOUNCE
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_IN)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.ASSEMBLY).build(),
        description = "Particles assemble starting from the edges and moving inward"
    )
)

// Fade strategy showcase items
val fadeShowcaseItems = listOf(
    ShowcaseItem(
        title = "Fade In",
        effect = EffectBuilder().withName("Fade In").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(
                    fadeMode = FadeStrategy.FadeMode.FADE_IN
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Fade Out",
        effect = EffectBuilder().withName("Fade Out").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(
                    fadeMode = FadeStrategy.FadeMode.FADE_OUT
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Pulse",
        effect = EffectBuilder().withName("Pulse").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(
                    fadeMode = FadeStrategy.FadeMode.PULSE
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Delayed Fade",
        effect = EffectBuilder().withName("Delayed Fade").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(
                    fadeMode = FadeStrategy.FadeMode.FADE_OUT, fadeStartDelay = 500.milliseconds
                )
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)

// Scale strategy showcase items
val scaleShowcaseItems = listOf(
    ShowcaseItem(
        title = "Grow",
        effect = EffectBuilder().withName("Grow").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.GROW, minScale = 0.2f, maxScale = 1.5f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Shrink",
        effect = EffectBuilder().withName("Shrink").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK, minScale = 0.2f, maxScale = 1.0f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Pulse",
        effect = EffectBuilder().withName("Scale Pulse").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.PULSE, minScale = 0.5f, maxScale = 1.5f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)

// Rotation strategy showcase items
val rotationShowcaseItems = listOf(
    ShowcaseItem(
        title = "Clockwise",
        effect = EffectBuilder().withName("Clockwise").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withParticleShape(CanvasRectangleShape()).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f, randomDirection = false
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Random Direction",
        effect = EffectBuilder().withName("Random Direction").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withParticleShape(CanvasRectangleShape()).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f, randomDirection = true
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Accelerating",
        effect = EffectBuilder().withName("Accelerating").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withParticleShape(CanvasRectangleShape()).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f, randomDirection = true, rotationAcceleration = 2f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)

// Color transform strategy showcase items
val colorShowcaseItems = listOf(
    ShowcaseItem(
        title = "To Red",
        effect = EffectBuilder().withName("To Red").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                ColorTransformStrategy(
                    targetColor = Color.RED, duration = 1000.milliseconds
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "To Gold",
        effect = EffectBuilder().withName("To Gold").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                ColorTransformStrategy(
                    targetColor = Color.rgb(255, 215, 0), // Gold
                    duration = 1000.milliseconds
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Delayed Transform",
        effect = EffectBuilder().withName("Delayed Transform").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                ColorTransformStrategy(
                    targetColor = Color.BLUE,
                    startDelay = 500.milliseconds,
                    duration = 500.milliseconds
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)

// Shape showcase items
val shapeShowcaseItems = listOf(
    ShowcaseItem(
        title = "Circle",
        effect = EffectBuilder().withName("Circle").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withParticleShape(CanvasCircleShape())
            .withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Rectangle",
        effect = EffectBuilder().withName("Rectangle").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f, randomDirection = true
                )
            ).withParticleShape(CanvasRectangleShape())
            .withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Star",
        effect = EffectBuilder().withName("Star").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f, verticalStrength = 0.5f, directionVariance = 60f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 45f, randomDirection = true
                )
            ).withParticleShape(CanvasStarShape())
            .withParticleLifetime(4000.milliseconds, 4500.milliseconds)
            .withMaxEmissionsPerFrame(500).withParticleDensity(6)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)


val confettiShowcaseItems = listOf(
    ShowcaseItem(
        title = "Party Confetti",
        effect = EffectBuilder().withName("Party Confetti").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f,
                    verticalStrength = 1.2f,
                    directionAngle = 90f, // Down
                    directionVariance = 45f,
                    gravity = 0.2f,
                    turbulenceStrength = 0.3f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f, randomDirection = true
                )
            ).withParticleShape(CanvasCircleShape())
            .withParticleLifetime(1000.milliseconds, 1800.milliseconds).withParticleDensity(4)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    ), ShowcaseItem(
        title = "Celebration",
        effect = EffectBuilder().withName("Celebration").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.CENTER_OUT
                )
            ).withPhysicsStrategy(
                ExplosionPhysicsStrategy(
                    initialVelocityMin = 4f,
                    initialVelocityMax = 8f,
                    gravityY = 0.15f,
                    randomness = 0.5f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.PULSE)
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 180f, randomDirection = true
                )
            ).withParticleShape(CanvasStarShape())
            .withParticleLifetime(1200.milliseconds, 1800.milliseconds).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.INSTANT).build()
    ), ShowcaseItem(
        title = "Rain Down",
        effect = EffectBuilder().withName("Rain Down").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                DirectionalEmissionStrategy(
                    direction = DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM,
                    emissionRate = 4,
                    emissionDelayMs = 50
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.3f,
                    verticalStrength = 1.5f,
                    directionAngle = 90f, // Down
                    directionVariance = 20f,
                    gravity = 0.25f,
                    windStrength = 0.2f,
                    windGustiness = 0.5f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f, randomDirection = true
                )
            ).withParticleLifetime(1500.milliseconds, 2000.milliseconds).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)
val otherShowcaseItems = listOf(
    ShowcaseItem(
        title = "Magic Sparkle",
        effect = EffectBuilder().withName("Magic Sparkle").withDuration(2000.milliseconds)
            .withEmissionStrategy(
                PatternEmissionStrategy(
                    pattern = PatternEmissionStrategy.Pattern.SPIRAL,
                    clockwise = true,
                    randomVariation = 0.2f
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.5f,
                    verticalStrength = 0.5f,
                    directionAngle = -30f, // Up and right
                    gravity = -0.05f, // Slight upward drift
                    turbulenceStrength = 0.2f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.PULSE)
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.PULSE, minScale = 0.5f, maxScale = 1.5f
                )
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f, randomDirection = true
                )
            ).withParticleShape(CanvasStarShape())
            .withParticleLifetime(1200.milliseconds, 1800.milliseconds).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ),

    ShowcaseItem(
        title = "Magic Sparkle2",
        effect = EffectBuilder()
            .withName("Magic Sparkle 2")
            .withDuration(5000.milliseconds)
            .withEmissionStrategy(
                RandomEmissionStrategy()
            ).withPhysicsStrategy(
                RandomFirePhysicsStrategy()
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.GROW,
                    minScale = 0.5f,
                    maxScale = 1.5f,
                    scaleRate = 5f
                )
            )
            .withAppearanceStrategy(
                RotationStrategy(
                    randomDirection = true
                )
            ).withParticleShape(CanvasStarShape())
            .withParticleLifetime(8000.milliseconds, 10000.milliseconds).withParticleDensity(5)
            .withMaxEmissionsPerFrame(10)
            .withContentDisappearanceMode(ContentDisappearanceMode.NONE).build()
    ), ShowcaseItem(
        title = "Thanos Effect Telegram  Message Deletion",
        effect = EffectBuilder().withName("Disintegration").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                InstantEmissionStrategy(
                    sortMode = InstantEmissionStrategy.SortMode.LEFT_TO_RIGHT
                )
            ).withPhysicsStrategy(
                DriftPhysics(
                    horizontalStrength = 0.7f,
                    verticalStrength = 0.7f,
                    directionAngle = -135f, // Up-right drift
                    directionVariance = 30f,
                    gravity = 0.1f,
                    turbulenceStrength = 0.8f
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK, minScale = 0.1f, maxScale = 1.0f
                )
            ).withParticleLifetime(1200.milliseconds, 1800.milliseconds).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    ), ShowcaseItem(
        title = "Black Hole",
        effect = EffectBuilder().withName("Black Hole").withDuration(2500.milliseconds)
            .withEmissionStrategy(
                RadialEmissionStrategy(
                    radiationMode = RadialEmissionStrategy.RadiationMode.EDGE_IN
                )
            ).withPhysicsStrategy(
                VortexPhysics(
                    rotationSpeed = 2.0f, pullStrength = 0.5f, turbulence = 0.1f, clockwise = true
                )
            ).withAppearanceStrategy(
                FadeStrategy(FadeStrategy.FadeMode.FADE_OUT)
            ).withAppearanceStrategy(
                ScaleStrategy(
                    scaleMode = ScaleStrategy.ScaleMode.SHRINK, minScale = 0.1f, maxScale = 1.0f
                )
            ).withAppearanceStrategy(
                RotationStrategy(
                    rotationSpeed = 90f, randomDirection = false, rotationAcceleration = 2.0f
                )
            ).withParticleLifetime(1500.milliseconds, 2000.milliseconds).withParticleDensity(5)
            .withContentDisappearanceMode(ContentDisappearanceMode.PROGRESSIVE).build()
    )
)
