package org.beobma.projectturngame.entity

open class Entity(
    val name: String,
    var maxHealth: Int,
    var health: Int,
    val speed: Int,
    var isDead: Boolean = false
)