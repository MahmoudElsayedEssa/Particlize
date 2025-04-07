package com.binissa.particlize.lib.renderer.canvas.shapes

import androidx.core.graphics.withSave
import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.renderer.ParticleShapeStrategy

class CanvasRectangleShape : ParticleShapeStrategy<CanvasContext> {
    override fun drawParticle(
        renderContext: CanvasContext, particle: Particle, offsetX: Float, offsetY: Float
    ) {
        val canvas = renderContext.first
        val paint = renderContext.second

        paint.color = particle.color
        paint.alpha = particle.alpha

        val x = offsetX + particle.x
        val y = offsetY + particle.y
        val radius = particle.radius * particle.scale

        canvas.withSave {
            if (particle.rotation != 0f) {
                rotate(particle.rotation, x, y)
            }
            drawRect(x - radius, y - radius, x + radius, y + radius, paint)
        }
    }

}
