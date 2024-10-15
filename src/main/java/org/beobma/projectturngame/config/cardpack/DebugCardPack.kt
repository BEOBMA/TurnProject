package org.beobma.projectturngame.config.cardpack

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.EnemyManager.death
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class DebugCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("디버그",
            listOf(
                Component.text("각종 디버그 카드가 담겨있다. 즉, 치트 카드가 모여있다.")
            ), mutableListOf()
        )

        //region developerPowers Legend Initialization
        val developerPowers = Card(
            "개발자의 권능", listOf(
                KeywordType.Remnant.component,
                Component.text(""),
                Component.text("바라보는 적을 제거한다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["잔존"]!!
            ), CardRarity.Legend, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.death()
                return@Card true
            }
        )
        //endregion





        cardPack.cardList.addAll(
            listOf(
                developerPowers
            )
        )

        cardList.addAll(cardPack.cardList)
    }
}