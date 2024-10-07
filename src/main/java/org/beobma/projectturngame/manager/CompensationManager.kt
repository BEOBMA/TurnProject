package org.beobma.projectturngame.manager

import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.InventoryManager.openCompensationInventory

interface CompensationHandler {
    fun Player.normalReward()
    fun Player.eliteReward()
    fun Player.relicsReward()
}

class DefaultCompensationManager : CompensationHandler {
    override fun Player.normalReward() {
        val game = Info.game ?: return
        val cardPacks = game.gameCardPack
        val cardList = cardPacks.random().cardList.filter { it.rarity != CardRarity.Legend }

        player.openCompensationInventory(cardList)
    }

    override fun Player.eliteReward() {
        val game = Info.game ?: return
        val cardPacks = game.gameCardPack
        val cardList = cardPacks.random().cardList.filter { it.rarity == CardRarity.Legend }

        player.openCompensationInventory(cardList)
    }

    override fun Player.relicsReward() {
        TODO("Not yet implemented")
    }
}

object CompensationManager {
    private val converter: CompensationHandler = DefaultCompensationManager()

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