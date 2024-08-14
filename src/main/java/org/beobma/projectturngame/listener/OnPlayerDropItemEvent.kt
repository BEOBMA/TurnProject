package org.beobma.projectturngame.listener

import org.beobma.projectturngame.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class OnPlayerDropItemEvent : Listener {

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (Info.isGaming()) event.isCancelled = true
    }
}