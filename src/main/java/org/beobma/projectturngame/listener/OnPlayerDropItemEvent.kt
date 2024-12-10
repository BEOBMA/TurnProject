package org.beobma.projectturngame.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.config.CardConfig.Companion.reforgeCardPair
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.CardManager.applyHotbar
import org.beobma.projectturngame.manager.CardManager.toItem
import org.beobma.projectturngame.manager.CustomStackManager.increaseStack
import org.beobma.projectturngame.manager.ParticleAnimationManager.isPlay
import org.beobma.projectturngame.manager.PlayerManager.isTurn
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

class OnPlayerDropItemEvent : Listener {

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        val item = event.itemDrop
        val player = event.player
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == player } ?: return
        val card = playerData.hand.find { it.toItem() == item.itemStack } ?: return
        val description = card.description

        if (!playerData.isTurn()) {
            event.isCancelled = true
            return
        }

        if (isPlay) {
            player.sendMessage(Component.text("[!] 카드 사용 연출이 재생중이므로 일시적으로 행동을 수행할 수 없습니다.", TextColorType.Red.textColor)
                .decorate(TextDecoration.BOLD))
            event.isCancelled = true
            return
        }
        // 연금술
        if (description.contains(KeywordType.AlchemYingredients.string)) {
            playerData.alchemYingredientsPile.add(card)
            playerData.hand.remove(card)
            player.inventory.remove(item.itemStack)
            playerData.applyHotbar()
            return
        }

        // 재련
        if (description.contains(KeywordType.Reforge.string)) {
            if (playerData.mana < 1) {
                event.isCancelled = true
                return
            }

            playerData.mana--
            val reforgedCard = reforgeCardPair[card]
            if (reforgedCard == null) {
                event.isCancelled = true
                return
            }

            fun MutableList<Card>.replaceCard(targetCard: Card, newCard: Card) {
                forEachIndexed { index, currentCard ->
                    if (currentCard.description == targetCard.description) {
                        this[index] = newCard
                    }
                }
            }

            playerData.hand = playerData.hand.toMutableList().apply { replaceCard(card, reforgedCard) }
            playerData.deck = playerData.deck.toMutableList().apply { replaceCard(card, reforgedCard) }
            playerData.graveyard = playerData.graveyard.toMutableList().apply { replaceCard(card, reforgedCard) }
            playerData.banish = playerData.banish.toMutableList().apply { replaceCard(card, reforgedCard) }

            playerData.increaseStack("ReforgeStack", 1)
            playerData.applyHotbar()
            return
        }

        event.isCancelled = true
    }
}