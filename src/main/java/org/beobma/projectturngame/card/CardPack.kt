package org.beobma.projectturngame.card

import net.kyori.adventure.text.Component

class CardPack(
    val name: String,
    val description: List<Component>,
    val cardList: MutableList<Card>
)