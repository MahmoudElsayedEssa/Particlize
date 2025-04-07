package com.binissa.particlize.lib.controller

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.binissa.particlize.lib.core.engine.ParticleSystem
import com.binissa.particlize.lib.core.model.CanvasContext
import com.binissa.particlize.lib.core.model.ParticleEffect
import com.binissa.particlize.lib.renderer.canvas.CanvasParticleRenderer
import com.binissa.particlize.lib.renderer.canvas.particle.ParticleRendererView
import com.binissa.particlize.lib.view.EffectView

/**
 * Central controller for particle effects.
 */
class ParticlizeController {
    companion object {

        private const val TAG = "ParticleController"
    }

    // Handler for posting operations to the main thread
    private val mainHandler = Handler(Looper.getMainLooper())

    // Track active views
    private val activeRendererViews = mutableMapOf<Context, ParticleRendererView>()
    private var particleSystem: ParticleSystem<CanvasContext>? = null

    /**
     * Applies an effect to a Compose component using its bitmap.
     */
    suspend fun applyComposeEffect(
        context: Context,
        bitmap: Bitmap,
        location: Offset,
        effect: ParticleEffect,
        onComplete: () -> Unit = {}
    ) {
        Log.d(TAG, "applyComposeEffect: effect.particleShape:${effect.particleShape}")
        particleSystem = ParticleSystem(
            renderer = CanvasParticleRenderer(
                bitmap = createBitmap(1, 1, Bitmap.Config.ARGB_8888),
                offset = Point(0, 0),
                particleShapeStrategy = effect.particleShape,
                contentRenderStrategy = null // Will be set per effect
            )
        )

        // Get or create renderer view
        val rendererView = getOrCreateRendererView(context)

        try {
            // Create an effect view from the bitmap and location
            val effectView = createEffectView(bitmap, location)


            // Apply the effect
            rendererView.applyEffect(
                effectView = effectView, effect = effect, onComplete = {
                    // Post completion callback to main thread
                    mainHandler.post {
                        onComplete()
                        checkCleanup(context)
                    }
                })
        } catch (e: Exception) {
            Log.e(TAG, "Error applying effect", e)
            mainHandler.post { onComplete() }
        }
    }

    /**
     * Check if we should clean up the renderer view.
     * Must be called on main thread.
     */
    private fun checkCleanup(context: Context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            // Make sure this runs on main thread
            mainHandler.post { checkCleanup(context) }
            return
        }

        val rendererView = activeRendererViews[context] ?: return

        if (!rendererView.hasActiveEffects()) {
            Log.d(TAG, "No active effects, cleaning up")
            destroy(context)
        }
    }

    /**
     * Pauses all particle effects.
     */
    fun pause(context: Context) {
        Log.d(TAG, "Pausing particle effects")
        activeRendererViews[context]?.pause()
    }

    /**
     * Resumes all particle effects.
     */
    fun resume(context: Context) {
        Log.d(TAG, "Resuming particle effects")
        activeRendererViews[context]?.resume()
    }

    /**
     * Destroys all particle effects and cleans up resources.
     * Must be called on main thread.
     */
    fun destroy(context: Context) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            // Make sure this runs on main thread
            mainHandler.post { destroy(context) }
            return
        }

        Log.d(TAG, "Destroying particle effects")
        val rendererView = activeRendererViews.remove(context)
        rendererView?.destroy()

        // Remove view from parent
        if (rendererView?.parent is ViewGroup) {
            (rendererView.parent as ViewGroup).removeView(rendererView)
        }
    }

    /**
     * Gets or creates a renderer view for the given context.
     * Must be called on main thread.
     */
    private fun getOrCreateRendererView(context: Context): ParticleRendererView {
        // Ensure we're on the main thread
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("getOrCreateRendererView must be called on main thread")
        }

        // Return existing renderer if available
        activeRendererViews[context]?.let { return it }

        // Find root view to attach renderer to
        val activity = context as? ComponentActivity
            ?: throw IllegalArgumentException("Context must be a ComponentActivity")

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
            ?: throw IllegalStateException("Root view not found")

        // Initialize renderer and particle system for each effect

        // Create new renderer view
        val rendererView = ParticleRendererView(context, particleSystem = particleSystem!!).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )

            // Ensure we don't intercept touch events
            setOnTouchListener { _, _ -> false }
        }

        // Add renderer view to root
        rootView.addView(rendererView)
        Log.d(TAG, "Created and added renderer view")

        // Store renderer view
        activeRendererViews[context] = rendererView

        // Setup lifecycle observer
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                pause(context)
            }

            override fun onResume(owner: LifecycleOwner) {
                resume(context)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                destroy(context)
                activity.lifecycle.removeObserver(this)
            }
        })

        return rendererView
    }

    /**
     * Creates an EffectView from a bitmap and offset.
     */
    private fun createEffectView(bitmap: Bitmap, offset: Offset): EffectView = object : EffectView {
        override val width: Int = bitmap.width
        override val height: Int = bitmap.height
        override val translationX: Float = 0f
        override val translationY: Float = 0f

        override fun getLocationInWindow(location: IntArray) {
            location[0] = offset.x.toInt()
            location[1] = offset.y.toInt()
        }

        override fun createBitmap(): Bitmap = bitmap

        override fun remove() {
            // Nothing to remove
        }
    }
}