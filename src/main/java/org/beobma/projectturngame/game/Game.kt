package org.beobma.projectturngame.game

import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.continueeffect.ContinueEffect
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.bukkit.inventory.Inventory

class Game(
    val players: MutableList<org.bukkit.entity.Player>,
    val gameType: GameType,
    val gameDifficulty: GameDifficulty,
    val gameCardPack: MutableList<CardPack>,
    var gameField: GameField,
    var battleType: GameDifficulty?,
    val playerDatas: MutableList<Player> = mutableListOf(),
    var gameDetailsField: GameDetailsField = GameDetailsField.None,
    var gameEnemys: MutableList<Enemy> = mutableListOf(),
    var gameTurnOrder: MutableList<Entity> = mutableListOf(),
    var gameSector: MutableList<GameField> = mutableListOf(),
    var gameMapInventory: Inventory? = null,
    var tileStep: Int = 0,
    var sectorStep: Int = 0,
    var drowCardInt: Int = 0,
    val turnEndUnit: MutableList<() -> Unit> = mutableListOf(),
    val battleEndUnit: MutableList<() -> Unit> = mutableListOf(),
    val sectorEndUnit: MutableList<() -> Unit> = mutableListOf(),
    val continueEffects: MutableList<ContinueEffect> = mutableListOf()
)
