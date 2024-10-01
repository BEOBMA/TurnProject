package org.beobma.projectturngame.manager

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
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
    fun Entity.increaseBurn(int: Int, caster: Entity)
    fun Entity.getBurn(): AbnormalityStatus?
    fun Entity.setBurn(int: Int, caster: Entity)
    fun Entity.decreaseBurn(int: Int, caster: Entity)
}

class DefaultBurnHandler : BurnHandler {
    override fun Entity.increaseBurn(int: Int, caster: Entity) {
        val burn = this.getBurn()

        if (burn is AbnormalityStatus) {
            burn.caster.add(caster)
            burn.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Burn, mutableListOf(caster), int))
        }
    }

    override fun Entity.getBurn(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Burn}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setBurn(int: Int, caster: Entity) {
        val burn = this.getBurn()

        if (burn is AbnormalityStatus) {
            burn.caster.add(caster)
            burn.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Burn, mutableListOf(caster), int))
        }
    }

    override fun Entity.decreaseBurn(int: Int, caster: Entity) {
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

    fun Entity.increaseBurn(int: Int, caster: Entity) {
        converter.run { increaseBurn(int, caster) }
    }

    fun Entity.getBurn(): AbnormalityStatus? {
        return converter.run { getBurn() }
    }

    fun Entity.setBurn(int: Int, caster: Entity) {
        return converter.run { setBurn(int, caster) }
    }

    fun Entity.decreaseBurn(int: Int, caster: Entity){
        converter.run { decreaseBurn(int, caster) }
    }
}


interface WeaknessHandler {
    fun Entity.increaseWeakness(int: Int, caster: Entity)
    fun Entity.getWeakness(): AbnormalityStatus?
    fun Entity.setWeakness(int: Int, caster: Entity)
    fun Entity.decreaseWeakness(int: Int, caster: Entity)
}

class DefaultWeaknessHandler : WeaknessHandler {
    override fun Entity.increaseWeakness(int: Int, caster: Entity) {
        val weakness = this.getWeakness()

        if (weakness is AbnormalityStatus) {
            weakness.caster.add(caster)
            weakness.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Weakness, mutableListOf(caster), int))
        }
    }

    override fun Entity.getWeakness(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Weakness}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setWeakness(int: Int, caster: Entity) {
        val weakness = this.getWeakness()

        if (weakness is AbnormalityStatus) {
            weakness.caster.add(caster)
            weakness.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Weakness, mutableListOf(caster), int))
        }
    }

    override fun Entity.decreaseWeakness(int: Int, caster: Entity) {
        val weakness = this.getWeakness()

        if (weakness is AbnormalityStatus) {
            weakness.caster.add(caster)

            if (weakness.power - int <= 0) {
                this.abnormalityStatus.remove(weakness)
            }
            weakness.power -= int
        }
    }
}

object WeaknessManager {
    private val converter: WeaknessHandler = DefaultWeaknessHandler()

    fun Entity.increaseWeakness(int: Int, caster: Entity) {
        converter.run { increaseWeakness(int, caster) }
    }

    fun Entity.getWeakness(): AbnormalityStatus? {
        return converter.run { getWeakness() }
    }

    fun Entity.setWeakness(int: Int, caster: Entity) {
        return converter.run { setWeakness(int, caster) }
    }

    fun Entity.decreaseWeakness(int: Int, caster: Entity){
        converter.run { decreaseWeakness(int, caster) }
    }
}


interface SlownessHandler {
    fun Entity.increaseSlowness(int: Int, caster: Entity)
    fun Entity.getSlowness(): AbnormalityStatus?
    fun Entity.setSlowness(int: Int, caster: Entity)
    fun Entity.decreaseSlowness(int: Int, caster: Entity)
}

class DefaultSlownessHandler : SlownessHandler {
    override fun Entity.increaseSlowness(int: Int, caster: Entity) {
        val slowness = this.getSlowness()

        if (slowness is AbnormalityStatus) {
            slowness.caster.add(caster)
            slowness.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Slowness, mutableListOf(caster), int))
        }
        turnOtherReload()
    }

    override fun Entity.getSlowness(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Slowness}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setSlowness(int: Int, caster: Entity) {
        val slowness = this.getSlowness()

        if (slowness is AbnormalityStatus) {
            slowness.caster.add(caster)
            slowness.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Blindness, mutableListOf(caster), int))
        }
        turnOtherReload()
    }

    override fun Entity.decreaseSlowness(int: Int, caster: Entity) {
        val slowness = this.getSlowness()

        if (slowness is AbnormalityStatus) {
            slowness.caster.add(caster)

            if (slowness.power - int <= 0) {
                this.abnormalityStatus.remove(slowness)
            }
            slowness.power -= int
        }
        turnOtherReload()
    }


    private fun turnOtherReload() {
        val game = Info.game ?: return

        game.gameTurnOrder.sortByDescending {
            val slowness = it.abnormalityStatus.find { it.keywordType == KeywordType.Slowness }
            if (slowness is AbnormalityStatus ) {
                it.speed - slowness.power
            }
            else {
                it.speed
            }
        }
    }
}

object SlownessManager {
    private val converter: SlownessHandler = DefaultSlownessHandler()

    fun Entity.increaseSlowness(int: Int, caster: Entity) {
        converter.run { increaseSlowness(int, caster) }
    }

    fun Entity.getSlowness(): AbnormalityStatus? {
        return converter.run { getSlowness() }
    }

    fun Entity.setSlowness(int: Int, caster: Entity) {
        return converter.run { setSlowness(int, caster) }
    }

    fun Entity.decreaseSlowness(int: Int, caster: Entity){
        converter.run { decreaseSlowness(int, caster) }
    }
}

interface BlindnessHandler {
    fun Entity.increaseBlindness(int: Int, caster: Entity)
    fun Entity.getBlindness(): AbnormalityStatus?
    fun Entity.setBlindness(int: Int, caster: Entity)
    fun Entity.decreaseBlindness(int: Int, caster: Entity)
}

class DefaultBlindnessHandler : BlindnessHandler {
    override fun Entity.increaseBlindness(int: Int, caster: Entity) {
        val blindness = this.getBlindness()

        if (blindness is AbnormalityStatus) {
            blindness.caster.add(caster)
            blindness.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Blindness, mutableListOf(caster), int))
        }
    }

    override fun Entity.getBlindness(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Blindness}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setBlindness(int: Int, caster: Entity) {
        val blindness = this.getBlindness()

        if (blindness is AbnormalityStatus) {
            blindness.caster.add(caster)
            blindness.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Blindness, mutableListOf(caster), int))
        }
    }

    override fun Entity.decreaseBlindness(int: Int, caster: Entity) {
        val blindness = this.getBlindness()

        if (blindness is AbnormalityStatus) {
            blindness.caster.add(caster)

            if (blindness.power - int <= 0) {
                this.abnormalityStatus.remove(blindness)
            }
            blindness.power -= int
        }
    }
}

object BlindnessManager {
    private val converter: BlindnessHandler = DefaultBlindnessHandler()

    fun Entity.increaseBlindness(int: Int, caster: Entity) {
        converter.run { increaseBlindness(int, caster) }
    }

    fun Entity.getBlindness(): AbnormalityStatus? {
        return converter.run { getBlindness() }
    }

    fun Entity.setBlindness(int: Int, caster: Entity) {
        return converter.run { setBlindness(int, caster) }
    }

    fun Entity.decreaseBlindness(int: Int, caster: Entity){
        converter.run { decreaseBlindness(int, caster) }
    }
}