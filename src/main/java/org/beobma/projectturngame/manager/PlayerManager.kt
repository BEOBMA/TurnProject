package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.BattleManager.playerLocationRetake
import org.beobma.projectturngame.manager.GameManager.gameOver
import org.beobma.projectturngame.manager.UtilManager.getScore
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.ResetType
import org.beobma.projectturngame.util.ResetType.*
import kotlin.random.Random

interface PlayerHandler {
    fun Player.deckShuffle()
    fun Player.graveyardReset()

    fun Player.setMaxMana(int: Int)
    fun Player.addMaxMana(int: Int)

    fun Player.setMana(int: Int)
    fun Player.addMana(int: Int)

    fun Player.addShield(int: Int)

    fun Player.damage(damage: Int, attacker: Enemy?, isTrueDamage: Boolean = false)
    fun Player.heal(damage: Int, healer: Player)
    fun Player.death()
    fun Player.resurrection()

    fun Player.diceRoll(min: Int, max: Int): Int

    fun Player.isTurn(): Boolean
    fun Player.addTag(tag: String, resetType: ResetType)
    fun Player.removeTag(tag: String)
}

class DefaultPlayerManager : PlayerHandler {
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

        this.applyScoreboard()
    }

    override fun Player.addMaxMana(int: Int) {
        this.maxMana += int

        if (this.maxMana < 0) {
            this.maxMana = 0
        }

        this.applyScoreboard()
    }

    override fun Player.setMana(int: Int) {
        this.mana = int

        if (this.mana < 0) {
            this.mana = 0
        }

        if (this.mana > this.maxMana) {
            this.mana = this.maxMana
        }

        this.applyScoreboard()
    }

    override fun Player.addMana(int: Int) {
        this.mana += int

        if (this.mana < 0) {
            this.mana = 0
        }

        if (this.mana > this.maxMana) {
            this.mana = this.maxMana
        }

        this.applyScoreboard()
    }

    override fun Player.addShield(int: Int) {
        this.shield += int

        // 보호막 수치가 0 미만인 경우
        if (this.shield < 0) {
            this.shield = 0
        }
    }

    override fun Player.damage(damage: Int, attacker: Enemy?, isTrueDamage: Boolean) {
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
        this.player.damage(finalDamage.toDouble(), attacker?.entity)
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
        val playerData = game.playerDatas.find { it.player == player } as Player

        this.isDead = true

        if (game.playerDatas.all { it.isDead }) {
            gameOver()
        } else {
            playerLocationRetake()
            game.gameTurnOrder.remove(playerData)
        }
    }

    override fun Player.resurrection() {
        this.isDead = false
        playerLocationRetake()
    }

    override fun Player.diceRoll(min: Int, max: Int): Int {
        var dice = Random.nextInt(min, max + 1)
        dice += this.diceWeight

        if (dice > max) {
            dice = max
        }
        else if (dice < min) {
            dice = min
        }

        if (player.scoreboardTags.contains("minMax")) {
            dice = if (Random.nextBoolean()) {
                min
            } else {
                max
            }
            player.scoreboardTags.remove("minMax")
        }

        if (player.scoreboardTags.contains("chanceAdvantage")) {
            val secondRoll = Random.nextInt(min, max + 1) + this.diceWeight

            val adjustedSecondRoll = when {
                secondRoll > max -> max
                secondRoll < min -> min
                else -> secondRoll
            }

            dice = maxOf(dice, adjustedSecondRoll)
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
        }
    }

    override fun Player.removeTag(tag: String) {
        this.player.scoreboardTags.remove(tag)
    }


    private fun Player.applyScoreboard() {
            player.getScore("mana").score = this@applyScoreboard.mana
            player.getScore("maxMana").score = this@applyScoreboard.maxMana
        }
}

object PlayerManager {
    private val converter: PlayerHandler = DefaultPlayerManager()

    fun Player.deckShuffle() {
        converter.run { this@deckShuffle.deckShuffle() }
    }

    fun Player.graveyardReset() {
        converter.run { this@graveyardReset.graveyardReset() }
    }

    fun Player.setMaxMana(int: Int) {
        converter.run { this@setMaxMana.setMaxMana(int) }
    }

    fun Player.addMaxMana(int: Int) {
        converter.run { this@addMaxMana.addMaxMana(int) }
    }

    fun Player.setMana(int: Int) {
        converter.run { this@setMana.setMana(int) }
    }

    fun Player.addMana(int: Int) {
        converter.run { this@addMana.addMana(int) }
    }

    fun Player.isTurn(): Boolean {
        return converter.run { this@isTurn.isTurn() }
    }

    fun Player.heal(damage: Int, healer: Player) {
        converter.run { this@heal.heal(damage, healer) }
    }

    fun Player.addTag(tag: String, resetType: ResetType) {
        converter.run { this@addTag.addTag(tag, resetType) }
    }

    fun Player.removeTag(tag: String) {
        converter.run { this@removeTag.removeTag(tag) }
    }

    fun Player.death() {
        converter.run { this@death.death() }
    }

    fun Player.damage(damage: Int, attacker: Enemy?, isTrueDamage: Boolean = false) {
        converter.run { damage(damage, attacker, isTrueDamage) }
    }

    fun Player.addShield(int: Int) {
        converter.run { addShield(int) }
    }

    fun Player.diceRoll(min: Int, max: Int): Int {
        return  converter.run { diceRoll(min, max) }
    }
}