package org.beobma.projectturngame.listener

import org.beobma.projectturngame.info.Info
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

class OnPlayerMoveEvent : Listener {

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val game = Info.game ?: return
        val player = event.player

        if (!Info.isGaming()) return
        if (player !in game.players) return

        if (event.from.x != event.to.x || event.from.y != event.to.y || event.from.z != event.to.z) {
            event.isCancelled = true
        }
    }
}