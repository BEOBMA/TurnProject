package org.beobma.projectturngame.config.cardpack

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardPackType
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

class CirculationCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("<gray>만물의 순환",
            listOf(
                "<gray>각종 자원을 순환시킨다."
            ), mutableListOf(), mutableListOf(), CardPackType.Universal
        )

        //region handCirculation Common Initialization
        val handCirculation = Card(
            "패 순환", listOf(
                "<gray>패에서 '패 순환'을 제외한 무작위 카드 1장을 버리고 발동할 수 있다.",
                "<gray>덱에서 카드 2장을 뽑는다."
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
                "<gray>덱에서 '덱 순환'을 제외한 무작위 카드 1장을 제외하고 발동할 수 있다.",
                "<gray>묘지에서 무작위 카드 2장을 덱에 넣는다."
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
                KeywordType.SameCardDisappears.string,
                KeywordType.Fix.string,
                "",
                "<gray>패, 묘지, 제외된 카드들을 모두 덱으로 되돌리고 덱에서 카드 5장을 뽑는다.",
                "",
                dictionary.dictionaryList[KeywordType.SameCardDisappears]!!,
                dictionary.dictionaryList[KeywordType.Fix]!!
            ), CardRarity.Legend, 1,
            { usePlayerData, _ ->
                val cardList: MutableList<Card> = mutableListOf()

                cardList.addAll(usePlayerData.banish)
                cardList.addAll(usePlayerData.graveyard)
                cardList.addAll(usePlayerData.hand)

                val removeCardList = cardList.filter { it.description.contains(KeywordType.Fix.string) }
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
                "<blue><bold>마나</bold><gray>를 1 회복한다."
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
                "<blue><bold>마나</bold><gray>를 2 회복한다.",
                "<gray>다음 턴 시작 시 <blue><bold>마나</bold><gray>를 0으로 만든다."
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
                "<gray>덱에서 카드 3장을 뽑는다.",
                "<gray>패에서 무작위 카드 1장을 버린다."
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
                "<gray>자신의 패가 비어있을 경우에 발동할 수 있다.",
                "<gray>덱에서 카드 3장을 뽑는다."
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
                "<gray>패의 카드를 모두 버린다."
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
                "<gray>뽑을 수 있는 만큼 덱에서 카드를 뽑는다.",
                "<gray>뽑은 후 덱에 남은 카드를 모두 제외한다."
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
                "<gray>패의 카드를 모두 덱으로 되돌리고 덱을 섞는다.",
                "<gray>되돌린 카드의 수 만큼 덱에서 카드를 뽑는다."
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
                "<gray>체력을 12 회복한다."
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