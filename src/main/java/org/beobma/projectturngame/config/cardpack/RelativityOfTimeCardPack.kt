package org.beobma.projectturngame.config.cardpack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.continueeffect.ContinueEffect
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.GameManager.turnEnd
import org.beobma.projectturngame.manager.HealthManager.addHealth
import org.beobma.projectturngame.manager.HealthManager.setHealth
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.SelectionFactordManager.allTeamMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.manager.TimeManager.decreaseTime
import org.beobma.projectturngame.manager.TimeManager.getTime
import org.beobma.projectturngame.manager.TimeManager.increaseTime
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.EffectTime

class RelativityOfTimeCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("시간의 상대성",
            listOf(
                Component.text("여러 카드의 효과로 시간 수치를 쌓아 추가 턴을 얻는 등. 강력한 효과를 얻는다.")
            ), mutableListOf()
        )

        //region saveTime Common Initialization
        val saveTime = Card(
            "시간 저장", listOf(
                KeywordType.Continue.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gray>전투 종료 시까지 어느 대상이든 턴을 종료할 때마다 자신은 <gold><bold>시간 </bold><gray>1을 얻는다."),
                Component.text(""),
                dictionary.dictionaryList["지속"]!!,
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val game = Info.game ?: return@Card false

                game.continueEffects.add(ContinueEffect(usePlayerData, EffectTime.TurnEnd, {
                    usePlayerData.increaseTime(1, usePlayerData)
                }))
                return@Card true
            }
        )
        //endregion

        //region resourceAcceleration Common Initialization
        val resourceAcceleration = Card(
            "자원 가속", listOf(
                MiniMessage.miniMessage().deserialize("<gold><bold>시간 </bold><gray>2를 소모하고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>덱에서 카드를 1장 뽑고 <blue><bold>마나 </bold><gray>1을 회복한다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()

                if (time !is AbnormalityStatus || time.power < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                usePlayerData.decreaseTime(2, usePlayerData)
                usePlayerData.drow(1)
                usePlayerData.addMana(1)

                return@Card true
            }
        )
        //endregion

        //region acceleration Common Initialization
        val acceleration = Card(
            "가속", listOf(
                KeywordType.Continue.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gold><bold>시간 </bold><gray>1을 소모하고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>전투 종료 시까지 자신의 속도가 3 증가한다."),
                Component.text(""),
                dictionary.dictionaryList["지속"]!!,
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()
                val game = Info.game ?: return@Card false

                if (time !is AbnormalityStatus || time.power < 1) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                usePlayerData.decreaseTime(1, usePlayerData)

                usePlayerData.speed += 3
                game.battleEndUnit.add {
                    usePlayerData.speed -= 3
                }

                return@Card true
            }
        )
        //endregion


        //region temporarySuspension Uncommon Initialization
        val temporarySuspension = Card(
            "일시적 중단", listOf(
                MiniMessage.miniMessage().deserialize("<gold><bold>시간 </bold><gray>5를 얻는다. 이후 자신의 턴을 종료한다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                usePlayerData.increaseTime(5, usePlayerData)
                usePlayerData.turnEnd()
                return@Card true
            }
        )
        //endregion

        //region accumulatedMoments Uncommon Initialization
        val accumulatedMoments = Card(
            "축적된 순간", listOf(
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적에게 자신의 <gold><bold>(시간 수치 x 2)</bold><gray> 만큼의 피해를 입힌다."),
                MiniMessage.miniMessage().deserialize("<gray>이후 자신의 <gold><bold>시간 </bold><gray>수치를 절반으로 만든다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()
                val time = usePlayerData.getTime()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                if (time !is AbnormalityStatus) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(time.power * 2, usePlayerData, DamageType.Normal)

                usePlayerData.decreaseTime(time.power / 2, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region timeTheft Uncommon Initialization
        val timeTheft = Card(
            "시간 절도", listOf(
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적의 속도 만큼 <gold><bold>시간</bold><gray>을 얻는다."),
                MiniMessage.miniMessage().deserialize("<gray>이후 대상의 속도를 0으로 만든다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Uncommon, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                val targetSpeed = target.speed.toInt()

                usePlayerData.increaseTime(targetSpeed, usePlayerData)
                target.speed = 0

                return@Card true
            }
        )
        //endregion


        //region extensionOfTime Rare Initialization
        val extensionOfTime = Card(
            "시간의 연장선", listOf(
                MiniMessage.miniMessage().deserialize("<gold><bold>시간 </bold><gray>10을 소모하고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>이번 라운드의 턴 순서 마지막에 추가 턴을 얻는다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()
                val game = Info.game ?: return@Card false

                if (time !is AbnormalityStatus || time.power < 10) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                usePlayerData.decreaseTime(10, usePlayerData)
                game.gameTurnOrder.add(usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region timeLifeIntersection Rare Initialization
        val timeLifeIntersection = Card(
            "시간 생명 교차", listOf(
                MiniMessage.miniMessage().deserialize("<gray>자신의 체력을 최대 10까지 소모하고 소모한 체력 만큼 <gold><bold>시간</bold><gray>을 얻는다."),
                MiniMessage.miniMessage().deserialize("<gray>이 효과로는 체력이 1 미만으로 내려가지 않으며, 이 경우 체력을 10 소모한 것으로 간주한다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                if (usePlayerData.health - 10 < 1) {
                    usePlayerData.setHealth(1)
                    usePlayerData.increaseTime(10, usePlayerData)
                    return@Card true
                }

                usePlayerData.addHealth(-10)
                usePlayerData.increaseTime(10, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region lifeTimeIntersection Rare Initialization
        val lifeTimeIntersection = Card(
            "생명 시간 교차", listOf(
                MiniMessage.miniMessage().deserialize("<gold><bold>시간 </bold><gray>수치만큼 체력을 회복하고 수치를 절반으로 만든다."),
                MiniMessage.miniMessage().deserialize("<gray>만약, 자신의 체력이 1이었다면 <gold><bold>시간 </bold><gray> 수치를 소모하지 않는다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()
                val isHealthZero = usePlayerData.health == 1

                if (time !is AbnormalityStatus) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                usePlayerData.heal(time.power, usePlayerData)
                if (!isHealthZero) {
                    usePlayerData.decreaseTime(time.power / 2, usePlayerData)
                }
                return@Card true
            }
        )
        //endregion


        //region parallelTime Legend Initialization
        val parallelTime = Card(
            "평행 시간", listOf(
                MiniMessage.miniMessage().deserialize("<gold><bold>시간 </bold><gray>50을 소모하고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>이번 라운드의 턴 순서 마지막에 모든 아군이 추가 턴을 얻는다."),
                Component.text(""),
                dictionary.dictionaryList["시간"]!!
            ), CardRarity.Legend, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val playerTeam = usePlayerData.allTeamMembers(true, false)
                val time = usePlayerData.getTime()
                val game = Info.game ?: return@Card false

                if (time !is AbnormalityStatus || time.power < 50) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                usePlayerData.decreaseTime(50, usePlayerData)
                game.gameTurnOrder.addAll(playerTeam)
                return@Card true
            }
        )
        //endregion




        cardPack.cardList.addAll(
            listOf(
                saveTime,
                saveTime,
                saveTime,
                resourceAcceleration,
                resourceAcceleration,
                resourceAcceleration,
                acceleration,
                acceleration,
                acceleration,
                temporarySuspension,
                temporarySuspension,
                temporarySuspension,
                accumulatedMoments,
                accumulatedMoments,
                accumulatedMoments,
                timeTheft,
                timeTheft,
                timeTheft,
                extensionOfTime,
                extensionOfTime,
                extensionOfTime,
                timeLifeIntersection,
                timeLifeIntersection,
                timeLifeIntersection,
                lifeTimeIntersection,
                lifeTimeIntersection,
                lifeTimeIntersection,
                parallelTime
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
    }
}