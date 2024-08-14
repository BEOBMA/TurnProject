package org.beobma.projectturngame.listener

import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class OnPlayerInteractEvent : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val game = Info.game ?: return
        val playerManager = PlayerManager(DefaultPlayerManager())
        val cardManager = CardManager(DefaultCardManager())
        val textManager = TextManager(DefaultTextManager())
        val playerData = game.playerDatas.find { it.player == player } ?: return

        playerManager.run {
            if (player !in game.players || !playerData.isTurn() || !event.action.name.contains("RIGHT")) return
        }

        cardManager.run {
            val card = cardList.find { it == item.toCard() } ?: return
            if (card.cost > playerData.mana) {
                player.sendMessage(textManager.manaFailText())
                return
            }
            playerData.use(card)
        }
    }
}