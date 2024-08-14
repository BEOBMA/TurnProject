package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.attribute.Attribute

interface EnemyHandler {
    fun Enemy.set()

    fun Enemy.setMaxHealth(int: Int)
    fun Enemy.setHealth(int: Int)
    fun Enemy.death()

    fun Enemy.damage(damage: Int, attacker: Player)
}

class DefaultEnemyManager : EnemyHandler{
    override fun Enemy.set() {
        val game = Info.game ?: return

        entity.apply {
            this.customName(Component.text("${this@set.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
            this.isCustomNameVisible = true
        }

        game.gameEnemys.add(this)
    }

    override fun Enemy.setMaxHealth(int: Int) {
        this.maxHealth = int
        this@setMaxHealth.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue = int.toDouble()
    }

    override fun Enemy.setHealth(int: Int) {
        if (int <= 0) {
            this.death()
        }
        if (this.maxHealth < int) {
            this.health = int
            this.entity.health = int.toDouble()
            return
        }
    }

    override fun Enemy.death() {
        val game = Info.game ?: return
        val gameManager = GameManager(DefaultGameManager())
        val battleManager = BattleManager(DefaultBattleManager())

        if (this.isDead) {
            return
        }

        game.gameTurnOrder.remove(this)
        game.gameEnemys.remove(this)
        this.entity.remove()

        if (game.gameEnemys.size < 1) {
            gameManager.run {
                game.battleStop()
            }
        } else {
            battleManager.enemyLocationRetake()
        }
    }

    override fun Enemy.damage(damage: Int, attacker: Player) {
        if (this.isDead) return

        var finalDamage = damage

        if (finalDamage <= 0) return
        if (this.health - finalDamage <= 0) {
            this.death()
            return
        }

        this.health -= finalDamage
        this.entity.damage(finalDamage.toDouble(), attacker.player)
        entity.apply {
            this.customName(Component.text("${this.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
            this.isCustomNameVisible = true
        }
    }
}

class EnemyManager(private val converter: EnemyHandler) {
    fun Enemy.set() {
        converter.run { this@set.set() }
    }

    fun Enemy.setMaxHealth(int: Int) {
        converter.run { this@setMaxHealth.setMaxHealth(int) }
    }

    fun Enemy.setHealth(int: Int) {
        converter.run { this@setHealth.setHealth(int) }
    }

    fun Enemy.damage(damage: Int, attacker: Player) {
        converter.run { this@damage.damage(damage, attacker) }
    }
}