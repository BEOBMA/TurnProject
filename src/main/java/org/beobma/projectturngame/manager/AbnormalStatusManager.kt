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
object HealthManager : HealthHandler {
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


interface MaxHealthHandler {
    fun Player.addMaxHealth(int: Int)
    fun Player.setMaxHealth(int: Int)
}
object MaxHealthManager: MaxHealthHandler {
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


interface BurnHandler {
    fun Entity.increaseBurn(int: Int, caster: Entity)
    fun Entity.getBurn(): AbnormalityStatus?
    fun Entity.setBurn(int: Int, caster: Entity)
    fun Entity.decreaseBurn(int: Int, caster: Entity)
}
object BurnManager : BurnHandler {
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


interface WeaknessHandler {
    fun Entity.increaseWeakness(int: Int, caster: Entity)
    fun Entity.getWeakness(): AbnormalityStatus?
    fun Entity.setWeakness(int: Int, caster: Entity)
    fun Entity.decreaseWeakness(int: Int, caster: Entity)
}
object WeaknessManager : WeaknessHandler {
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


interface BlindnessHandler {
    fun Entity.increaseBlindness(int: Int, caster: Entity)
    fun Entity.getBlindness(): AbnormalityStatus?
    fun Entity.setBlindness(int: Int, caster: Entity)
    fun Entity.decreaseBlindness(int: Int, caster: Entity)
}
object BlindnessManager : BlindnessHandler {
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