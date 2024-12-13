package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityCardThrowEvent
import org.beobma.projectturngame.event.EntityTurnStartEvent
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.GameManager.turnEnd
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.graveyardReset
import org.beobma.projectturngame.manager.TextManager.cardNotAvailableText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.CardPosition
import org.beobma.projectturngame.util.CardPosition.*
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


interface CardHandler {
    fun Player.use(card: Card)
    fun Player.drow(int: Int)
    fun Player.cardThrow(card: Card, cardPosition: CardPosition = Hand)
    fun Player.addCard(card: Card, cardPosition: CardPosition = Hand)
    fun Player.addDeckCard(card: Card)
    fun Player.clearHand()
    fun Player.clearGraveyard()
    fun Player.clearBanish()
    fun Player.clearDeck()
    fun Player.cardBanish(card: Card)
    fun Player.extinction(card: Card)

    fun findCard(name: String): Card?
    fun Card.toItem(): ItemStack
    fun ItemStack.toCard(): Card
    fun Player.applyHotbar()

    fun Card.isFix(): Boolean
}

object CardManager : CardHandler {
    override fun Player.use(card: Card) {
        // 사용 불가
        if (card.description.contains(KeywordType.NotAvailable.string)) {
            this.player.sendMessage(cardNotAvailableText())
            return
        }

        // 사용 시 효과가 없는 경우
        if (card.cardUseEffect == null) return
        val isUsing = card.cardUseEffect.invoke(this@use, card)

        val game = Info.game ?: return

        if (game.gameEnemys.isEmpty()) {
            return
        }

        // 카드 사용에 실패한 경우
        if (!isUsing) return

        this@use.addMana(-card.cost)

        // 사용 후 카드 처리
        this.useCardProcessing(card)

        card.postCardUseEffect?.invoke(this@use, card)
        applyHotbar()
    }

    private fun Player.useCardProcessing(card: Card) {
        // 잔존 카드 처리
        if (card.description.contains(KeywordType.Remnant.string)) {
            return
        }

        // 소멸 카드 처리
        if (card.description.contains(KeywordType.Extinction.string)) {
            this.hand.remove(card)
            this.banish.add(card)
            return
        }

        // 휘발 카드 처리
        if (card.description.contains(KeywordType.Volatilization.string)) {
            this.hand.remove(card)
            return
        }

        // 동일 카드 소멸 카드 처리
        if (card.description.contains(KeywordType.SameCardDisappears.string)) {
            val handCards = this.hand.filter { it.name == card.name }
            this.hand.removeAll(handCards)
            this.banish.addAll(handCards)

            val deckCards = this.deck.filter { it.name == card.name }
            this.deck.removeAll(deckCards)
            this.banish.addAll(deckCards)

            val graveyardCards = this.graveyard.filter { it.name == card.name }
            this.graveyard.removeAll(graveyardCards)
            this.banish.addAll(graveyardCards)
            return
        }


        // 일반 카드 효과 처리
        this.hand.remove(card)
        this.graveyard.add(card)
    }

    override fun Player.drow(int: Int) {
        val game = Info.game ?: return

        repeat(int) {
            if (this.hand.size >= 9) return

            val drawCard = deck.removeFirstOrNull() ?: run {
                graveyardReset()
                deck.removeFirstOrNull()
            } ?: return

            this.hand.add(drawCard)
            game.drowCardInt++

            this.applyHotbar()
        }
    }

    override fun Player.cardThrow(card: Card, cardPosition: CardPosition) {
        // 고정 효과가 적용된 경우
        if (card.description.contains(KeywordType.Fix.string)) {
            return
        }

        val event = EntityCardThrowEvent(this, card, cardPosition)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            return
        }

        player.sendMessage(MiniMessage.miniMessage().deserialize("<gray>${card.name} 카드를 버렸습니다."))
        card.cardThrowEffect?.invoke(this, card)

        when (cardPosition) {
            Hand -> {
                this.hand.remove(card)
                this.graveyard.add(card)
            }
            Deck -> {
                this.deck.remove(card)
                this.graveyard.add(card)
            }
            Graveyard -> {
                this.graveyard.remove(card)
                this.banish.add(card)
            }
            Banish -> {
                this.banish.remove(card)
            }
        }
        applyHotbar()
    }

    override fun Player.addCard(card: Card, cardPosition: CardPosition) {
        val game = Info.game ?: return

        if (cardPosition == Hand && this.hand.size >= 9) return

        when (cardPosition) {
            Hand -> this.hand.add(card)
            Deck -> this.deck.add(card)
            Graveyard -> this.graveyard.add(card)
            Banish -> this.banish.add(card)
        }

        game.drowCardInt++
        this.applyHotbar()
    }

    override fun Player.addDeckCard(card: Card) {
        if (card.description.contains(KeywordType.Fix.string)) {
            return
        }
        this.deck.add(card)
    }

    override fun Player.clearHand() {
        // 고정된 카드 제외
        val cardList = this.hand.filter { !it.description.contains(KeywordType.Fix.string) }

        this.hand.removeAll(cardList)
        applyHotbar()
    }

    override fun Player.clearGraveyard() {
        // 고정된 카드 제외
        val cardList = this.hand.filter { !it.description.contains(KeywordType.Fix.string) }

        this.graveyard.removeAll(cardList)
    }

    override fun Player.clearBanish() {
        // 고정된 카드 제외
        val cardList = this.hand.filter { !it.description.contains(KeywordType.Fix.string) }

        this.banish.removeAll(cardList)
    }

    override fun Player.clearDeck() {
        // 고정된 카드 제외
        val cardList = this.hand.filter { !it.description.contains(KeywordType.Fix.string) }

        this.deck.removeAll(cardList)
    }

    override fun Player.cardBanish(card: Card) {
        if (!card.description.contains(KeywordType.Fix.string)) {
            this.banish.add(card)
        }
    }

    override fun Player.extinction(card: Card) {
        this.graveyard.remove(card)
    }

    override fun findCard(name: String): Card? {
        return cardList.find { it.name == name }
    }

    override fun Card.toItem(): ItemStack {
        val displayName = when (rarity) {
            CardRarity.Common -> {
                Component.text(name, TextColorType.White.textColor).decorate(TextDecoration.BOLD)
            }

            CardRarity.Uncommon -> {
                Component.text(name, TextColorType.Blue.textColor).decorate(TextDecoration.BOLD)
            }

            CardRarity.Rare -> {
                Component.text(name, TextColorType.Green.textColor).decorate(TextDecoration.BOLD)
            }

            CardRarity.Legend -> {
                Component.text(name, TextColorType.Yellow.textColor).decorate(TextDecoration.BOLD)
            }
        }

        val cardItem = ItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(displayName.append(Component.text(" ($cost)")))
                lore(this@toItem.description.map { MiniMessage.miniMessage().deserialize(it) })
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }

        return cardItem
    }

    override fun ItemStack.toCard(): Card {
        val card = cardList.find { it.toItem() == this }

        return card
            ?: Card(
                "오류", listOf(
                    "<gray>이 카드는 존재할 수 없는 카드입니다."
                ), CardRarity.Common, 999
            )
    }

    override fun Player.applyHotbar() {
        this.player.inventory.clear()
        for (i in this.hand.indices) {
            val material = this.hand[i]
            player.inventory.setItem(i, material.toItem())
        }
    }

    override fun Card.isFix(): Boolean {
        return this.description.contains(KeywordType.Fix.string)
    }
}