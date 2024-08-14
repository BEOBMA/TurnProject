package org.beobma.projectturngame.game

import org.beobma.projectturngame.localization.Localization
import org.bukkit.inventory.ItemStack

enum class GameField(val itemStack: ItemStack) {
    Forest(Localization().forestSector), Cave(Localization().caveSector), Sea(Localization().seaSector), End(Localization().endSector)
}