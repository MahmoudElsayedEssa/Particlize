package com.binissa.particlize.lib.factory

import android.graphics.Bitmap
import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.strategy.emission.EmissionPoint
import kotlin.time.Duration

interface ParticleFactory {
    fun createParticle(
        id: Int,
        point: EmissionPoint,
        bitmap: Bitmap,
        minLifetime: Duration,
        maxLifetime: Duration,
        particleScaling: Float = 1.0f
    ): Particle

    fun recycleParticle(particle: Particle)
}