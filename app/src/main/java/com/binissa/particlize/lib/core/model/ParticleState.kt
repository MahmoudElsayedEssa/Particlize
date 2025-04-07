package com.binissa.particlize.lib.core.model

import kotlin.time.Duration

data class ParticleState(
    val particles: List<Particle>,
    val elapsedTime: Duration,
    var emissionProgress: Float,
    val isComplete: Boolean,
    var contentVisibility: Float // 0.0 to 1.0
)