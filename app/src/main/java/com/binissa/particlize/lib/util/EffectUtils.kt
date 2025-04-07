package com.binissa.particlize.lib.util

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.get

/**
 * Utility functions for the Particlize effect.
 */
object EffectUtils {

    /**
     * Checks if a pixel can be drawn based on its transparency.
     */
    fun canDrawPixel(bitmap: Bitmap, x: Int, y: Int): Boolean {
        try {
            if (x < 0 || y < 0 || x >= bitmap.width || y >= bitmap.height) {
                return false
            }

            // Get the pixel color
            val pixel = bitmap[x, y]

            // Extract alpha value (0-255)
            val alpha = Color.alpha(pixel)

            // Much more permissive threshold - any visible pixel (alpha > 20) counts
            return alpha > 20
        } catch (e: Exception) {
            Log.e("EffectUtils", "Error checking pixel at $x,$y", e)
            return false
        }
    }

}