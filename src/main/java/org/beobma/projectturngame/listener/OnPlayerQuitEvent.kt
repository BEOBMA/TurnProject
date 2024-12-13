package org.beobma.projectturngame.listener

import org.beobma.projectturngame.config.CardConfig
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.GameManager.stop
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class OnPlayerQuitEvent : Listener {

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val game = Info.game ?: return

        if (player in game.players) {
            if (game.players.size <= 1) {
                game.stop()
            } else {
                player.isGlowing = false
                player.gameMode = GameMode.ADVENTURE
                val tags = player.scoreboardTags.toList()
                tags.forEach { tag ->
                    player.removeScoreboardTag(tag)
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset ${player.name}")
                player.teleport(Location(player.world, 0.5, -60.0, 0.5))
                player.inventory.clear()
            }
        }
    }
}