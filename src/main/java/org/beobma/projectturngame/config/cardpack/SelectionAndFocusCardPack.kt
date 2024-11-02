package org.beobma.projectturngame.config.cardpack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.cardThrow
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.addShield
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.SelectionFactordManager.allEnemyMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.allTeamMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.DamageType
import org.bukkit.Particle
import org.bukkit.Sound

class SelectionAndFocusCardPack {
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
                MiniMessage.miniMessage().deserialize("<gray>이 카드가 버려지면 카드를 1장 뽑는다."),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!
            ), CardRarity.Common, 0, null, null,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                player.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                usePlayerData.drow(1)
            }
        )
        //endregion

        //region equivalentExchange Common Initialization
        val equivalentExchange = Card(
            "등가교환", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gray>이 카드가 버려지면 <blue><bold>마나</bold><gray>를 1 회복한다."),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!
            ), CardRarity.Common, 0, null, null,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                player.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                usePlayerData.addMana(1)
            }
        )
        //endregion

        //region protectiveSelling Common Initialization
        val protectiveSelling = Card(
            "보호적 매도", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gray>이 카드가 버려지면 5의 피해를 막는 <aqua><bold>보호막</bold><gray>을 얻는다."),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["보호막"]!!
            ), CardRarity.Common, 0, null, null,
            { usePlayerData, _ ->
                usePlayerData.addShield(5)
            }
        )
        //endregion

        //region sacrificialChoice Uncommon Initialization
        val sacrificialChoice = Card(
            "희생적 선택", listOf(
                MiniMessage.miniMessage().deserialize("<gray>패에서 '희생적 선택'을 제외한 무작위 카드 1장을 버리고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적에게 20의 피해를 입힌다."),
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<dark_gray>단, 버려졌을 때 효과가 있는 카드를 우선하여 버린다.")
            ), CardRarity.Uncommon, 1, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val cardList = usePlayerData.hand.filter { it.name != "희생적 선택" }

                if (cardList.isEmpty()) {
                    player.sendMessage(cardUseFailText())
                    return@Card false
                }

                val cardToDiscard = cardList.firstOrNull { it.cardThrowEffect != null } ?: cardList.random()


                usePlayerData.cardThrow(cardToDiscard)
                target.damage(20, usePlayerData)

                return@Card true
            }
        )
        //endregion

        //region totalLoss Uncommon Initialization
        val totalLoss = Card(
            "총체적 손실", listOf(
                MiniMessage.miniMessage().deserialize("<gray>패에서 '총체적 손실'을 제외한 무작위 카드 3장을 버리고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>모든 적에게 10의 피해를 입힌다.")
            ), CardRarity.Uncommon, 1, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.allEnemyMembers()

                val cardList = usePlayerData.hand.filter { it.name != "총체적 손실" }

                if (cardList.size < 3) {
                    player.sendMessage(cardUseFailText())
                    return@Card false
                }

                val finalCardList = cardList.shuffled()
                usePlayerData.cardThrow(finalCardList[0])
                usePlayerData.cardThrow(finalCardList[1])
                usePlayerData.cardThrow(finalCardList[2])

                target.forEach {
                    it.damage(10, usePlayerData)
                }
                return@Card true
            }
        )
        //endregion

        //region liquidationOfTotalAssets Uncommon Initialization
        val liquidationOfTotalAssets = Card(
            "총자산 청산", listOf(
                MiniMessage.miniMessage().deserialize("<gray>패의 모든 카드를 버리고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적에게 40의 피해를 입힌다.")
            ), CardRarity.Uncommon, 2, { usePlayerData, card ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                val cardList = usePlayerData.hand.filter { it !== card }

                cardList.forEach {
                    usePlayerData.cardThrow(it)
                }
                target.damage(40, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region coerciveBurden Rare Initialization
        val coerciveBurden = Card(
            "강제적 부담", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gray>이 카드가 버려지면 모든 적은 3의 <white><bold>고정피해</bold><gray>를 입는다."),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["고정 피해"]!!,
            ), CardRarity.Rare, 0, null, null,
            { usePlayerData, _ ->
                val enemys = usePlayerData.allEnemyMembers()

                enemys.forEach {
                    it.damage(3, usePlayerData, DamageType.True)
                }
            }
        )
        //endregion

        //region welfareBenefits Rare Initialization
        val welfareBenefits = Card(
            "복지 혜택", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gray>이 카드가 버려지면 모든 아군은 체력을 3 회복한다."),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!
            ), CardRarity.Rare, 0, null, null,
            { usePlayerData, _ ->
                val targets = usePlayerData.allTeamMembers(excludeSelf = true, includeDeceased = false)

                targets.forEach {
                    it.heal(3, usePlayerData)
                }
            }
        )
        //endregion

        //region safetyGuaranteed Rare Initialization
        val safetyGuaranteed = Card(
            "안전 보장", listOf(
                KeywordType.NotAvailable.component,
                Component.text(""),
                MiniMessage.miniMessage().deserialize("<gray>이 카드가 버려지면 모든 아군은 3의 피해를 막는 <aqua><bold>보호막</bold><gray>을 얻는다."),
                Component.text(""),
                dictionary.dictionaryList["사용 불가"]!!,
                dictionary.dictionaryList["보호막"]!!,
            ), CardRarity.Rare, 0, null, null,
            { usePlayerData, _ ->
                val targets = usePlayerData.allTeamMembers(excludeSelf = true, includeDeceased = false)
                targets.forEach {
                    it.addShield(3)
                }
            }
        )
        //endregion

        //region diversifiedInvestment Legend Initialization
        val diversifiedInvestment = Card(
            "분산 투자", listOf(
                MiniMessage.miniMessage().deserialize("<gray>패의 모든 카드를 버리고 발동할 수 있다."),
                MiniMessage.miniMessage().deserialize("<gray>모든 적에게 (버린 카드의 수 x 10)의 피해를 나누어 입힌다.")
            ), CardRarity.Legend, 0,
            { usePlayerData, card ->
                val target = usePlayerData.allEnemyMembers()
                val cardList = usePlayerData.hand.filter { it !== card }

                cardList.forEach {
                    usePlayerData.cardThrow(it)
                }


                target.forEach {
                    it.damage(((cardList.size * 10) / target.size), usePlayerData)
                }
                return@Card true
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
                sacrificialChoice,
                sacrificialChoice,
                sacrificialChoice,
                totalLoss,
                totalLoss,
                totalLoss,
                liquidationOfTotalAssets,
                liquidationOfTotalAssets,
                liquidationOfTotalAssets,
                coerciveBurden,
                coerciveBurden,
                coerciveBurden,
                welfareBenefits,
                welfareBenefits,
                welfareBenefits,
                safetyGuaranteed,
                safetyGuaranteed,
                safetyGuaranteed,
                diversifiedInvestment
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
    }
}