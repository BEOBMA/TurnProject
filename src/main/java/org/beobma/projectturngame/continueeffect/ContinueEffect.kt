package org.beobma.projectturngame.continueeffect

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.event.EntityCardThrowEvent
import org.beobma.projectturngame.util.EffectTime
import org.beobma.projectturngame.util.ResetType

interface ContinueEffectHandler {
    val normalEffect: ((Entity) -> Unit)
    val cardThrowEffect: ((Entity, Card, EntityCardThrowEvent) -> Unit)
}

class ContinueEffect(
    val player: Entity,
    val effectTime: EffectTime,
    val effect: ContinueEffectHandler,
    val endTime: ResetType = ResetType.BattleEnd
)