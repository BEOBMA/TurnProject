package org.beobma.projectturngame.listener

import org.beobma.projectturngame.event.GameBattleStartEvent
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.util.EffectTime
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnGameBattleStartEvent : Listener {

    @EventHandler
    fun onGameBattleStartEvent(event: GameBattleStartEvent) {
        val game = Info.game ?: return
        val battleType = event.battleType

        game.playerDatas.forEach { player ->
            if (player.relics.isEmpty()) return@forEach
            val playerRelicsList = player.relics.filter { it.effectTime == EffectTime.OnBattleStart }

            if (playerRelicsList.isEmpty()) return@forEach

            playerRelicsList.forEach { relics ->
                relics.effect?.invoke(player, listOf(battleType))
            }
        }
    }
}