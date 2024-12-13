package org.beobma.projectturngame.event

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.util.CardPosition
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EntityCardThrowEvent(val player: Player, val card: Card, val cardPosition: CardPosition) : Event(), Cancellable {
    private var isCancelled = false

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }

    companion object {
        private val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }

    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
}