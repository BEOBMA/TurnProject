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

class SelectionAndFocusCardPack {
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
        val cardPack = CardPack("선택과 집중",
            listOf(
                Component.text("강력한 효과와 안정성, 둘 모두를 얻을 수는 없다.")
            ), mutableListOf()
        )

        //region sacrifice Common Initialization
        val sacrifice = Card(
            "희생", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                Component.text("이 카드가 버려지면 카드를 1장 뽑는다.", TextColorType.Gray.textColor),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!
            ), CardRarity.Common, 0, null, null,
            { usePlayerData, _ ->
                cardManager.run {
                    usePlayerData.drow(1)
                }
            }
        )
        //endregion

        //region equivalentExchange Common Initialization
        val equivalentExchange = Card(
            "등가교환", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                Component.text("이 카드가 버려지면 ", TextColorType.Gray.textColor).append(KeywordType.Mana.component.append(Component.text("를 1 회복한다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!
            ), CardRarity.Common, 0, null, null,
            { usePlayerData, _ ->
                playerManager.run {
                    usePlayerData.addMana(1)
                }
            }
        )
        //endregion

        //region protectiveSelling Common Initialization
        val protectiveSelling = Card(
            "보호적 매도", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                Component.text("이 카드가 버려지면 5의 피해를 막는 ", TextColorType.Gray.textColor).append(KeywordType.Shield.component.append(Component.text("을 얻는다.", TextColorType.Gray.textColor))),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["보호막"]!!
            ), CardRarity.Common, 0, null, null,
            { usePlayerData, _ ->
                playerManager.run {
                    usePlayerData.addShield(5)
                }
            }
        )
        //endregion



        cardPack.cardList.addAll(
            listOf(
                sacrifice,
                sacrifice,
                sacrifice,
                equivalentExchange,
                equivalentExchange,
                equivalentExchange,
                protectiveSelling,
                protectiveSelling,
                protectiveSelling,
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
    }
}