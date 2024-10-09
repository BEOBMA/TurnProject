package org.beobma.projectturngame.listener

import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.CardManager.applyHotbar
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class OnPlayerDropItemEvent : Listener {

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop
        val player = event.player
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == player } ?: return
        val card = playerData.hand.find { it.description == item.itemStack.lore() } ?: return
        val description = card.description

        if (description.contains(KeywordType.AlchemYingredients.component)) {
            playerData.alchemYingredientsPile.add(card)
            playerData.hand.remove(card)
            player.inventory.remove(item.itemStack)
            playerData.applyHotbar()
            return
        }

        event.isCancelled = true
    }
}