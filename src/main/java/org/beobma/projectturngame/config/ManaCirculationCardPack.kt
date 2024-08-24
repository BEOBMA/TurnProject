package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.*
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.ResetType

class ManaCirculationCardPack {
    private val selectionFactordManager = SelectionFactordManager(DefaultSelectionFactordManager())
    private val playerManager = PlayerManager(DefaultPlayerManager())
    private val enemyManager = EnemyManager(DefaultEnemyManager())
    private val textManager = TextManager(DefaultTextManager())
    private val soundManager = SoundManager(DefaultSoundManager())
    private val cardManager = CardManager(DefaultCardManager())
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("마나의 순환",
            listOf(
                Component.text("마나를 순환하고, 회복하는 카드 팩")
            ), mutableListOf()
        )

        //region borrowedTime Initialization
        val borrowedTime = Card(
            "빌려온 시간", listOf(
                KeywordType.Mana.component.append(Component.text("를 2 회복한다.", TextColorType.Gray.textColor)),
                Component.text("다음 턴 시작 시 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(
                    Component.text("를 0으로 만든다.", TextColorType.Gray.textColor)
                ))
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                playerManager.run {
                    usePlayerData.addMana(2)
                    usePlayerData.turnStartUnit.add {
                        usePlayerData.setMana(0)
                    }
                }
                return@Card true
            }
        )
        //endregion

        //region manaBurn Initialization
        val manaBurn = Card(
            "마나 소각", listOf(
                KeywordType.Mana.component.append(Component.text("를 0으로 만든다. ", TextColorType.Gray.textColor)),
                Component.text("바라보는 적에게 (소모한 수치 x 10)의 피해를 입힌다.", TextColorType.Gray.textColor)
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                selectionFactordManager.run {
                    val player = usePlayerData.player
                    val target = usePlayerData.focusOn()

                    if (target !is Enemy) {
                        val targetingFailText = textManager.targetingFailText()
                        player.sendMessage(targetingFailText)
                        soundManager.run { player.playTargetingFailSound() }
                        return@Card false
                    }

                    val mana = usePlayerData.mana
                    val damage = mana * 10

                    enemyManager.run {
                        target.damage(damage, usePlayerData)
                    }
                }

                return@Card true
            }
        )
        //endregion

        //region manaOverload Initialization
        val manaOverload = Card(
            "마나 과부하", listOf(
                KeywordType.Extinction.component,
                Component.text("이번 턴동안 자신의 ", TextColorType.Gray.textColor).append(
                    KeywordType.Mana.component.append(
                        Component.text(" 최대치를 10으로 만든다.", TextColorType.Gray.textColor)
                    )
                ),
                dictionary.dictionaryList["소멸"]!!
            ), CardRarity.Legend, 3,
            { usePlayerData, _ ->
                val originalMaxMana = usePlayerData.maxMana
                playerManager.run {
                    usePlayerData.setMaxMana(10)
                    usePlayerData.turnStartUnit.add {
                        usePlayerData.setMaxMana(originalMaxMana)
                    }
                }

                return@Card true
            },
            { usePlayerData, _ ->

            }
        )
        //endregion

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