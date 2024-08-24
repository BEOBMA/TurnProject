package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.bukkit.attribute.Attribute


interface HealthHandler {
    fun Player.addHealth(int: Int)
    fun Player.setHealth(int: Int)
}

class DefaultHealthHandler : HealthHandler {
    private val playerManager = PlayerManager(DefaultPlayerManager())

    override fun Player.addHealth(int: Int) {
        if (this.maxMana + int <= 0) {
            playerManager.run { death() }
        }
        this.player.health += int
        this.health += int
    }

    override fun Player.setHealth(int: Int) {
        if (int <= 0) {
            playerManager.run { death() }
        }
        this.player.health = int.toDouble()
        this.health = int
    }
}

class HealthManager(private val converter: HealthHandler) {
    fun Player.addHealth(int: Int) {
        converter.run { addHealth(int) }
    }
    fun Player.setHealth(int: Int) {
        converter.run { setHealth(int) }
    }
}



interface MaxHealthHandler {
    fun Player.addMaxHealth(int: Int)
    fun Player.setMaxHealth(int: Int)
}

class DefaultMaxHealthHandler : MaxHealthHandler {
    private val playerManager = PlayerManager(DefaultPlayerManager())

    override fun Player.addMaxHealth(int: Int) {
        if (this.maxMana + int <= 0) {
            playerManager.run { this@addMaxHealth.death() }
        }
        this.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += int
        this.maxHealth += int
    }

    override fun Player.setMaxHealth(int: Int) {
        if (int <= 0) {
            playerManager.run { this@setMaxHealth.death() }
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



interface SpecialAbnormalStatusHandler {
    fun Entity.addCloudy()
    fun Entity.isCloudy(): Boolean
    fun Entity.removeCloudy()

    fun Entity.addElectroshock()
    fun Entity.isElectroshock(): Boolean
    fun Entity.removeElectroshock()
}

class DefaultSpecialAbnormalStatusHandler : SpecialAbnormalStatusHandler {
    private val enemyManager = EnemyManager(DefaultEnemyManager())
    private val playerManager = PlayerManager(DefaultPlayerManager())

    override fun Entity.addCloudy() {
        if (this.isElectroshock()) {
            if (this is Enemy) {
                enemyManager.run {
                    damage(18, null, true)
                }
                this.removeElectroshock()
                return
            }

            if (this is Player) {
                playerManager.run {
                    damage(18, null, true)
                }
                this.removeElectroshock()
                return
            }
        }

        this.abnormalityStatus["Cloudy"] = 1
    }

    override fun Entity.isCloudy(): Boolean {
        return this.abnormalityStatus.containsKey("Cloudy")
    }

    override fun Entity.removeCloudy() {
        this.abnormalityStatus.remove("Cloudy")
    }


    override fun Entity.addElectroshock() {
        if (this.isCloudy()) {
            if (this is Enemy) {
                enemyManager.run {
                    damage(18, null, true)
                }
                this.removeCloudy()
                return
            }

            if (this is Player) {
                playerManager.run {
                    damage(18, null, true)
                }
                this.removeCloudy()
                return
            }
        }
        this.abnormalityStatus["Electroshock"] = 1
    }

    override fun Entity.isElectroshock(): Boolean {
        return this.abnormalityStatus.containsKey("Electroshock")
    }

    override fun Entity.removeElectroshock() {
        this.abnormalityStatus.remove("Electroshock")
    }
}

class SpecialAbnormalStatusManager(private val converter: SpecialAbnormalStatusHandler) {
    fun Entity.addCloudy() {
        converter.run { addCloudy() }
    }

    fun Entity.isCloudy(): Boolean {
        return converter.run { isCloudy() }
    }

    fun Entity.removeCloudy() {
        converter.run { removeCloudy() }
    }


    fun Entity.addElectroshock() {
        converter.run { this@addElectroshock.addElectroshock() }
    }

    fun Entity.isElectroshock(): Boolean {
        return converter.run { this@isElectroshock.isElectroshock() }
    }

    fun Entity.removeElectroshock() {
        converter.run { this@removeElectroshock.removeElectroshock() }
    }
}


class AbnormalStatusManager {
    fun createMaxHealthManager(): MaxHealthManager {
        return MaxHealthManager(DefaultMaxHealthHandler())
    }

    fun createHealthManager(): HealthManager {
        return HealthManager(DefaultHealthHandler())
    }

    fun createSpecialAbnormalStatusManager(): SpecialAbnormalStatusManager {
        return SpecialAbnormalStatusManager(DefaultSpecialAbnormalStatusHandler())
    }
}