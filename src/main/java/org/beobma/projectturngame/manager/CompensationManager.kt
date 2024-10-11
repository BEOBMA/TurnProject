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

object CompensationManager : CompensationHandler {
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