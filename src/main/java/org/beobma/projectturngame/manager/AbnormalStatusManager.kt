package org.beobma.projectturngame.manager

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.manager.PlayerManager.death
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.attribute.Attribute


interface HealthHandler {
    fun Player.addHealth(int: Int)
    fun Player.setHealth(int: Int)
}

class DefaultHealthHandler : HealthHandler {
    override fun Player.addHealth(int: Int) {
        if (this.health + int <= 0) {
            death()
            return
        }

        if (this.health + int > this.maxHealth) {
            this.health = this.maxHealth
            this.player.health = this.health.toDouble()
            return
        }

        this.player.health += int
        this.health += int
    }

    override fun Player.setHealth(int: Int) {
        if (int <= 0) {
            death()
            return
        }

        if (int > this.maxHealth) {
            this.health = this.maxHealth
            this.player.health = this.health.toDouble()
            return
        }

        this.player.health = int.toDouble()
        this.health = int
    }
}

object HealthManager {
    private val converter: HealthHandler = DefaultHealthHandler()

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
    override fun Player.addMaxHealth(int: Int) {
        if (this.maxMana + int <= 0) {
            death()
            return
        }

        this.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue += int
        this.maxHealth += int
    }

    override fun Player.setMaxHealth(int: Int) {
        if (int <= 0) {
            death()
            return
        }
        this.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = int.toDouble()
        this.maxHealth = int
    }
}

object MaxHealthManager {
    private val converter: MaxHealthHandler = DefaultMaxHealthHandler()

    fun Player.addMaxHealth(int: Int) {
        converter.run { this@addMaxHealth.addMaxHealth(int) }
    }

    fun Player.setMaxHealth(int: Int) {
        converter.run { this@setMaxHealth.setMaxHealth(int) }
    }
}

interface BurnHandler {
    fun Player.increaseBurn(int: Int, caster: Entity)
    fun Player.getBurn(): AbnormalityStatus?
    fun Player.setBurn(int: Int, caster: Entity)
    fun Player.decreaseBurn(int: Int, caster: Entity)
}

class DefaultBurnHandler : BurnHandler {
    override fun Player.increaseBurn(int: Int, caster: Entity) {
        val burn = this.getBurn()

        if (burn is AbnormalityStatus) {
            burn.caster.add(caster)
            burn.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Burn, mutableListOf(caster), int))
        }
    }

    override fun Player.getBurn(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Burn}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Player.setBurn(int: Int, caster: Entity) {
        val burn = this.getBurn()

        if (burn is AbnormalityStatus) {
            burn.caster.add(caster)
            burn.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Burn, mutableListOf(caster), int))
        }
    }

    override fun Player.decreaseBurn(int: Int, caster: Entity) {
        val burn = this.getBurn()

        if (burn is AbnormalityStatus) {
            burn.caster.add(caster)

            if (burn.power - int <= 0) {
                this.abnormalityStatus.remove(burn)
            }
            burn.power -= int
        }
    }
}

object BurnManager {
    private val converter: BurnHandler = DefaultBurnHandler()

    fun Player.increaseBurn(int: Int) {
        converter.run { increaseBurn(int) }
    }

    fun Player.getBurn(): AbnormalityStatus? {
        return converter.run { getBurn() }
    }

    fun Player.setBurn(int: Int) {
        return converter.run { setBurn(int) }
    }

    fun Player.decreaseBurn(int: Int){
        converter.run { decreaseBurn(int) }
    }
}