package com.binissa.particlize.lib.renderer.canvas.content

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.Shader
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.core.model.ContentDisappearanceMode
import com.binissa.particlize.lib.renderer.ContentRenderStrategy
import com.binissa.particlize.lib.strategy.emission.DirectionalEmissionStrategy
import com.binissa.particlize.lib.strategy.emission.EmissionStrategy
import com.binissa.particlize.lib.strategy.emission.RadialEmissionStrategy
import kotlin.math.hypot
import kotlin.math.min

class CanvasContentRenderer(
    private val emissionStrategy: EmissionStrategy,
    private val contentDisappearanceMode: ContentDisappearanceMode
) : ContentRenderStrategy<CanvasContext> {

    // Source and destination rectangles for bitmap drawing
    private val srcRect = Rect()
    private val dstRect = Rect()
    private val touchPoint: Point? = null // Optional touch point for touch-based effects

    /**
     * Set a touch point for touch-based effects.
     */
    fun setTouchPoint(x: Int, y: Int) {
        touchPoint?.set(x, y)
    }

    override fun renderContent(
        renderContext: CanvasContext,
        bitmap: Bitmap,
        offset: Point,
        contentVisibility: Float,
        emissionProgress: Float
    ) {
        // Skip if content is not visible
        if (contentVisibility <= 0f) {
            return
        }

        val canvas = renderContext.first
        val paint = renderContext.second

        // Store original paint settings
        val originalAlpha = paint.alpha
        val originalXfermode = paint.xfermode
        val originalShader = paint.shader
        val originalColor = paint.color

        try {
            // Set destination rectangle (where the bitmap will be drawn on screen)
            dstRect.set(
                offset.x, offset.y, offset.x + bitmap.width, offset.y + bitmap.height
            )

            // Set source rectangle (what part of the bitmap to draw)
            srcRect.set(0, 0, bitmap.width, bitmap.height)

            // Apply disappearance mode
            when (contentDisappearanceMode) {
                ContentDisappearanceMode.NONE -> {
                    // Draw full bitmap
                    canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
                }

                ContentDisappearanceMode.INSTANT -> {
                    // Show only at the beginning
                    if (emissionProgress <= 0.01f) {
                        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
                    }
                }

                ContentDisappearanceMode.FADE -> {
                    // Simple alpha fade
                    paint.alpha = ((1f - emissionProgress) * 255).toInt().coerceIn(0, 255)
                    if (paint.alpha > 0) {
                        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
                    }
                }

                ContentDisappearanceMode.PROGRESSIVE -> {
                    // Create a saved layer for proper compositing
                    val saveCount = canvas.saveLayer(
                        offset.x.toFloat(),
                        offset.y.toFloat(),
                        (offset.x + bitmap.width).toFloat(),
                        (offset.y + bitmap.height).toFloat(),
                        null
                    )

                    try {
                        // First draw the bitmap
                        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)

                        when (emissionStrategy) {
                            is DirectionalEmissionStrategy -> {
                                applyDirectionalMasking(
                                    canvas,
                                    paint,
                                    bitmap,
                                    offset,
                                    emissionProgress,
                                    emissionStrategy.direction
                                )
                            }

                            is RadialEmissionStrategy -> {
                                applyCenterOutMasking(
                                    canvas, paint, bitmap, offset, emissionProgress
                                )
                            }

                            else -> {
                                // Default to fade for unknown strategies
                                paint.alpha =
                                    ((1f - emissionProgress) * 255).toInt().coerceIn(0, 255)
                                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
                                canvas.drawRect(dstRect, paint)
                            }
                        }
                    } finally {
                        canvas.restoreToCount(saveCount)
                    }
                }

                ContentDisappearanceMode.ASSEMBLY -> {
                    // Use the new assembly rendering method
                    if (contentVisibility <= 1f) {
                        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
                    }

                }

            }
        } finally {
            // Restore original paint settings
            paint.alpha = originalAlpha
            paint.xfermode = originalXfermode
            paint.shader = originalShader
            paint.color = originalColor
        }
    }

    /**
     * Apply directional masking based on the emission direction.
     */
    private fun applyDirectionalMasking(
        canvas: Canvas,
        paint: Paint,
        bitmap: Bitmap,
        offset: Point,
        progress: Float,
        direction: DirectionalEmissionStrategy.Direction
    ) {
        if (progress >= 1f) return

        // Configure paint for masking
        paint.color = Color.BLACK
        paint.alpha = 255
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        when (direction) {
            DirectionalEmissionStrategy.Direction.LEFT_TO_RIGHT -> {
                // Create a rectangle mask covering the left portion of the image
                canvas.drawRect(
                    offset.x.toFloat(),
                    offset.y.toFloat(),
                    offset.x + bitmap.width * progress,
                    offset.y + bitmap.height.toFloat(),
                    paint
                )

                // Add a soft gradient edge
                if (progress > 0.01f && progress < 0.99f) {
                    val gradientWidth = min(bitmap.width * 0.05f, 10f)
                    val gradientStart = offset.x + bitmap.width * progress - gradientWidth
                    val gradientEnd = offset.x + bitmap.width * progress

                    val shader = LinearGradient(
                        gradientStart,
                        offset.y.toFloat(),
                        gradientEnd,
                        offset.y.toFloat(),
                        Color.BLACK,
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )

                    paint.shader = shader

                    canvas.drawRect(
                        gradientStart,
                        offset.y.toFloat(),
                        gradientEnd,
                        offset.y + bitmap.height.toFloat(),
                        paint
                    )
                }
            }

            DirectionalEmissionStrategy.Direction.RIGHT_TO_LEFT -> {
                // Create a rectangle mask covering the right portion of the image
                canvas.drawRect(
                    offset.x + bitmap.width * (1 - progress),
                    offset.y.toFloat(),
                    offset.x + bitmap.width.toFloat(),
                    offset.y + bitmap.height.toFloat(),
                    paint
                )

                // Add a soft gradient edge
                if (progress > 0.01f && progress < 0.99f) {
                    val gradientWidth = min(bitmap.width * 0.05f, 10f)
                    val gradientStart = offset.x + bitmap.width * (1 - progress)
                    val gradientEnd = gradientStart + gradientWidth

                    val shader = LinearGradient(
                        gradientStart,
                        offset.y.toFloat(),
                        gradientEnd,
                        offset.y.toFloat(),
                        Color.TRANSPARENT,
                        Color.BLACK,
                        Shader.TileMode.CLAMP
                    )

                    paint.shader = shader

                    canvas.drawRect(
                        gradientStart,
                        offset.y.toFloat(),
                        gradientEnd,
                        offset.y + bitmap.height.toFloat(),
                        paint
                    )
                }
            }

            DirectionalEmissionStrategy.Direction.TOP_TO_BOTTOM -> {
                // Create a rectangle mask covering the top portion of the image
                canvas.drawRect(
                    offset.x.toFloat(),
                    offset.y.toFloat(),
                    offset.x + bitmap.width.toFloat(),
                    offset.y + bitmap.height * progress,
                    paint
                )

                // Add a soft gradient edge
                if (progress > 0.01f && progress < 0.99f) {
                    val gradientHeight = min(bitmap.height * 0.05f, 10f)
                    val gradientStart = offset.y + bitmap.height * progress - gradientHeight
                    val gradientEnd = offset.y + bitmap.height * progress

                    val shader = LinearGradient(
                        offset.x.toFloat(),
                        gradientStart,
                        offset.x.toFloat(),
                        gradientEnd,
                        Color.BLACK,
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )

                    paint.shader = shader

                    canvas.drawRect(
                        offset.x.toFloat(),
                        gradientStart,
                        offset.x + bitmap.width.toFloat(),
                        gradientEnd,
                        paint
                    )
                }
            }

            DirectionalEmissionStrategy.Direction.BOTTOM_TO_TOP -> {
                // Create a rectangle mask covering the bottom portion of the image
                canvas.drawRect(
                    offset.x.toFloat(),
                    offset.y + bitmap.height * (1 - progress),
                    offset.x + bitmap.width.toFloat(),
                    offset.y + bitmap.height.toFloat(),
                    paint
                )

                // Add a soft gradient edge
                if (progress > 0.01f && progress < 0.99f) {
                    val gradientHeight = min(bitmap.height * 0.05f, 10f)
                    val gradientStart = offset.y + bitmap.height * (1 - progress)
                    val gradientEnd = gradientStart + gradientHeight

                    val shader = LinearGradient(
                        offset.x.toFloat(),
                        gradientStart,
                        offset.x.toFloat(),
                        gradientEnd,
                        Color.TRANSPARENT,
                        Color.BLACK,
                        Shader.TileMode.CLAMP
                    )

                    paint.shader = shader

                    canvas.drawRect(
                        offset.x.toFloat(),
                        gradientStart,
                        offset.x + bitmap.width.toFloat(),
                        gradientEnd,
                        paint
                    )
                }
            }
        }
    }

    /**
     * Center-out masking effect (hole from center).
     */
    private fun applyCenterOutMasking(
        canvas: Canvas, paint: Paint, bitmap: Bitmap, offset: Point, progress: Float
    ) {
        if (progress >= 1f) return

        // Calculate center and radius
        val centerX = offset.x + bitmap.width / 2f
        val centerY = offset.y + bitmap.height / 2f
        val maxRadius = hypot(bitmap.width / 2.0, bitmap.height / 2.0).toFloat()
        val holeRadius = maxRadius * progress

        // Create the hole mask
        if (holeRadius > 0) {
            paint.color = Color.BLACK
            paint.alpha = 255
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

            // Draw a circle to create the hole
            canvas.drawCircle(centerX, centerY, holeRadius, paint)

            // Add a smooth edge
            if (progress > 0.01f && progress < 0.99f) {
                val gradientWidth = min(maxRadius * 0.1f, 15f)
                val gradientInnerRadius = holeRadius - gradientWidth

                if (gradientInnerRadius > 0) {
                    val shader = RadialGradient(
                        centerX,
                        centerY,
                        holeRadius,
                        intArrayOf(Color.BLACK, Color.TRANSPARENT),
                        floatArrayOf(gradientInnerRadius / holeRadius, 1f),
                        Shader.TileMode.CLAMP
                    )

                    paint.shader = shader
                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

                    // Draw over the hole edge to smooth it
                    canvas.drawCircle(centerX, centerY, holeRadius, paint)
                }
            }
        }
    }

    override fun cleanup() {
        // Nothing to clean up
    }
}