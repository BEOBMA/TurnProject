package org.beobma.projectturngame.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OnEntityDamageByEntityEvent : Listener {

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            event.isCancelled = true
        }
    }
}