package org.beobma.projectturngame.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.GameManager.turnEnd
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnPlayerJumpEvent : Listener {

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val game = Info.game ?: return

        if (player in game.players) {
            if (player.scoreboardTags.contains("this_Turn")) {
                val playerData = game.playerDatas.find { it.player == player }

                playerData?.turnEnd()
            }
            event.isCancelled = true
        }
    }
}