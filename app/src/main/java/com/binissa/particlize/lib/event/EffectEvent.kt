package com.binissa.particlize.lib.event

data class EffectEvent(
    val effectId: String,
    val type: EffectEventType,
    val progress: Float
)

enum class EffectEventType {
    STARTED,
    PROGRESS,
    COMPLETED
}