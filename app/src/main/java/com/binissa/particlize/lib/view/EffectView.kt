package com.binissa.particlize.lib.view

import android.graphics.Bitmap

interface EffectView {
    val width: Int
    val height: Int
    val translationX: Float
    val translationY: Float

    fun getLocationInWindow(location: IntArray)
    fun createBitmap(): Bitmap
    fun remove()
}