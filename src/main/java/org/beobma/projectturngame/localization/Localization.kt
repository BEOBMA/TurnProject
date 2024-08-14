package org.beobma.projectturngame.localization

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Localization {
    val emptySlot = ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("비어 있음", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
        }
    }

    val startSlot = ItemStack(Material.GOLD_BLOCK, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("시작", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("여정의 시작.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val battleSlot = ItemStack(Material.RED_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("전투", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("적과의 전투.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val hardBattleSlot = ItemStack(Material.RED_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("결투", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("강적과의 결투.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val eventSlot = ItemStack(Material.BLACK_GLAZED_TERRACOTTA, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("사건", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("무작위 사건의 발생.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val restSlot = ItemStack(Material.GREEN_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("휴식", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("화톳불.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val bossSlot = ItemStack(Material.RED_GLAZED_TERRACOTTA, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("사투", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("가장 강력한 적과의 사투.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val endSlot = ItemStack(Material.EMERALD_BLOCK, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("끝", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("한 지역의 끝", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }


    val forestSector = ItemStack(Material.OAK_LOG, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("숲", TextColorType.Green.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("가장 안전한 곳.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val caveSector = ItemStack(Material.STONE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("동굴", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("각종 적들이 등장하는 어두운 곳.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val seaSector = ItemStack(Material.SAND, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("바다", TextColorType.Aqua.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("맑은 바다.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }

    val endSector = ItemStack(Material.END_STONE, 1).apply {
        itemMeta = itemMeta.apply {
            displayName(Component.text("엔드", TextColorType.DarkPurple.textColor).decorate(TextDecoration.BOLD))
            lore(listOf(
                Component.text("최종장.", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)
            ))
        }
    }
}

