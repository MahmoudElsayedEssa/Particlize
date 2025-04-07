package com.binissa.particlize.lib.renderer.canvas.shapes


import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.renderer.ParticleShapeStrategy

class CanvasCircleShape : ParticleShapeStrategy<CanvasContext> {

    override fun drawParticle(
        context: CanvasContext,
        particle: Particle,
        offsetX: Float,
        offsetY: Float
    ) {
        val canvas = context.first
        val paint = context.second

        // Set paint properties
        paint.color = particle.color
        paint.alpha = particle.alpha

        // Draw circle
        canvas.drawCircle(
            particle.x + offsetX,
            particle.y + offsetY,
            particle.radius * particle.scale,
            paint
        )
    }

}