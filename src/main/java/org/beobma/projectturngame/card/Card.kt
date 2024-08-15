package org.beobma.projectturngame.card

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.entity.player.Player

class Card(
    val name: String,
    val description: List<Component>,
    val rarity: CardRarity,
    val cost: Int,
    val cardUseEffect: ((Player) -> Boolean)? = null
    val postCardUseEffect: ((Player) -> Boolean)? = null
)