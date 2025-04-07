package com.binissa.particlize.lib.renderer

import com.binissa.particlize.lib.core.model.Particle

interface ParticleShapeStrategy<T> {
    /**
     * Draw a particle with the specified shape.
     */
    fun drawParticle(
        context: T,
        particle: Particle,
        offsetX: Float,
        offsetY: Float
    )

}