package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.config.EventConfig.Companion.eventOptionList
import org.beobma.projectturngame.gameevent.EventOption
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


interface EventHandler {
    fun EventOption.toItem(): ItemStack
    fun ItemStack.toEventOption(): EventOption
}

object EventManager : EventHandler {
    override fun EventOption.toItem(): ItemStack {
        return ItemStack(itemMaterial, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(Component.text(name, TextColorType.Gray.textColor))
                lore(description)
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }
    }

    override fun ItemStack.toEventOption(): EventOption {
        val eventOption = eventOptionList.find { it.description == this.itemMeta.lore() }

        if (eventOption !is EventOption) {
            return EventOption(
                "오류 선택지",
                listOf(Component.text("이 선택지는 존재할 수 없는 선택지입니다.", TextColorType.Gray.textColor)),
                Material.STRUCTURE_VOID,
                null
            )
        }

        return eventOption
    }
}