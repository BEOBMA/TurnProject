package org.beobma.projectturngame.entity.enemy

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.game.GameDifficulty
import org.beobma.projectturngame.util.ActionType

class EnemyAction(
    val actionName: String,
    val actionDescription: List<String>,
    val actionType: ActionType,
    val actionCondition: (Enemy) -> Boolean,
    val action: (Enemy) -> Unit,
    val difficulty: GameDifficulty
)