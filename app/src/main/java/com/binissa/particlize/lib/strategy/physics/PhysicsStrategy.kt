package com.binissa.particlize.lib.strategy.physics

import android.graphics.Point
import com.binissa.particlize.lib.core.model.Particle
import kotlin.time.Duration

interface PhysicsStrategy {
    fun updateParticle(
        particle: Particle,
        deltaTime: Duration,
        centerPoint: Point? = null
    )
}