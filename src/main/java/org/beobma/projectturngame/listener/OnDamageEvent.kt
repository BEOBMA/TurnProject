package org.beobma.projectturngame.listener

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityDamageEvent
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.EffectTime
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

        if (attacker is Entity) {
            weaknessHandler(attacker, event)
            blindnessHandler(attacker, event)

            if (attacker is Player) {
                val attackerRelicsList = attacker.relics.filter { it.effectTime == EffectTime.OnHit }

                if (attackerRelicsList.isNotEmpty()) {
                    attackerRelicsList.forEach {
                        val finalDamage = it.effect?.invoke(attacker, listOf(entity, damage, damageType))

                        if (finalDamage is Int) {
                            event.damage += finalDamage
                        }
                    }
                }
            }
            if (entity is Player) {
                val entityRelicsList = entity.relics.filter { it.effectTime == EffectTime.WhenHit }

                if (entityRelicsList.isNotEmpty()) {
                    entityRelicsList.forEach {
                        val finalDamage = it.effect?.invoke(entity, listOf(attacker, damage, damageType))

                        if (finalDamage is Int) {
                            event.damage += finalDamage
                        }
                    }
                }
            }
        }
        protectHandler(entity, event)
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
    private fun protectHandler(entity: Entity, event: EntityDamageEvent) {
        val protect = entity.abnormalityStatus.find { it.keywordType == KeywordType.Protect }

        if (protect !is AbnormalityStatus) return

        if (event.damageType != DamageType.Normal) return

        event.damage -= protect.power
    }
}