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
            this@normalReward.player.openCompensationInventory(cardPacks)
        }
    }

    override fun Player.eliteReward() {
        TODO("Not yet implemented")
    }

    override fun Player.relicsReward() {
        TODO("Not yet implemented")
    }

    private fun handleCardOperations(cardPack: CardPack, vararg rarities: CardRarity): List<Card> {
        return cardPack.cardList.filter { it.rarity in rarities }.shuffled()
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