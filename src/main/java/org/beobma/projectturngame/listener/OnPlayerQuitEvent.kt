package org.beobma.projectturngame.listener

import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.DefaultGameManager
import org.beobma.projectturngame.manager.GameManager
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
        val gameManager = GameManager(DefaultGameManager())

        if (player in game.players) {
            if (game.players.size <= 1) {
                gameManager.run {
                    game.stop()
                }
            } else {
                val playerData = game.playerDatas.find { it.player == player }
                game.players.remove(player)
                game.playerDatas.remove(playerData)

                player.isGlowing = false
                player.inventory.clear()
                player.gameMode = GameMode.ADVENTURE
                val tags = player.scoreboardTags.toList()
                tags.forEach { tag ->
                    player.removeScoreboardTag(tag)
                }
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset ${player.name}")
                player.teleport(Location(player.world, 0.5, -60.0, 0.5))
            }
        }
    }
}