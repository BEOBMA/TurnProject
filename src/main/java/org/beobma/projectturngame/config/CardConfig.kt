package org.beobma.projectturngame.config

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.config.cardpack.CirculationCardPack
import org.beobma.projectturngame.config.cardpack.DebugCardPack
import org.beobma.projectturngame.config.cardpack.IcosahedronCardPack
import org.beobma.projectturngame.config.cardpack.MasterOfAlchemyCardPack
import org.beobma.projectturngame.config.cardpack.RelativityOfTimeCardPack
import org.beobma.projectturngame.config.cardpack.SelectionAndFocusCardPack
import org.beobma.projectturngame.config.cardpack.StartCardPack


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
        RelativityOfTimeCardPack()

        DebugCardPack()
    }
}