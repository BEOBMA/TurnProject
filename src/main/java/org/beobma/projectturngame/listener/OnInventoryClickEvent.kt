package org.beobma.projectturngame.listener

import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.game.Game
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Localization
import org.beobma.projectturngame.manager.CardManager.toItem
import org.beobma.projectturngame.manager.GameManager.battleStart
import org.beobma.projectturngame.manager.GameManager.bossStart
import org.beobma.projectturngame.manager.GameManager.eventStart
import org.beobma.projectturngame.manager.GameManager.hardBattleStart
import org.beobma.projectturngame.manager.GameManager.nextSector
import org.beobma.projectturngame.manager.GameManager.restStart
import org.beobma.projectturngame.manager.InventoryManager.openSectorInventory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class OnInventoryClickEvent : Listener {

    @EventHandler
    fun onClickItem(event: InventoryClickEvent) {
        val player = event.whoClicked
        val clickItem = event.currentItem ?: return
        val game = Info.game ?: return

        if (player !is Player) return

        if (player.scoreboardTags.contains("inventory_MapChoice")) {
            mapMoveHandler(game, clickItem, event.slot)
            event.isCancelled = true
            return
        }

        if (player.scoreboardTags.contains("inventory_SectorChoice")) {
            sectorChoiceHandler(game, clickItem)
            event.isCancelled = true
            return
        }

        if (player.scoreboardTags.contains("inventory_CardChoice")) {
            compensationCardChoiceHandler(game, clickItem, player)
            event.isCancelled = true
            return
        }

        event.isCancelled = true
    }

    private fun mapMoveHandler(game: Game, clickItem: ItemStack, slot: Int) {
        val localization = Localization()
        val battleSlot = localization.battleSlot
        val eventSlot = localization.eventSlot
        val hardBattleSlot = localization.hardBattleSlot
        val restSlot = localization.restSlot
        val bossSlot = localization.bossSlot
        val endSlot = localization.endSlot
        when (game.tileStep) {
            1 -> {
                if (slot != 1 && slot != 10 && slot != 19) {
                    return
                }
            }
            2 -> {
                if (slot != 2 && slot != 11 && slot != 20) {
                    return
                }
            }
            3 -> {
                if (slot != 3 && slot != 12 && slot != 21) {
                    return
                }
            }
            4 -> {
                if (slot != 13) {
                    return
                }
            }
            5 -> {
                if (slot != 5 && slot != 14 && slot != 23) {
                    return
                }
            }
            6 -> {
                if (slot != 15) {
                    return
                }
            }
            7 -> {
                if (slot != 16) {
                    return
                }
            }
            8 -> {
                if (slot != 17) {
                    return
                }
            }
        }
        when (clickItem) {
            battleSlot -> game.battleStart()
            eventSlot -> game.eventStart()
            hardBattleSlot -> game.hardBattleStart()
            restSlot -> game.restStart()
            bossSlot -> game.bossStart()
            endSlot -> game.players.forEach { player -> player.openSectorInventory() }
            else -> {
                return
            }
        }
        game.players.forEach { player ->
            player.scoreboardTags.remove("inventory_MapChoice")
            player.closeInventory()
        }
        when (game.tileStep) {
            1 -> {
                game.gameMapInventory?.setItem(1, null)
                game.gameMapInventory?.setItem(10, null)
                game.gameMapInventory?.setItem(19, null)
            }
            2 -> {
                game.gameMapInventory?.setItem(2, null)
                game.gameMapInventory?.setItem(11, null)
                game.gameMapInventory?.setItem(20, null)
            }
            3 -> {
                game.gameMapInventory?.setItem(3, null)
                game.gameMapInventory?.setItem(12, null)
                game.gameMapInventory?.setItem(21, null)
            }
            4 -> {
                game.gameMapInventory?.setItem(13, null)
            }
            5 -> {
                game.gameMapInventory?.setItem(4, null)
                game.gameMapInventory?.setItem(13, null)
                game.gameMapInventory?.setItem(22, null)
            }
            6 -> {
                game.gameMapInventory?.setItem(15, null)
            }
            7 -> {
                game.gameMapInventory?.setItem(16, null)
            }
        }
    }

    private fun sectorChoiceHandler(game: Game, clickItem: ItemStack) {
        val field = GameField.entries.find { it.itemStack == clickItem }
        if (field !is GameField) return

        game.gameField = field
        game.players.forEach { player ->
            player.scoreboardTags.remove("inventory_MapChoice")
            player.closeInventory()
        }
        game.nextSector()
    }

    private fun compensationCardChoiceHandler(game: Game, clickItem: ItemStack, player: Player) {
        val card = cardList.find { it.toItem() == clickItem } ?: return
        val playerData = game.playerDatas.find { it.player == player } ?: return

        playerData.deck.add(card)
        player.scoreboardTags.remove("inventory_MapChoice")
        player.inventory.clear()
        player.closeInventory()
        if (game.players.none { it.scoreboardTags.contains("inventory_MapChoice") }) {
            game.nextSector()
        }
    }
}