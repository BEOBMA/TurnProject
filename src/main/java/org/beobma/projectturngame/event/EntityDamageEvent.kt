package org.beobma.projectturngame.event

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.util.DamageType
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class EntityDamageEvent(var damage: Int, val damageType: DamageType, val entity: Entity, val attacker: Entity) : Event(), Cancellable {
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