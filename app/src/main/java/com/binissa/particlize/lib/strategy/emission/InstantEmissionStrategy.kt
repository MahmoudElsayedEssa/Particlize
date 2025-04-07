package com.binissa.particlize.lib.strategy.emission

import android.graphics.Bitmap
import com.binissa.particlize.lib.util.EffectUtils
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Emission strategy that makes all particles appear simultaneously.
 * Good for flash effects, bursts, and sudden transformations.
 */
class InstantEmissionStrategy(
    private val delayMs: Long = 0, // Delay before emission starts
    private val burstDurationMs: Long = 100, // How long the burst lasts
    private val sortMode: SortMode = SortMode.RANDOM // How to order particles
) : EmissionStrategy {

    enum class SortMode {
        RANDOM,         // Random order (good for explosions)
        INSIDE_OUT,     // Center particles first (good for bursts)
        OUTSIDE_IN,     // Edge particles first (good for implosions)
        TOP_TO_BOTTOM,  // Top particles first
        BOTTOM_TO_TOP,  // Bottom particles first
        LEFT_TO_RIGHT,  // Left particles first
        RIGHT_TO_LEFT   // Right particles first
    }

    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val centerX = width / 2
        val centerY = height / 2
        val points = ArrayList<EmissionPoint>()

        // Collect valid pixels
        val validPoints = mutableListOf<Pair<Int, Int>>()
        for (x in 0 until width step density) {
            for (y in 0 until height step density) {
                if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                    validPoints.add(Pair(x, y))
                }
            }
        }

        // Apply sorting mode
        val random = Random(42)

        val sortedPoints = when (sortMode) {
            SortMode.RANDOM -> {
                validPoints.shuffled(random)
            }

            SortMode.INSIDE_OUT -> {
                validPoints.sortedBy { (x, y) ->
                    val dx = x - centerX
                    val dy = y - centerY
                    dx * dx + dy * dy // Square of distance from center
                }
            }

            SortMode.OUTSIDE_IN -> {
                validPoints.sortedByDescending { (x, y) ->
                    val dx = x - centerX
                    val dy = y - centerY
                    dx * dx + dy * dy // Square of distance from center
                }
            }

            SortMode.TOP_TO_BOTTOM -> {
                validPoints.sortedBy { it.second }
            }

            SortMode.BOTTOM_TO_TOP -> {
                validPoints.sortedByDescending { it.second }
            }

            SortMode.LEFT_TO_RIGHT -> {
                validPoints.sortedBy { it.first }
            }

            SortMode.RIGHT_TO_LEFT -> {
                validPoints.sortedByDescending { it.first }
            }
        }

        // All points have the same order (0) and progress (1.0)
        // This ensures they all emit at once
        return sortedPoints.map { (x, y) ->
            EmissionPoint(
                x = x,
                y = y,
                order = 0,
                progress = 0f // All start at same progress
            )
        }
    }

    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
        // Wait for delay before starting emission
        val delayDuration = delayMs.toDuration(DurationUnit.MILLISECONDS)


        if (currentTime < delayDuration) {
            return 0f // No progress during delay
        }

        // Fast burst progress after delay
        val adjustedTime = currentTime - delayDuration
        val burstDuration = burstDurationMs.toDuration(DurationUnit.MILLISECONDS)

        // Progress races to 1.0 during the burst duration
        return if (burstDuration == Duration.ZERO) 1f
        else (adjustedTime / burstDuration).coerceIn(0.0, 1.0).toFloat()
    }

    override fun shouldEmitParticle(
        point: EmissionPoint,
        currentTime: Duration,
        totalDuration: Duration
    ): Boolean {
        val progress = getEmissionProgress(currentTime, totalDuration)
        // All particles emit once progress starts
        return progress > 0f
    }
}