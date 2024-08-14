package org.beobma.projectturngame.config

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack


class CardConfig {
    companion object {
        val cardPackList: MutableList<CardPack> = mutableListOf()

        val cardList: MutableList<Card> = mutableListOf()
    }

    init {
        StartCardPack()
        cardConfig()
    }

    private fun cardConfig() {

    }
}