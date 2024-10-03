package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.BlindnessManager.increaseBlindness
import org.beobma.projectturngame.manager.BurnManager.getBurn
import org.beobma.projectturngame.manager.BurnManager.increaseBurn
import org.beobma.projectturngame.manager.CardManager.addCard
import org.beobma.projectturngame.manager.CardManager.cardBanish
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.addShield
import org.beobma.projectturngame.manager.SelectionFactordManager.allEnemyMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playTargetingFailSound
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.manager.WeaknessManager.increaseWeakness
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class MasterOfAlchemyCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("연금술의 대가",
            listOf(
                Component.text("각기 다른 카드를 조합하여 새로운 카드를 만든다.")
            ), mutableListOf()
        )

        //region alchemy ingredients Initialization
        val water = Card(
            "물", listOf(
        KeywordType.NotAvailable.component,
        KeywordType.AlchemYingredients.component,
        Component.text(""),
        Component.text("그 누구도 물 없이 살 수는 없었다.", TextColorType.Gray.textColor),
        Component.text(""),
        dictionary.dictionaryList["사용 불가"]!!,
        dictionary.dictionaryList["연금술 재료"]!!
        ), CardRarity.Common, 0
        )

        val fire = Card(
            "불", listOf(
                KeywordType.NotAvailable.component,
                KeywordType.AlchemYingredients.component,
                Component.text(""),
                Component.text("불을 발견한 사람은 인류를 창조했다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["연금술 재료"]!!
            ), CardRarity.Common, 0
        )

        val dirt = Card(
            "흙", listOf(
                KeywordType.NotAvailable.component,
                KeywordType.AlchemYingredients.component,
                Component.text(""),
                Component.text("흙은 모든 생명의 어머니이자 보호자다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["연금술 재료"]!!
            ), CardRarity.Common, 0
        )

        val air = Card(
            "공기", listOf(
                KeywordType.NotAvailable.component,
                KeywordType.AlchemYingredients.component,
                Component.text(""),
                Component.text("공기는 우리의 첫 번째 음식이다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["연금술 재료"]!!
            ), CardRarity.Common, 0
        )


        val river = Card(
            "강", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                KeywordType.Mana.component.append(Component.text("를 2 회복한다.", TextColorType.Gray.textColor)),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                usePlayerData.addMana(2)
                return@Card true
            }
        )

        val sun = Card(
            "태양", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("모든 적에게 ", TextColorType.Gray.textColor).append(KeywordType.Burn.component.append(Component.text(" 5를 부여한다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["화상"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val targets = usePlayerData.allEnemyMembers()

                targets.forEach {
                    it.increaseBurn(5, usePlayerData)
                }

                return@Card true
            }
        )

        val earth = Card(
            "대지", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("10의 피해를 막는 ", TextColorType.Gray.textColor).append(KeywordType.Shield.component.append(Component.text("을 얻는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["보호막"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                usePlayerData.addShield(10)

                return@Card true
            }
        )

        val wind = Card(
            "바람", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("덱에서 카드를 2장 뽑는다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                usePlayerData.drow(2)
                return@Card true
            }
        )

        val steam = Card(
            "증기", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("바라보는 적에게 ", TextColorType.Gray.textColor).append(KeywordType.Weakness.component.append(Component.text(" 3을 부여한다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["나약함"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }
                target.increaseWeakness(3, usePlayerData)
                return@Card true
            }
        )

        val mud = Card(
            "진흙", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("모든 적의 속도가 1 감소한다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val targets = usePlayerData.allEnemyMembers()

                targets.forEach {
                    it.speed -= 1
                }

                return@Card true
            }
        )

        val fog = Card(
            "안개", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("모든 적에게 ", TextColorType.Gray.textColor).append(KeywordType.Blindness.component.append(Component.text(" 1을 부여한다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["실명"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val targets = usePlayerData.allEnemyMembers()

                targets.forEach {
                    it.increaseBlindness(1, usePlayerData)
                }

                return@Card true
            }
        )

        val lava = Card(
            "용암", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("모든 적에게 10의 피해를 입힌다. 대상에게 ", TextColorType.Gray.textColor).append(KeywordType.Burn.component.append(Component.text("이 있었다면 추가로 10의 피해를 입힌다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["화상"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val targets = usePlayerData.allEnemyMembers()

                targets.forEach {
                    it.damage(10, usePlayerData)

                    if (it.getBurn() is AbnormalityStatus) {
                        it.damage(10, usePlayerData)
                    }
                }

                return@Card true
            }
        )

        val lightning = Card(
            "번개", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("무작위 적에게 3의 피해를 입힌다.", TextColorType.Gray.textColor),
                Component.text("위 효과는 3번 사용하며, 첫번째 대상에게 여러번 적중할 때마다 추가로 3의 피해를 입힌다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val targets = usePlayerData.allEnemyMembers()
                val firstTarget = targets.random()

                firstTarget.damage(3, usePlayerData)
                repeat(2) {
                    val target = targets.random()

                    target.damage(3, usePlayerData)
                    if (target == firstTarget) {
                        target.damage(3, usePlayerData)
                    }
                }
                return@Card true
            }
        )

        val dust = Card(
            "먼지", listOf(
                KeywordType.Volatilization.component,
                Component.text(""),
                Component.text("바라보는 적에게 ", TextColorType.Gray.textColor).append(KeywordType.Blindness.component.append(Component.text(" 10을 부여한다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["실명"]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                target.increaseBlindness(10, usePlayerData)

                return@Card true
            }
        )
        //endregion


        //region lesserConjugation Common Initialization
        val lesserConjugation = Card(
            "하급 연성", listOf(
                KeywordType.AlchemYingredientsPile.component.append(Component.text("의 카드들 중, 무작위 카드 2장을 ", TextColorType.Gray.textColor).append(KeywordType.Ductility.component.append(Component.text("한다.", TextColorType.Gray.textColor)))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료 더미"]!!,
                dictionary.dictionaryList["연성"]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                val cardA = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardA)

                val cardB = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardB)

                usePlayerData.addCard(
                    when (setOf(cardA.name, cardB.name)) {
                        setOf("물, 물") -> {
                            river
                        }
                        setOf("불, 불") -> {
                            sun
                        }
                        setOf("흙, 흙") -> {
                            earth
                        }
                        setOf("공기, 공기") -> {
                            wind
                        }
                        setOf("물, 불") -> {
                            steam
                        }
                        setOf("물, 흙") -> {
                            mud
                        }
                        setOf("물, 공기") -> {
                            fog
                        }
                        setOf("불, 흙") -> {
                            lava
                        }
                        setOf("불, 공기") -> {
                            lightning
                        }
                        setOf("흙, 공기") -> {
                            dust
                        }

                        else -> {
                            return@Card false
                        }
                    }
                )

                return@Card true
            }
        )
        //endregion

        //region kilnOfCreation Common Initialization
        val kilnOfCreation = Card(
            "창조의 가마", listOf(
                Component.text("'연금술의 대가' 카드팩에 존재하는 ", TextColorType.Gray.textColor).append(KeywordType.AlchemYingredients.component.append(Component.text(" 카드들 중, 무작위 2장을 생성하고 패에 넣는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료"]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val cardList = listOf(water, fire, dirt, air)
                usePlayerData.addCard(cardList.random())
                usePlayerData.addCard(cardList.random())
                return@Card true
            }
        )
        //endregion

        //region materialReproduction Common Initialization
        val materialReproduction = Card(
            "재료 복제", listOf(
                KeywordType.AlchemYingredientsPile.component.append(Component.text("의 무작위 카드 1장과 동일한 카드를 생성하고 ", TextColorType.Gray.textColor).append(KeywordType.AlchemYingredientsPile.component.append(Component.text("에 넣는다.", TextColorType.Gray.textColor)))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료 더미"]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cards = usePlayerData.alchemYingredientsPile

                if (cards.isEmpty()) {
                    player.sendMessage(cardUseFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                usePlayerData.alchemYingredientsPile.add(cards.random())
                return@Card true
            }
        )
        //endregion


        //region intermediateSoftness Uncommon Initialization
        val intermediateSoftness = Card(
            "중급 연성", listOf(
                KeywordType.AlchemYingredientsPile.component.append(Component.text("의 카드들 중, 무작위 카드 2장을 ", TextColorType.Gray.textColor).append(KeywordType.Ductility.component.append(Component.text("한다.", TextColorType.Gray.textColor)))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료 더미"]!!,
                dictionary.dictionaryList["연성"]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                val cardA = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardA)

                val cardB = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardB)

                usePlayerData.addCard(
                    when (setOf(cardA.name, cardB.name)) {
                        setOf("물, 물") -> {
                            river
                        }
                        setOf("불, 불") -> {
                            sun
                        }
                        setOf("흙, 흙") -> {
                            earth
                        }
                        setOf("공기, 공기") -> {
                            wind
                        }
                        setOf("물, 불") -> {
                            steam
                        }
                        setOf("물, 흙") -> {
                            mud
                        }
                        setOf("물, 공기") -> {
                            fog
                        }
                        setOf("불, 흙") -> {
                            lava
                        }
                        setOf("불, 공기") -> {
                            lightning
                        }
                        setOf("흙, 공기") -> {
                            dust
                        }

                        else -> {
                            return@Card false
                        }
                    }
                )

                return@Card true
            }
        )
        //endregion\

        //region disappearanceOfMaterials Uncommon Initialization
        val disappearanceOfMaterials = Card(
            "재료의 소멸", listOf(
                KeywordType.AlchemYingredientsPile.component.append(Component.text("의 카드들 중, 무작위 카드 1장을 소멸시키고 발동할 수 있다.", TextColorType.Gray.textColor)),
                Component.text("덱에서 카드를 1장 뽑고 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 1 회복한다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료 더미"]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                val cardA = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardA)

                val cardB = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardB)

                usePlayerData.addCard(
                    when (setOf(cardA.name, cardB.name)) {
                        setOf("물, 물") -> {
                            river
                        }
                        setOf("불, 불") -> {
                            sun
                        }
                        setOf("흙, 흙") -> {
                            earth
                        }
                        setOf("공기, 공기") -> {
                            wind
                        }
                        setOf("물, 불") -> {
                            steam
                        }
                        setOf("물, 흙") -> {
                            mud
                        }
                        setOf("물, 공기") -> {
                            fog
                        }
                        setOf("불, 흙") -> {
                            lava
                        }
                        setOf("불, 공기") -> {
                            lightning
                        }
                        setOf("흙, 공기") -> {
                            dust
                        }

                        else -> {
                            return@Card false
                        }
                    }
                )

                return@Card true
            }
        )
        //endregion

        //region deactivateConjugation Uncommon Initialization
        val deactivateConjugation = Card(
            "연성 해제", listOf(
                Component.text("패에 ", TextColorType.Gray.textColor).append(KeywordType.Ductility.component.append(Component.text("을 통해 생성된 카드를 소멸시키고 발동할 수 있다.", TextColorType.Gray.textColor))),
                Component.text("소멸시킨 카드의 재료가 되는 카드를 ", TextColorType.Gray.textColor).append(KeywordType.AlchemYingredientsPile.component.append(Component.text("에 넣는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["연성"]!!,
                dictionary.dictionaryList["연금술 재료 더미"]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cards = listOf(river, sun, earth, wind, steam, mud, fog, lava, lightning, dust)

                if (!usePlayerData.hand.containsAll(cards)) {
                    player.sendMessage(cardUseFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }
                val cardList = usePlayerData.hand.filter { cards.contains(it) }
                val card = cardList.random()

                when (card) {
                    river -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(water)
                    }
                    sun -> {
                        usePlayerData.alchemYingredientsPile.add(sun)
                        usePlayerData.alchemYingredientsPile.add(sun)
                    }
                    earth -> {
                        usePlayerData.alchemYingredientsPile.add(dirt)
                        usePlayerData.alchemYingredientsPile.add(dirt)
                    }
                    wind -> {
                        usePlayerData.alchemYingredientsPile.add(air)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    steam -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(fire)
                    }
                    mud -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(dirt)
                    }
                    fog -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    lava -> {
                        usePlayerData.alchemYingredientsPile.add(fire)
                        usePlayerData.alchemYingredientsPile.add(dirt)
                    }
                    lightning -> {
                        usePlayerData.alchemYingredientsPile.add(fire)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    dust -> {
                        usePlayerData.alchemYingredientsPile.add(dirt)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    else -> {
                        return@Card false
                    }
                }

                return@Card true
            }
        )
        //endregion


        //region urgentDelivery Rare Initialization
        val urgentDelivery = Card(
            "긴급 배달", listOf(
                Component.text("'연금술의 대가' 카드팩에 존재하는 ", TextColorType.Gray.textColor).append(KeywordType.AlchemYingredients.component.append(Component.text(" 카드들 중, 무작위 카드를 가능한 만큼 생성하고 패에 넣는다,", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료"]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val cardList = listOf(water, fire, dirt, air)

                while (usePlayerData.hand.size <= 9) {
                    usePlayerData.addCard(cardList.random())
                }
                return@Card true
            }
        )
        //endregion


        //region substitutionDuctility Legend Initialization
        val substitutionDuctility = Card(
            "치환 연성", listOf(
                KeywordType.AlchemYingredientsPile.component.append(Component.text("의 카드들 중, 무작위 카드 2장을 제외하고 발동할 수 있다.", TextColorType.Gray.textColor)),
                Component.text("제외한 카드를", TextColorType.Gray.textColor).append(KeywordType.Ductility.component.append(Component.text("한 것으로 간주하고 연성한 카드를 패에 넣는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["연금술 재료 더미"]!!,
                dictionary.dictionaryList["연성"]!!
            ), CardRarity.Legend, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playTargetingFailSound()
                    return@Card false
                }

                val cardA = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardA)
                usePlayerData.cardBanish(cardA)

                val cardB = usePlayerData.alchemYingredientsPile.random()
                usePlayerData.alchemYingredientsPile.remove(cardB)
                usePlayerData.cardBanish(cardB)

                usePlayerData.addCard(
                    when (setOf(cardA.name, cardB.name)) {
                        setOf("물, 물") -> {
                            river
                        }
                        setOf("불, 불") -> {
                            sun
                        }
                        setOf("흙, 흙") -> {
                            earth
                        }
                        setOf("공기, 공기") -> {
                            wind
                        }
                        setOf("물, 불") -> {
                            steam
                        }
                        setOf("물, 흙") -> {
                            mud
                        }
                        setOf("물, 공기") -> {
                            fog
                        }
                        setOf("불, 흙") -> {
                            lava
                        }
                        setOf("불, 공기") -> {
                            lightning
                        }
                        setOf("흙, 공기") -> {
                            dust
                        }

                        else -> {
                            return@Card false
                        }
                    }
                )

                return@Card true
            }
        )
        //endregion

        cardPack.cardList.addAll(
            listOf(
                lesserConjugation,
                lesserConjugation,
                lesserConjugation,
                kilnOfCreation,
                kilnOfCreation,
                kilnOfCreation,
                materialReproduction,
                materialReproduction,
                materialReproduction,
                intermediateSoftness,
                intermediateSoftness,
                intermediateSoftness,
                disappearanceOfMaterials,
                disappearanceOfMaterials,
                disappearanceOfMaterials,
                substitutionDuctility,
                substitutionDuctility,
                substitutionDuctility,
                urgentDelivery,
                urgentDelivery,
                urgentDelivery,
                substitutionDuctility
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
        cardList.addAll(listOf(water, fire, air, dirt, river, sun, earth, wind, steam, mud, fog, lava, lightning, dust))
    }
}