package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.enemy.EnemyAction
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityDamageEvent
import org.beobma.projectturngame.event.EntityDeathEvent
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.BattleManager.enemyLocationRetake
import org.beobma.projectturngame.manager.GameManager.battleStop
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.DamageType
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

interface EnemyHandler {
    fun Enemy.set()

    fun Enemy.setMaxHealth(int: Int)
    fun Enemy.setHealth(int: Int)
    fun Enemy.death()

    fun Enemy.damage(damage: Int, attacker: Player?, damageType: DamageType = DamageType.Normal)
    fun Enemy.heal(damage: Int, healer: Enemy)
    fun Enemy.addShield(int: Int)

    fun Enemy.toItem(): ItemStack
    fun EnemyAction.toItem(): ItemStack
}

object EnemyManager : EnemyHandler {
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
        this@setMaxHealth.entity.getAttribute(Attribute.MAX_HEALTH)!!.baseValue = int.toDouble()
        entity.apply {
            this.customName(Component.text("${this.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
            this.isCustomNameVisible = true
        }
    }

    override fun Enemy.setHealth(int: Int) {
        if (int <= 0) {
            this.death()
        }
        if (this.maxHealth <= int) {
            this.health = int
            this.entity.health = int.toDouble()
            entity.apply {
                this.customName(Component.text("${this.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
                this.isCustomNameVisible = true
            }
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
        this.isDead = true
        this.entity.remove()

        if (game.gameEnemys.isEmpty()) {
            game.battleStop()
        } else {
            enemyLocationRetake()
        }
    }

    override fun Enemy.damage(damage: Int, attacker: Player?, damageType: DamageType) {
        // 대상이 이미 사망한 경우
        if (this.isDead) return

        var finalDamage = damage

        val event = EntityDamageEvent(finalDamage, damageType, this, attacker)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            return
        }
        finalDamage = event.damage

        // 고정 피해가 아닌 경우
        if (damageType != DamageType.True) {
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
                entity.world.playSound(entity.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                this.shield -= finalDamage
                finalDamage = 0
            }
        }

        // 최종 피해가 0 이하인 경우
        if (finalDamage <= 0) return

        // 초과 피해
        if (this.health - finalDamage <= 0) {
            val event = EntityDeathEvent(damageType, this, attacker)
            ProjectTurnGame.instance.server.pluginManager.callEvent(event)
            if (event.isCancelled) {
                return
            }
            this.death()
            return
        }

        // 일반 피해
        this.health -= finalDamage
        this.entity.damage(finalDamage.toDouble(), attacker?.player)
        this.entity.noDamageTicks = 0
        entity.apply {
            this.customName(Component.text("${this.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
            this.isCustomNameVisible = true
        }
    }

    override fun Enemy.heal(damage: Int, healer: Enemy) {
        // 대상이 이미 사망한 경우
        if (this.isDead) return

        var finalHealDamage = damage

        // 회복량 계산

        // 최종 회복량이 0 이하인 경우
        if (finalHealDamage <= 0) return

        // 초과 회복
        if (this.health + finalHealDamage > this.maxHealth) {
            this.health = this.maxHealth
            this.entity.health = this.maxHealth.toDouble()
            return
        }

        // 일반 회복
        this.health += finalHealDamage
        this.entity.health += finalHealDamage
        entity.apply {
            this.customName(Component.text("${this.health}",TextColorType.DarkRed.textColor).decorate(TextDecoration.BOLD))
            this.isCustomNameVisible = true
        }
    }

    override fun Enemy.addShield(int: Int) {
        this.shield += int

        // 보호막 수치가 0 미만인 경우
        if (this.shield < 0) {
            this.shield = 0
        }
    }

    override fun Enemy.toItem(): ItemStack {
        val name = this.name
        val abnormalityStatusList = mutableListOf<String>()

        this.abnormalityStatus.forEach {
            abnormalityStatusList.add("${it.keywordType.string}<gray>: ${it.power}")
        }
        val lore = mutableListOf<String>(
            "<gray>체력 / 최대 체력 : ${this.health} / ${this.maxHealth}",
            "<gray>속도 : ${this.speed}",
            "<gray>상태이상 목록:"
        )

        lore.addAll(abnormalityStatusList)
        val item = ItemStack(Material.ZOMBIE_HEAD, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(Component.text(name))
                lore(lore.map { MiniMessage.miniMessage().deserialize(it) })
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }
        return item
    }

    override fun EnemyAction.toItem(): ItemStack {
        val name = this.actionName
        val lore = this.actionDescription

        val item = ItemStack(Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(Component.text(name))
                lore(lore.map { MiniMessage.miniMessage().deserialize(it) })
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }
        return item
    }
}