package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityDamageEvent
import org.beobma.projectturngame.event.EntityDeathEvent
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.BattleManager.playerLocationRetake
import org.beobma.projectturngame.manager.GameManager.gameOver
import org.beobma.projectturngame.manager.UtilManager.getScore
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.ResetType
import org.beobma.projectturngame.util.ResetType.*
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

// 미래를 위한 용어 변경 예정
// 기존 add 함수를 add / remove 함수로 개별화 예정
interface PlayerHandler {
    fun Player.deckShuffle()
    fun Player.graveyardReset()

    fun Player.setMaxMana(int: Int)
    fun Player.addMaxMana(int: Int)

    fun Player.setMana(int: Int, ignoreMaxValue: Boolean = false)
    fun Player.addMana(int: Int)

    fun Player.addShield(int: Int)

    fun Player.damage(damage: Int, attacker: Enemy?, damageType: DamageType = DamageType.Normal)
    fun Player.heal(damage: Int, healer: Player)
    fun Player.death()
    fun Player.resurrection()

    fun Player.diceRoll(min: Int, max: Int): Int

    fun Player.isTurn(): Boolean
    fun Player.addTag(tag: String, resetType: ResetType)
    fun Player.removeTag(tag: String)

    fun Player.toItem(): ItemStack
}

object PlayerManager : PlayerHandler {
    override fun Player.deckShuffle() {
        this.deck.shuffle()
    }

    override fun Player.graveyardReset() {
        this.graveyard.forEach {
            deck.add(it)
        }
        this.graveyard.clear()
        this.deckShuffle()
    }

    override fun Player.setMaxMana(int: Int) {
        this.maxMana = int

        if (this.maxMana < 0) {
            this.maxMana = 0
        }

        this.applyPlayerInfo()
    }

    override fun Player.addMaxMana(int: Int) {
        this.maxMana += int

        if (this.maxMana < 0) {
            this.maxMana = 0
        }

        this.applyPlayerInfo()
    }

    override fun Player.setMana(int: Int, ignoreMaxValue: Boolean) {
        this.mana = int

        if (this.mana < 0) {
            this.mana = 0
        }

        if (!ignoreMaxValue) {
            if (this.mana > this.maxMana) {
                this.mana = this.maxMana
            }
        }

        this.applyPlayerInfo()
    }

    override fun Player.addMana(int: Int) {
        this.mana += int

        if (this.mana < 0) {
            this.mana = 0
        }

        if (this.mana > this.maxMana) {
            this.mana = this.maxMana
        }

        this.applyPlayerInfo()
    }

    override fun Player.addShield(int: Int) {
        this.shield += int

        // 보호막 수치가 0 미만인 경우
        if (this.shield < 0) {
            this.shield = 0
        }
    }

    override fun Player.damage(damage: Int, attacker: Enemy?, damageType: DamageType) {
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
                this.shield -= finalDamage
                finalDamage = 0
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
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
        this.player.damage(0.1, attacker?.entity)
        this.player.health = this.health.toDouble()
    }

    override fun Player.heal(damage: Int, healer: Player) {
        // 대상이 이미 사망한 경우
        if (this.isDead) return

        var finalHealDamage = damage

        // 회복량 계산

        // 최종 회복량이 0 이하인 경우
        if (finalHealDamage <= 0) return

        // 초과 회복
        if (this.health + finalHealDamage > this.maxHealth) {
            this.health = this.maxHealth
            this.player.health = this.maxHealth.toDouble()
            return
        }

        // 일반 회복
        this.health += finalHealDamage
        this.player.health += finalHealDamage
    }

    override fun Player.death() {
        val game = Info.game ?: return

        this.isDead = true

        if (game.playerDatas.all { it.isDead }) {
            game.gameOver()
        } else {
            playerLocationRetake()
            game.gameTurnOrder.remove(this)
        }
    }

    override fun Player.resurrection() {
        this.isDead = false
        playerLocationRetake()
    }

    override fun Player.diceRoll(min: Int, max: Int): Int {
        val baseRoll = Random.nextInt(min, max + 1) + diceWeight
        var dice = baseRoll.coerceIn(min, max)

        if (player.scoreboardTags.remove("minMax")) {
            dice = if (Random.nextBoolean()) min else max
        } else if (player.scoreboardTags.remove("chanceAdvantage")) {
            val secondRoll = (Random.nextInt(min, max + 1) + diceWeight).coerceIn(min, max)
            dice = maxOf(dice, secondRoll)
        }

        player.sendMessage(Component.text("주사위를 굴린 결과, ${dice}이(가) 나왔습니다.", TextColorType.Gray.textColor))
        return dice
    }

    override fun Player.isTurn(): Boolean {
        return this.player.scoreboardTags.contains("this_Turn")
    }

    override fun Player.addTag(tag: String, resetType: ResetType) {
        val game = Info.game ?: return

        this.player.scoreboardTags.add(tag)

        when (resetType) {
            None -> return
            TurnEnd -> game.turnEndUnit.add { this.player.scoreboardTags.remove(tag) }
            BattleEnd -> game.battleEndUnit.add { this.player.scoreboardTags.remove(tag) }
            CardUseEnd -> TODO()
            SectorEnd -> TODO()
            TurnStart -> TODO()
        }
    }

    override fun Player.removeTag(tag: String) {
        this.player.scoreboardTags.remove(tag)
    }

    override fun Player.toItem(): ItemStack {
        val game = Info.game ?: return ItemStack(Material.BARRIER)
        val playerData = game.playerDatas.find { it == this } ?: return ItemStack(Material.BARRIER)
        val name = playerData.name
        val abnormalityStatusList: MutableList<String> = mutableListOf()

        playerData.abnormalityStatus.forEach {
            abnormalityStatusList.add("${it.keywordType.string}<gray>: ${it.power}")
        }
        val lore = mutableListOf<String>(
            "<gray>체력 / 최대 체력 : ${playerData.health} / ${playerData.maxHealth}",
            "<gray>마나 / 최대 마나 : ${playerData.mana} / ${playerData.maxMana}",
            "<gray>속도 : ${playerData.speed}",
            "<gray>상태이상 목록:"
        )

        lore.addAll(abnormalityStatusList)
        val item = ItemStack(Material.PLAYER_HEAD, 1).apply {
            itemMeta = itemMeta?.apply {
                displayName(Component.text(name))
                lore(lore.map { MiniMessage.miniMessage().deserialize(it) })
                addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
            }
        }

        return item
    }


    private fun Player.applyPlayerInfo() {
        player.getScore("mana").score = mana
        player.level = mana
        player.getScore("maxMana").score = maxMana

        if (mana <= 0) {
            player.exp = 0.01f
        } else {
            player.exp = 0.99f
        }
    }
}