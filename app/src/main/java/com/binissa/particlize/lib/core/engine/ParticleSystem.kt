package com.binissa.particlize.lib.core.engine

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.core.model.ParticleState
import com.binissa.particlize.lib.event.EffectEvent
import com.binissa.particlize.lib.event.EffectEventType
import com.binissa.particlize.lib.event.EventBus
import com.binissa.particlize.lib.factory.ParticleFactory
import com.binissa.particlize.lib.factory.PooledParticleFactory
import com.binissa.particlize.lib.renderer.ParticleRenderer
import com.binissa.particlize.lib.strategy.emission.AssemblyEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.EmissionPoint
import com.binissa.particlize.lib.strategy.physics.AssemblyPhysicsStrategy
import com.binissa.particlize.lib.view.EffectView
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ParticleSystem<T>(
    internal var renderer: ParticleRenderer<T>,
    internal val eventBus: EventBus = EventBus(),
    private val particleFactory: ParticleFactory = PooledParticleFactory()
) {
    private val activeEffects = mutableListOf<ActiveEffect>()

    fun applyEffect(
        effectView: EffectView, effect: ParticleEffect, autoRemoveView: Boolean = true
    ) {
        val bitmap = effectView.createBitmap()
        val viewLocation = IntArray(2)
        effectView.getLocationInWindow(viewLocation)

        val offset = Point(viewLocation[0], viewLocation[1])
        val center = Point(effectView.width / 2, effectView.height / 2)

        // Connect assembly emission and physics strategies
        if (effect.emissionStrategy is AssemblyEmissionStrategy && effect.physicsStrategy is AssemblyPhysicsStrategy) {
            effect.physicsStrategy.setEmissionStrategy(effect.emissionStrategy)
            effect.physicsStrategy.setEffectDuration(effect.duration)

        }

        val emissionPoints = effect.emissionStrategy.calculateEmissionPoints(
            bitmap, effect.particleDensity
        )

        val activeEffect = ActiveEffect(
            effect = effect,
            bitmap = bitmap,
            emissionPoints = emissionPoints,
            offset = offset,
            center = center,
            particles = mutableListOf(),
            state = ParticleState(
                particles = emptyList(),
                elapsedTime = Duration.ZERO,
                emissionProgress = 0f,
                isComplete = false,
                contentVisibility = if (effect.contentDisappearanceMode == ContentDisappearanceMode.ASSEMBLY) 0f else 1f
            ),
            effectView = effectView,
            autoRemoveView = autoRemoveView,
            emittedPoints = BooleanArray(emissionPoints.size),
            nextParticleId = 0,
            maxEmissionsPerFrame = effect.maxEmissionsPerFrame
        )
        activeEffects.add(activeEffect)

        eventBus.publish(
            EffectEvent(
                effectId = effect.id, type = EffectEventType.STARTED, progress = 0f
            )
        )
    }

    private fun emitParticles(activeEffect: ActiveEffect, elapsedTime: Duration) {
        val totalDuration = activeEffect.effect.duration
        var emittedThisFrame = 0

        // Get current progress
        val progress = activeEffect.effect.emissionStrategy.getEmissionProgress(
            elapsedTime, totalDuration
        )

        // For assembly mode, handle final positions for particles
        val isAssembly =
            activeEffect.effect.contentDisappearanceMode == ContentDisappearanceMode.ASSEMBLY
        val emissionStrategy =
            if (isAssembly) activeEffect.effect.emissionStrategy as? AssemblyEmissionStrategy
            else null

        // Emit particles based on progress value
        for (i in activeEffect.emissionPoints.indices) {
            if (activeEffect.emittedPoints[i]) continue

            val point = activeEffect.emissionPoints[i]
            if (point.progress <= progress) {
                try {
                    // Create particle with increased size for better visibility
                    val particle = particleFactory.createParticle(
                        id = activeEffect.nextParticleId++,
                        point = point,
                        bitmap = activeEffect.bitmap,
                        minLifetime = activeEffect.effect.particleMinLifetime,
                        maxLifetime = activeEffect.effect.particleMaxLifetime,
                        particleScaling = 2.0f // Larger particles
                    )

                    // For assembly, store final position in userData
                    if (isAssembly && emissionStrategy != null) {
                        val finalPosition = emissionStrategy.getFinalPosition(point)
                        if (finalPosition != null) {
                            particle.withUserData("finalX", finalPosition.first)
                            particle.withUserData("finalY", finalPosition.second)
                            particle.withUserData(
                                "progress", emissionStrategy.getArrivalTiming(point)
                            )
                        }
                    }

                    // Add to active particles
                    activeEffect.particles.add(particle)
                    activeEffect.emittedPoints[i] = true
                    emittedThisFrame++

                    if (emittedThisFrame >= activeEffect.maxEmissionsPerFrame) {
                        break
                    }
                } catch (e: Exception) {
                    Log.e("ParticleSystem", "Error creating particle", e)
                }
            }
        }
    }

    fun update(deltaTime: Duration, renderingContext: T) {
        renderer.prepare(renderingContext)

        var i = 0
        while (i < activeEffects.size) {
            val activeEffect = activeEffects[i]

            // Update effect time
            val elapsedTime = activeEffect.state.elapsedTime + deltaTime
            val isComplete = elapsedTime >= activeEffect.effect.duration

            // Calculate emission progress - This drives both content disappearance and particle emission
            val emissionProgress = activeEffect.effect.emissionStrategy.getEmissionProgress(
                elapsedTime, activeEffect.effect.duration
            )

            if (!isComplete) {
                emitParticles(activeEffect, elapsedTime)
            }

            val updatedParticles = updateParticles(activeEffect, deltaTime)
            val contentVisibility = when (activeEffect.effect.contentDisappearanceMode) {
                ContentDisappearanceMode.NONE -> 1f
                ContentDisappearanceMode.INSTANT -> if (emissionProgress > 0.01f) 0f else 1f
                ContentDisappearanceMode.FADE -> (1f - emissionProgress).coerceIn(0f, 1f)
                ContentDisappearanceMode.PROGRESSIVE -> (1f - emissionProgress).coerceIn(0f, 1f)
                ContentDisappearanceMode.ASSEMBLY -> {
                    if (elapsedTime >= activeEffect.effect.duration + 1000.milliseconds) {
                        1f
                    } else {
                        // Keep content hidden while particles are still assembling
                        0f
                    }
                }
            }


            activeEffect.state = activeEffect.state.copy(
                particles = updatedParticles,
                elapsedTime = elapsedTime,
                emissionProgress = emissionProgress,
                isComplete = isComplete,
                contentVisibility = contentVisibility
            )

            // First render the content (with proper masking)
            if (contentVisibility > 0f) {
                renderer.renderContent(renderingContext, activeEffect.state)
            }

            // Then render the particles on top
            if (updatedParticles.isNotEmpty()) {
                renderer.renderParticles(renderingContext, activeEffect.state)
            }

            // Check if effect is complete
            if (isComplete && updatedParticles.isEmpty()) {
                // Mark for removal
                activeEffects.removeAt(i)

                // Clean up
                renderer.cleanup(renderingContext)

                // Remove view if needed
                if (activeEffect.autoRemoveView) {
                    activeEffect.effectView.remove()
                }

                // Notify listeners
                eventBus.publish(
                    EffectEvent(
                        effectId = activeEffect.effect.id,
                        type = EffectEventType.COMPLETED,
                        progress = 1f
                    )
                )
            } else {
                if (!isComplete) {
                    // Periodically log progress for debugging
                    if (elapsedTime.inWholeMilliseconds % 200 == 0L) {
                        Log.d(
                            "ParticleSystem",
                            "Effect ${activeEffect.effect.id}: progress=${emissionProgress}"
                        )
                    }

                    eventBus.publish(
                        EffectEvent(
                            effectId = activeEffect.effect.id,
                            type = EffectEventType.PROGRESS,
                            progress = emissionProgress
                        )
                    )
                }
                i++
            }
        }
    }

    private fun updateParticles(activeEffect: ActiveEffect, deltaTime: Duration): List<Particle> {
        val iterator = activeEffect.particles.iterator()
        var removedCount = 0

        while (iterator.hasNext()) {
            val particle = iterator.next()
            particle.updateTime(deltaTime)

            // Check if particle has exceeded its lifetime
            if (!particle.isAlive) {
                particleFactory.recycleParticle(particle)
                iterator.remove()
                removedCount++
                continue
            }

            try {
                // Apply physics - updating the same particle instance
                activeEffect.effect.physicsStrategy.updateParticle(
                    particle, deltaTime, activeEffect.center
                )

                // Apply appearance strategies
                activeEffect.effect.appearanceStrategies.forEach {
                    it.updateAppearance(
                        particle = particle, deltaTime = deltaTime
                    )
                }

                if (particle.alpha <= 0) {
                    particleFactory.recycleParticle(particle)
                    iterator.remove()
                    removedCount++
                    continue
                }

                val bitmapWidth = activeEffect.bitmap.width
                val bitmapHeight = activeEffect.bitmap.height
                val margin = particle.radius * particle.scale * 5  // Much larger margin

                if (particle.x < -bitmapWidth * 2 - margin || particle.x > bitmapWidth * 3 + margin || particle.y < -bitmapHeight * 2 - margin || particle.y > bitmapHeight * 3 + margin) {
                    particleFactory.recycleParticle(particle)
                    iterator.remove()
                    removedCount++
                }
            } catch (e: Exception) {
                Log.e("ParticleSystem", "Error updating particle", e)
                particleFactory.recycleParticle(particle)
                iterator.remove()
                removedCount++
            }
        }

        return activeEffect.particles
    }


    private data class ActiveEffect(
        val effect: ParticleEffect,
        val bitmap: Bitmap,
        val emissionPoints: List<EmissionPoint>,
        val offset: Point,
        val center: Point,
        val particles: MutableList<Particle>,
        var state: ParticleState,
        val effectView: EffectView,
        val maxEmissionsPerFrame: Int,
        val autoRemoveView: Boolean,
        val emittedPoints: BooleanArray,
        var nextParticleId: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ActiveEffect) return false

            if (effect != other.effect) return false
            if (offset != other.offset) return false
            if (center != other.center) return false
            if (effectView != other.effectView) return false

            return true
        }

        override fun hashCode(): Int {
            var result = effect.hashCode()
            result = 31 * result + offset.hashCode()
            result = 31 * result + center.hashCode()
            result = 31 * result + effectView.hashCode()
            return result
        }
    }

    // Clear all effects (useful for cleanup)
    fun clearAllEffects() {
        activeEffects.forEach { activeEffect ->
            // Notify completion
            eventBus.publish(
                EffectEvent(
                    effectId = activeEffect.effect.id,
                    type = EffectEventType.COMPLETED,
                    progress = 1f
                )
            )

            // Recycle particles
            activeEffect.particles.forEach { particle ->
                particleFactory.recycleParticle(particle)
            }
        }

        // Clear the list
        activeEffects.clear()
    }
}