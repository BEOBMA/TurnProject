package org.beobma.projectturngame.config.cardpack

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.continueeffect.ContinueEffect
import org.beobma.projectturngame.continueeffect.ContinueEffectHandler
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.addCard
import org.beobma.projectturngame.manager.CardManager.cardThrow
import org.beobma.projectturngame.manager.CardManager.clearHand
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.addShield
import org.beobma.projectturngame.manager.PlayerManager.addTag
import org.beobma.projectturngame.manager.PlayerManager.death
import org.beobma.projectturngame.manager.PlayerManager.diceRoll
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.PlayerManager.setMana
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.EffectTime
import org.beobma.projectturngame.util.ResetType
import org.bukkit.Particle
import org.bukkit.Sound

class IcosahedronCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("<gray>20면체",
            listOf(
                "<gray>운에 따라, 효과가 달라진다."
            ), mutableListOf(), mutableListOf(), CardPackType.Limitation
        )

        //region attack Common Initialization
        val attack = Card(
            "공격", listOf(
                "<gray>바라보는 적에게 7의 피해를 입힌다."
            ), CardRarity.Common, 1, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(7, usePlayerData)
                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                return@Card true
            }
        )
        //endregion

        //region defense Common Initialization
        val defense = Card(
            "수비", listOf(
                "<gray>10의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Shield]!!
            ), CardRarity.Common, 1, { usePlayerData, _ ->
                val player = usePlayerData.player

                usePlayerData.addShield(10)
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                return@Card true
            }
        )
        //endregion

        //region rest Common Initialization
        val rest = Card(
            "휴식", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>나온 값이 10을 초과한다면 ${KeywordType.Mana.string}를 2 회복한다.",
                "<gray>이외의 경우에는 ${KeywordType.Mana.string}를 1 회복한다.",
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice > 10) {
                    usePlayerData.addMana(2)
                }
                else {
                    usePlayerData.addMana(1)
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                return@Card true
            }
        )
        //endregion

        //region drow Common Initialization
        val drow = Card(
            "뽑기", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>나온 값이 10을 초과한다면 덱에서 카드를 2장 뽑는다.",
                "<gray>이외의 경우에는 덱에서 카드를 1장 뽑는다.",
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player

                val dice = usePlayerData.diceRoll(1, 20)

                if (dice > 10) {
                    usePlayerData.drow(2)
                }
                else {
                    usePlayerData.drow(1)
                }

                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                return@Card true
            }
        )
        //endregion

        //region powerAttack Common Initialization
        val powerAttack = Card(
            "강공", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>바라보는 적에게 (10 + 주사위 값) 만큼의 피해를 입힌다."
            ), CardRarity.Uncommon, 2, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()
                val dice = usePlayerData.diceRoll(1, 20)

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(10 + dice, usePlayerData)

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                return@Card true
            }
        )
        //endregion


        //region variable Common Initialization
        val variable = Card(
            "변수", listOf(
                "<gray>바라보는 적에게 20면체 주사위를 굴려 나온 값만큼 피해를 입힌다.)"
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val dice = usePlayerData.diceRoll(1, 20)

                if (dice < 10) {
                    player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                }
                else {
                    player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 2.0F)
                }
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(dice, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region gambling Common Initialization
        val gambling = Card(
            "도박수", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>값이 10 이상이라면 덱에서 카드를 3장 뽑는다.",
                "<gray>이 외의 경우에는 패에서 무작위 카드 1장을 버린다."
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice >= 10) {
                    usePlayerData.drow(3)
                    player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                }
                else {
                    val card = usePlayerData.hand.random()
                    usePlayerData.cardThrow(card)
                    player.world.playSound(player.location, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0F, 0.5F)
                }
                return@Card true
            }
        )
        //endregion

        //region scaleOfFate Common Initialization
        val scaleOfFate = Card(
            "운명의 눈금", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>값이 20이라면 <blue><bold>마나</bold><gray>를 최대로 회복하고 덱에서 카드를 5장 뽑는다.",
                "<gray>이 외의 경우에는 <blue><bold>마나</bold><gray>를 0으로 만들고 패의 카드를 모두 버린다."
            ), CardRarity.Common, 3,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice == 20) {
                    usePlayerData.addMana(usePlayerData.maxMana)
                    usePlayerData.drow(5)
                    player.world.playSound(player.location, Sound.ITEM_GOAT_HORN_SOUND_1, 1.0F, 2.0F)
                }
                else {
                    usePlayerData.setMana(0)
                    usePlayerData.clearHand()
                    player.world.playSound(player.location, Sound.ENTITY_WITHER_DEATH, 0.5F, 1.0F)
                }
                return@Card true
            }
        )
        //endregion

        //region zeroRisk Uncommon Initialization
        val zeroRisk = Card(
            "제로 리스크", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>값이 1이라면 자신은 사망한다.",
                "<gray>이 외의 경우에는 바라보는 적에게 15의 피해를 입힌다."
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val dice = usePlayerData.diceRoll(1, 20)

                if (dice == 1) {
                    player.world.playSound(player.location, Sound.ENTITY_WITHER_DEATH, 0.5F, 1.0F)
                    usePlayerData.death()
                }
                else {
                    target.damage(15, usePlayerData)
                    player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                    player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                }
                return@Card true
            }
        )
        //endregion

        //region sniffling Uncommon Initialization
        val sniffling = Card(
            "홀짝", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>값이 짝수라면 <blue><bold>마나</bold><gray>를 2 회복한다.",
                "<gray>값이 홀수라면 <blue><bold>마나</bold><gray>를 1 회복한다."
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val dice = usePlayerData.diceRoll(1, 20)

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)

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
                "<gray>체력을 20면체 주사위를 굴려 나온 값만큼 회복한다."
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val dice = usePlayerData.diceRoll(1, 20)

                player.world.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.HEART, player.location, 5, 0.1, 0.1, 0.1, 0.0)
                usePlayerData.heal(dice, usePlayerData)

                return@Card true
            }
        )
        //endregion

        //region weightedDice Rare Initialization
        val weightedDice = Card(
            "가중치 주사위", listOf(
                "<gray>이번 턴 동안 주사위를 굴리면 그 값에 5를 더한다."
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.diceWeight += 5
                usePlayerData.turnEndUnit.add { usePlayerData.diceWeight -= 5 }

                player.world.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 2.0F)
                return@Card true
            }
        )
        //endregion

        //region contradictoryRoll Rare Initialization
        val contradictoryRoll = Card(
            "상반된 굴림", listOf(
                "<gray>20면체 주사위를 굴린다.",
                "<gray>값이 10 이상이면 이번 턴 동안 주사위를 굴려 나온 값에 이 주사위 값을 뺀다.",
                "<gray>이외의 경우에는 이번 턴 동안 주사위를 굴려 나온 값에 이 주사위 값을 더한다."
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val dice = usePlayerData.diceRoll(1, 20)

                if (dice >= 10) {
                    usePlayerData.diceWeight -= dice
                    usePlayerData.turnEndUnit.add { usePlayerData.diceWeight += dice }
                    player.world.playSound(player.location, Sound.BLOCK_BEACON_DEACTIVATE, 1.0F, 0.5F)
                }
                else {
                    usePlayerData.diceWeight += dice
                    usePlayerData.turnEndUnit.add { usePlayerData.diceWeight -= dice }
                    player.world.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 2.0F)
                }
                return@Card true
            }
        )
        //endregion

        //region minMax Rare Initialization
        val minMax = Card(
            "최소 최대", listOf(
                "<gray>다음번 주사위를 굴리면 그 값은 반드시 최솟값 또는 최댓값으로 결정된다."
            ), CardRarity.Rare, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.addTag("minMax", ResetType.None)
                player.world.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 2.0F)
                return@Card true
            }
        )
        //endregion

        //region chanceAdvantage Legend Initialization
        val chanceAdvantage = Card(
            "확률 우위", listOf(
                "<gray>다음번 주사위를 굴리면 주사위를 2개 굴려 더 높은 값이 나온 주사위를 사용한다."
            ), CardRarity.Legend, 3,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.addTag("chanceAdvantage", ResetType.None)
                player.world.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, 1.0F, 2.0F)
                return@Card true
            }
        )
        //endregion

        //region bigBlind Legend Initialization
        val bigBlind = Card(
            "빅 블라인드", listOf(
                KeywordType.Continue.string,
                "",
                "<gray>전투 종료 시까지 매 턴을 시작할 때 20면체 주사위를 굴린다.",
                "<gray>나온 값이 15를 초과할 경우 최대치와 관계 없이 ${KeywordType.Mana.string}를 10으로 만든다.",
                "<gray>이외의 경우에는 ${KeywordType.Mana.string}를 0으로 만든다.",
                "",
                dictionary.dictionaryList[KeywordType.Continue]!!
            ), CardRarity.Legend, 3,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val game = Info.game ?: return@Card false

                player.world.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.END_ROD, player.location, 10, 0.0, 0.0, 0.0, 0.3)
                game.continueEffects.add(ContinueEffect(usePlayerData, EffectTime.TurnStart, { entity: Entity ->
                    val dice = usePlayerData.diceRoll(1, 20)

                    if (dice > 15) {
                        usePlayerData.setMana(10, true)
                    }
                    else {
                        usePlayerData.setMana(0, true)
                    }
                } as ContinueEffectHandler
                ))
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
                chanceAdvantage,
                bigBlind,
                bigBlind
            )
        )

        cardPack.startCardList.addAll(
            listOf(
                attack,
                attack,
                attack,
                defense,
                defense,
                defense,
                rest,
                rest,
                drow,
                drow,
                powerAttack,
                powerAttack
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.startCardList)
        cardList.addAll(cardPack.cardList)
    }
}