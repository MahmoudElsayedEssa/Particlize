package com.binissa.particlize.lib.strategy.appearance

import com.binissa.particlize.lib.core.model.Particle
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

/**
 * A strategy that controls the fade-in, fade-out, or pulse behavior of particles.
 *
 * @param fadeMode The type of fade effect to apply
 * @param fadeStartDelay Time to wait before starting the fade effect
 * @param fadeDuration How long the fade transition takes
 * @param ensureCompleteDisappearance Whether to ensure particles fully disappear at the end (for FADE_OUT)
 */
class FadeStrategy(
    private val fadeMode: FadeMode = FadeMode.FADE_OUT,
    private val fadeStartDelay: Duration = ZERO,
    private val fadeDuration: Duration = 1.0.seconds,
    private val ensureCompleteDisappearance: Boolean = true
) : AppearanceStrategy {

    enum class FadeMode {
        FADE_IN,   // Particle starts transparent and becomes opaque
        FADE_OUT,  // Particle starts opaque and becomes transparent
        PULSE      // Particle alternates between transparent and opaque
    }

    override fun updateAppearance(particle: Particle, deltaTime: Duration) {
        // Get the particle's current progress through its lifetime (0.0 to 1.0)
        val progress = particle.progress

        // Get the initial alpha value from userData, default to 255 if not found
        val initialAlpha = (particle.userData["initialAlpha"] as? Int) ?: 255

        // Make sure we initialize alpha correctly for FADE_IN on the first update
        if (fadeMode == FadeMode.FADE_IN && !particle.userData.containsKey("fadeInitialized")) {
            // Set initial alpha to 0 for fade-in
            particle.alpha = 0
            particle.withUserData("fadeInitialized", true)
        }

        // Special case: Force complete disappearance near the end for FADE_OUT mode
        if (fadeMode == FadeMode.FADE_OUT && ensureCompleteDisappearance && progress >= 0.95f) {
            // Map 0.95-1.0 to 0-1 for final disappearance
            val finalFade = ((progress - 0.95f) / 0.05f).coerceIn(0f, 1f)
            particle.alpha = ((1f - finalFade) * initialAlpha * (1f - getFadeProgress(progress))).toInt().coerceIn(0, 255)
            return
        }

        // Calculate alpha based on fade mode
        val alpha = when (fadeMode) {
            FadeMode.FADE_IN -> {
                // For fade in, alpha increases with progress
                // Use a simpler fade-in calculation for more reliable results
                val fadeInProgress = if (fadeDuration > ZERO) {
                    (progress / (fadeDuration.inWholeMilliseconds / particle.lifetime.inWholeMilliseconds.toFloat()))
                        .coerceIn(0f, 1f)
                } else {
                    progress // If no duration specified, use overall progress
                }

                (initialAlpha * fadeInProgress).toInt().coerceIn(0, 255)
            }

            FadeMode.FADE_OUT -> {
                // For fade out, alpha decreases with progress
                val fadeProgress = getFadeProgress(progress)
                (initialAlpha * (1f - fadeProgress)).toInt().coerceIn(0, 255)
            }

            FadeMode.PULSE -> {
                // For pulse, we create a wave pattern, but ensure it fades out at the end
                if (progress >= 0.85f) {
                    // Final fade out in the last 15% of lifetime
                    val tailProgress = (progress - 0.85f) / 0.15f
                    (initialAlpha * (1f - tailProgress)).toInt().coerceIn(0, 255)
                } else {
                    // Create a sine wave pattern for smooth pulsing
                    val pulseFrequency = 3.5f
                    val pulseValue = (Math.sin(progress * pulseFrequency * Math.PI * 2) * 0.5 + 0.5).toFloat()
                    val pulseAlpha = 0.2f + pulseValue * 0.8f

                    (initialAlpha * pulseAlpha).toInt().coerceIn(0, 255)
                }
            }
        }

        // Update the particle's alpha
        particle.alpha = alpha
    }

    /**
     * Calculate how far through the fade effect we are, accounting for delay.
     */
    private fun getFadeProgress(progress: Float): Float {
        // Handle edge case where fadeDuration is zero
        if (fadeDuration == ZERO) return 1f

        // Calculate at what progress point the fade should start
        val fadeStartPoint = if (fadeStartDelay > ZERO) {
            (fadeStartDelay.inWholeMilliseconds.toFloat() /
                    (fadeStartDelay.inWholeMilliseconds + fadeDuration.inWholeMilliseconds).toFloat())
                .coerceIn(0f, 0.9f) // Ensure we still have time for the fade
        } else {
            0f
        }

        // If we haven't reached the fade start point yet
        if (progress < fadeStartPoint) return 0f

        // Calculate how far we are through the fade portion
        val fadeProgress = ((progress - fadeStartPoint) / (1f - fadeStartPoint)).coerceIn(0f, 1f)

        // Apply easing for smoother fade transitions
        return smoothstep(fadeProgress)
    }

    /**
     * Apply smoothstep easing function for more natural-looking fades.
     */
    private fun smoothstep(x: Float): Float {
        // Smoothstep function: 3x² - 2x³
        return x * x * (3 - 2 * x)
    }
}