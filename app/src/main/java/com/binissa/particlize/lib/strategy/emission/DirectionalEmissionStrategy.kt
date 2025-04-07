package com.binissa.particlize.lib.strategy.emission

import android.graphics.Bitmap
import android.util.Log
import com.binissa.particlize.lib.util.EffectUtils
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class DirectionalEmissionStrategy(
    internal val direction: Direction,
    internal val emissionDelay: Duration = 50.milliseconds,
    internal val emissionRate: Int = 2,
) : EmissionStrategy {

    enum class Direction { LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP }

    // Constructor for backward compatibility
    constructor(
        direction: Direction, emissionRate: Int = 2, emissionDelayMs: Int = 50
    ) : this(
        direction = direction,
        emissionRate = emissionRate,
        emissionDelay = emissionDelayMs.milliseconds
    )

    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
        val width = bitmap.width
        val height = bitmap.height
        val points = ArrayList<EmissionPoint>()

        Log.d("EmissionStrategy", "Calculating points for ${width}x${height} bitmap, density=$density")

        // Sample pixels directly from bitmap - no coordinate manipulation
        when (direction) {
            Direction.LEFT_TO_RIGHT -> {
                for (x in 0 until width step density) {
                    val progress = x.toFloat() / width
                    var hasContent = false
                    for (y in 0 until height step density) {
                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                            points.add(EmissionPoint(x, y, x / density, progress))
                            hasContent = true
                        }
                    }

                    if (!hasContent && x % (density * 2) == 0) {
                        val y = height / 2
                        points.add(EmissionPoint(x, y, x / density, progress))
                    }
                }
            }

            Direction.RIGHT_TO_LEFT -> {
                for (x in width - 1 downTo 0 step density) {
                    val progress = 1f - (x.toFloat() / width)
                    var hasContent = false
                    for (y in 0 until height step density) {
                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                            points.add(EmissionPoint(x, y, (width - x) / density, progress))
                            hasContent = true
                        }
                    }

                    if (!hasContent && x % (density * 2) == 0) {
                        val y = height / 2
                        points.add(EmissionPoint(x, y, (width - x) / density, progress))
                    }
                }
            }

            Direction.TOP_TO_BOTTOM -> {
                for (y in 0 until height step density) {
                    val progress = y.toFloat() / height
                    var hasContent = false
                    for (x in 0 until width step density) {
                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                            points.add(EmissionPoint(x, y, y / density, progress))
                            hasContent = true
                        }
                    }

                    if (!hasContent && y % (density * 2) == 0) {
                        val x = width / 2
                        points.add(EmissionPoint(x, y, y / density, progress))
                    }
                }
            }

            Direction.BOTTOM_TO_TOP -> {
                for (y in height - 1 downTo 0 step density) {
                    val progress = 1f - (y.toFloat() / height)
                    var hasContent = false
                    for (x in 0 until width step density) {
                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
                            points.add(EmissionPoint(x, y, (height - y) / density, progress))
                            hasContent = true
                        }
                    }

                    if (!hasContent && y % (density * 2) == 0) {
                        val x = width / 2
                        points.add(EmissionPoint(x, y, (height - y) / density, progress))
                    }
                }
            }
        }

        Log.d("EmissionStrategy", "Generated ${points.size} emission points")

        return points
    }

    // Make emission progress aligned with content disappearance
    // Progress is simply based on elapsed time relative to total duration
    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
        val progress = if (totalDuration == Duration.ZERO) 1f
        else (currentTime / totalDuration).coerceIn(0.0, 1.0).toFloat()

        // Add logging to help debug
        if (currentTime.inWholeMilliseconds % 200 == 0L) {
            Log.d("EmissionStrategy", "Progress: $progress (current=$currentTime, total=$totalDuration)")
        }

        return progress
    }

    // Use the point's progress directly to determine emission timing
    // This ensures perfect sync with content disappearance
    override fun shouldEmitParticle(
        point: EmissionPoint, currentTime: Duration, totalDuration: Duration
    ): Boolean {
        // Get overall progress
        val progress = getEmissionProgress(currentTime, totalDuration)

        // Use progress to determine if this particle should be emitted
        // We'll emit particles when overall progress reaches their point progress
        return point.progress <= progress
    }
}
//package com.binissa.particlize.lib.strategy.emission
//
//import android.graphics.Bitmap
//import android.util.Log
//import com.binissa.particlize.util.EffectUtils
//import kotlin.time.Duration
//import kotlin.time.Duration.Companion.milliseconds
//
//class DirectionalEmissionStrategy(
//    internal val direction: Direction,
//    internal val emissionDelay: Duration = 50.milliseconds,
//    internal val emissionRate: Int = 2,
//) : EmissionStrategy {
//
//    enum class Direction { LEFT_TO_RIGHT, RIGHT_TO_LEFT, TOP_TO_BOTTOM, BOTTOM_TO_TOP }
//
//    // Constructor for backward compatibility
//    constructor(
//        direction: Direction, emissionRate: Int = 2, emissionDelayMs: Int = 50
//    ) : this(
//        direction = direction,
//        emissionRate = emissionRate,
//        emissionDelay = emissionDelayMs.milliseconds
//    )
//
//    override fun calculateEmissionPoints(bitmap: Bitmap, density: Int): List<EmissionPoint> {
//        val width = bitmap.width
//        val height = bitmap.height
//        val points = ArrayList<EmissionPoint>()
//
//        Log.d("MAMO", "Calculating points for ${width}x${height} bitmap, density=$density")
//
//        // Sample pixels directly from bitmap - no coordinate manipulation
//        when (direction) {
//            Direction.LEFT_TO_RIGHT -> {
//                for (x in 0 until width step density) {
//                    val progress = x.toFloat() / width
//                    var hasContent = false
//                    for (y in 0 until height step density) {
//                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
//                            points.add(EmissionPoint(x, y, x / density, progress))
//                            hasContent = true
//                        }
//                    }
//
//                    if (!hasContent && x % (density * 2) == 0) {
//                        val y = height / 2
//                        points.add(EmissionPoint(x, y, x / density, progress))
//                    }
//                }
//            }
//
//            Direction.RIGHT_TO_LEFT -> {
//                for (x in width - 1 downTo 0 step density) {
//                    val progress = 1f - (x.toFloat() / width)
//                    var hasContent = false
//                    for (y in 0 until height step density) {
//                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
//                            points.add(EmissionPoint(x, y, (width - x) / density, progress))
//                            hasContent = true
//                        }
//                    }
//
//                    if (!hasContent && x % (density * 2) == 0) {
//                        val y = height / 2
//                        points.add(EmissionPoint(x, y, (width - x) / density, progress))
//                    }
//                }
//            }
//
//            Direction.TOP_TO_BOTTOM -> {
//                for (y in 0 until height step density) {
//                    val progress = y.toFloat() / height
//                    var hasContent = false
//                    for (x in 0 until width step density) {
//                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
//                            points.add(EmissionPoint(x, y, y / density, progress))
//                            hasContent = true
//                        }
//                    }
//
//                    if (!hasContent && y % (density * 2) == 0) {
//                        val x = width / 2
//                        points.add(EmissionPoint(x, y, y / density, progress))
//                    }
//                }
//            }
//
//            Direction.BOTTOM_TO_TOP -> {
//                for (y in height - 1 downTo 0 step density) {
//                    val progress = 1f - (y.toFloat() / height)
//                    var hasContent = false
//                    for (x in 0 until width step density) {
//                        if (EffectUtils.canDrawPixel(bitmap, x, y)) {
//                            points.add(EmissionPoint(x, y, (height - y) / density, progress))
//                            hasContent = true
//                        }
//                    }
//
//                    if (!hasContent && y % (density * 2) == 0) {
//                        val x = width / 2
//                        points.add(EmissionPoint(x, y, (height - y) / density, progress))
//                    }
//                }
//            }
//        }
//
//        Log.d("EmissionStrategy", "Generated ${points.size} emission points")
//
//        return points
//    }
//
//    override fun getEmissionProgress(currentTime: Duration, totalDuration: Duration): Float {
//        val progress = if (totalDuration == Duration.ZERO) 1f
//        else (currentTime / totalDuration).coerceIn(0.0, 1.0).toFloat()
//        return progress
//    }
//
//    override fun shouldEmitParticle(
//        point: EmissionPoint, currentTime: Duration, totalDuration: Duration
//    ): Boolean {
//        if (emissionDelay <= Duration.ZERO) return true
//
//        val targetOrder = (currentTime.inWholeMilliseconds / emissionDelay.inWholeMilliseconds / emissionRate)
//        return point.order <= targetOrder
//    }
//}