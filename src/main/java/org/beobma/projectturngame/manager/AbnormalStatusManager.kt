package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.manager.PlayerManager.death
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Score


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


interface TimeHandler {
    fun Entity.increaseTime(int: Int, caster: Entity)
    fun Entity.getTime(): AbnormalityStatus?
    fun Entity.setTime(int: Int, caster: Entity)
    fun Entity.decreaseTime(int: Int, caster: Entity)
}
object TimeManager : TimeHandler {
    override fun Entity.increaseTime(int: Int, caster: Entity) {
        val blindness = this.getTime()

        if (blindness is AbnormalityStatus) {
            blindness.caster.add(caster)
            blindness.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Time, mutableListOf(caster), int))
        }
    }

    override fun Entity.getTime(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Time}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setTime(int: Int, caster: Entity) {
        val blindness = this.getTime()

        if (blindness is AbnormalityStatus) {
            blindness.caster.add(caster)
            blindness.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Time, mutableListOf(caster), int))
        }
    }

    override fun Entity.decreaseTime(int: Int, caster: Entity) {
        val blindness = this.getTime()

        if (blindness is AbnormalityStatus) {
            blindness.caster.add(caster)

            if (blindness.power - int <= 0) {
                this.abnormalityStatus.remove(blindness)
            }
            blindness.power -= int
        }
    }
}


interface StunHandler {
    fun Entity.addStun()
    fun Entity.isStun(): Boolean
    fun Entity.removeStun()
}
object StunManager : StunHandler {
    override fun Entity.addStun() {
        if (this is Player) {
            if (player.scoreboardTags.contains("dontStun")) return
            this.player.scoreboardTags.add("Stun")
        }
        if (this is Enemy) {
           if (player.scoreboardTags.contains("dontStun")) return
            this.entity.scoreboardTags.add("Stun")
        }
    }

    override fun Entity.isStun(): Boolean {
        if (this is Player) {
            return this.player.scoreboardTags.contains("Stun")
        }
        if (this is Enemy) {
            return this.entity.scoreboardTags.contains("Stun")
        }
        return false
    }

    override fun Entity.removeStun() {
        if (this is Player) {
            this.player.scoreboardTags.remove("Stun")
        }
        if (this is Enemy) {
            this.entity.scoreboardTags.remove("Stun")
        }
    }
}

interface ProtectHandler {
    fun Entity.increaseProtect(int: Int, caster: Entity)
    fun Entity.getProtect(): AbnormalityStatus?
    fun Entity.setProtect(int: Int, caster: Entity)
    fun Entity.decreaseProtect(int: Int, caster: Entity)
}
object ProtectManager : ProtectHandler {
    override fun Entity.increaseProtect(int: Int, caster: Entity) {
        val protect = this.getProtect()

        if (protect is AbnormalityStatus) {
            protect.caster.add(caster)
            protect.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Protect, mutableListOf(caster), int))
        }
    }

    override fun Entity.getProtect(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Protect}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setProtect(int: Int, caster: Entity) {
        val protect = this.getProtect()

        if (protect is AbnormalityStatus) {
            protect.caster.add(caster)
            protect.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Protect, mutableListOf(caster), int))
        }
    }

    override fun Entity.decreaseProtect(int: Int, caster: Entity) {
        val protect = this.getProtect()

        if (protect is AbnormalityStatus) {
            protect.caster.add(caster)

            if (protect.power - int <= 0) {
                this.abnormalityStatus.remove(protect)
            }
            protect.power -= int
        }
    }
}


interface CustomStackHandler {
    fun Entity.increaseStack(stackName: String, int: Int)
    fun Entity.getStack(stackName: String): Score
    fun Entity.setStack(stackName: String, int: Int)
    fun Entity.decreaseStack(stackName: String, int: Int)
}
object CustomStackManager : CustomStackHandler {
    override fun Entity.increaseStack(stackName: String, int: Int) {
        val stack = this.getStack(stackName)

        stack.score += int
    }

    override fun Entity.getStack(stackName: String): Score {
        val scoreboard = Bukkit.getScoreboardManager().mainScoreboard

        if (scoreboard.getObjective(stackName) !is Objective) {
            scoreboard.registerNewObjective(stackName, Criteria.DUMMY, Component.text(stackName))
        }
        return scoreboard.getObjective(stackName)!!.getScore(this.name)
    }

    override fun Entity.setStack(stackName: String,int: Int) {
        val stack = this.getStack(stackName)

        stack.score = int
    }

    override fun Entity.decreaseStack(stackName: String,int: Int) {
        val stack = this.getStack(stackName)

        stack.score -= int
    }
}


interface BleedingHandler {
    fun Entity.increaseBleeding(int: Int, caster: Entity)
    fun Entity.getBleeding(): AbnormalityStatus?
    fun Entity.setBleeding(int: Int, caster: Entity)
    fun Entity.decreaseBleeding(int: Int, caster: Entity)
}
object BleedingManager : BleedingHandler {
    override fun Entity.increaseBleeding(int: Int, caster: Entity) {
        val bleeding = this.getBleeding()

        if (bleeding is AbnormalityStatus) {
            bleeding.caster.add(caster)
            bleeding.power += int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Bleeding, mutableListOf(caster), int))
        }

    }

    override fun Entity.getBleeding(): AbnormalityStatus? {
        val abnormalityStatus = this.abnormalityStatus.find { it.keywordType == KeywordType.Bleeding}

        return if (abnormalityStatus !is AbnormalityStatus) {
            return null
        } else {
            abnormalityStatus
        }
    }

    override fun Entity.setBleeding(int: Int, caster: Entity) {
        val bleeding = this.getBleeding()

        if (bleeding is AbnormalityStatus) {
            bleeding.caster.add(caster)
            bleeding.power = int
        }
        else {
            this.abnormalityStatus.add(AbnormalityStatus(KeywordType.Bleeding, mutableListOf(caster), int))
        }
    }

    override fun Entity.decreaseBleeding(int: Int, caster: Entity) {
        val bleeding = this.getBleeding()

        if (bleeding is AbnormalityStatus) {
            bleeding.caster.add(caster)

            if (bleeding.power - int <= 0) {
                this.abnormalityStatus.remove(bleeding)
            }
            bleeding.power -= int
        }
    }
}