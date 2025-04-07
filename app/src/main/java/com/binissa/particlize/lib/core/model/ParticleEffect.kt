package com.binissa.particlize.lib.core.model

import com.binissa.particlize.lib.renderer.ParticleShapeStrategy
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
import com.binissa.particlize.lib.strategy.appearance.AppearanceStrategy
import com.binissa.particlize.lib.strategy.emission.EmissionStrategy
import com.binissa.particlize.lib.strategy.physics.PhysicsStrategy
import kotlin.time.Duration


data class ParticleEffect(
    val id: String,
    val name: String,
    val duration: Duration,
    val emissionStrategy: EmissionStrategy,
    val physicsStrategy: PhysicsStrategy,
    val appearanceStrategies: List<AppearanceStrategy>,
    val particleMinLifetime: Duration,
    val particleMaxLifetime: Duration,
    val maxEmissionsPerFrame: Int,
    val particleDensity: Int,
    val contentDisappearanceMode: ContentDisappearanceMode,
    val particleShape: ParticleShapeStrategy<CanvasContext> = CanvasCircleShape(),
    val customCanvasShapeStrategy: ParticleShapeStrategy<CanvasContext>? = null,
    val customComposeShapeStrategy: ParticleShapeStrategy<ComposeContext>? = null
)

enum class ContentDisappearanceMode {
    NONE, INSTANT, FADE, PROGRESSIVE, ASSEMBLY
}