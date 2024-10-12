package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.config.EventConfig.Companion.eventOptionList
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.gameevent.EventOption
import org.beobma.projectturngame.manager.CompensationManager.normalReward
import org.beobma.projectturngame.manager.CompensationManager.relicsReward
import org.beobma.projectturngame.manager.PlayerManager.diceRoll
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material

class BackgammonEvent {
    init {
        eventConfig()
    }

    private fun eventConfig() {
        val eventOption = EventOption(
            "주사위 굴리기",
            listOf(
                Component.text("20면체 주사위를 굴려 나온 값에 따라 아래의 효과를 얻는다.", TextColorType.Gray.textColor),
                Component.text("", TextColorType.Gray.textColor),
                Component.text("1 - 아무 효과도 없다.", TextColorType.Gray.textColor),
                Component.text("2~19 - 일반 카드 보상을 얻는다.", TextColorType.Gray.textColor),
                Component.text("20 - 유물 보상을 얻는다.", TextColorType.Gray.textColor)
            ),
            Material.WHITE_CONCRETE
        ) { player ->
            val dice = player.diceRoll(1, 20)

            when (dice) {
                in 2..19 -> player.normalReward()
                20 -> player.relicsReward()
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