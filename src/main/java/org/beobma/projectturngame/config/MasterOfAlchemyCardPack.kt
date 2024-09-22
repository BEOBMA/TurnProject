package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.getCard
import org.beobma.projectturngame.manager.SoundManager.playTargetingFailSound
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
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
                Component.text("모든 아군은 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 2 회복한다.", TextColorType.Gray.textColor)))
            ), CardRarity.Common, 1
        )

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

                usePlayerData.getCard(
                    when {
                        cardA.name == "물" && cardB.name == "물" -> {
                            river
                        }
                        cardA.name == "불" && cardB.name == "불" -> {
                            // 태양
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "흙" && cardB.name == "흙" -> {
                            // 대지
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "공기" && cardB.name == "공기" -> {
                            // 바람
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }

                        cardA.name == "물" && cardB.name == "불" || cardA.name == "불" && cardB.name == "물" -> {
                            // 증기
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "물" && cardB.name == "흙" || cardA.name == "흙" && cardB.name == "물" -> {
                            // 진흙
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "물" && cardB.name == "공기" || cardA.name == "공기" && cardB.name == "물" -> {
                            // 안개
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "불" && cardB.name == "흙" || cardA.name == "흙" && cardB.name == "불" -> {
                            // 용암
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "불" && cardB.name == "공기" || cardA.name == "공기" && cardB.name == "불" -> {
                            // 번개
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }
                        cardA.name == "흙" && cardB.name == "공기" || cardA.name == "공기" && cardB.name == "흙" -> {
                            // 먼지
                            Card(
                                "물", listOf(
                                    KeywordType.NotAvailable.component,
                                    KeywordType.AlchemYingredients.component,
                                    Component.text(""),
                                    dictionary.dictionaryList["사용 불가"]!!,
                                    dictionary.dictionaryList["연금술 재료"]!!
                                ), CardRarity.Common, 0
                            )
                        }

                        else -> {
                            Card(
                                "연금술 잔여물", listOf(
                                    Component.text("연금술에 실패한 잔여물.")
                                ), CardRarity.Common, 0
                            )
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
                usePlayerData.getCard(cardList.random(), cardList.random())
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
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
        cardList.addAll(listOf(water, fire, air, dirt))
    }
}