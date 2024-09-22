package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.BattleManager.enemyLocationRetake
import org.beobma.projectturngame.manager.GameManager.battleStop
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.attribute.Attribute

interface EnemyHandler {
    fun Enemy.set()

    fun Enemy.setMaxHealth(int: Int)
    fun Enemy.setHealth(int: Int)
    fun Enemy.death()

    fun Enemy.damage(damage: Int, attacker: Player?, isTrueDamage: Boolean = false)
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

        if (this.isDead) {
            return
        }

        game.gameTurnOrder.remove(this)
        game.gameEnemys.remove(this)
        this.entity.remove()

        if (game.gameEnemys.size < 1) {
            game.battleStop()
        } else {
            enemyLocationRetake()
        }
    }

    override fun Enemy.damage(damage: Int, attacker: Player?, isTrueDamage: Boolean) {
        // 대상이 이미 사망한 경우
        if (this.isDead) return

        var finalDamage = damage

        // 고정 피해가 아닌 경우
        if (!isTrueDamage) {
            // 피해 계산 추가
        }

        // 대상의 보호막 수치가 1 이상인 경우
        if (this.shield > 0) {
            // 최종 피해가 보호막 수치보다 이상일 경우
            if (finalDamage >= this.shield) {
                finalDamage -= this.shield
                this.shield = 0
            }
            // 최종 피해가 보호막 수치보다 미만일 경우
            else {
                this.shield -= finalDamage
                finalDamage = 0
            }
        }

        // 최종 피해가 0 이하인 경우
        if (finalDamage <= 0) return

        // 초과 피해
        if (this.health - finalDamage <= 0) {
            this.death()
            return
        }

        // 일반 피해
        this.health -= finalDamage
        this.entity.damage(finalDamage.toDouble(), attacker?.player)
        entity.apply {
            this.customName(Component.text("${this.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
            this.isCustomNameVisible = true
        }
    }
}

object EnemyManager {
    private val converter: EnemyHandler = DefaultEnemyManager()

    fun Enemy.set() {
        converter.run { this@set.set() }
    }

    fun Enemy.setMaxHealth(int: Int) {
        converter.run { this@setMaxHealth.setMaxHealth(int) }
    }

    fun Enemy.setHealth(int: Int) {
        converter.run { this@setHealth.setHealth(int) }
    }

    fun Enemy.damage(damage: Int, attacker: Player?, isTrueDamage: Boolean = false) {
        converter.run { this@damage.damage(damage, attacker, isTrueDamage) }
    }
}