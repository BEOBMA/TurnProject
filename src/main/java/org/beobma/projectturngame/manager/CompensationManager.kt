package org.beobma.projectturngame.manager

import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.RelicsConfig.Companion.relicsList
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.InventoryManager.openCardCompensationInventory
import org.beobma.projectturngame.manager.InventoryManager.openRelicsCompensationInventory
import org.beobma.projectturngame.relics.Relics

interface CompensationHandler {
    fun Player.normalReward()
    fun Player.eliteReward()
    fun Player.relicsReward()
}

object CompensationManager : CompensationHandler {
    override fun Player.normalReward() {
        val cardPacks = this.cardPack
        val cardList = cardPacks.cardList.filter { it.rarity != CardRarity.Legend }

        player.openCardCompensationInventory(cardList)
    }

    override fun Player.eliteReward() {
        val cardPacks = this.cardPack
        val cardList = cardPacks.cardList.filter { it.rarity == CardRarity.Legend }

        player.openCardCompensationInventory(cardList)
    }

    override fun Player.relicsReward() {
        val game = Info.game ?: return
        val relics = relicsList
        val relicsList = mutableListOf<Relics>()

        relics.forEach { relic ->
            if (!game.playerDatas.any { it.relics.contains(relic) }) {
                relicsList.add(relic)
            }
        }

        player.openRelicsCompensationInventory(relicsList)
    }
}