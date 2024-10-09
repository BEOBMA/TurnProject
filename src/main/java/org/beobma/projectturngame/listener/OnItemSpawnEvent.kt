package org.beobma.projectturngame.listener

import org.beobma.projectturngame.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent

class OnItemSpawnEvent : Listener {

    @EventHandler
    fun onItemSpawn(event: ItemSpawnEvent) {
        val item = event.entity
        val game = Info.game ?: return

        item.remove()
    }
}