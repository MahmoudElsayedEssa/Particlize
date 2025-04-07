package com.binissa.particlize.lib.renderer.canvas.particle

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.SurfaceTexture
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.TextureView
import com.binissa.particlize.lib.core.engine.ParticleSystem
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.event.EffectEvent
import com.binissa.particlize.lib.event.EffectEventType
import com.binissa.particlize.lib.event.EventListener
import com.binissa.particlize.lib.renderer.canvas.CanvasParticleRenderer
import com.binissa.particlize.lib.renderer.canvas.content.CanvasContentRenderer
import com.binissa.particlize.lib.view.EffectView
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ParticleRendererView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val particleSystem: ParticleSystem<CanvasContext>,
) : TextureView(context, attrs, defStyleAttr), TextureView.SurfaceTextureListener {

    private val TAG = "ParticleRendererView"

    // Main thread handler for UI operations
    private val mainHandler = Handler(Looper.getMainLooper())

    // Rendering thread
    private var renderThread: RenderThread? = null

    // Paint for drawing
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    // Track active effects
    private val activeEffectIds = CopyOnWriteArrayList<String>()

    // Flag to track if rendering is active
    @Volatile
    private var isRendering = false

    // Initialize renderer and particle system for each effect
//    private val particleSystem = ParticleSystem(
//        renderer = CanvasParticleRenderer(
//            bitmap = createBitmap(1, 1),
//            offset = Point(0, 0),
//            particleShapeStrategy = CanvasCircleShape(),
//            contentRenderStrategy = null // Will be set per effect
//        )
//    )

    init {
        isOpaque = false
        surfaceTextureListener = this
    }

    /**
     * Apply an effect to the given view.
     */
    fun applyEffect(
        effectView: EffectView, effect: ParticleEffect, onComplete: () -> Unit = {}
    ) {
        Log.d(TAG, "Applying effect ${effect.id}")

        // Start rendering if needed
        startRenderingIfNeeded()

        // Get bitmap and location
        val bitmap = effectView.createBitmap()
        val location = IntArray(2)
        effectView.getLocationInWindow(location)

        // Create appropriate content renderer based on effect params
        val contentRenderer = CanvasContentRenderer(
            emissionStrategy = effect.emissionStrategy,
            contentDisappearanceMode = effect.contentDisappearanceMode
        )

        // Configure renderer with the new bitmap, location, and content renderer
        val renderer = particleSystem.renderer as CanvasParticleRenderer
        renderer.updateBitmap(bitmap)
        renderer.setOffset(Point(location[0], location[1]))
        renderer.setContentRenderStrategy(contentRenderer)

        // Add to active effects - thread-safe list
        activeEffectIds.add(effect.id)

        // Register completion listener
        particleSystem.eventBus.addListener(object : EventListener {
            override fun onEvent(event: EffectEvent) {
                if (event.effectId == effect.id && event.type == EffectEventType.COMPLETED) {
                    // The EventBus might call this from the render thread, so make sure
                    // any callbacks/UI operations are posted to main thread
                    activeEffectIds.remove(effect.id)
                    particleSystem.eventBus.removeListener(this)

                    mainHandler.post {
                        onComplete()
                    }
                }
            }
        })

        // Apply effect
        particleSystem.applyEffect(effectView, effect)
    }

    /**
     * Check if there are any active effects.
     */
    fun hasActiveEffects(): Boolean {
        return activeEffectIds.isNotEmpty()
    }

    /**
     * Starts the rendering process if it's not already running.
     */
    private fun startRenderingIfNeeded() {
        if (renderThread == null && isAvailable) {
            Log.d(TAG, "Starting render thread")
            isRendering = true
            renderThread = RenderThread().apply {
                start()
            }
        }
    }

    /**
     * Pauses rendering.
     */
    fun pause() {
        isRendering = false
        Log.d(TAG, "Rendering paused")
    }

    /**
     * Resumes rendering.
     */
    fun resume() {
        if (renderThread != null) {
            isRendering = true
            Log.d(TAG, "Rendering resumed")
        } else {
            startRenderingIfNeeded()
        }
    }

    /**
     * Stops and cleans up the renderer.
     * Must be called on the main thread.
     */
    fun destroy() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post { destroy() }
            return
        }

        Log.d(TAG, "Destroying renderer")
        isRendering = false

        // Interrupt the render thread safely
        val thread = renderThread
        renderThread = null
        thread?.interrupt()

        // Clear all effects - safely done even from non-UI thread
        activeEffectIds.clear()
    }

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture, width: Int, height: Int
    ) {
        Log.d(TAG, "Surface texture available: $width x $height")
        startRenderingIfNeeded()
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture, width: Int, height: Int
    ) {
        Log.d(TAG, "Surface texture size changed: $width x $height")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Log.d(TAG, "Surface texture destroyed")
        isRendering = false
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // Not needed
    }

    /**
     * Thread responsible for rendering the particle effects.
     */
    private inner class RenderThread : Thread() {
        // Target frame time for 60 FPS
        private val targetFrameTime = (1000000000 / 60).toDuration(DurationUnit.NANOSECONDS)

        // Track frame timing
        private var lastFrameTime = System.nanoTime().toDuration(DurationUnit.NANOSECONDS)

        override fun run() {
            Log.d(TAG, "Render thread started")

            try {
                while (!isInterrupted && isRendering) {
                    // Calculate delta time
                    val currentTime = System.nanoTime().toDuration(DurationUnit.NANOSECONDS)
                    val deltaTime = currentTime - lastFrameTime
                    lastFrameTime = currentTime

                    // Skip if we somehow got a huge delta
                    if (deltaTime.inWholeMilliseconds > 100) {
                        continue
                    }

                    if (isAvailable) {
                        val canvas = try {
                            lockCanvas()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error locking canvas", e)
                            null
                        }

                        if (canvas != null) {
                            try {
                                // Clear canvas
                                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

                                // Update particle system
                                val context = canvas to paint
                                particleSystem.update(deltaTime, context)
                            } catch (e: Exception) {
                                Log.e(TAG, "Error in render loop", e)
                            } finally {
                                try {
                                    unlockCanvasAndPost(canvas)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error unlocking canvas", e)
                                }
                            }
                        }
                    }

                    // Sleep to maintain frame rate
                    try {
                        val frameTime =
                            System.nanoTime().toDuration(DurationUnit.NANOSECONDS) - currentTime
                        val remaining = targetFrameTime - frameTime

                        if (remaining > Duration.Companion.ZERO) {
                            sleep(remaining.inWholeMilliseconds)
                        }
                    } catch (e: InterruptedException) {
                        interrupt()
                        break
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Render thread error", e)
            } finally {
                Log.d(TAG, "Render thread stopped")
            }
        }
    }
}