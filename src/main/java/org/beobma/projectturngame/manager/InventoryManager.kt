package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.config.RelicsConfig.Companion.relicsList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Localization
import org.beobma.projectturngame.manager.CardManager.toItem
import org.beobma.projectturngame.manager.GameManager.stop
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.random.Random


interface InventoryHandler {
    fun Player.openMapInventory(inventoryOpenType: InventoryOpenType)
    fun Player.openSectorInventory()
    fun Player.openCardCompensationInventory(cardList: List<Card>)
    fun Player.openRelicsCompensationInventory()

    fun Player.openEventInventory(event: Event)

    fun Player.openDeckInfoInventory(page: Int = 0)
    fun Player.openGraveyardInfoInventory(page: Int = 0)
    fun Player.openBanishInfoInventory(page: Int = 0)
    fun Player.openAlchemYingredientsPileInfoInventory(page: Int = 0)
    fun Player.openTurnOtherInfoInventory()
    fun Player.openMyInfoInventory()
}

object InventoryManager : InventoryHandler {
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

    override fun Player.openCardCompensationInventory(cardList: List<Card>) {
        val inventory = createCardCompensationInventory(cardList)
        this.loadDeckToInventory()
        this.scoreboardTags.add("inventory_CardChoice")
        this.openInventory(inventory)
    }

    override fun Player.openRelicsCompensationInventory() {
        val inventory = createRelicsCompensationInventory()
        this.loadDeckToInventory()
        this.scoreboardTags.add("inventory_RelicsChoice")
        this.openInventory(inventory)
    }

    override fun Player.openEventInventory(event: Event) {
        val inventory = createEventInventory(event)
        this.loadDeckToInventory()
        this.scoreboardTags.add("inventory_EventOptionChoice")
        this.openInventory(inventory)
    }

    override fun Player.openDeckInfoInventory(page: Int) {
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == this } ?: return
        val cardList = playerData.deck.shuffled()
        val totalPages = (cardList.size + 18 - 1) / 18
        val localization = Localization()
        val nextPage = localization.nextPage
        val previousPage = localization.previousPage

        if (page < 0 || page >= totalPages) return
        val inventory = createEmptyInfoInventory(Component.text("현재 덱 정보 (페이지 ${page + 1}/$totalPages, 순서 랜덤)"))

        val startIdx = page * 18
        val endIdx = minOf(startIdx + 18, cardList.size)
        for (i in startIdx until endIdx) {
            inventory.setItem(i - startIdx, cardList[i].toItem())
        }

        if (page > 0) {
            inventory.setItem(18, previousPage)
        }

        if (page < totalPages - 1) {
            inventory.setItem(26, nextPage)
        }

