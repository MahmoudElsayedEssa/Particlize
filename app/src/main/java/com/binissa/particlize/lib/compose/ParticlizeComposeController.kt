package com.binissa.particlize.lib.compose

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.binissa.particlize.lib.controller.ParticlizeController
import com.binissa.particlize.lib.core.model.ParticleEffect

/**
 * Controller for particle effects in Compose.
 */
class ParticlizeComposeController {
    // Component state
    internal val location = mutableStateOf(Offset.Zero)
    internal var graphicsLayer: GraphicsLayer? = null

    // Effect state
    private var _isEffectStarted by mutableStateOf(false)

    /**
     * Starts the particle effect.
     */
    suspend fun start(
        context: android.content.Context, effect: ParticleEffect, onComplete: () -> Unit = {}
    ) {
        // Prevent multiple starts
        if (_isEffectStarted) return

        val gl = graphicsLayer ?: return
        _isEffectStarted = true

        try {
            val bitmap = gl.toImageBitmap().asAndroidBitmap()

            if (bitmap.width <= 1 || bitmap.height <= 1) {
                _isEffectStarted = false
                onComplete()
                return
            }

            Log.d("ParticleEffectCompose", "Starting effect: ${bitmap.width}x${bitmap.height}")

            // Copy to ARGB_8888 format if needed
            val effectBitmap = bitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true)

            // Apply effect through the controller
            ParticlizeController().applyComposeEffect(
                context = context, bitmap = effectBitmap, location = location.value, effect = effect
            ) {
                Log.d("ParticleEffectCompose", "Effect complete")
                effectBitmap.recycle()
                _isEffectStarted = false
                onComplete()
            }
        } catch (e: Exception) {
            Log.e("ParticleEffectCompose", "Error starting effect", e)
            _isEffectStarted = false
            onComplete()
        }
    }

    /**
     * Checks if the effect has started.
     */
    fun hasEffectStarted(): Boolean = _isEffectStarted
}

