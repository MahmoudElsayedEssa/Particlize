package com.binissa.particlize.lib.strategy.emission

import android.graphics.Bitmap
import com.binissa.particlize.lib.util.EffectUtils
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration

/**
 * Emission strategy that creates visually distinctive patterns for particle emission.
 */
class PatternEmissionStrategy(
    private val pattern: Pattern = Pattern.SPIRAL,
    private val clockwise: Boolean = true,
    private val revolutions: Float = 2f,
    private val randomVariation: Float = 0.1f // 0.0 to 1.0
) : EmissionStrategy {

    enum class Pattern {
        SPIRAL,      // Classic spiral pattern
        VORTEX,      // Spiral with varying radius
        RIPPLE,      // Concentric rings rippling outward
        GRID,        // Grid pattern from top-left to bottom-right
        ZIGZAG,      // Zigzag pattern across the image
        HEARTBEAT,   // Pulses from center with pauses
        SHATTER      // Breaking glass pattern from center
    }

    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val centerX = width / 2
        val centerY = height / 2
        val maxDistance = kotlin.math.hypot(width.toDouble(), height.toDouble()).toFloat() / 2

        // Collect valid emission points
        val validPoints = mutableListOf<Pair<Int, Int>>()
        for (x in 0 until width step density) {
            for (y in 0 until height step density) {
                if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                    validPoints.add(Pair(x, y))
                }
            }
        }

        // Early return if no valid points
        if (validPoints.isEmpty()) {
            return emptyList()
        }

        // Apply the selected pattern
        return when (pattern) {
            Pattern.SPIRAL -> createSpiralPattern(validPoints, centerX, centerY, maxDistance)
            Pattern.VORTEX -> createVortexPattern(validPoints, centerX, centerY, maxDistance)
            Pattern.RIPPLE -> createRipplePattern(validPoints, centerX, centerY, maxDistance)
            Pattern.GRID -> createGridPattern(validPoints, width, height)
            Pattern.ZIGZAG -> createZigzagPattern(validPoints, width, height)
            Pattern.HEARTBEAT -> createHeartbeatPattern(validPoints, centerX, centerY, maxDistance)
            Pattern.SHATTER -> createShatterPattern(validPoints, centerX, centerY, maxDistance)
        }
    }

    private fun createSpiralPattern(
        validPoints: List<Pair<Int, Int>>, centerX: Int, centerY: Int, maxDistance: Float
    ): List<EmissionPoint> {
        val random = Random(42)

        // Map each point to its angle and distance from center
        val pointData = validPoints.map { (x, y) ->
            val dx = x - centerX
            val dy = y - centerY
            val angle = atan2(dy.toDouble(), dx.toDouble())
            val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble())

            // Include the original coordinates for a unique identifier
            Pair(Pair(x, y), Triple(angle, distance, x * 10000 + y))
        }

        // Calculate the spiral parameter for each point - FIXED COMPARISON
        val maxRevs = revolutions * 2 * Math.PI

        val sortedPoints = pointData.sortedBy { (_, data) ->
            val (angle, distance, uniqueId) = data

            // Spiral parameter: angle plus distance factor
            // If clockwise, angle increases with distance; otherwise decreases
            val spiralParam = if (clockwise) angle + (distance / maxDistance) * maxRevs
            else angle - (distance / maxDistance) * maxRevs

            // Instead of random values, use deterministic variation based on coordinates
            val deterministicVariation = if (randomVariation > 0) {
                // Create variation based on the uniqueId (derived from x,y)
                (uniqueId % 1000) * randomVariation * 0.001
            } else {
                0.0
            }

            // The primary sort key + a small deterministic variation to preserve the visual effect
            spiralParam + deterministicVariation
        }

        // Create emission points with order and progress
        return sortedPoints.mapIndexed { index, (point, _) ->
            val progress = index.toFloat() / sortedPoints.size
            EmissionPoint(point.first, point.second, index, progress)
        }
    }

    private fun createRipplePattern(
        validPoints: List<Pair<Int, Int>>, centerX: Int, centerY: Int, maxDistance: Float
    ): List<EmissionPoint> {
        val random = Random(42)

        // Group points by distance
        val ringWidth = maxDistance / 20 // Create ~20 rings
        val rings = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()

        validPoints.forEach { (x, y) ->
            val dx = x - centerX
            val dy = y - centerY
            val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble())
            val ringIndex = (distance / ringWidth).toInt()

            rings.getOrPut(ringIndex) { mutableListOf() }.add(Pair(x, y))
        }

        // Create points ring by ring
        val result = mutableListOf<EmissionPoint>()
        var order = 0

        // Get sorted ring indices
        val ringIndices = rings.keys.sorted()

        ringIndices.forEach { ringIndex ->
            val ringPoints = rings[ringIndex] ?: return@forEach

            // Sort points in ring by angle for smooth ripple - FIXED COMPARISON
            val sortedRingPoints = ringPoints.sortedBy { (x, y) ->
                // Calculate the angle
                val angle = atan2((y - centerY).toDouble(), (x - centerX).toDouble())

                // Instead of adding random perturbation directly to the sort key,
                // we use the x,y coordinates as secondary sort keys for stable sorting
                // This ensures the comparison contract is satisfied
                val seed = x * 31 + y
                val sortKey = if (randomVariation > 0) {
                    // Use a deterministic randomization based on coordinates
                    angle + (seed % 100) * randomVariation * 0.01 * Math.PI
                } else {
                    angle
                }
                sortKey
            }

            // Add ring points with same order but sequential indices
            val ringProgress = ringIndex.toFloat() / ringIndices.size

            sortedRingPoints.forEachIndexed { index, (x, y) ->
                result.add(
                    EmissionPoint(
                        x = x, y = y, order = ringIndex, progress = ringProgress
                    )
                )
            }
        }

        return result
    }

    private fun createVortexPattern(
        validPoints: List<Pair<Int, Int>>, centerX: Int, centerY: Int, maxDistance: Float
    ): List<EmissionPoint> {
        val random = Random(42)

        // Map each point to its angle and distance from center
        val pointData = validPoints.map { (x, y) ->
            val dx = x - centerX
            val dy = y - centerY
            val angle = atan2(dy.toDouble(), dx.toDouble())
            val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble())

            // Include the original coordinates as part of the data for stable sorting
            Pair(Pair(x, y), Triple(angle, distance, x * 10000 + y))
        }

        // Calculate the vortex parameter for each point (tighter spiral) - FIXED COMPARISON
        val maxRevs = revolutions * 2 * Math.PI * 2 // Double the revolutions for vortex

        val sortedPoints = pointData.sortedBy { (_, data) ->
            val (angle, distance, uniqueId) = data

            // Vortex parameter: angle plus accelerating distance factor
            val distanceFactor = (distance / maxDistance).pow(1.5)
            val vortexParam = if (clockwise) angle + distanceFactor * maxRevs
            else angle - distanceFactor * maxRevs

            // Instead of using random, use a deterministic value based on coordinates
            // This ensures the comparison is always consistent
            val randomOffset = if (randomVariation > 0) {
                // We use the uniqueId (derived from x,y) to ensure stable sorting
                (uniqueId % 1000) * randomVariation * 0.001
            } else {
                0.0
            }

            // Primary sort key + secondary sort key to ensure stable comparisons
            vortexParam + randomOffset
        }

        // Create emission points with order and progress
        return sortedPoints.mapIndexed { index, (point, _) ->
            val progress = index.toFloat() / sortedPoints.size
            EmissionPoint(point.first, point.second, index, progress)
        }
    }

    private fun createGridPattern(
        validPoints: List<Pair<Int, Int>>, width: Int, height: Int
    ): List<EmissionPoint> {
        // Create a grid sequence from top-left to bottom-right
        val cellSize = 20 // Grid cell size
        val gridPoints = mutableMapOf<Pair<Int, Int>, MutableList<Pair<Int, Int>>>()

        // Group points by grid cell
        validPoints.forEach { (x, y) ->
            val gridX = x / cellSize
            val gridY = y / cellSize

            gridPoints.getOrPut(Pair(gridX, gridY)) { mutableListOf() }.add(Pair(x, y))
        }

        // Process grid cells in order
        val result = mutableListOf<EmissionPoint>()
        var order = 0

        // Get grid dimensions
        val maxGridX = width / cellSize
        val maxGridY = height / cellSize

        // Process in grid order (by sum of coordinates then by individual coordinates)
        val sortedCells =
            gridPoints.keys.sortedWith(
                compareBy(
                    { it.first + it.second }, // Sum of coordinates (diagonal order)
                    { it.first },             // Then by x (for cells in same diagonal)
                    { it.second }             // Then by y (rarely needed but ensures determinism)
                ))

        sortedCells.forEachIndexed { cellIndex, cell ->
            val cellPoints = gridPoints[cell] ?: return@forEachIndexed
            val cellProgress = cellIndex.toFloat() / sortedCells.size

            // Add all points in this cell
            cellPoints.forEach { (x, y) ->
                result.add(
                    EmissionPoint(
                        x = x, y = y, order = cellIndex, progress = cellProgress
                    )
                )
            }
        }

        return result
    }

    private fun createZigzagPattern(
        validPoints: List<Pair<Int, Int>>, width: Int, height: Int
    ): List<EmissionPoint> {
        // Create zigzag rows
        val rowHeight = height / 20 // ~20 rows for the zigzag
        val rowPoints = mutableMapOf<Int, MutableList<Pair<Int, Int>>>()

        // Group points by row
        validPoints.forEach { (x, y) ->
            val rowIndex = y / rowHeight
            rowPoints.getOrPut(rowIndex) { mutableListOf() }.add(Pair(x, y))
        }

        // Process rows in zigzag order
        val result = mutableListOf<EmissionPoint>()
        val rows = rowPoints.keys.sorted()

        rows.forEachIndexed { rowIndex, row ->
            val rowProgress = rowIndex.toFloat() / rows.size

            // Get points in this row
            val pointsInRow = rowPoints[row] ?: return@forEachIndexed

            // Sort points by x, alternating direction for zigzag effect
            val sortedRowPoints = if (rowIndex % 2 == 0) {
                // Even rows: left to right
                pointsInRow.sortedBy { it.first }
            } else {
                // Odd rows: right to left
                pointsInRow.sortedByDescending { it.first }
            }

            // Add all points in this row
            sortedRowPoints.forEach { (x, y) ->
                result.add(
                    EmissionPoint(
                        x = x, y = y, order = rowIndex, progress = rowProgress
                    )
                )
            }
        }

        return result
    }

    private fun createHeartbeatPattern(
        validPoints: List<Pair<Int, Int>>, centerX: Int, centerY: Int, maxDistance: Float
    ): List<EmissionPoint> {
        // Group points by distance into pulses
        val pulseCount = 5 // Number of pulses
        val pulseWidth = maxDistance / pulseCount

        // Map points to distances
        val pointDistances = validPoints.map { (x, y) ->
            val dx = x - centerX
            val dy = y - centerY
            val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble())

            Triple(x, y, distance)
        }

        // Create the heartbeat pattern
        val result = mutableListOf<EmissionPoint>()

        // Define pulse timing (0 = pulse, 1 = pause)
        val pulseTiming = listOf(0, 0, 1, 0, 1, 0, 0, 1, 0, 0)
        val pulseProgressStep = 1.0f / pulseTiming.size

        // Create ordered pulses
        var currentOrder = 0
        pulseTiming.forEachIndexed { pulseIndex, pulseType ->
            if (pulseType == 0) { // Active pulse
                // Get points for this pulse distance range
                val pulsePoints = pointDistances.filter { (_, _, distance) ->
                    val normalizedDist = distance / maxDistance
                    val pulsePosition = normalizedDist * pulseCount

                    // Include points within pulse width
                    val pulseFraction = pulsePosition - pulsePosition.toInt()
                    pulseFraction < 0.3
                }

                // Sort pulse points by angle for circular emission
                val sortedPulsePoints = pulsePoints.sortedBy { (x, y, _) ->
                    atan2((y - centerY).toDouble(), (x - centerX).toDouble())
                }

                // Add points with pulse progress
                val pulseProgress = pulseIndex * pulseProgressStep

                sortedPulsePoints.forEach { (x, y, _) ->
                    result.add(
                        EmissionPoint(
                            x = x, y = y, order = currentOrder, progress = pulseProgress
                        )
                    )
                }

                currentOrder++
            }
        }

        return result
    }

    private fun createShatterPattern(
        validPoints: List<Pair<Int, Int>>, centerX: Int, centerY: Int, maxDistance: Float
    ): List<EmissionPoint> {
        val random = Random(42)

        // Create crack lines emanating from center
        val crackCount = 10
        val crackAngles = Array(crackCount) {
            2 * PI * it / crackCount + random.nextDouble(-0.2, 0.2)
        }

        // Calculate crack influence for each point
        val pointCrackData = validPoints.map { (x, y) ->
            val dx = x - centerX
            val dy = y - centerY
            val angle = atan2(dy.toDouble(), dx.toDouble())
            val distance = kotlin.math.hypot(dx.toDouble(), dy.toDouble())

            // Find closest crack
            val closestCrackIndex = crackAngles.indices.minByOrNull { i ->
                // Calculate angular distance (considering circularity)
                var angDist = Math.abs(angle - crackAngles[i])
                if (angDist > PI) angDist = 2 * PI - angDist
                angDist
            } ?: 0

            // Calculate angular distance to closest crack
            var angularDistance = Math.abs(angle - crackAngles[closestCrackIndex])
            if (angularDistance > PI) angularDistance = 2 * PI - angularDistance

            // Create shatter parameter: distance from center and from crack
            val crackProximity = 1.0 - (angularDistance / (PI / crackCount))

            // Points closest to cracks and farthest from center break first
            val shatterParam = crackProximity * (distance / maxDistance)

            // Add randomness
            val finalParam = shatterParam + random.nextDouble(
                -randomVariation.toDouble(), randomVariation.toDouble()
            )

            Triple(Pair(x, y), finalParam, closestCrackIndex)
        }

        // Sort by shatter parameter (descending for fastest breakage first)
        val sortedPoints = pointCrackData.sortedByDescending { it.second }

        // Create emission points
        return sortedPoints.mapIndexed { index, (point, _, crackIndex) ->
            val progress = index.toFloat() / sortedPoints.size
            EmissionPoint(point.first, point.second, crackIndex, progress)
        }
    }

    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
        val linearProgress = if (totalDuration == Duration.ZERO) 1f
        else (currentTime / totalDuration).coerceIn(0.0, 1.0).toFloat()

        // Apply pattern-specific progress curves
        return when (pattern) {
            Pattern.HEARTBEAT -> {
                // Create heartbeat-like progress with pauses
                val pulsePositions = listOf(0f, 0.2f, 0.4f, 0.6f, 0.8f)
                val pulseDuration = 0.1f

                // Find if we're in a pulse
                val inPulse = pulsePositions.any { pulsePos ->
                    linearProgress >= pulsePos && linearProgress <= pulsePos + pulseDuration
                }

                // Accelerate during pulses
                if (inPulse) {
                    // Map local pulse progress to global emission curve
                    val pulsePosition = pulsePositions.find { pulsePos ->
                        linearProgress >= pulsePos && linearProgress <= pulsePos + pulseDuration
                    } ?: 0f

                    val localProgress = (linearProgress - pulsePosition) / pulseDuration
                    val pulseIndex = pulsePositions.indexOf(pulsePosition)

                    // Map pulse progress to overall emission progress
                    pulseIndex.toFloat() / pulsePositions.size + (1f / pulsePositions.size) * localProgress
                } else {
                    // Between pulses, stay at previous progress
                    val previousPulseIndex = pulsePositions.filter { it <= linearProgress }.size - 1
                    if (previousPulseIndex >= 0) {
                        (previousPulseIndex + 1).toFloat() / pulsePositions.size
                    } else {
                        0f
                    }
                }
            }

            Pattern.SHATTER -> {
                // Use accelerating curve for shatter
                linearProgress * linearProgress
            }

            else -> linearProgress
        }
    }

    override fun shouldEmitParticle(
        point: EmissionPoint, currentTime: Duration, totalDuration: Duration
    ): Boolean {
        val progress = getEmissionProgress(currentTime, totalDuration)
        return point.progress <= progress
    }

}