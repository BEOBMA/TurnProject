package org.beobma.projectturngame.config.gameevent

import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.config.EventConfig.Companion.eventOptionList
import org.beobma.projectturngame.config.RelicsConfig.Companion.relicsList
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.gameevent.EventOption
import org.beobma.projectturngame.manager.CompensationManager.normalReward
import org.beobma.projectturngame.manager.CompensationManager.relicsReward
import org.beobma.projectturngame.manager.PlayerManager.diceRoll
import org.bukkit.Material

class BackgammonEvent {
    init {
        eventConfig()
    }

    private fun eventConfig() {
        val eventOption = EventOption(
            "주사위 굴리기",
            listOf(
                "<gray>20면체 주사위를 굴려 나온 값에 따라 아래의 효과를 얻는다.",
                "",
                "<dark_gray>1 - 아무 효과도 없다.",
                "<gold>2~19 - 무작위 공용 카드를 얻는다.",
                "<yellow>20 - 무작위 유물을 얻는다."
            ),
            Material.WHITE_CONCRETE
        ) { player ->
            val dice = player.diceRoll(1, 20)

            when (dice) {
                in 2..19 -> {
                    player.deck.add(cardPackList.filter { it.cardPackType == CardPackType.Universal }.random().cardList.random())
                }
                20 -> {
                    player.relics.add(relicsList.random())
                }
            }
        }

        val event = Event(
            "주사위 놀이",
            listOf(
                eventOption
            )
        )


        eventList.add(event)
        eventOptionList.addAll(listOf(eventOption))
    }
}