package com.binissa.particlize.lib.renderer.canvas.shapes

import android.graphics.Path
import androidx.core.graphics.withSave
import com.binissa.particlize.lib.core.model.Particle
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.renderer.ParticleShapeStrategy
import kotlin.math.cos
import kotlin.math.sin


class CanvasStarShape : ParticleShapeStrategy<CanvasContext> {
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

        val path = createStarPath(x, y, radius)

        canvas.withSave {
            if (particle.rotation != 0f) {
                rotate(particle.rotation, x, y)
            }
            drawPath(path, paint)
        }
    }


    private fun createStarPath(cx: Float, cy: Float, radius: Float): Path {
        val path = Path()
        val angle = Math.PI / 2.0 * 3.0 // Start angle (pointing up)
        val step = Math.PI / 5.0 // Angle step for star points

        path.moveTo(
            (cx + radius * cos(angle)).toFloat(), (cy - radius * sin(angle)).toFloat()
        )

        for (i in 1..10) {
            val r = if (i % 2 == 0) radius else radius / 2.5f
            val x = cx + r * cos(angle + step * i)
            val y = cy - r * sin(angle + step * i)
            path.lineTo(x.toFloat(), y.toFloat())
        }

        path.close()
        return path
    }
}
