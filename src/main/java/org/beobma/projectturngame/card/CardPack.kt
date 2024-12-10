package org.beobma.projectturngame.card

class CardPack(
    val name: String,
    val description: List<String>,
    val startCardList: MutableList<Card>,
    val cardList: MutableList<Card>,
    val cardPackType: CardPackType
)