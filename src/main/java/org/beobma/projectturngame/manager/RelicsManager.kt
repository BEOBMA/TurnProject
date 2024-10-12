package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.relics.Relics
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

interface RelicsHandler {
    fun Relics.toItem(): ItemStack
}

object RelicsManager : RelicsHandler {
    override fun Relics.toItem(): ItemStack {
        val relicsItem = ItemStack(Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(Component.text(name, TextColorType.Gray.textColor))
                lore(description)
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }

        return relicsItem
    }
}