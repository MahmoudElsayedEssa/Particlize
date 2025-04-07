package com.binissa.particlize.lib.renderer

import android.graphics.Bitmap
import android.graphics.Point
import com.binissa.particlize.lib.renderer.canvas.content.CanvasContentRenderer
import com.binissa.particlize.lib.core.model.ParticleState

interface ParticleRenderer<T> {
    fun prepare(context: T)
    fun renderParticles(context: T, state: ParticleState)
    fun renderContent(context: T, state: ParticleState)
    fun updateBitmap(newBitmap: Bitmap)
    fun setOffset(newOffset: Point)
    fun setContentRenderStrategy(newOffset: CanvasContentRenderer)

    fun cleanup(context: T)
}