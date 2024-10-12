package org.beobma.projectturngame.config.gameevent

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.config.EventConfig.Companion.eventOptionList
import org.beobma.projectturngame.gameevent.Event
import org.beobma.projectturngame.gameevent.EventOption
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material

class DrawGameEvent {
    init {
        eventConfig()
    }

    private fun eventConfig() {
        val eventOption1 = EventOption(
            "뽑기 카드",
            listOf(
                Component.text("총 27개의 칸에 각기 다른 효과를 지닌 카드들이 무작위로 배치되어 있다.", TextColorType.Gray.textColor),
                Component.text("섞여있는 카드의 수와 효과는 아래와 같다.", TextColorType.Gray.textColor),
                Component.text(""),
                Component.text("꽝(9개) - 아무 효과도 없다.", TextColorType.Gray.textColor),
                Component.text("3등상(9개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 1개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("2등상(8개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 2개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("1등상(1개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 10개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                Dictionary().dictionaryList["에메랄드"]!!
            ),
            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE
        ) { player ->

        }

        val eventOption2 = EventOption(
            "뽑기 카드",
            listOf(
                Component.text("총 27개의 칸에 각기 다른 효과를 지닌 카드들이 무작위로 배치되어 있다.", TextColorType.Gray.textColor),
                Component.text("섞여있는 카드의 수와 효과는 아래와 같다.", TextColorType.Gray.textColor),
                Component.text(""),
                Component.text("꽝(9개) - 아무 효과도 없다.", TextColorType.Gray.textColor),
                Component.text("3등상(9개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 1개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("2등상(8개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 2개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("1등상(1개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 10개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                Dictionary().dictionaryList["에메랄드"]!!
            ),
            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE
        ) { player ->
            player.emerald++
        }

        val eventOption3 = EventOption(
            "뽑기 카드",
            listOf(
                Component.text("총 27개의 칸에 각기 다른 효과를 지닌 카드들이 무작위로 배치되어 있다.", TextColorType.Gray.textColor),
                Component.text("섞여있는 카드의 수와 효과는 아래와 같다.", TextColorType.Gray.textColor),
                Component.text(""),
                Component.text("꽝(9개) - 아무 효과도 없다.", TextColorType.Gray.textColor),
                Component.text("3등상(9개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 1개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("2등상(8개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 2개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("1등상(1개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 10개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                Dictionary().dictionaryList["에메랄드"]!!
            ),
            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE
        ) { player ->
            player.emerald += 2
        }

        val eventOption4 = EventOption(
            "뽑기 카드",
            listOf(
                Component.text("총 27개의 칸에 각기 다른 효과를 지닌 카드들이 무작위로 배치되어 있다.", TextColorType.Gray.textColor),
                Component.text("섞여있는 카드의 수와 효과는 아래와 같다.", TextColorType.Gray.textColor),
                Component.text(""),
                Component.text("꽝(9개) - 아무 효과도 없다.", TextColorType.Gray.textColor),
                Component.text("3등상(9개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 1개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("2등상(8개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 2개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text("1등상(1개) - ", TextColorType.Gray.textColor).append(KeywordType.Emerald.component.append(Component.text(" 10개를 얻는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                Dictionary().dictionaryList["에메랄드"]!!
            ),
            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE
        ) { player ->
            player.emerald += 10
        }

        val list = listOf<EventOption>(eventOption1, eventOption1, eventOption1, eventOption1, eventOption1, eventOption1, eventOption1, eventOption1, eventOption1,
            eventOption2, eventOption2, eventOption2, eventOption2, eventOption2, eventOption2, eventOption2, eventOption2, eventOption2,
            eventOption3, eventOption3, eventOption3, eventOption3, eventOption3, eventOption3, eventOption3, eventOption3,
            eventOption4
        )

        val event = Event(
            "뽑기 게임",
            list.shuffled()
        )


        eventList.add(event)
        eventOptionList.addAll(listOf(eventOption1, eventOption2, eventOption3, eventOption4))
    }
}