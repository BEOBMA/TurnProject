package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.config.EventConfig.Companion.eventOptionList
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.gameevent.EventOption
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material

class ChaosEvent {
    init {
        eventConfig()
    }

    private fun eventConfig() {
        val eventOption1 = EventOption(
            "나는 혼돈을 좋아한다.",
            listOf(Component.text("자신의 덱에 있는 카드를 모두 무작위 카드로 변경한다.", TextColorType.Gray.textColor)),
            Material.GREEN_CONCRETE
        ) { player ->
            val playerDeck = player.deck.toList()

            player.deck.clear()
            playerDeck.forEach {
                player.deck.add(cardList.random())
            }
        }

        val eventOption2 = EventOption(
            "나는 혼돈을 싫어한다.",
            listOf(Component.text("아무 효과도 없다.", TextColorType.Gray.textColor)),
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