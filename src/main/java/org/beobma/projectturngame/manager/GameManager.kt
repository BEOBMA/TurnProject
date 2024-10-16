package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.config.CardConfig
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.config.cardpack.StartCardPack.Companion.startCardList
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityTurnStartEvent
import org.beobma.projectturngame.event.GameBattleStartEvent
import org.beobma.projectturngame.game.Game
import org.beobma.projectturngame.game.GameDifficulty
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.BattleManager.enemyLocationRetake
import org.beobma.projectturngame.manager.BattleManager.playerLocationRetake
import org.beobma.projectturngame.manager.BattleManager.spawnNormalEnemy
import org.beobma.projectturngame.manager.CardManager.applyHotbar
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.CompensationManager.eliteReward
import org.beobma.projectturngame.manager.CompensationManager.normalReward
import org.beobma.projectturngame.manager.CompensationManager.relicsReward
import org.beobma.projectturngame.manager.HealthManager.setHealth
import org.beobma.projectturngame.manager.InventoryManager.openEventInventory
import org.beobma.projectturngame.manager.InventoryManager.openMapInventory
import org.beobma.projectturngame.manager.MaxHealthManager.setMaxHealth
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.setMana
import org.beobma.projectturngame.manager.PlayerManager.setMaxMana
import org.beobma.projectturngame.util.BattleType
import org.beobma.projectturngame.util.ResetType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable


interface GameHandler {
    fun Game.start()
    fun Game.stop()
    fun Game.battleStart()
    fun Game.battleStop()
    fun Game.hardBattleStart()
    fun Game.hardBattleStop()
    fun Game.bossStart()
    fun Game.bossStop()
    fun Game.eventStart()
    fun Game.restStart()
    fun Game.nextSector()
    fun Game.moveTile()

    fun gameOver()

    fun Entity.turnStart()
    fun Entity.turnEnd()
}

object GameManager : GameHandler {

    override fun Game.start() {
        Info.game = this@start

        broadcast(Component.text("[!] 잠시 후 게임을 준비합니다.").decorate(TextDecoration.BOLD))
        broadcastDelayedMessages(
            listOf(
                Component.text("[!] 해당 플러그인과 맵은 BEOBMA에 의해 개발되었습니다.").decorate(TextDecoration.BOLD),
                Component.text("[!] 잠시 후 게임에 등장할 카드 팩을 뽑습니다.").decorate(TextDecoration.BOLD),
                Component.text("[!] 등장할 카드 팩:").decorate(TextDecoration.BOLD)
            )
        )

        object : BukkitRunnable() {
            override fun run() {
                val shuffledCardPackList = cardPackList.shuffled()
                gameCardPack.addAll(shuffledCardPackList.take(5))
                gameCardPack.forEach { broadcast(Component.text(it.name)) }
                broadcast(Component.text("[!] 특정 이벤트로 이 외의 카드 팩 또한 게임에 등장할 수 있습니다.").decorate(TextDecoration.BOLD))
                broadcast(Component.text("[!] 잠시 후 게임이 시작됩니다.").decorate(TextDecoration.BOLD))

                object : BukkitRunnable() {
                    override fun run() {
                        firstStart()
                    }
                }.runTaskLater(ProjectTurnGame.instance, 60L)
            }
        }.runTaskLater(ProjectTurnGame.instance, 280L)
    }

    override fun Game.stop() {
        ProjectTurnGame.instance.server.scheduler.cancelTasks(ProjectTurnGame.instance)

        players.forEach { player ->
            player.isGlowing = false
            player.gameMode = GameMode.ADVENTURE
            val tags = player.scoreboardTags.toList()
            tags.forEach { tag ->
                player.removeScoreboardTag(tag)
            }
            CardConfig()
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset @a")
            player.teleport(Location(player.world, 0.5, -60.0, 0.5))
            player.inventory.clear()
        }

        gameEnemys.forEach {
            it.entity.remove()
        }

        Info.game = null
    }

    override fun Game.battleStart() {
        playerDatas.forEach { player ->
            player.battleStartReset()
        }

        val event = GameBattleStartEvent(BattleType.Normal)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            moveTile()
            return
        }

        val field = gameField

