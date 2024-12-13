package org.beobma.projectturngame.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.CardManager.toCard
import org.beobma.projectturngame.manager.CardManager.use
import org.beobma.projectturngame.manager.ParticleAnimationManager.isPlay
import org.beobma.projectturngame.manager.PlayerManager.isTurn
import org.beobma.projectturngame.manager.TextManager.manaFailText
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent

class OnPlayerInteractEvent : Listener {

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == player } ?: return

        if (player !in game.players || !playerData.isTurn() || !event.action.name.contains("RIGHT")) return
        if (isPlay) {
            player.sendMessage(Component.text("[!] 카드 사용 연출이 재생중이므로 일시적으로 행동을 수행할 수 없습니다.", TextColorType.Red.textColor)
                .decorate(TextDecoration.BOLD))
            event.isCancelled = true
            return
        }

        val card = playerData.hand.getOrNull(player.inventory.heldItemSlot) ?: return

        if (card.cost > playerData.mana) {
            player.sendMessage(manaFailText())
            return
        }
        playerData.use(card)
    }
}