package org.beobma.projectturngame.manager

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info

interface CompensationHandler {
    fun Player.normalReward()
    fun Player.eliteReward()
    fun Player.relicsReward()
}

class DefaultCompensationManager : CompensationHandler {
    override fun Player.normalReward() {
        val game = Info.game ?: return
        val inventoryManager = InventoryManager(DefaultInventoryManager())
        val cardPacks = game.gameCardPack
        inventoryManager.run {
            player.openCompensationInventory(cardPacks, CardRarity.Common)
        }
    }

    override fun Player.eliteReward() {
        val game = Info.game ?: return
        val inventoryManager = InventoryManager(DefaultInventoryManager())
        val cardPacks = game.gameCardPack
        inventoryManager.run {
            player.openCompensationInventory(cardPacks, CardRarity.Legend)
        }
    }

    override fun Player.relicsReward() {
        TODO("Not yet implemented")
    }
}

class CompensationManager(private val converter: CompensationHandler) {
    fun Player.normalReward() {
        converter.run { this@normalReward.normalReward() }
    }

    fun Player.eliteReward() {
        converter.run { this@eliteReward.eliteReward() }
    }

    fun Player.relicsReward() {
        converter.run { this@relicsReward.relicsReward() }
    }
}