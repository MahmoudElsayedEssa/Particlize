package com.binissa.particlize.lib.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow

/**
 * Apply the particle effect to a Composable using the provided controller.
 */
fun Modifier.particlize(controller: ParticlizeComposeController): Modifier = composed {
    controller.graphicsLayer = rememberGraphicsLayer().apply { clip = true }

    Modifier
        .onGloballyPositioned { coordinates ->
            controller.location.value = coordinates.positionInWindow()
        }
        .drawWithContent {
            // Record content to graphics layer
            controller.graphicsLayer?.let { graphicsLayer ->
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }

                // Only draw the original content if no effect is active
                if (!controller.hasEffectStarted()) {
                    drawLayer(graphicsLayer)
                }
            }
        }
}


/**
 * Create a reusable particle effect controller.
 */
@Composable
fun rememberParticleEffect(): ParticlizeComposeController {
    return remember { ParticlizeComposeController() }
}