package org.beobma.projectturngame.listener

import org.beobma.projectturngame.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerSwapHandItemsEvent

class OnPlayerSwapHandItemsEvent : Listener {

    @EventHandler
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        if (Info.isGaming()) {
            event.isCancelled = true
        }
    }
}