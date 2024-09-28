package org.beobma.projectturngame.event

import org.beobma.projectturngame.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EntityTurnEndEvent(val entity: Entity) : Event() {

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