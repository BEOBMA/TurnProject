package org.beobma.projectturngame.config

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.config.cardpack.CirculationCardPack
import org.beobma.projectturngame.config.cardpack.DebugCardPack
import org.beobma.projectturngame.config.cardpack.HammerOfReforgingCardPack
import org.beobma.projectturngame.config.cardpack.IcosahedronCardPack
import org.beobma.projectturngame.config.cardpack.MasterOfAlchemyCardPack
import org.beobma.projectturngame.config.cardpack.RelativityOfTimeCardPack
import org.beobma.projectturngame.config.cardpack.SelectionAndFocusCardPack


class CardConfig {
    companion object {
        val cardPackList: MutableSet<CardPack> = mutableSetOf()

        val cardList: MutableSet<Card> = mutableSetOf()
        val reforgeCardPair: HashMap<Card, Card> = hashMapOf()
    }

    init {
        cardPackList.clear()
        cardList.clear()

        cardConfig()
    }

    private fun cardConfig() {
        CirculationCardPack()
        SelectionAndFocusCardPack()
        IcosahedronCardPack()
        MasterOfAlchemyCardPack()
        RelativityOfTimeCardPack()
        HammerOfReforgingCardPack()

        DebugCardPack()
    }
}