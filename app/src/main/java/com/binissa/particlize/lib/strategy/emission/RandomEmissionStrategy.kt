package com.binissa.particlize.lib.strategy.emission

import android.graphics.Bitmap
import android.util.Log
import com.binissa.particlize.lib.util.EffectUtils
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Emission strategy that randomly emits particles across the entire image.
 */
class RandomEmissionStrategy(
    private val randomSeed: Int = Random.nextInt(),
    private val groupSize: Int = 5,  // Number of particles to emit in each group
    private val randomnessMode: RandomnessMode = RandomnessMode.PURE_RANDOM
) : EmissionStrategy {

    enum class RandomnessMode {
        PURE_RANDOM,      // Completely random order
        CLUSTERED,        // Particles emit in nearby clusters
        CHAOTIC,          // Some areas emit faster than others
        POPCORN           // Particles appear in small bursts
    }

    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val points = ArrayList<EmissionPoint>()
        val random = Random(randomSeed)

        // Collect valid pixels
        for (x in 0 until width step density) {
            for (y in 0 until height step density) {
                if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                    points.add(EmissionPoint(x, y, 0, 0f)) // Initial values
                }
            }
        }

        // Randomize based on selected mode
        when (randomnessMode) {
            RandomnessMode.PURE_RANDOM -> {
                // Simple shuffle and assign sequential progress
                points.shuffle(random)
                val totalPoints = points.size.toFloat()

                return points.mapIndexed { index, point ->
                    point.order = index / groupSize
                    point.progress = index / totalPoints
                    point
//                    point.copy(
//                        order = index / groupSize,
//                        progress = index / totalPoints
//                    )
                }
            }

            RandomnessMode.CLUSTERED -> {
                // Create random cluster centers
                val clusters = mutableListOf<Pair<Int, Int>>()
                val clusterCount = (width + height) / (density * 20)

                repeat(clusterCount) {
                    clusters.add(
                        Pair(
                            random.nextInt(width),
                            random.nextInt(height)
                        )
                    )
                }

                // Sort points by distance to nearest cluster
                return points.mapIndexed { index, point ->
                    val nearestClusterDistance = clusters.minOf { cluster ->
                        val dx = point.x - cluster.first
                        val dy = point.y - cluster.second
                        (dx * dx + dy * dy)
                    }

                    point.order = (nearestClusterDistance / 1000).toInt()
                    point.progress = index.toFloat() / points.size
//                    point.copy(
//                        order = (nearestClusterDistance / 1000).toInt(),
//                        progress = index.toFloat() / points.size
//                    )
                    point
                }.sortedBy { it.order }
            }

            RandomnessMode.CHAOTIC -> {
                // Calculate noise-based order to create chaotic patterns
                val noiseScale = 0.01f // Scale for Perlin-like noise

                return points.mapIndexed { index, point ->
                    // Create pseudo-noise value based on position
                    val noiseX = sin(point.x * noiseScale * 1.7) * 0.5 + 0.5
                    val noiseY = cos(point.y * noiseScale * 2.3) * 0.5 + 0.5
                    val noise = (noiseX * noiseY * 100).toInt()

                    point.order = noise
                    point.progress = index.toFloat() / points.size
                    point
//                    point.copy(
//                        order = noise,
//                        progress = index.toFloat() / points.size
//                    )
                }.sortedBy { it.order }
            }

            RandomnessMode.POPCORN -> {
                // Create "popcorn" effect with small bursts
                val burstCount = points.size / groupSize
                val burstOrder = Array(burstCount) { it }
                burstOrder.shuffle(random)

                val result = mutableListOf<EmissionPoint>()

                // Group points into bursts
                for (i in 0 until burstCount) {
                    val startIdx = i * groupSize
                    val endIdx = minOf(startIdx + groupSize, points.size)

                    if (startIdx >= points.size) break

                    // Add points in this burst
                    for (j in startIdx until endIdx) {
                        result.add(
                            points[j].apply {

                                points[j].order = burstOrder[i]
                                points[j].progress = burstOrder[i].toFloat() / burstCount
                            }

                        )

                    }
                }

                return result
            }
        }

        Log.d("RandomEmissionStrategy", "Generated ${points.size} emission points")
        return points
    }

    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
        return if (totalDuration == Duration.ZERO) 1f
        else (currentTime / totalDuration).coerceIn(0.0, 1.0).toFloat()
    }

    override fun shouldEmitParticle(
        point: EmissionPoint,
        currentTime: Duration,
        totalDuration: Duration
    ): Boolean {
        val progress = getEmissionProgress(currentTime, totalDuration)
        return point.progress <= progress
    }
}