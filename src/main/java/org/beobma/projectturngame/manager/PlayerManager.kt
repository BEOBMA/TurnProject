package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.util.ResetType
import org.beobma.projectturngame.util.ResetType.*

interface PlayerHandler {
    fun Player.deckShuffle()
    fun Player.graveyardReset()

    fun Player.setMaxMana(int: Int)
    fun Player.addMaxMana(int: Int)

    fun Player.setMana(int: Int)
    fun Player.addMana(int: Int)

    fun Player.damage(damage: Int, attacker: Enemy)
    fun Player.heal(damage: Int, healer: Player)
    fun Player.death()
    fun Player.resurrection()

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

    override fun Player.damage(damage: Int, attacker: Enemy) {
        if (this.isDead) return

        var finalDamage = damage

        if (finalDamage <= 0) return
        if (this.health - finalDamage <= 0) {
            this.death()
            return
        }

        this.health -= finalDamage
        this.player.damage(finalDamage.toDouble(), attacker.entity)
    }

    override fun Player.heal(damage: Int, healer: Player) {
        if (this.isDead) return

        var finalHealDamage = damage

        if (finalHealDamage <= 0) return
        if (this.health + finalHealDamage > this.maxHealth) {
            this.health = this.maxHealth
            this.player.health = this.maxHealth.toDouble()
            return
        }

        this.health += finalHealDamage
        this.player.health += finalHealDamage
    }

    override fun Player.death() {
        val game = Info.game ?: return
        val playerData = game.playerDatas.find { it.player == player } as Player
        val gameManager = GameManager(DefaultGameManager())
        val battleManager = BattleManager(DefaultBattleManager())
        this.isDead = true

        if (game.playerDatas.all { it.isDead }) {
            gameManager.gameOver()
        } else {
            battleManager.playerLocationRetake()
            game.gameTurnOrder.remove(playerData)
        }
    }

    override fun Player.resurrection() {
        val battleManager = BattleManager(DefaultBattleManager())

        this.isDead = false
        battleManager.playerLocationRetake()
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
        }
    }

    override fun Player.removeTag(tag: String) {
        this.player.scoreboardTags.remove(tag)
    }


    private fun Player.applyScoreboard() {
        val utilManager = UtilManager(DefaultUtilManager())

        utilManager.run {
            this@applyScoreboard.player.getScore("mana").score = this@applyScoreboard.mana
            this@applyScoreboard.player.getScore("maxMana").score = this@applyScoreboard.maxMana
        }
    }
}

class PlayerManager(private val converter: PlayerHandler) {
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
}