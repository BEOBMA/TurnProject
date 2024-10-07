package org.beobma.projectturngame.gameevent

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.entity.player.Player
import org.bukkit.Material

class EventOption(
    val name: String,
    val description: List<Component>,
    val itemMaterial: Material,
    val optionChoiceEffect: ((Player) -> Unit)?
)