        this.scoreboardTags.add("inventory_DeckInfo")
        this.openInventory(inventory)
    }

    override fun Player.openGraveyardInfoInventory(page: Int) {
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == this } ?: return
        val cardList = playerData.graveyard
        val totalPages = (cardList.size + 18 - 1) / 18
        val localization = Localization()
        val nextPage = localization.nextPage
        val previousPage = localization.previousPage

        if (page < 0 || page >= totalPages) return
        val inventory = createEmptyInfoInventory(Component.text("현재 묘지 정보 (페이지 ${page + 1}/$totalPages)"))

        val startIdx = page * 18
        val endIdx = minOf(startIdx + 18, cardList.size)
        for (i in startIdx until endIdx) {
            inventory.setItem(i - startIdx, cardList[i].toItem())
        }

        if (page > 0) {
            inventory.setItem(18, previousPage)
        }

        if (page < totalPages - 1) {
            inventory.setItem(26, nextPage)
        }

        this.scoreboardTags.add("inventory_GraveyardInfo")
        this.openInventory(inventory)
    }

    override fun Player.openBanishInfoInventory(page: Int) {
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == this } ?: return
        val cardList = playerData.banish
        val totalPages = (cardList.size + 18 - 1) / 18
        val localization = Localization()
        val nextPage = localization.nextPage
        val previousPage = localization.previousPage

        if (page < 0 || page >= totalPages) return
        val inventory = createEmptyInfoInventory(Component.text("현재 제외된 카드 정보 (페이지 ${page + 1}/$totalPages)"))

        val startIdx = page * 18
        val endIdx = minOf(startIdx + 18, cardList.size)
        for (i in startIdx until endIdx) {
            inventory.setItem(i - startIdx, cardList[i].toItem())
        }

        if (page > 0) {
            inventory.setItem(18, previousPage)
        }

        if (page < totalPages - 1) {
            inventory.setItem(26, nextPage)
        }

        this.scoreboardTags.add("inventory_BanishInfo")
        this.openInventory(inventory)
    }

    override fun Player.openAlchemYingredientsPileInfoInventory(page: Int) {
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == this } ?: return
        val cardList = playerData.alchemYingredientsPile
        val totalPages = (cardList.size + 18 - 1) / 18
        val localization = Localization()
        val nextPage = localization.nextPage
        val previousPage = localization.previousPage

        if (page < 0 || page >= totalPages) return
        val inventory = createEmptyInfoInventory(Component.text("연금술 재료 더미 (페이지 ${page + 1}/$totalPages)"))

        val startIdx = page * 18
        val endIdx = minOf(startIdx + 18, cardList.size)
        for (i in startIdx until endIdx) {
            inventory.setItem(i - startIdx, cardList[i].toItem())
        }

        if (page > 0) {
            inventory.setItem(18, previousPage)
        }

        if (page < totalPages - 1) {
            inventory.setItem(26, nextPage)
        }

        this.scoreboardTags.add("inventory_AlchemYingredientsPileInfo")
        this.openInventory(inventory)
    }

    override fun Player.openTurnOtherInfoInventory() {
        val game = Info.game ?: return
        val inventory = createEmptyInfoInventory(Component.text("턴 순서"))
        val turnOther = game.gameTurnOrder

        for (i in 0 until minOf(turnOther.size, 27)) {
            val entity = turnOther[i]
            val item = when (entity) {
                is Enemy -> EnemyManager.run { entity.toItem() }
                is org.beobma.projectturngame.entity.player.Player -> PlayerManager.run { entity.toItem() }
                else -> continue
            }
            inventory.setItem(i, item)
        }

        this.openInventory(inventory)
    }

    override fun Player.openMyInfoInventory() {
        val game = Info.game ?: return
        val inventory = createEmptyInfoInventory(Component.text("자신 정보"))
        val playerData = game.playerDatas.find { it.player == this }

        inventory.setItem(13, PlayerManager.run { playerData?.toItem() })
        this.openInventory(inventory)
    }

    private fun Player.loadDeckToInventory() {
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == this } ?: return
        val inventoryDeck = playerData.deck
        val nonHotbarSlots = 35
        inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
            this@loadDeckToInventory.inventory.setItem(index + 9, card.toItem())
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
        if (inventory.getItem(11) == emptySlot && inventory.getItem(13) == emptySlot && inventory.getItem(15) == emptySlot) game.stop()
        game.gameMapInventory = inventory
    }

    private fun createCardCompensationInventory(cardList: List<Card>): Inventory {
        val localization = Localization()
        val emptySlot = localization.emptySlot
        val randomCardList = cardList.shuffled()
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text("일반 보상"))

        for (i in 0 until inventory.size) {
            inventory.setItem(i, emptySlot)
        }



        if (randomCardList.size > 3) {
            inventory.setItem(11, randomCardList[0].toItem())
            inventory.setItem(13, randomCardList[1].toItem())
            inventory.setItem(15, randomCardList[2].toItem())
        }
        return inventory
    }

    private fun createRelicsCompensationInventory(): Inventory {
        val localization = Localization()
        val emptySlot = localization.emptySlot
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text("유물 보상"))

        for (i in 0 until inventory.size) {
            inventory.setItem(i, emptySlot)
        }


        if (relicsList.size > 3) {
            val relics1 = relicsList.random()
            val relics2 = relicsList.filter { it != relics1 }.random()
            val relics3 = relicsList.filter { it != relics1 && it != relics2 }.random()

            RelicsManager.run {
                inventory.setItem(11, relics1.toItem())
                inventory.setItem(13, relics2.toItem())
                inventory.setItem(15, relics3.toItem())
            }
        }
        return inventory
    }

    private fun createEmptyInfoInventory(name: Component): Inventory {
        return Bukkit.createInventory(null, 27, name)
    }

    private fun createEventInventory(event: Event): Inventory {
        val eventManager = EventManager
        val localization = Localization()
        val emptySlot = localization.emptySlot
        val inventory: Inventory = Bukkit.createInventory(null, 27, Component.text(event.name))

        for (i in 0 until inventory.size) {
            inventory.setItem(i, emptySlot)
        }

        when (event.options.size) {
            1 -> {
                inventory.setItem(13, eventManager.run { event.options[0].toItem() })
            }

            2 -> {
                inventory.setItem(11, eventManager.run { event.options[0].toItem() })
                inventory.setItem(15, eventManager.run { event.options[1].toItem() })
            }

            27 -> {
                for (i in 0..26) {
                    inventory.setItem(i, eventManager.run { event.options[i].toItem() })
                }
            }
            // 이벤트 선택지가 늘어날 경우 추가해야함.
        }
        return inventory
    }
}


enum class InventoryOpenType {
    OnlyView, Choice
}