        spawnNormalEnemy(field)
        playerLocationRetake()
        enemyLocationRetake()
        allTurnStart()
    }

    override fun Game.battleStop() {
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            playerData.normalReward()
        }
    }

    override fun Game.hardBattleStart() {
        playerDatas.forEach { player ->
            player.battleStartReset()
        }

        val event = GameBattleStartEvent(BattleType.Hard)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            moveTile()
            return
        }

        val field = gameField

        spawnNormalEnemy(field)
        playerLocationRetake()
        enemyLocationRetake()

        allTurnStart()
    }

    override fun Game.hardBattleStop() {
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            playerData.eliteReward()
        }
    }

    override fun Game.bossStart() {
        playerDatas.forEach { player ->
            player.battleStartReset()
        }

        val event = GameBattleStartEvent(BattleType.Boss)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            moveTile()
            return
        }

        val field = gameField

        spawnNormalEnemy(field)
        playerLocationRetake()
        enemyLocationRetake()

        allTurnStart()
    }

    override fun Game.bossStop() {
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            playerData.relicsReward()
        }
    }

    override fun Game.eventStart() {
        if (eventList.isEmpty()) {
            moveTile()
            return
        }

        val event = eventList.random()

        players.forEach {
            it.openEventInventory(event)
        }
    }

    override fun Game.restStart() {
        TODO("Not yet implemented")
    }

    override fun Game.nextSector() {
        this.tileStep = 0

        this.playerDatas.forEach { playerData ->
            playerData.setHealth(playerData.maxHealth)
        }
        this.gameSector.remove(this.gameField)
        this.gameMapInventory = null
        moveTile()
    }

    override fun gameOver() {
        val game = Info.game ?: return

        game.stop()
    }

    override fun Entity.turnStart() {
        val game = Info.game ?: return
        game.gameTurnOrder.remove(this)
        val event = EntityTurnStartEvent(this)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            this.turnEnd()
            return
        }

        if (this is Player) {
            this@turnStart.addMana(1)

            this.player.run {
                sendMessage(Component.text("당신의 턴입니다.").decorate(TextDecoration.BOLD))
                sendMessage(Component.text("점프하면 턴을 종료합니다.").decorate(TextDecoration.BOLD))
                isGlowing = true
                playSound(location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 1.0F)
                drow(1)
                scoreboardTags.add("this_Turn")
                turnStartUnit.forEach {
                    it.invoke()
                }
                turnStartUnit.clear()
            }
        }

        if (this is Enemy) {
            this.entity.run {
                isGlowing = true
                scoreboardTags.add("this_Turn")
                // 적 행동 정리
                this@turnStart.turnEnd()
            }
        }
    }

    override fun Entity.turnEnd() {
        val game = Info.game ?: return

        if (this is Player) {
            this.player.run {
                isGlowing = false
                scoreboardTags.remove("this_Turn")
                turnEndUnit.forEach {
                    it.invoke()
                }
                turnEndUnit.clear()
            }
        }

        if (this is Enemy) {
            this.entity.run {
                isGlowing = false
                scoreboardTags.remove("this_Turn")
            }
        }
        val firstEntry = game.gameTurnOrder.firstOrNull()
        if (firstEntry != null) {
            firstEntry.turnStart()
        } else {
            allTurnEnd()
        }
    }

    private fun allTurnStart() {
        val game = Info.game ?: return
        val speed: MutableMap<Int, MutableList<Entity>> = mutableMapOf()
        val diceSides = 12

        fun rollDice(sides: Int): Int {
            return (1..sides).random()
        }

        fun addEntitySpeed(entity: Entity, speedMap: MutableMap<Int, MutableList<Entity>>, baseSpeed: Int?) {
            baseSpeed?.let {
                // 주사위를 굴려 나온 값과 속도를 더함
                val finalSpeed = it + rollDice(diceSides)
                speedMap.getOrPut(finalSpeed) { mutableListOf() }.add(entity)
            }
        }

        game.playerDatas.forEach {
            if (!it.isDead) {
                val playerSpeed = it.speed
                addEntitySpeed(it, speed, playerSpeed)
            }
        }

        game.gameEnemys.forEach {
            val enemySpeed = it.speed
            addEntitySpeed(it, speed, enemySpeed)
        }
        val sortedSpeed = speed.toSortedMap(compareByDescending { it })

        game.gameTurnOrder = sortedSpeed.values.flatten().toMutableList()
        game.gameTurnOrder.firstOrNull()?.turnStart() ?: allTurnEnd()
    }

    private fun allTurnEnd() {
        allTurnStart()
    }

    private fun firstStart() {
        val game = Info.game ?: return
        game.players.forEach { player ->
            game.playerDatas.add(
                Player(
                    player,
                    0,
                    3,
                    mutableListOf(),
                    mutableListOf(),
                    mutableListOf(),
                    mutableListOf()
                )
            )
        }

        game.playerDatas.forEach { playerData ->
            playerData.setMaxHealth(40)
            playerData.setHealth(40)
            playerData.setMaxMana(3)
            playerData.setMana(3)
            playerData.deck.addAll(startCardList)
        }

        game.gameSector.addAll(GameField.entries)
        game.gameField = when (game.gameDifficulty) {
            GameDifficulty.Easy -> GameField.Forest
            GameDifficulty.Normal -> GameField.Cave
            GameDifficulty.Hard -> GameField.Sea
        }
        game.gameSector.remove(game.gameField)
        playerLocationRetake()

        game.moveTile()
    }

    override fun Game.moveTile() {
        if (!this.playerDatas.filter { it.player.scoreboardTags.contains("inventory_MapChoice") }.isEmpty()) return

        tileStep++
        players.forEach { player ->
            player.openMapInventory(InventoryOpenType.Choice)
        }
    }

    private fun broadcast(message: Component) {
        Bukkit.broadcast(message)
    }

    private fun broadcastDelayedMessages(messages: List<Component>) {
        messages.forEachIndexed { index, message ->
            object : BukkitRunnable() {
                override fun run() {
                    Bukkit.broadcast(message)
                }
            }.runTaskLater(ProjectTurnGame.instance, 60L * (index + 1))
        }
    }

    private fun Player.battleStartReset() {
        this.deck.shuffle()
        this.drow(5)
        this@battleStartReset.setMana(this@battleStartReset.maxMana)
    }

    private fun Player.battleEndReset() {
        this.setMana(this.maxMana)
        this.abnormalityStatus.removeAll(this.abnormalityStatus.filter { it.resetType == ResetType.BattleEnd })

        this.turnEndUnit.forEach {
            it.invoke()
        }
        this.turnEndUnit.clear()
        this.abnormalityStatus.clear()
        this.deck.addAll(this.hand)
        this.deck.addAll(this.graveyard)
        this.deck.addAll(this.banish)
        this.hand.clear()
        this.graveyard.clear()
        this.banish.clear()

        this.applyHotbar()
    }
}