package org.beobma.projectturngame.listener

import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.CardManager.toCard
import org.beobma.projectturngame.manager.CardManager.use
import org.beobma.projectturngame.manager.PlayerManager.isTurn
import org.beobma.projectturngame.manager.TextManager.manaFailText
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class OnPlayerInteractEvent : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == player } ?: return

        if (player !in game.players || !playerData.isTurn() || !event.action.name.contains("RIGHT")) return
        val card = cardList.find { it == item.toCard() } ?: return

        if (card.cost > playerData.mana) {
            player.sendMessage(manaFailText())
            return
        }
        playerData.use(card)
    }
}