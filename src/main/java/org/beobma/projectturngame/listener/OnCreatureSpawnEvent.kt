package org.beobma.projectturngame.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent

class OnCreatureSpawnEvent : Listener {

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity
        entity.setAI(false)
    }
}