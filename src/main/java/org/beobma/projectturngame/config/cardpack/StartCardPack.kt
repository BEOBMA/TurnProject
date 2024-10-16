package org.beobma.projectturngame.config.cardpack

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class StartCardPack {
    companion object {
        val startCardList: MutableList<Card> = mutableListOf()
    }

    init {
        cardConfig()
    }

    private fun cardConfig() {
        //region attack Initialization
        val attack = Card(
            "공격", listOf(
                Component.text("바라보는 적에게 5의 피해를 입힌다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(5, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region heal Initialization
        val heal = Card(
            "가벼운 치유", listOf(
                Component.text("체력을 5 회복한다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                usePlayerData.heal(5, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region powerAttack Initialization
        val powerAttack = Card(
            "강공", listOf(
                Component.text("바라보는 적에게 12의 피해를 입힌다", TextColorType.Gray.textColor)
            ), CardRarity.Common, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(12, usePlayerData)

                return@Card true
            }
        )
        //endregion

        //region rest Initialization
        val rest = Card(
            "휴식", listOf(
                KeywordType.Mana.component.append(Component.text("를 1 회복한다.", TextColorType.Gray.textColor))
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                usePlayerData.addMana(1)
                return@Card true
            }
        )
        //endregion

        //region continuousAttack Initialization
        val continuousAttack = Card(
            "연공", listOf(
                Component.text("바라보는 적에게 20의 피해를 입힌다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 3,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                target.damage(20, usePlayerData)

                return@Card true
            }
        )
        //endregion


        startCardList.addAll(
            listOf(
                attack, attack, attack, heal, heal, powerAttack, powerAttack, rest, rest, continuousAttack
            )
        )

        cardList.addAll(startCardList)
    }
}