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

        val burn = entity.abnormalityStatus.find { it.keywordType == KeywordType.Burn }
        if (burn is AbnormalityStatus) {
            entity.abnormalityStatusBurnHandler(burn)
        }
    }

    // 화상 피해 계산
    private fun Entity.abnormalityStatusBurnHandler(abnormalityStatus: AbnormalityStatus) {

        if (this is Enemy) {
            this.damage(abnormalityStatus.power, null, DamageType.AbnormalStatus)
        }

        if (this is Player) {
            this.damage(abnormalityStatus.power, null, DamageType.AbnormalStatus)
        }

        abnormalityStatus.power /= 2
    }
}