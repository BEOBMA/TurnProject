package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.manager.*
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.ResetType

class HarmonyOfNatureCardPack {
    private val selectionFactordManager = SelectionFactordManager(DefaultSelectionFactordManager())
    private val playerManager = PlayerManager(DefaultPlayerManager())
    private val enemyManager = EnemyManager(DefaultEnemyManager())
    private val textManager = TextManager(DefaultTextManager())
    private val soundManager = SoundManager(DefaultSoundManager())
    private val cardManager = CardManager(DefaultCardManager())

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("자연의 조화",
            listOf(
                Component.text("서로 조화를 이루는 카드 팩."),
                Component.text("여러가지 키워드를 지원한다.")
            ), mutableListOf()
        )

        val sea = Card(
            "바다", listOf(
                Component.text("모든 아군의 체력을 5 회복시킨다.", TextColorType.Gray.textColor),
                Component.text("이번 턴에 사용하는 '식물' 카드의 위력이 3 증가한다.", TextColorType.Gray.textColor),
                Component.text("위 효과는 중첩되지 않는다.", TextColorType.DarkGray.textColor)
            ), CardRarity.Uncommon, 1
        ) { usePlayerData ->
            selectionFactordManager.run {
                val target = usePlayerData.allTeamMembers(excludeSelf = true, includeDeceased = false)

                playerManager.run {
                    target.forEach {
                        it.heal(5, usePlayerData)
                    }
                    usePlayerData.addTag("seaTag", ResetType.TurnEnd)
                }
            }
            return@Card true
        }

        val plants = Card(
            "식물", listOf(
                Component.text("바라보는 적에게 6의 피해를 입힌다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 1
        ) { usePlayerData ->
            selectionFactordManager.run {
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    val targetingFailText = textManager.targetingFailText()
                    player.sendMessage(targetingFailText)
                    soundManager.run { player.playTargetingFailSound() }
                    return@Card false
                }

                if (player.scoreboardTags.contains("seaTag")) {
                    enemyManager.run {
                        target.damage(9, usePlayerData)
                    }
                }
                else {
                    enemyManager.run {
                        target.damage(6, usePlayerData)
                    }
                }
            }
            return@Card true
        }

        val sky = Card(
            "하늘", listOf(
                Component.text("카드를 2장 뽑는다.", TextColorType.Gray.textColor),
                Component.text("이번 턴에 사용하는 '구름' 카드의 위력이 2 증가한다.", TextColorType.Gray.textColor),
                Component.text("위 효과는 중첩되지 않는다.", TextColorType.DarkGray.textColor)
            ), CardRarity.Uncommon, 1
        ) { usePlayerData ->
            cardManager.run {
                usePlayerData.drow(2)
            }

            playerManager.run {
                usePlayerData.addTag("skyTag", ResetType.TurnEnd)
            }
            return@Card true
        }

        val cloud = Card(
            "구름", listOf(
                Component.text("", TextColorType.Gray.textColor)
            ), CardRarity.Common, 1
        ) { usePlayerData ->
            selectionFactordManager.run {
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    val targetingFailText = textManager.targetingFailText()
                    player.sendMessage(targetingFailText)
                    soundManager.run { player.playTargetingFailSound() }
                    return@Card false
                }

                if (player.scoreboardTags.contains("seaTag")) {
                    enemyManager.run {
                        target.damage(9, usePlayerData)
                    }
                }
                else {
                    enemyManager.run {
                        target.damage(6, usePlayerData)
                    }
                }
            }
            return@Card true
        }

        cardPack.cardList.addAll(
            listOf(
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
    }
}