package org.beobma.projectturngame.listener

import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class OnEntityDamageByEntityEvent : Listener {

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (event.damager is Player && entity is Player) {
            event.isCancelled = true
        }
    }
}