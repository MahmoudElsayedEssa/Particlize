package com.binissa.particlize.lib.renderer

import android.graphics.Bitmap
import android.graphics.Point

interface ContentRenderStrategy<T> {
    fun renderContent(
        renderContext: T,
        bitmap: Bitmap,
        offset: Point,
        contentVisibility: Float,
        emissionProgress: Float
    )
    fun cleanup()
}