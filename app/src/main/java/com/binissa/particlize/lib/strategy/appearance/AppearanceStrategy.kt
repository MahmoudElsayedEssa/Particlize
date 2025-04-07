package com.binissa.particlize.lib.strategy.appearance

import com.binissa.particlize.lib.core.model.Particle
import kotlin.time.Duration

interface AppearanceStrategy {
    fun updateAppearance(particle: Particle, deltaTime: Duration)
}