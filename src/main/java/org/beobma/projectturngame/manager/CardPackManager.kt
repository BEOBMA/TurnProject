package org.beobma.projectturngame.manager

import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

interface CardPackHandler {
    fun CardPack.toItem(): ItemStack
    fun ItemStack.toCardPack(): CardPack
}

object CardPackManager : CardPackHandler {
    override fun CardPack.toItem(): ItemStack {
        val cardPackItem = ItemStack(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(MiniMessage.miniMessage().deserialize(name))
                lore(description.map { MiniMessage.miniMessage().deserialize(it) })
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }

        return cardPackItem
    }

    override fun ItemStack.toCardPack(): CardPack {
        val cardPacks = cardPackList
        val cardPack = cardPacks.find {
            this == it.toItem()
        }

        if (cardPack !is CardPack) {
            return CardPack("<gray>에러팩", listOf("<gray>이 카드팩은 존재할 수 없는 카드팩입니다."), mutableListOf(), mutableListOf(), CardPackType.Limitation)
        }
        return cardPack
    }
}