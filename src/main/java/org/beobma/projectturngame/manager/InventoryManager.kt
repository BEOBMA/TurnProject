package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Localization
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.random.Random

interface InventoryHandler {
    fun Player.openMapInventory(inventoryOpenType: InventoryOpenType)
    fun Player.openSectorInventory()
    fun Player.openCompensationInventory(cardList: List<Card>)
}

class DefaultInventoryManager : InventoryHandler {
    override fun Player.openMapInventory(inventoryOpenType: InventoryOpenType) {
        val game = Info.game ?: return

        createNewMapInventory()
        this.openInventory(game.gameMapInventory!!)
        if (inventoryOpenType == InventoryOpenType.Choice) this.scoreboardTags.add("inventory_MapChoice")
    }

    override fun Player.openSectorInventory() {
        val game = Info.game ?: return
        game.sectorStep++

        createSectorInventory()
        this.openInventory(game.gameMapInventory!!)
        this.scoreboardTags.add("inventory_SectorChoice")
    }

    override fun Player.openCompensationInventory(cardList: List<Card>) {
        val inventory = createCompensationInventory(cardList)
        this.scoreboardTags.add("inventory_CardChoice")
        this.loadDeckToInventory()
        this.openInventory(inventory)
    }


    private fun Player.loadDeckToInventory() {
        val game = Info.game ?: return
        val cardManager = CardManager(DefaultCardManager())
        val playerData = game.playerDatas.find { it.player == this } ?: return
        val inventoryDeck = playerData.deck
        val nonHotbarSlots = 35

        cardManager.run {
            inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
                this@loadDeckToInventory.inventory.setItem(index + 9, card.toItem())
            }
        }
    }

    private fun createNewMapInventory() {
        val game = Info.game ?: return
        if (game.gameMapInventory is Inventory) {
            return
        }

        val localization = Localization()
        val emptySlot = localization.emptySlot
        val startSlot = localization.startSlot
        val battleSlot = localization.battleSlot
        val eventSlot = localization.eventSlot
        val hardBattle = localization.hardBattleSlot
        val restSlot = localization.restSlot
        val bossSlot = localization.bossSlot
        val endSlot = localization.endSlot
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text(game.gameField.name))


        for (i in 0 until inventory.size) {
            inventory.setItem(i, emptySlot)
        }
        inventory.setItem(9, startSlot)
        inventory.setItem(17, endSlot)

        fun setRandomTile(slots: List<Int>) {
            for (slot in slots) {
                val randomTile = when (Random.nextInt(1, 8)) {
                    in 1..4 -> battleSlot
                    in 5..6 -> eventSlot
                    7 -> hardBattle
                    else -> battleSlot
                }
                inventory.setItem(slot, randomTile)
            }
        }
        inventory.setItem(13, hardBattle)
        inventory.setItem(15, restSlot)
        inventory.setItem(16, bossSlot)
        setRandomTile(listOf(1, 10, 19, 2, 11, 20, 3, 12, 21, 5, 14, 23))

        game.gameMapInventory = inventory
    }

    private fun createSectorInventory() {
        val game = Info.game ?: return

        val localization = Localization()
        val gameManager = GameManager(DefaultGameManager())
        val emptySlot = localization.emptySlot
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text("다음에 진행할 필드를 선택하세요."))

        for (i in 0 until inventory.size) {
            inventory.setItem(i, emptySlot)
        }

        if (game.sectorStep >= 3) {
            inventory.setItem(13, GameField.End.itemStack)
        } else {
            val fieldList = game.gameSector.shuffled().filter { it != GameField.End }.toMutableList()

            (11..15).filter { it != 12 && it != 14 }.forEach { i ->
                if (fieldList.isEmpty()) return@forEach
                val field = fieldList.random()
                inventory.setItem(i, field.itemStack)
                fieldList.remove(field)
            }
        }
        if (inventory.getItem(11) == emptySlot && inventory.getItem(13) == emptySlot && inventory.getItem(15) == emptySlot) gameManager.run { game.stop() }
        game.gameMapInventory = inventory
    }

    private fun createCompensationInventory(cardList: List<Card>): Inventory {
        val localization = Localization()
        val cardManager = CardManager(DefaultCardManager())
        val emptySlot = localization.emptySlot
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text("일반 보상"))

        for (i in 0 until inventory.size) {
            inventory.setItem(i, emptySlot)
        }


        cardManager.run {
            inventory.setItem(11, cardList[0].toItem())
            inventory.setItem(13, cardList[1].toItem())
            inventory.setItem(15, cardList[2].toItem())
        }
        return inventory
    }
}

class InventoryManager(private val converter: InventoryHandler) {
    fun Player.openMapInventory(inventoryOpenType: InventoryOpenType) {
        converter.run { this@openMapInventory.openMapInventory(inventoryOpenType) }
    }

    fun Player.openSectorInventory() {
        converter.run { this@openSectorInventory.openSectorInventory() }
    }

    fun Player.openCompensationInventory(cardList: List<Card>) {
        converter.run { this@openCompensationInventory.openCompensationInventory(cardList) }
    }
}

enum class InventoryOpenType {
    OnlyView, Choice
}