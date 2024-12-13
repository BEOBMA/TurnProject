package org.beobma.projectturngame.entity.enemy

import org.beobma.projectturngame.entity.Entity
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity

class Enemy(
    val enemyName: String,
    val entity: LivingEntity,
    val actionList: List<EnemyAction>
) : Entity(enemyName, (entity.getAttribute(Attribute.MAX_HEALTH)!!.baseValue).toInt(),(entity.health).toInt(), 1)