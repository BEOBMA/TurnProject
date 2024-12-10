package org.beobma.projectturngame.config.gameevent

import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.config.EventConfig.Companion.eventOptionList
import org.beobma.projectturngame.config.cardpack.DebugCardPack
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.gameevent.EventOption
import org.bukkit.Material

class ChaosEvent {
    init {
        eventConfig()
    }

    private fun eventConfig() {
        val eventOption1 = EventOption(
            "나는 혼돈을 좋아한다.",
            listOf(
                "<gray>자신의 덱에 있는 카드를 모두 무작위 카드로 변경한다.",
                "<gray>치트 카드와 같이 얻을 수 없는 카드를 얻을 경우 해당 카드는 소멸한다."
            ),
            Material.GREEN_CONCRETE
        ) { player ->
            val playerDeck = player.deck.toList()

            player.deck.clear()
            playerDeck.forEach {
                val cardPack = cardPackList.random()

                if (cardPack.cardPackType == CardPackType.Special) return@forEach

                val newCard = cardList.random()
                player.deck.add(newCard)
            }
        }

        val eventOption2 = EventOption(
            "나는 혼돈을 싫어한다.",
            listOf(
                "<gray>아무 효과도 없다."
            ),
            Material.RED_CONCRETE,
            null
        )

        val event = Event(
            "혼돈",
            listOf(
                eventOption1,
                eventOption2
            )
        )


        eventList.add(event)
        eventOptionList.addAll(listOf(eventOption1, eventOption2))
    }
}