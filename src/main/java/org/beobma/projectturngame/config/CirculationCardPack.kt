package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.*
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class CirculationCardPack {
    private val selectionFactordManager = SelectionFactordManager(DefaultSelectionFactordManager())
    private val playerManager = PlayerManager(DefaultPlayerManager())
    private val enemyManager = EnemyManager(DefaultEnemyManager())
    private val textManager = TextManager(DefaultTextManager())
    private val soundManager = SoundManager(DefaultSoundManager())
    private val cardManager = CardManager(DefaultCardManager())
    private val utilManager = UtilManager(DefaultUtilManager())
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("만물의 순환",
            listOf(
                Component.text("패를 순환시키는 카드 팩."),
                Component.text("기본적으로 카드를 버리고 효과를 얻는 카드들이 모여있다.")
            ), mutableListOf()
        )

        //region handCirculation Initialization
        val handCirculation = Card(
            "패 순환", listOf(
                Component.text("패에서 '패 순환'을 제외한 무작위 카드 1장을 버리고, 덱에서 카드 2장을 뽑는다.", TextColorType.Gray.textColor)
            ), CardRarity.Common, 0,
            { usePlayerData ->
                val cardList = usePlayerData.hand.filter { it.name != "패 순환" }

                if (cardList.isEmpty()) {
                    usePlayerData.player.sendMessage(textManager.cardUseFailText())
                    return@Card false
                }
                cardManager.run {
                    usePlayerData.cardThrow(cardList.random())
                    usePlayerData.drow(2)
                }

                return@Card true
            }
        )
        //endregion

        //region deckCirculation Initialization
        val deckCirculation = Card(
            "덱 순환", listOf(
                Component.text("덱에서 '덱 순환'을 제외한 무작위 카드 1장을 제외하고, 묘지에서 무작위 카드 2장을 덱에 넣는다.", TextColorType.Gray.textColor)
            ), CardRarity.Uncommon, 0,
            { usePlayerData ->
                val cardList = usePlayerData.deck.filter { it.name != "덱 순환" }

                if (cardList.isEmpty()) {
                    usePlayerData.player.sendMessage(textManager.cardUseFailText())
                    return@Card false
                }

                cardManager.run {
                    val card = cardList.random()
                    usePlayerData.deck.remove(card)
                    usePlayerData.banish.add(card)
                    repeat(2) {
                        usePlayerData.deck.add(usePlayerData.graveyard.random())
                    }
                }

                return@Card true
            }
        )
        //endregion

        //region bigCirculation Initialization
        val bigCirculation = Card(
            "대순환", listOf(
                KeywordType.SameCardDisappears.component,
                KeywordType.Fix.component,
                Component.text("패, 묘지, 제외된 카드들을 모두 덱으로 되돌리고 덱에서 카드 5장을 뽑는다.", TextColorType.Gray.textColor),
                dictionary.dictionaryList["동일 카드 소멸"]!!,
                dictionary.dictionaryList["고정"]!!
            ), CardRarity.Legend, 1,
            { usePlayerData ->
                val cardList: MutableList<Card> = mutableListOf()

                cardList.addAll(usePlayerData.banish)
                cardList.addAll(usePlayerData.graveyard)
                cardList.addAll(usePlayerData.hand)
                cardManager.run {
                    usePlayerData.addDeckCard(*cardList.toTypedArray())
                    usePlayerData.clearHand()
                    usePlayerData.clearGraveyard()
                    usePlayerData.clearBanish()
                }
                cardManager.run {
                    usePlayerData.drow(5)
                }
                return@Card true
            },
            { usePlayerData ->
                cardManager.run {
                    val card = findCard("대순환") ?: return@Card
                    usePlayerData.extinction(card)
                }
            }
        )
        //endregion

        //region rainCloud Initialization
        val rainCloud = Card(
            "비구름", listOf(
                KeywordType.Volatilization.component,
                Component.text("모든 적에게 ", TextColorType.Gray.textColor).append(KeywordType.Cloudy.component).append(Component.text("을 적용한다.", TextColorType.Gray.textColor)),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["흐림"]!!
            ), CardRarity.Common, 0,
            { usePlayerData ->
                selectionFactordManager.run {
                    val abnormalStatusManager = AbnormalStatusManager()
                    val specialAbnormalStatusManager = abnormalStatusManager.createSpecialAbnormalStatusManager()
                    val target = usePlayerData.allEnemyMembers()

                    specialAbnormalStatusManager.run {
                        target.forEach {
                            it.addCloudy()
                        }
                    }
                }
                return@Card true
            },
            { usePlayerData ->
                cardManager.run {
                    val card = usePlayerData.graveyard.find { it.name == "비구름" }
                    usePlayerData.graveyard.remove(card)
                }
            }
        )
        //endregion

        //region lightningStrike Initialization
        val lightningStrike = Card(
            "낙뢰", listOf(
                KeywordType.Volatilization.component,
                Component.text("모든 적에게 ", TextColorType.Gray.textColor).append(KeywordType.Electroshock.component).append(Component.text("를 적용한다.", TextColorType.Gray.textColor)),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["전격"]!!
            ), CardRarity.Common, 1,
            { usePlayerData ->
                selectionFactordManager.run {
                    val abnormalStatusManager = AbnormalStatusManager()
                    val specialAbnormalStatusManager = abnormalStatusManager.createSpecialAbnormalStatusManager()
                    val target = usePlayerData.allEnemyMembers()

                    specialAbnormalStatusManager.run {
                        target.forEach {
                            it.addElectroshock()
                        }
                    }
                }
                return@Card true
            },
            { usePlayerData ->
                cardManager.run {
                    val card = usePlayerData.graveyard.find { it.name == "낙뢰" }
                    usePlayerData.graveyard.remove(card)
                }
            }
        )
        //endregion

        //region cloud Initialization
        val cloud = Card(
            "구름", listOf(
                Component.text("'비구름' 카드 1장을 생성하고, 패에 넣는다.", TextColorType.Gray.textColor),
                Component.text("[ 비구름 | (0) | 휘발 ]: 모든 적에게 흐림을 적용한다.", TextColorType.DarkGray.textColor),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["흐림"]!!
            ), CardRarity.Common, 1,
            { usePlayerData ->
                cardManager.run {
                    usePlayerData.getCard(rainCloud)
                    if (usePlayerData.player.scoreboardTags.contains("skyTag")) {
                        usePlayerData.getCard(lightningStrike)
                    }
                }
                return@Card true
            }
        )
        //endregion

        //region cloud Initialization
        val ground = Card(
            "대지", listOf(
                Component.text("모든 적에게 ", TextColorType.Gray.textColor),
                Component.text("[ 비구름 | (0) | 휘발 ]: 모든 적에게 흐림을 적용한다.", TextColorType.DarkGray.textColor),
                dictionary.dictionaryList["휘발"]!!,
                dictionary.dictionaryList["흐림"]!!
            ), CardRarity.Common, 1,
            { usePlayerData ->
                cardManager.run {
                    usePlayerData.getCard(rainCloud)
                    if (usePlayerData.player.scoreboardTags.contains("skyTag")) {
                        usePlayerData.getCard(lightningStrike)
                    }
                }
                return@Card true
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