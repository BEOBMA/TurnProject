package org.beobma.projectturngame.listener

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.event.EntityTurnEndEvent
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnEntityTurnEndEvent : Listener {

    @EventHandler
    fun onEntityTurnEndEvent(event: EntityTurnEndEvent) {
        val entity = event.entity

        val weakness = entity.abnormalityStatus.find { it.keywordType == KeywordType.Weakness }
        if (weakness is AbnormalityStatus) {
            entity.abnormalityStatus.remove(weakness)
        }
    }
}