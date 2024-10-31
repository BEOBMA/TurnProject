package org.beobma.projectturngame.listener

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityTurnStartEvent
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.damage
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.DamageType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnEntityTurnStartEvent : Listener {

    @EventHandler
    fun onEntityTurnStartEvent(event: EntityTurnStartEvent) {
        val entity = event.entity

        burnHandler(entity)
        protectHandler(entity)
    }

    private fun burnHandler(entity: Entity) {
        val burn = entity.abnormalityStatus.find { it.keywordType == KeywordType.Burn }

        if (burn !is AbnormalityStatus) return

        if (entity is Enemy) {
            entity.damage(burn.power, null, DamageType.AbnormalStatus)
        }

        if (entity is Player) {
            entity.damage(burn.power, null, DamageType.AbnormalStatus)
        }

        burn.power = (burn.power / 2).toInt()

        if (burn.power <= 1) {
            entity.abnormalityStatus.remove(burn)
        }
    }
    private fun protectHandler(entity: Entity) {
        val protect = entity.abnormalityStatus.find { it.keywordType == KeywordType.Protect }
        if (protect is AbnormalityStatus) {
            entity.abnormalityStatus.remove(protect)
        }
    }
}