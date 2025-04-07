package com.binissa.particlize.lib.strategy.appearance

import android.graphics.Color
import com.binissa.particlize.lib.core.model.Particle
import kotlin.time.Duration

class ColorTransformStrategy(
    private val targetColor: Int,
    private val startDelay: Duration = Duration.ZERO,
    private val duration: Duration = Duration.ZERO
) : AppearanceStrategy {

    override fun updateAppearance(particle: Particle, deltaTime: Duration) {
        val progress = particle.progress

        // Store initial color if not already stored
        if (!particle.userData.containsKey("initialColor")) {
            particle.userData["initialColor"] = particle.color
        }

        // Get initial color from userData
        val initialColor = particle.userData["initialColor"] as Int

        // If duration is zero or we're at full progress, just set to target color
        if (duration <= Duration.ZERO || progress >= 1.0f) {
            particle.color = targetColor
            return
        }

        // Calculate normalized delay as a fraction of total lifetime
        val delayFraction = if (particle.lifetime > Duration.ZERO) {
            (startDelay.inWholeNanoseconds.toFloat() / particle.lifetime.inWholeNanoseconds).coerceIn(0f, 1f)
        } else {
            0f
        }

        // Skip if before start delay
        if (progress < delayFraction) {
            return
        }

        // Calculate duration as a fraction of total lifetime
        val durationFraction = if (particle.lifetime > Duration.ZERO) {
            (duration.inWholeNanoseconds.toFloat() / particle.lifetime.inWholeNanoseconds).coerceIn(0f, 1f - delayFraction)
        } else {
            1f
        }

        // Calculate color transition progress
        val transitionProgress = if (durationFraction > 0f) {
            ((progress - delayFraction) / durationFraction).coerceIn(0f, 1f)
        } else {
            1f
        }

        // Interpolate between colors
        particle.color = interpolateColor(initialColor, targetColor, transitionProgress)
    }

    private fun interpolateColor(startColor: Int, endColor: Int, fraction: Float): Int {
        val startA = Color.alpha(startColor)
        val startR = Color.red(startColor)
        val startG = Color.green(startColor)
        val startB = Color.blue(startColor)

        val endA = Color.alpha(endColor)
        val endR = Color.red(endColor)
        val endG = Color.green(endColor)
        val endB = Color.blue(endColor)

        return Color.argb(
            (startA + (endA - startA) * fraction).toInt(),
            (startR + (endR - startR) * fraction).toInt(),
            (startG + (endG - startG) * fraction).toInt(),
            (startB + (endB - startB) * fraction).toInt()
        )
    }
}