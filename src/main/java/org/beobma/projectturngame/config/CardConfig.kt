package org.beobma.projectturngame.config

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack


class CardConfig {
    companion object {
        val cardPackList: MutableSet<CardPack> = mutableSetOf()

        val cardList: MutableSet<Card> = mutableSetOf()
    }

    init {
        cardPackList.clear()
        cardList.clear()

        cardConfig()
    }

    private fun cardConfig() {
        StartCardPack()
        CirculationCardPack()
        SelectionAndFocusCardPack()
        IcosahedronCardPack()
        MasterOfAlchemyCardPack()
        DebugCardPack()
    }
}