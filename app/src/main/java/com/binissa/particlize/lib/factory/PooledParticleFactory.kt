package com.binissa.particlize.lib.factory

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.get
import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.strategy.emission.EmissionPoint
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PooledParticleFactory(
    private val initialCapacity: Int = 10000
) : ParticleFactory {

    private val pool = ConcurrentLinkedQueue<Particle>()
    private var nextId = 0

    init {
        // Pre-allocate particles
        for (i in 0 until initialCapacity) {
            pool.add(createNewParticle(i))
        }
    }

    private fun createNewParticle(id: Int): Particle {
        return Particle(
            id = id,
            initialX = 0f,
            initialY = 0f,
            x = 0f,
            y = 0f,
            rotation = 0f,
            scale = 1f,
            alpha = 255,
            color = 0,
            radius = 1f,
            lifetime = 1000.milliseconds,
            elapsedTime = Duration.ZERO,
            currentTime = Duration.ZERO,
            userData = mutableMapOf()
        )
    }

    override fun createParticle(
        id: Int,
        point: EmissionPoint,
        bitmap: Bitmap,
        minLifetime: Duration,
        maxLifetime: Duration,
        particleScaling: Float
    ): Particle {
        // Try to get from pool
        val pooledParticle = pool.poll() ?: createNewParticle(nextId++)

        // Generate particle ID
        val particleId = id.takeIf { it >= 0 } ?: nextId++

        // Calculate random radius
        val random = Random(particleId)
        val size = (Math.abs(point.order % 5) + 3).coerceAtLeast(1)
        val minRadius = size / 4f
        val maxRadius = size.toFloat()
        val radius = (minRadius + random.nextFloat() * (maxRadius - minRadius)) * particleScaling

        // Get pixel color safely - important fix
        val color = safeGetPixelColor(bitmap, point, random)

        // Randomize lifetime
        val lifetime = if (minLifetime == maxLifetime) {
            minLifetime
        } else {
            minLifetime + randomDurationBetween(minLifetime, maxLifetime)
        }

        // Calculate alpha
        val alpha = (Color.alpha(color) * 0.9 + random.nextFloat() * 0.2).toInt().coerceIn(0, 255)

        // Initialize the particle (reusing the same instance)
        pooledParticle.id = particleId
        pooledParticle.initialX = point.x.toFloat()
        pooledParticle.initialY = point.y.toFloat()
        pooledParticle.x = point.x.toFloat()
        pooledParticle.y = point.y.toFloat()
        pooledParticle.rotation = 0f
        pooledParticle.scale = 1f
        pooledParticle.alpha = alpha
        pooledParticle.color = color
        pooledParticle.radius = radius
        pooledParticle.lifetime = lifetime
        pooledParticle.elapsedTime = Duration.ZERO
        pooledParticle.currentTime = Duration.ZERO
        pooledParticle.userData.clear()
        pooledParticle.withUserData("initialAlpha", alpha)

        return pooledParticle
    }

    /**
     * Safely get pixel color from bitmap, handling out-of-bounds coordinates.
     * This is critical for assembly effects where particles start off-screen.
     */
    private fun safeGetPixelColor(bitmap: Bitmap, point: EmissionPoint, random: Random): Int {
        // Check if the particle has a color in userData (set by AssemblyEmissionStrategy)
        val userData = point.userData
        if (userData != null && userData.containsKey("color")) {
            return userData["color"] as Int
        }

        // Check if point has a "sampleX" and "sampleY" in userData
        val sampleX = userData?.get("sampleX") as? Int
        val sampleY = userData?.get("sampleY") as? Int

        // Try to use provided sample coordinates
        if (sampleX != null && sampleY != null) {
            if (sampleX in 0 until bitmap.width && sampleY in 0 until bitmap.height) {
                return try {
                    bitmap.getPixel(sampleX, sampleY)
                } catch (e: Exception) {
                    generateRandomColor(random)
                }
            }
        }

        // If point is within bitmap bounds, use its color
        if (point.x in 0 until bitmap.width && point.y in 0 until bitmap.height) {
            return try {
                bitmap.getPixel(point.x, point.y)
            } catch (e: Exception) {
                generateRandomColor(random)
            }
        }

        // For off-screen particles, use a default color or sample from a valid position
        // If point order is available, use it to sample from within bitmap
        val validX = Math.abs(point.order % bitmap.width)
        val validY = Math.abs(point.order % bitmap.height)

        return try {
            bitmap.getPixel(validX, validY)
        } catch (e: Exception) {
            generateRandomColor(random)
        }
    }

    /**
     * Generate a random suitable color for particles
     */
    private fun generateRandomColor(random: Random): Int {
        // Create a visually appealing random color with good alpha
        val r = random.nextInt(180, 256)
        val g = random.nextInt(180, 256)
        val b = random.nextInt(180, 256)
        val a = random.nextInt(200, 256)
        return Color.argb(a, r, g, b)
    }

    fun randomDurationBetween(min: Duration, max: Duration): Duration {
        require(min <= max) { "minDuration must be less than or equal to maxDuration" }

        if (min == max) return min

        val minMillis = min.inWholeMilliseconds
        val maxMillis = max.inWholeMilliseconds

        val randomMillis = Random.nextLong(from = minMillis, until = maxMillis + 1)
        return randomMillis.milliseconds
    }

    override fun recycleParticle(particle: Particle) {
        // Reset particle state
        particle.reset()

        // Only keep a reasonable number of particles in the pool
        if (pool.size < initialCapacity * 2) {
            pool.offer(particle)
        }
    }
}
//package com.binissa.particlize.lib.factory
//
//import android.graphics.Bitmap
//import android.graphics.Color
//import androidx.core.graphics.get
//import com.binissa.particlize.lib.core.model.Particle
//import com.binissa.particlize.lib.strategy.emission.EmissionPoint
//import java.util.concurrent.ConcurrentLinkedQueue
//import kotlin.random.Random
//import kotlin.time.Duration
//import kotlin.time.Duration.Companion.milliseconds
//
//class PooledParticleFactory(
//    private val initialCapacity: Int = 10000
//) : ParticleFactory {
//
//    private val pool = ConcurrentLinkedQueue<Particle>()
//    private var nextId = 0
//
//    init {
//        // Pre-allocate particles
//        for (i in 0 until initialCapacity) {
//            pool.add(createNewParticle(i))
//        }
//    }
//
//    private fun createNewParticle(id: Int): Particle {
//        return Particle(
//            id = id,
//            initialX = 0f,
//            initialY = 0f,
//            x = 0f,
//            y = 0f,
//            rotation = 0f,
//            scale = 1f,
//            alpha = 255,
//            color = 0,
//            radius = 1f,
//            lifetime = 1000.milliseconds,
//            elapsedTime = Duration.ZERO,
//            currentTime = Duration.ZERO,
//            userData = mutableMapOf()
//        )
//    }
//
//    override fun createParticle(
//        id: Int,
//        point: EmissionPoint,
//        bitmap: Bitmap,
//        minLifetime: Duration,
//        maxLifetime: Duration,
//        particleScaling: Float
//    ): Particle {
//        // Try to get from pool
//        val pooledParticle = pool.poll() ?: createNewParticle(nextId++)
//
//        // Generate particle ID
//        val particleId = id.takeIf { it >= 0 } ?: nextId++
//
//        // Calculate random radius
//        val random = Random(particleId)
//        val size = (point.x % 5 + 3).coerceAtLeast(1)
//        val minRadius = size / 4f
//        val maxRadius = size.toFloat()
//        val radius = (minRadius + random.nextFloat() * (maxRadius - minRadius)) * particleScaling
//
//        // Get color from bitmap
//        val color = bitmap[point.x, point.y]
//
//        // Randomize lifetime
//        val lifetime = if (minLifetime == maxLifetime) {
//            minLifetime
//        } else {
//            minLifetime + randomDurationBetween(minLifetime, maxLifetime)
//        }
//
//        // Calculate alpha
//        val alpha = (Color.alpha(color) * 0.9 + random.nextFloat() * 0.2).toInt().coerceIn(0, 255)
//
//        // Initialize the particle (reusing the same instance)
//        pooledParticle.id = particleId
//        pooledParticle.initialX = point.x.toFloat()
//        pooledParticle.initialY = point.y.toFloat()
//        pooledParticle.x = point.x.toFloat()
//        pooledParticle.y = point.y.toFloat()
//        pooledParticle.rotation = 0f
//        pooledParticle.scale = 1f
//        pooledParticle.alpha = alpha
//        pooledParticle.color = color
//        pooledParticle.radius = radius
//        pooledParticle.lifetime = lifetime
//        pooledParticle.elapsedTime = Duration.ZERO
//        pooledParticle.currentTime = Duration.ZERO
//        pooledParticle.userData.clear()
//        pooledParticle.withUserData("initialAlpha", alpha)
//
//        return pooledParticle
//    }
//
//    fun randomDurationBetween(min: Duration, max: Duration): Duration {
//        require(min <= max) { "minDuration must be less than or equal to maxDuration" }
//
//        if (min == max) return min
//
//        val minMillis = min.inWholeMilliseconds
//        val maxMillis = max.inWholeMilliseconds
//
//        val randomMillis = Random.nextLong(from = minMillis, until = maxMillis + 1)
//        return randomMillis.milliseconds
//    }
//
//    override fun recycleParticle(particle: Particle) {
//        // Reset particle state
//        particle.reset()
//
//        // Only keep a reasonable number of particles in the pool
//        if (pool.size < initialCapacity * 2) {
//            pool.offer(particle)
//        }
//    }
//}