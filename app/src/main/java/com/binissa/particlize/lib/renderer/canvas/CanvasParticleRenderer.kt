package com.binissa.particlize.lib.renderer.canvas

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Point
import android.util.Log
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.renderer.canvas.content.CanvasContentRenderer
import com.binissa.particlize.lib.renderer.ContentRenderStrategy
import com.binissa.particlize.lib.core.model.ParticleState
import com.binissa.particlize.lib.renderer.ParticleRenderer
import com.binissa.particlize.lib.renderer.ParticleShapeStrategy

class CanvasParticleRenderer(
    private var bitmap: Bitmap,
    private var offset: Point,
    private val particleShapeStrategy: ParticleShapeStrategy<CanvasContext>,
    private var contentRenderStrategy: ContentRenderStrategy<CanvasContext>?
) : ParticleRenderer<CanvasContext> {

    // Pre-create shared Paint object to avoid allocations
    private val sharedPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

    // For performance tracking
    private var frameCount = 0
    private val logInterval = 60

    override fun updateBitmap(newBitmap: Bitmap) {
        this.bitmap = newBitmap
        Log.d("CanvasRenderer", "Updated bitmap to ${newBitmap.width}x${newBitmap.height}")
    }

    // Add an explicit offset setter
    override fun setOffset(newOffset: Point) {
        offset.set(newOffset.x, newOffset.y)
        Log.d("CanvasRenderer", "Updated offset to (${offset.x}, ${offset.y})")
    }

    override fun setContentRenderStrategy(strategy: CanvasContentRenderer) {
        this.contentRenderStrategy = strategy
        Log.d("CanvasRenderer", "Updated content render strategy")

    }

    // Add a setter for content render strategy

    override fun prepare(context: CanvasContext) {
        // Reset shared paint to default state
        sharedPaint.reset()
        sharedPaint.isAntiAlias = true
        sharedPaint.isDither = true
        sharedPaint.style = Paint.Style.FILL

        // Get the canvas and paint from context
        val canvas = context.first
        val providedPaint = context.second

        // Copy any special settings from the provided paint
        sharedPaint.alpha = providedPaint.alpha
        sharedPaint.colorFilter = providedPaint.colorFilter
        sharedPaint.xfermode = providedPaint.xfermode
    }

    override fun renderParticles(context: CanvasContext, state: ParticleState) {
        val canvas = context.first

        // Skip if no particles
        if (state.particles.isEmpty()) {
            return
        }

        // Save canvas state
        val saveCount = canvas.save()

        try {
            // Process performance tracking
            frameCount++
            if (frameCount % logInterval == 0) {
                Log.d("CanvasRenderer", "Rendering ${state.particles.size} particles")
            }

            // Get canvas dimensions for bounds checking
            val canvasWidth = canvas.width
            val canvasHeight = canvas.height

            // Draw each particle
            for (particle in state.particles) {
                try {
                    // Skip particles with zero or very low alpha
                    if (particle.alpha <= 5) {
                        continue
                    }

                    // Skip particles outside the viewport
                    val margin = (particle.radius * particle.scale * 2).coerceAtLeast(10f)
                    val screenX = particle.x + offset.x
                    val screenY = particle.y + offset.y

                    if (screenX + margin < 0 || screenX - margin > canvasWidth ||
                        screenY + margin < 0 || screenY - margin > canvasHeight) {
                        continue
                    }

                    // Set color and alpha (reusing the shared paint)
                    sharedPaint.color = particle.color
                    sharedPaint.alpha = particle.alpha

                    // Create a new context with our shared paint to avoid allocations
                    val optimizedContext = canvas to sharedPaint

                    // Draw the particle
                    particleShapeStrategy.drawParticle(
                        optimizedContext, particle, offset.x.toFloat(), offset.y.toFloat()
                    )
                } catch (e: Exception) {
                    Log.e("CanvasRenderer", "Error drawing particle: $e")
                }
            }
        } finally {
            // Restore canvas state
            canvas.restoreToCount(saveCount)
        }
    }

    override fun renderContent(context: CanvasContext, state: ParticleState) {
        // Skip rendering if content is invisible
        if (state.contentVisibility <= 0) {
            return
        }

        // Skip if no content renderer strategy or bitmap is invalid
        if (contentRenderStrategy == null || bitmap.width <= 0 || bitmap.height <= 0) {
            return
        }

        // Save canvas state before content rendering
        val canvas = context.first
        val saveCount = canvas.save()

        try {
            contentRenderStrategy?.renderContent(
                context, bitmap, offset, state.contentVisibility, state.emissionProgress
            )
        } catch (e: Exception) {
            Log.e("CanvasRenderer", "Error rendering content: $e")
        } finally {
            // Always restore canvas
            canvas.restoreToCount(saveCount)
        }
    }

    override fun cleanup(context: CanvasContext) {
        // Clean up resources
        sharedPaint.reset()

        // If the content renderer has a cleanup method, call it
        contentRenderStrategy?.cleanup()
    }
}