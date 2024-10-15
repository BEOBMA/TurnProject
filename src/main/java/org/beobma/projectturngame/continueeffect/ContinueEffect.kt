package org.beobma.projectturngame.continueeffect

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.util.EffectTime
import org.beobma.projectturngame.util.ResetType

class ContinueEffect(
    val player: Entity,
    val effectTime: EffectTime,
    val effect: (Entity) -> Unit,
    val endTime: ResetType = ResetType.BattleEnd
)