package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.graveyardReset
import org.beobma.projectturngame.manager.TextManager.cardNotAvailableText
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


// 파라미터 vararg -> List로 변경 필요
interface CardHandler {
    fun Player.use(card: Card)
    fun Player.drow(int: Int)
    fun Player.cardThrow(vararg card: Card)
    fun Player.getCard(vararg card: Card) // addCard로 변경 필요
    fun Player.addDeckCard(vararg card: Card)
    fun Player.clearHand()
    fun Player.clearGraveyard()
    fun Player.clearBanish()
    fun Player.clearDeck()
    fun Player.cardBanish(vararg card: Card)
    fun Player.extinction(card: Card)

    fun filterIsMoveCard(vararg card: Card): List<Card>
    fun findCard(name: String): Card?
    fun Card.toItem(): ItemStack
    fun ItemStack.toCard(): Card
    fun Player.applyHotbar()
}

class DefaultCardManager : CardHandler {
    override fun Player.use(card: Card) {
        // 사용 불가
        if (card.description.contains(KeywordType.NotAvailable.component)) {
            this.player.sendMessage(cardNotAvailableText())
            return
        }

        // 사용 시 효과가 없는 경우
        if (card.cardUseEffect == null) return
        val isUsing = card.cardUseEffect.invoke(this@use, card)

        // 카드 사용에 실패한 경우
        if (!isUsing) return

        // 카드 사용에 성공한 경우
        // 각종 키워드 효과를 이곳으로 이동 예정
        this.hand.remove(card)
        this@use.addMana(-card.cost)
        card.postCardUseEffect?.invoke(this@use, card)
        applyHotbar()
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

    override fun Player.cardThrow(vararg card: Card) {
        card.forEach {
            // 고정 효과가 적용된 경우
            if (it.description.contains(KeywordType.Fix.component)) {
                return@forEach
            }

            it.cardThrowEffect?.invoke(this, it)
            this.hand.remove(it)
        }
        applyHotbar()
    }

    override fun Player.getCard(vararg card: Card) {
        val game = Info.game ?: return

        card.forEach {
            if (this.hand.size >= 9) return

            this.hand.add(it)
            game.drowCardInt++

            this.applyHotbar()
        }
    }

    override fun Player.addDeckCard(vararg card: Card) {
        card.forEach {
            if (it.description.contains(KeywordType.Fix.component)) {
                return
            }
        }
        this.deck.addAll(card)
    }

    override fun Player.clearHand() {
        val cardList = filterIsMoveCard(*this.hand.toTypedArray())
        this.hand.removeAll(cardList)
        applyHotbar()
    }

    override fun Player.clearGraveyard() {
        val cardList = filterIsMoveCard(*this.graveyard.toTypedArray())
        this.graveyard.removeAll(cardList)
    }

    override fun Player.clearBanish() {
        val cardList = filterIsMoveCard(*this.banish.toTypedArray())
        this.banish.removeAll(cardList)
    }

    override fun Player.clearDeck() {
        val cardList = filterIsMoveCard(*this.deck.toTypedArray())
        this.deck.removeAll(cardList)
    }

    override fun Player.cardBanish(vararg card: Card) {
        card.forEach {
            if (!it.description.contains(KeywordType.Fix.component)) {
                this.hand.remove(it)
            }
        }
        applyHotbar()
    }

    override fun Player.extinction(card: Card) {
        this.graveyard.remove(card)
    }

    override fun filterIsMoveCard(vararg card: Card): List<Card> {
        val cardList = card.filter { !it.description.contains(KeywordType.Fix.component) }
        return cardList
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
                lore(this@toItem.description)
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }

        return cardItem
    }

    override fun ItemStack.toCard(): Card {
        val card = cardList.find { it.toItem().displayName() == this.displayName() && it.toItem().lore() == this.lore() }

        return if (card !is Card) {
            Card(
                "오류", listOf(
                    Component.text("이 카드는 존재할 수 없는 카드입니다.", TextColorType.Gray.textColor)
                ), CardRarity.Common, 999
            )
        } else {
            card
        }
    }

    override fun Player.applyHotbar() {
        this.player.inventory.clear()
        for (i in this.hand.indices) {
            val material = this.hand[i]
            player.inventory.setItem(i, material.toItem())
        }
    }
}

object CardManager {
    private val converter: CardHandler = DefaultCardManager()

    fun Player.use(card: Card) {
        converter.run { use(card) }
    }

    fun Player.drow(int: Int) {
        converter.run { drow(int) }
    }

    fun Player.getCard(vararg card: Card) {
        converter.run { getCard(*card) }
    }

    fun Card.toItem(): ItemStack {
        return converter.run { toItem() }
    }

    fun ItemStack.toCard(): Card {
        return converter.run { toCard() }
    }

    fun Player.cardThrow(vararg card: Card) {
        converter.run { cardThrow(*card) }
    }

    fun Player.applyHotbar() {
        return converter.run { applyHotbar() }
    }

    fun Player.addDeckCard(vararg card: Card) {
        converter.run { addDeckCard(*card) }
    }

    fun Player.clearHand() {
        converter.run { clearHand() }
    }

    fun Player.clearGraveyard() {
        converter.run { clearGraveyard() }
    }

    fun Player.clearBanish() {
        converter.run { clearBanish() }
    }

    fun Player.clearDeck() {
        converter.run { clearDeck() }
    }

    fun Player.extinction(card: Card) {
        converter.run { extinction(card) }
    }

    fun findCard(name: String): Card? {
        return converter.run { findCard(name) }
    }

    fun Player.cardBanish(vararg card: Card) {
        converter.run { cardBanish(*card) }
    }

    fun filterIsMoveCard(vararg card: Card): List<Card> {
        return converter.run { filterIsMoveCard(*card) }
    }
}