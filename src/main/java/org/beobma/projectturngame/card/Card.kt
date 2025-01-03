package org.beobma.projectturngame.card

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.entity.player.Player

data class Card(
    val name: String,
    val description: List<String>,
    val rarity: CardRarity,
    val cost: Int,
    val cardUseEffect: ((Player, Card) -> Boolean)? = null,
    val postCardUseEffect: ((Player, Card) -> Unit)? = null,
    val cardThrowEffect: ((Player, Card) -> Unit)? = null,
    val nextTurnEffect: ((Player, Card) -> Unit)? = null,
    val id: Int = 0
)