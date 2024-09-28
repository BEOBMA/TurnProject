package org.beobma.projectturngame.entity

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus

open class Entity(
    val name: String,
    var maxHealth: Int,
    var health: Int,
    val speed: Int,
    var isDead: Boolean = false,
    val abnormalityStatus: MutableList<AbnormalityStatus> = mutableListOf(),
    var shield: Int = 0
)