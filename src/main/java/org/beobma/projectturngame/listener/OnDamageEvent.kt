package org.beobma.projectturngame.listener

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.event.EntityDamageEvent
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.random.Random

class OnDamageEvent : Listener {

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val damage = event.damage
        val damageType = event.damageType
        val entity = event.entity
        val attacker = event.attacker

        weaknessHandler(attacker, event)
        blindnessHandler(attacker, event)

    }

    private fun weaknessHandler(attacker: Entity, event: EntityDamageEvent) {
        val weakness = attacker.abnormalityStatus.find { it.keywordType == KeywordType.Weakness }

        if (weakness !is AbnormalityStatus) return

        event.damage -= weakness.power
    }
    private fun blindnessHandler(attacker: Entity, event: EntityDamageEvent) {
        val blindness = attacker.abnormalityStatus.find { it.keywordType == KeywordType.Blindness }

        if (blindness !is AbnormalityStatus) return

        if (Random.nextInt(100) < blindness.power * 5) {
            event.isCancelled = true
            attacker.abnormalityStatus.remove(blindness)
        }
    }
}