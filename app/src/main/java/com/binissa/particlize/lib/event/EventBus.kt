package com.binissa.particlize.lib.event

import java.util.concurrent.CopyOnWriteArrayList

class EventBus {
    private val listeners = CopyOnWriteArrayList<EventListener>()
    
    fun addListener(listener: EventListener) {
        listeners.add(listener)
    }
    
    fun removeListener(listener: EventListener) {
        listeners.remove(listener)
    }
    
    fun publish(event: EffectEvent) {
        for (listener in listeners) {
            listener.onEvent(event)
        }
    }
}