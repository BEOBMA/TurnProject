package org.beobma.projectturngame.config.cardpack

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.cardThrow
import org.beobma.projectturngame.manager.CardManager.clearHand
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.addTag
import org.beobma.projectturngame.manager.PlayerManager.death
import org.beobma.projectturngame.manager.PlayerManager.diceRoll
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.PlayerManager.setMana
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playTargetingFailSound
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.ResetType

class IcosahedronCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("20면체",
            listOf(
                Component.text("운에 따라, 효과가 달라진다.")
            ), mutableListOf()
        )

        //region variable Common Initialization
        val variable = Card(
            "변수", listOf(
                Component.text("바라보는 적에게 20면체 주사위를 굴려 나온 값만큼 피해를 입힌다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                val dice = usePlayerData.diceRoll(1, 20)

                target.damage(dice, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region gambling Common Initialization
        val gambling = Card(
            "도박수", listOf(
                Component.text("20면체 주사위를 굴린다.", TextColorType.Gray.textColor),
                Component.text("값이 10 이상이라면 덱에서 카드를 2장 뽑는다.", TextColorType.Gray.textColor),
                Component.text("이 외의 경우에는 패에서 무작위 카드 1장을 버린다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice >= 10) {
                    usePlayerData.drow(2)
                }
                else {
                    val card = usePlayerData.hand.random()
                    usePlayerData.cardThrow(card)
                }
                return@Card true
            }
        )
        //endregion

        //region scaleOfFate Common Initialization
        val scaleOfFate = Card(
            "운명의 눈금", listOf(
                Component.text("20면체 주사위를 굴린다.", TextColorType.Gray.textColor),
                Component.text("값이 20이라면 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 최대로 회복하고 덱에서 카드를 5장 뽑는다.", TextColorType.Gray.textColor))),
                Component.text("이 외의 경우에는 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 0으로 만들고 패의 카드를 전부 버린다.", TextColorType.Gray.textColor)))
            ), CardRarity.Common, 3,
            { usePlayerData, _ ->
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice == 20) {
                    usePlayerData.addMana(usePlayerData.maxMana)

                    usePlayerData.drow(5)
                }
                else {
                    usePlayerData.setMana(0)
                    usePlayerData.clearHand()
                }
                return@Card true
            }
        )
        //endregion

        //region zeroRisk Uncommon Initialization
        val zeroRisk = Card(
            "제로 리스크", listOf(
                Component.text("20면체 주사위를 굴린다.", TextColorType.Gray.textColor),
                Component.text("값이 1이라면 자신은 사망한다.", TextColorType.Gray.textColor),
                Component.text("이 외의 경우에는 바라보는 적에게 15의 피해를 입힌다.", TextColorType.Gray.textColor)
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                val dice = usePlayerData.diceRoll(1, 20)

                if (dice == 1) {
                    usePlayerData.death()
                }
                else {
                    target.damage(15, usePlayerData)
                }
                return@Card true
            }
        )
        //endregion

        //region sniffling Uncommon Initialization
        val sniffling = Card(
            "홀짝", listOf(
                Component.text("20면체 주사위를 굴린다.", TextColorType.Gray.textColor),
                Component.text("값이 짝수라면 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 2 회복한다.", TextColorType.Gray.textColor))),
                Component.text("값이 홀수라면 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 1 회복한다.", TextColorType.Gray.textColor)))
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice % 2 == 0) {
                    usePlayerData.addMana(2)
                }
                else {
                    usePlayerData.addMana(1)

                }
                return@Card true
            }
        )
        //endregion

        //region recoveryVariables Uncommon Initialization
        val recoveryVariables = Card(
            "회복 변수", listOf(
                Component.text("체력을 20면체 주사위를 굴려 나온 값만큼 회복한다.", TextColorType.Gray.textColor),
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val dice = usePlayerData.diceRoll(1, 20)

                usePlayerData.heal(dice, usePlayerData)

                return@Card true
            }
        )
        //endregion

        //region weightedDice Rare Initialization
        val weightedDice = Card(
            "가중치 주사위", listOf(
                Component.text("이번 턴 동안 주사위를 굴리면 그 값에 1을 더한다.", TextColorType.Gray.textColor),
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                usePlayerData.diceWeight += 1
                usePlayerData.turnEndUnit.add { usePlayerData.diceWeight -= 1 }
                return@Card true
            }
        )
        //endregion

        //region contradictoryRoll Rare Initialization
        val contradictoryRoll = Card(
            "상반된 굴림", listOf(
                Component.text("20면체 주사위를 굴린다.", TextColorType.Gray.textColor),
                Component.text("값이 10 이상이면 이번 턴 동안 주사위를 굴려 나온 값에 이 주사위 값을 뺀다.", TextColorType.Gray.textColor),
                Component.text("이외의 경우에는 이번 턴 동안 주사위를 굴려 나온 값에 이 주사위 값을 더한다.", TextColorType.Gray.textColor),
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice >= 10) {
                    usePlayerData.diceWeight -= dice
                    usePlayerData.turnEndUnit.add { usePlayerData.diceWeight += dice }
                }
                else {
                    usePlayerData.diceWeight += dice
                    usePlayerData.turnEndUnit.add { usePlayerData.diceWeight -= dice }
                }
                return@Card true
            }
        )
        //endregion

        //region minMax Rare Initialization
        val minMax = Card(
            "최소 최대", listOf(
                Component.text("다음번 주사위를 굴리면 그 값은 반드시 최솟값 또는 최댓값으로 결정된다.", TextColorType.Gray.textColor)
            ), CardRarity.Rare, 2,
            { usePlayerData, _ ->
                usePlayerData.addTag("minMax", ResetType.None)

                return@Card true
            }
        )
        //endregion

        //region chanceAdvantage Legend Initialization
        val chanceAdvantage = Card(
            "확률 우위", listOf(
                Component.text("다음번 주사위를 굴리면 주사위를 2개 굴려 더 높은 값이 나온 주사위를 사용한다.", TextColorType.Gray.textColor)
            ), CardRarity.Legend, 3,
            { usePlayerData, _ ->
                usePlayerData.addTag("chanceAdvantage", ResetType.None)
                return@Card true
            }
        )
        //endregion



        cardPack.cardList.addAll(
            listOf(
                variable,
                variable,
                variable,
                gambling,
                gambling,
                gambling,
                scaleOfFate,
                scaleOfFate,
                scaleOfFate,
                zeroRisk,
                zeroRisk,
                zeroRisk,
                sniffling,
                sniffling,
                sniffling,
                recoveryVariables,
                recoveryVariables,
                recoveryVariables,
                weightedDice,
                weightedDice,
                weightedDice,
                contradictoryRoll,
                contradictoryRoll,
                contradictoryRoll,
                minMax,
                minMax,
                minMax,
                chanceAdvantage
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
    }
}