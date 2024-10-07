package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.cardThrow
import org.beobma.projectturngame.manager.CardManager.clearBanish
import org.beobma.projectturngame.manager.CardManager.clearDeck
import org.beobma.projectturngame.manager.CardManager.clearGraveyard
import org.beobma.projectturngame.manager.CardManager.clearHand
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.CardManager.isFix
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.PlayerManager.setMana
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class CirculationCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("만물의 순환",
            listOf(
                Component.text("만물의 모든 것은 순환하기 마련이다.")
            ), mutableListOf()
        )

        //region handCirculation Common Initialization
        val handCirculation = Card(
            "패 순환", listOf(
                Component.text("패에서 '패 순환'을 제외한 무작위 카드 1장을 버리고 발동할 수 있다.", TextColorType.Gray.textColor),
                Component.text("덱에서 카드 2장을 뽑는다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val cardList = usePlayerData.hand.filter { it.name != "패 순환" }

                if (cardList.isEmpty()) {
                    usePlayerData.player.sendMessage(cardUseFailText())
                    return@Card false
                }

                usePlayerData.cardThrow(cardList.random())
                usePlayerData.drow(2)


                return@Card true
            }
        )
        //endregion

        //region deckCirculation Uncommon Initialization
        val deckCirculation = Card(
            "덱 순환", listOf(
                Component.text("덱에서 '덱 순환'을 제외한 무작위 카드 1장을 제외하고 발동할 수 있다.", TextColorType.Gray.textColor),
                Component.text("묘지에서 무작위 카드 2장을 덱에 넣는다.", TextColorType.Gray.textColor)
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val cardList = usePlayerData.deck.filter { it.name != "덱 순환" }

                if (cardList.isEmpty()) {
                    usePlayerData.player.sendMessage(cardUseFailText())
                    return@Card false
                }

                val card = cardList.random()
                usePlayerData.deck.remove(card)
                usePlayerData.banish.add(card)
                repeat(2) {
                    usePlayerData.deck.add(usePlayerData.graveyard.random())
                    usePlayerData.deck.shuffle()
                }

                return@Card true
            }
        )
        //endregion

        //region bigCirculation Legend Initialization
        val bigCirculation = Card(
            "대순환", listOf(
                KeywordType.SameCardDisappears.component,
                KeywordType.Fix.component,
                Component.text(""),
                Component.text("패, 묘지, 제외된 카드들을 모두 덱으로 되돌리고 덱에서 카드 5장을 뽑는다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["동일 카드 소멸"]!!,
                dictionary.dictionaryList["고정"]!!
            ), CardRarity.Legend, 1,
            { usePlayerData, _ ->
                val cardList: MutableList<Card> = mutableListOf()

                cardList.addAll(usePlayerData.banish)
                cardList.addAll(usePlayerData.graveyard)
                cardList.addAll(usePlayerData.hand)

                val removeCardList = cardList.filter { it.description.contains(KeywordType.Fix.component) }
                cardList.removeAll(removeCardList)

                usePlayerData.deck.addAll(cardList)
                usePlayerData.clearHand()
                usePlayerData.clearGraveyard()
                usePlayerData.clearBanish()
                usePlayerData.drow(5)
                return@Card true
            }
        )
        //endregion

        //region manaCirculation Common Initialization
        val manaCirculation = Card(
            "마나 순환", listOf(
                KeywordType.Mana.component.append(Component.text("를 1 회복한다.", TextColorType.Gray.textColor))
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                usePlayerData.addMana(1)
                return@Card true
            }
        )
        //endregion

        //region borrowedTime Common Initialization
        val borrowedTime = Card(
            "빌려온 시간", listOf(
                KeywordType.Mana.component.append(Component.text("를 2 회복한다.", TextColorType.Gray.textColor)),
                Component.text("다음 턴 시작 시 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(
                    Component.text("를 0으로 만든다.", TextColorType.Gray.textColor)
                ))
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                    usePlayerData.addMana(2)
                    usePlayerData.turnStartUnit.add {
                        usePlayerData.setMana(0)
                    }
                return@Card true
            }
        )
        //endregion

        //region reverseCycle Uncommon Initialization
        val reverseCycle = Card(
            "역순환", listOf(
                Component.text("덱에서 카드 3장을 뽑는다.", TextColorType.Gray.textColor),
                Component.text("패에서 무작위 카드 1장을 버린다.", TextColorType.Gray.textColor),
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                usePlayerData.drow(3)
                usePlayerData.cardThrow(usePlayerData.hand.random())
                return@Card true
            }
        )
        //endregion

        //region balance Uncommon Initialization
        val balance = Card(
            "균형", listOf(
                Component.text("자신의 패가 비어있을 경우에 발동할 수 있다.", TextColorType.Gray.textColor),
                Component.text("덱에서 카드 3장을 뽑는다.", TextColorType.Gray.textColor)
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                if (usePlayerData.hand.isNotEmpty()) {
                    usePlayerData.player.sendMessage(cardUseFailText())
                    return@Card false
                }
                usePlayerData.drow(3)
                return@Card true
            }
        )
        //endregion

        //region imbalance Uncommon Initialization
        val imbalance = Card(
            "불균형", listOf(
                Component.text("패의 카드를 모두 버린다.", TextColorType.Gray.textColor)
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val cardList = usePlayerData.hand.toList()
                cardList.forEach {
                    usePlayerData.cardThrow(it)
                }
                return@Card true
            }
        )
        //endregion

        //region brokenBalance Rare Initialization
        val brokenBalance = Card(
            "깨진 균형", listOf(
                Component.text("뽑을 수 있는 만큼 덱에서 카드를 뽑는다.", TextColorType.Gray.textColor),
                Component.text("뽑은 후 덱에 남은 카드를 모두 제외한다.", TextColorType.Gray.textColor)
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val handSize = (usePlayerData.hand.size - 8) * -1
                val cardList = usePlayerData.deck.filter { !it.isFix() }

                usePlayerData.drow(handSize)
                usePlayerData.banish.addAll(cardList)
                usePlayerData.clearDeck()

                return@Card true
            }
        )
        //endregion

        //region rotation Rare Initialization
        val rotation = Card(
            "회전", listOf(
                Component.text("패의 카드를 모두 덱으로 되돌리고 덱을 섞는다.", TextColorType.Gray.textColor),
                Component.text("되돌린 카드의 수 만큼 덱에서 카드를 뽑는다.", TextColorType.Gray.textColor)
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val handSize = (usePlayerData.hand.size - 8) * -1
                val cardList = usePlayerData.hand.filter { !it.isFix() }

                usePlayerData.deck.addAll(cardList)
                usePlayerData.deck.shuffle()
                usePlayerData.clearHand()
                usePlayerData.drow(handSize)


                return@Card true
            }
        )
        //endregion

        //region lifeCycle Rare Initialization
        val lifeCycle = Card(
            "생명의 순환", listOf(
                Component.text("체력을 12 회복한다.", TextColorType.Gray.textColor)
            ), CardRarity.Rare, 2,
            { usePlayerData, _ ->
                usePlayerData.heal(12, usePlayerData)
                return@Card true
            }
        )
        //endregion




        cardPack.cardList.addAll(
            listOf(
                handCirculation,
                handCirculation,
                handCirculation,
                deckCirculation,
                deckCirculation,
                deckCirculation,
                bigCirculation,
                manaCirculation,
                manaCirculation,
                manaCirculation,
                borrowedTime,
                borrowedTime,
                borrowedTime,
                reverseCycle,
                reverseCycle,
                reverseCycle,
                balance,
                balance,
                balance,
                imbalance,
                imbalance,
                imbalance,
                brokenBalance,
                brokenBalance,
                rotation,
                rotation,
                lifeCycle,
                lifeCycle
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
    }
}