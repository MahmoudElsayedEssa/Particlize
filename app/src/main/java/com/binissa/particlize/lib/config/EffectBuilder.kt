package com.binissa.particlize.lib.config

import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.renderer.ParticleShapeStrategy
import com.binissa.particlize.lib.renderer.canvas.shapes.CanvasCircleShape
import com.binissa.particlize.lib.strategy.appearance.AppearanceStrategy
import com.binissa.particlize.lib.strategy.emission.EmissionStrategy
import com.binissa.particlize.lib.strategy.physics.PhysicsStrategy
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class EffectBuilder {
    private var id: String = UUID.randomUUID().toString()
    private var name: String = "Custom Effect"
    private var duration: Duration = 2000.milliseconds
    private var emissionStrategy: EmissionStrategy? = null
    private var physicsStrategy: PhysicsStrategy? = null
    private var appearanceStrategies: MutableList<AppearanceStrategy> = mutableListOf()
    private var particleMinLifetime: Duration = 3000.milliseconds
    private var particleMaxLifetime: Duration = 4000.milliseconds
    private var maxEmissionsPerFrame: Int = 500
    private var particleDensity: Int = 6
    private var particleShape: ParticleShapeStrategy<CanvasContext> = CanvasCircleShape()

    private var contentDisappearanceMode: ContentDisappearanceMode =
        ContentDisappearanceMode.PROGRESSIVE

    fun withId(id: String) = apply { this.id = id }

    fun withName(name: String) = apply { this.name = name }

    fun withParticleShape(shape: ParticleShapeStrategy<CanvasContext>) = apply { this.particleShape = shape }

    fun withMaxEmissionsPerFrame(maxEmissionsPerFrame: Int) =
        apply { this.maxEmissionsPerFrame = maxEmissionsPerFrame }

    fun withDuration(duration: Duration) = apply { this.duration = duration }

    fun withEmissionStrategy(strategy: EmissionStrategy) =
        apply { this.emissionStrategy = strategy }

    fun withPhysicsStrategy(strategy: PhysicsStrategy) = apply { this.physicsStrategy = strategy }

    fun withAppearanceStrategy(strategy: AppearanceStrategy) = apply {
        this.appearanceStrategies.add(strategy)
    }

    fun withAppearanceStrategies(strategies: List<AppearanceStrategy>) = apply {
        this.appearanceStrategies.addAll(strategies)
    }

    fun withParticleLifetime(min: Duration, max: Duration) = apply {
        require(min <= max) { "Minimum lifetime cannot be greater than maximum lifetime" }
        this.particleMinLifetime = min
        this.particleMaxLifetime = max
    }

    fun withParticleDensity(density: Int) = apply { this.particleDensity = density }

    fun withContentDisappearanceMode(mode: ContentDisappearanceMode) = apply {
        this.contentDisappearanceMode = mode
    }

    fun build(): ParticleEffect {
        requireNotNull(emissionStrategy) { "Emission strategy must be specified" }
        requireNotNull(physicsStrategy) { "Physics strategy must be specified" }

        return ParticleEffect(
            id = id,
            name = name,
            duration = duration,
            emissionStrategy = emissionStrategy!!,
            physicsStrategy = physicsStrategy!!,
            appearanceStrategies = appearanceStrategies.toList(),
            particleMinLifetime = particleMinLifetime,
            particleMaxLifetime = particleMaxLifetime,
            maxEmissionsPerFrame = maxEmissionsPerFrame,
            particleDensity = particleDensity,
            particleShape = particleShape,
            contentDisappearanceMode = contentDisappearanceMode
        )
    }
}
