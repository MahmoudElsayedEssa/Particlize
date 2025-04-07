package com.binissa.particlize.lib.strategy.appearance

import com.binissa.particlize.lib.core.model.Particle
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

class ScaleStrategy(
    private val scaleMode: ScaleMode = ScaleMode.SHRINK,
    private val minScale: Float = 0.1f,
    private val maxScale: Float = 1.5f,
    private val scaleRate: Float = 1f
) : AppearanceStrategy {

    enum class ScaleMode {
        SHRINK, GROW, PULSE
    }

    override fun updateAppearance(particle: Particle, deltaTime: Duration) {
        val progress = particle.progress

        val scale = when (scaleMode) {
            ScaleMode.SHRINK -> {
                // Linear shrink from original scale (usually 1.0) to minScale
                val initialScale = particle.userData["initialScale"] as? Float ?: 1f
                initialScale - (progress * scaleRate * (initialScale - minScale))
                    .coerceIn(0f, initialScale - minScale)
            }

            ScaleMode.GROW -> {
                // Linear growth from minScale to maxScale
                minScale + progress * (maxScale - minScale) * scaleRate
                    .coerceIn(0f, maxScale - minScale)
            }

            ScaleMode.PULSE -> {
                // Use sin function for smoother pulsing
                val pulsePhase = (progress * Math.PI * 3).toFloat()
                val pulseFactor = (Math.sin(pulsePhase.toDouble()) + 1) / 2
                minScale + (pulseFactor * (maxScale - minScale)).toFloat()
            }
        }

        // Store initial scale if this is the first update
        if (!particle.userData.containsKey("initialScale")) {
            particle.userData["initialScale"] = particle.scale
        }

        // Apply scale, ensuring it stays within reasonable bounds
        particle.scale = scale.coerceIn(0.01f, maxScale * 1.2f)
    }
}