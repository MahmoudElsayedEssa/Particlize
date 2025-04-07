package com.binissa.particlize.lib.strategy.emission


import android.graphics.Bitmap
import android.util.Log
import com.binissa.particlize.lib.util.EffectUtils
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sqrt
import kotlin.time.Duration

/**
 * Emission strategy that radiates particles from the center outward or edge inward.
 */
class RadialEmissionStrategy(
    private val radiationMode: RadiationMode = RadiationMode.CENTER_OUT,
    private val emissionRate: Int = 2,
    private val centerOffsetX: Float = 0f, // -1f to 1f (percentage of width)
    private val centerOffsetY: Float = 0f  // -1f to 1f (percentage of height)
) : EmissionStrategy {

    enum class RadiationMode {
        CENTER_OUT,   // Particles emit from center to edge
        EDGE_IN,      // Particles emit from edge to center
        IMPLOSION,    // Edge to center with faster edge disappearance
        EXPLOSION     // Center to edge with burst effect
    }

    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val points = ArrayList<EmissionPoint>()

        // Calculate custom center point if offset is provided
        val centerX = width / 2 + (width * centerOffsetX / 2).toInt()
        val centerY = height / 2 + (height * centerOffsetY / 2).toInt()

        // Maximum possible distance from center
        val maxDistance = hypot(width.toDouble(), height.toDouble()).toInt()

        // Create distance groups to process points ordered by distance
        val distanceGroups = Array<MutableList<Triple<Int, Int, Int>>?>(maxDistance + 1) { null }

        // Sample pixels and organize by distance from center
        for (x in 0 until width step density) {
            for (y in 0 until height step density) {
                if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                    // Calculate distance from center
                    val dx = x - centerX
                    val dy = y - centerY
                    val distance = hypot(dx.toDouble(), dy.toDouble()).toInt()

                    // Calculate angle for ordering points in spiral/swirl patterns
                    val angle = atan2(dy.toDouble(), dx.toDouble())
                    val angleDegrees = Math.toDegrees(angle).toInt() + 180

                    // Get or create list for this distance
                    val list = distanceGroups[distance] ?: ArrayList<Triple<Int, Int, Int>>().also {
                        distanceGroups[distance] = it
                    }

                    // Store point with its angle for ordered emission
                    list.add(Triple(x, y, angleDegrees))
                }
            }
        }

        // Process distance groups based on radiation mode
        var order = 0

        when (radiationMode) {
            RadiationMode.CENTER_OUT -> {
                // Process from center outward
                for (distance in 0..maxDistance) {
                    distanceGroups[distance]?.let { pointsAtDistance ->
                        // Sort points by angle for smooth emission
                        pointsAtDistance.sortedBy { it.third }

                        val progress = distance.toFloat() / maxDistance
                        for ((x, y, _) in pointsAtDistance) {
                            points.add(EmissionPoint(x, y, order, progress))
                        }
                        order++
                    }
                }
            }

            RadiationMode.EDGE_IN -> {
                // Process from edge inward
                for (distance in maxDistance downTo 0) {
                    distanceGroups[distance]?.let { pointsAtDistance ->
                        // Sort points by angle for smooth emission
                        pointsAtDistance.sortedBy { it.third }

                        val progress = 1f - (distance.toFloat() / maxDistance)
                        for ((x, y, _) in pointsAtDistance) {
                            points.add(EmissionPoint(x, y, order, progress))
                        }
                        order++
                    }
                }
            }

            RadiationMode.EXPLOSION -> {
                // Like CENTER_OUT but with faster initial progress
                for (distance in 0..maxDistance) {
                    distanceGroups[distance]?.let { pointsAtDistance ->
                        // For explosion, use squared progress for acceleration
                        val progress = sqrt(distance.toFloat() / maxDistance)
                        for ((x, y, _) in pointsAtDistance) {
                            points.add(EmissionPoint(x, y, order, progress))
                        }
                        order++
                    }
                }
            }

            RadiationMode.IMPLOSION -> {
                // Like EDGE_IN but with faster final progress
                for (distance in maxDistance downTo 0) {
                    distanceGroups[distance]?.let { pointsAtDistance ->
                        // For implosion, use squared progress for acceleration
                        val progress = sqrt(1f - (distance.toFloat() / maxDistance))
                        for ((x, y, _) in pointsAtDistance) {
                            points.add(EmissionPoint(x, y, order, progress))
                        }
                        order++
                    }
                }
            }
        }

        Log.d("RadialEmissionStrategy", "Generated ${points.size} emission points")
        return points
    }

    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
        val progress = if (totalDuration == Duration.ZERO) 1f
        else (currentTime / totalDuration).coerceIn(0.0, 1.0).toFloat()

        return progress
    }

    override fun shouldEmitParticle(
        point: EmissionPoint, currentTime: Duration, totalDuration: Duration
    ): Boolean {
        // Use point's progress directly to determine emission timing
        val progress = getEmissionProgress(currentTime, totalDuration)
        return point.progress <= progress
    }
}