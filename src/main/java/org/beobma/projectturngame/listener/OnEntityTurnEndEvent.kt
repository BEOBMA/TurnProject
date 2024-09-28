package org.beobma.projectturngame.listener

import org.beobma.projectturngame.event.EntityTurnEndEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnEntityTurnEndEvent : Listener {

    @EventHandler
    fun onEntityTurnEndEvent(event: EntityTurnEndEvent) {
        val entity = event
    }
}