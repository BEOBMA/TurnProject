package org.beobma.projectturngame.listener

import org.beobma.projectturngame.event.EntityCardThrowEvent
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.util.EffectTime
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnPlayerCardThrowEvent : Listener {

    @EventHandler
    fun onPlayerCardThrow(event: EntityCardThrowEvent) {
        val game = Info.game ?: return
        val player = event.player
        val card = event.card

        val continueEffects = game.continueEffects.filter { it.effectTime == EffectTime.CardThrow }

        continueEffects.forEach {
            it.effect.cardThrowEffect.invoke(player, card, event)
        }
    }
}