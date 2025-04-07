package com.binissa.particlize.lib.strategy.emission

import android.graphics.Bitmap
import android.util.Log
import kotlin.math.hypot
import kotlin.random.Random
import kotlin.time.Duration

class AssemblyEmissionStrategy(
    private val startPosition: StartPosition = StartPosition.RANDOM_OFFSCREEN,
    private val assemblyOrder: AssemblyOrder = AssemblyOrder.UNIFORM,
    private val scatterFactor: Float = 2.0f, // How far particles start (multiplier of screen size)
    private val staggering: Float = 0.3f // How varied the arrival timing is (0-1)
) : EmissionStrategy {

    enum class StartPosition {
        RANDOM_OFFSCREEN, // Particles start from random positions off screen
        FROM_BOTTOM,      // Particles rise from below
        FROM_TOP,         // Particles fall from above
        FROM_SIDES,       // Particles come in from left/right
        SPIRAL_IN         // Particles start in a spiral pattern
    }

    enum class AssemblyOrder {
        UNIFORM,       // All particles arrive around the same time
        INSIDE_OUT,    // Center forms first
        OUTSIDE_IN,    // Edges form first
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP
    }

    // Store final positions and arrival timing for particles
    private val finalPositionMap = HashMap<String, Pair<Float, Float>>()
    private val arrivalTimingMap = HashMap<String, Float>()
    private val random = Random(System.currentTimeMillis())

    // Make assembly order accessible to other components
    fun getAssemblyOrder(): AssemblyOrder = assemblyOrder
    fun getStartPosition(): StartPosition = startPosition
    fun getScatterFactor(): Float = scatterFactor

    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val centerX = width / 2
        val centerY = height / 2
        val points = ArrayList<EmissionPoint>()

        // Clear maps
        finalPositionMap.clear()
        arrivalTimingMap.clear()

        // Collect valid pixels based on density
        val validPixels = mutableListOf<Pair<Int, Int>>()
        val step = Math.max(1, density)

        for (x in 0 until width step step) {
            for (y in 0 until height step step) {
                // Only include pixels with sufficient alpha
                try {
                    val pixel = bitmap.getPixel(x, y)
                    if (android.graphics.Color.alpha(pixel) > 50) {
                        validPixels.add(Pair(x, y))
                    }
                } catch (e: Exception) {
                    // Skip invalid pixels
                }
            }
        }

        if (validPixels.isEmpty()) {
            Log.e("AssemblyEmission", "No valid pixels found in bitmap")
            return points
        }

        // Order pixels based on assembly pattern
        val orderedPixels = orderPixelsByPattern(validPixels, centerX, centerY, width, height)

        // Create emission points
        var index = 0
        for (pixel in orderedPixels) {
            val (finalX, finalY) = pixel

            // Create a unique key for this point
            val pointKey = "point_${index}"

            // Calculate arrival timing value (0-1) based on assembly order
            val arrivalTiming = calculateArrivalTiming(
                index, orderedPixels.size,
                finalX, finalY, centerX, centerY,
                width, height
            )

            // Calculate starting position (far from final position)
            val startPos = calculateStartPosition(
                finalX, finalY, centerX, centerY,
                width, height, index
            )

            // Get pixel color for later use
            val pixelColor = try {
                bitmap.getPixel(finalX, finalY)
            } catch (e: Exception) {
                android.graphics.Color.WHITE
            }

            // Create emission point with enhanced userData
            val emissionPoint = EmissionPoint(
                x = startPos.first,
                y = startPos.second,
                order = index,
                progress = arrivalTiming // Use progress for staggered emission
            ).withData("sampleX", finalX)
                .withData("sampleY", finalY)
                .withData("color", pixelColor)
                .withData("finalX", finalX)
                .withData("finalY", finalY)
                .withData("assemblyOrder", assemblyOrder.ordinal)

            points.add(emissionPoint)

            // Store final position and arrival timing
            finalPositionMap[pointKey] = Pair(finalX.toFloat(), finalY.toFloat())
            arrivalTimingMap[pointKey] = arrivalTiming

            index++
        }

        Log.d("AssemblyEmission", "Created ${points.size} emission points")
        return points
    }

    // Rest of the implementation remains the same...

    private fun orderPixelsByPattern(
        pixels: List<Pair<Int, Int>>,
        centerX: Int,
        centerY: Int,
        width: Int,
        height: Int
    ): List<Pair<Int, Int>> {
        return when (assemblyOrder) {
            AssemblyOrder.UNIFORM -> {
                // For uniform, shuffle the pixels for randomized arrival
                pixels.shuffled(random)
            }

            AssemblyOrder.INSIDE_OUT -> {
                // Sort by distance from center (ascending)
                pixels.sortedBy { (x, y) ->
                    val dx = x - centerX
                    val dy = y - centerY
                    hypot(dx.toDouble(), dy.toDouble())
                }
            }

            AssemblyOrder.OUTSIDE_IN -> {
                // Sort by distance from center (descending)
                pixels.sortedByDescending { (x, y) ->
                    val dx = x - centerX
                    val dy = y - centerY
                    hypot(dx.toDouble(), dy.toDouble())
                }
            }

            AssemblyOrder.LEFT_TO_RIGHT -> pixels.sortedBy { it.first }
            AssemblyOrder.RIGHT_TO_LEFT -> pixels.sortedByDescending { it.first }
            AssemblyOrder.TOP_TO_BOTTOM -> pixels.sortedBy { it.second }
            AssemblyOrder.BOTTOM_TO_TOP -> pixels.sortedByDescending { it.second }
        }
    }

    private fun calculateArrivalTiming(
        index: Int,
        total: Int,
        x: Int,
        y: Int,
        centerX: Int,
        centerY: Int,
        width: Int,
        height: Int
    ): Float {
        // Base timing (0-1) based on assembly order and position
        val baseTiming = when (assemblyOrder) {
            AssemblyOrder.UNIFORM -> index.toFloat() / total

            AssemblyOrder.INSIDE_OUT -> {
                // Distance-based timing
                val dx = x - centerX
                val dy = y - centerY
                val distance = hypot(dx.toDouble(), dy.toDouble())
                val maxDistance = hypot(width / 2.0, height / 2.0)
                (distance / maxDistance).toFloat()
            }

            AssemblyOrder.OUTSIDE_IN -> {
                // Inverse distance-based timing
                val dx = x - centerX
                val dy = y - centerY
                val distance = hypot(dx.toDouble(), dy.toDouble())
                val maxDistance = hypot(width / 2.0, height / 2.0)
                1f - (distance / maxDistance).toFloat()
            }

            AssemblyOrder.LEFT_TO_RIGHT -> x.toFloat() / width
            AssemblyOrder.RIGHT_TO_LEFT -> 1f - (x.toFloat() / width)
            AssemblyOrder.TOP_TO_BOTTOM -> y.toFloat() / height
            AssemblyOrder.BOTTOM_TO_TOP -> 1f - (y.toFloat() / height)
        }

        // Add randomness based on staggering factor
        val randomness = random.nextFloat() * staggering

        // Final timing is a mix of sequential and random
        return (baseTiming * (1 - staggering) + randomness).coerceIn(0f, 0.95f)
    }

    private fun calculateStartPosition(
        finalX: Int,
        finalY: Int,
        centerX: Int,
        centerY: Int,
        width: Int,
        height: Int,
        seed: Int
    ): Pair<Int, Int> {
        // Implementation remains the same
        // Use a consistent random generator for this point
        val pointRandom = Random(seed + random.nextInt())

        // Maximum distance to scatter
        val maxDistance = hypot(width.toDouble(), height.toDouble()) * scatterFactor

        return when (startPosition) {
            StartPosition.RANDOM_OFFSCREEN -> {
                // Random angle from center
                val angle = pointRandom.nextDouble() * 2 * Math.PI

                // Distance beyond screen bounds
                val distance = maxDistance * (0.8 + pointRandom.nextDouble() * 0.4)

                // Calculate position
                val startX = (centerX + Math.cos(angle) * distance).toInt()
                val startY = (centerY + Math.sin(angle) * distance).toInt()

                Pair(startX, startY)
            }

            StartPosition.FROM_BOTTOM -> {
                // Start below the screen
                val startX = finalX + pointRandom.nextInt(-width/4, width/4)
                val startY = height + pointRandom.nextInt(height/2, height)

                Pair(startX, startY)
            }

            StartPosition.FROM_TOP -> {
                // Start above the screen
                val startX = finalX + pointRandom.nextInt(-width/4, width/4)
                val startY = -pointRandom.nextInt(height/2, height)

                Pair(startX, startY)
            }

            StartPosition.FROM_SIDES -> {
                // Start from left or right
                val fromRight = pointRandom.nextBoolean()
                val startX = if (fromRight) width + pointRandom.nextInt(width/2, width)
                else -pointRandom.nextInt(width/2, width)

                val startY = finalY + pointRandom.nextInt(-height/4, height/4)

                Pair(startX, startY)
            }

            StartPosition.SPIRAL_IN -> {
                // Calculate angle based on distance from center
                val dx = finalX - centerX
                val dy = finalY - centerY
                val baseDistance = hypot(dx.toDouble(), dy.toDouble())
                val baseAngle = Math.atan2(dy.toDouble(), dx.toDouble())

                // Add spiral factor
                val spiralFactor = 2.0 + pointRandom.nextDouble() * 2.0
                val spiralAngle = baseAngle + baseDistance * 0.01 * spiralFactor

                // Distance increases with spiral
                val spiralDistance = baseDistance + maxDistance * 0.5

                val startX = (centerX + Math.cos(spiralAngle) * spiralDistance).toInt()
                val startY = (centerY + Math.sin(spiralAngle) * spiralDistance).toInt()

                Pair(startX, startY)
            }
        }
    }

    // Get the final position for a specific emission point
    fun getFinalPosition(emissionPoint: EmissionPoint): Pair<Float, Float>? {
        val key = "point_${emissionPoint.order}"
        return finalPositionMap[key]
    }

    // Get the arrival timing for a specific emission point
    fun getArrivalTiming(emissionPoint: EmissionPoint): Float {
        val key = "point_${emissionPoint.order}"
        return arrivalTimingMap[key] ?: 0f
    }

    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
        // Simple linear progress
        val progress = if (totalDuration > Duration.ZERO) {
            (currentTime.inWholeMilliseconds.toFloat() / totalDuration.inWholeMilliseconds.toFloat())
                .coerceIn(0f, 1f)
        } else 1f

        // Apply easing for smoother motion
        return progress * progress * (3 - 2 * progress) // Smooth step function
    }

    override fun shouldEmitParticle(point: EmissionPoint, currentTime: Duration, totalDuration: Duration): Boolean {
        // For assembly, all particles are emitted based on their progress value
        val progress = getEmissionProgress(currentTime, totalDuration)
        return point.progress <= progress
    }
}
