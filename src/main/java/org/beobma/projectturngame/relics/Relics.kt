package org.beobma.projectturngame.relics

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.util.EffectTime

class Relics(
    val name: String,
    val description: List<Component>,
    val effectTime: EffectTime,
    val effect: ((Player, List<Any>) -> Any)?
)