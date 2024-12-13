package org.beobma.projectturngame.listener

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.event.EntityDeathEvent
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnEntityDeathEvent : Listener {

    @EventHandler
    fun onEntityDeathEvent(event: EntityDeathEvent) {
        val entity = event.entity
        val attacker = event.attacker
        val damageType = event.damageType

        deathResistanceHandler(entity, event)
    }

    private fun deathResistanceHandler(entity: Entity, event: EntityDeathEvent) {
        val deathResistance = entity.abnormalityStatus.find { it.keywordType == KeywordType.DeathResistance }
        if (deathResistance is AbnormalityStatus) {
            event.isCancelled = true
            deathResistance.power -= 1

            if (deathResistance.power <= 0) {
                entity.abnormalityStatus.remove(deathResistance)
            }
        }
    }
}