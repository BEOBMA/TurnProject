package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.player.Player
import org.bukkit.attribute.Attribute


interface HealthHandler {
    fun Player.addHealth(int: Int)
    fun Player.setHealth(int: Int)
}

class DefaultHealthHandler : HealthHandler {
    private val deathManager = AbnormalStatusManager().createDeathManager()

    override fun Player.addHealth(int: Int) {
        if (this.maxMana + int <= 0) {
            deathManager.run { this@addHealth.death() }
        }
        this.player.health += int
        this.health += int
    }

    override fun Player.setHealth(int: Int) {
        if (int <= 0) {
            deathManager.run { this@setHealth.death() }
        }
        this.player.health = int.toDouble()
        this.health = int
    }
}

class HealthManager(private val converter: HealthHandler) {
    fun Player.addHealth(int: Int) {
        converter.run { this@addHealth.addHealth(int) }
    }
    fun Player.setHealth(int: Int) {
        converter.run { this@setHealth.setHealth(int) }
    }
}



interface MaxHealthHandler {
    fun Player.addMaxHealth(int: Int)
    fun Player.setMaxHealth(int: Int)
}

class DefaultMaxHealthHandler : MaxHealthHandler {
    private val deathManager = AbnormalStatusManager().createDeathManager()

    override fun Player.addMaxHealth(int: Int) {
        if (this.maxMana + int <= 0) {
            deathManager.run { this@addMaxHealth.death() }
        }
        this.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += int
        this.maxHealth += int
    }

    override fun Player.setMaxHealth(int: Int) {
        if (int <= 0) {
            deathManager.run { this@setMaxHealth.death() }
        }
        this.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = int.toDouble()
        this.maxHealth = int
    }
}

class MaxHealthManager(private val converter: MaxHealthHandler) {
    fun Player.addMaxHealth(int: Int) {
        converter.run { this@addMaxHealth.addMaxHealth(int) }
    }
    fun Player.setMaxHealth(int: Int) {
        converter.run { this@setMaxHealth.setMaxHealth(int) }
    }
}



interface DeathHandler {
    fun Entity.death()
    fun Entity.resurrection()
}

class DefaultDeathHandler : DeathHandler {
    override fun Entity.death() {
        TODO("Not yet implemented")
    }

    override fun Entity.resurrection() {
        TODO("Not yet implemented")
    }

}

class DeathManager(private val converter: DeathHandler) {
    fun Entity.death() {
        converter.run { this@death.death() }
    }
    fun Entity.resurrection() {
        converter.run { this@resurrection.resurrection() }
    }
}

class AbnormalStatusManager {
    fun createMaxHealthManager(): MaxHealthManager {
        return MaxHealthManager(DefaultMaxHealthHandler())
    }

    fun createHealthManager(): HealthManager {
        return HealthManager(DefaultHealthHandler())
    }

    fun createDeathManager(): DeathManager {
        return DeathManager((DefaultDeathHandler()))
    }
}