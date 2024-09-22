package org.beobma.projectturngame.listener

import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.game.Game
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.GameManager.nextSector
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable

class OnInventoryCloseEvent : Listener {

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val game = Info.game ?: return

        if (player !in game.players) return

        when {
            player.scoreboardTags.contains("inventory_MapChoice") -> reopenInventoryLater(
                player,
                game.gameMapInventory,
                "inventory_MapChoice"
            )

            player.scoreboardTags.contains("inventory_SectorChoice") -> reopenInventoryLater(
                player,
                game.gameMapInventory,
                "inventory_SectorChoice"
            )

            player.scoreboardTags.contains("inventory_MapChoice") -> handleRewardChoice(player, game)
        }
    }

    private fun reopenInventoryLater(player: Player, inventory: Inventory?, tag: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (player.scoreboardTags.contains(tag)) {
                    player.openInventory(inventory!!)
                }
            }
        }.runTaskLater(ProjectTurnGame.instance, 10L)
    }

    private fun handleRewardChoice(player: Player, game: Game) {
        player.scoreboardTags.remove("inventory_MapChoice")
        player.inventory.clear()

        if (game.players.none { it.scoreboardTags.contains("inventory_MapChoice") }) {
            game.nextSector()
        }
    }
}