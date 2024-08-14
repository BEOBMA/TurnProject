package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.config.CardConfig
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.config.StartCardPack.Companion.startCardList
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.game.Game
import org.beobma.projectturngame.game.GameDifficulty
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
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
    fun gameOver()

    fun Entity.turnStart()
    fun Entity.turnEnd()
}


class DefaultGameManager : GameHandler {
    private val abnormalStatusManager = AbnormalStatusManager()
    private val maxHealthManager = abnormalStatusManager.createMaxHealthManager()
    private val healthManager = abnormalStatusManager.createHealthManager()
    private val playerManager = PlayerManager(DefaultPlayerManager())
    private val cardManager = CardManager(DefaultCardManager())
    private val compensationManager = CompensationManager(DefaultCompensationManager())

    override fun Game.start() {
        Info.game = this@start

        broadcast(Component.text("[!] 잠시 후 게임을 준비합니다.").decorate(TextDecoration.BOLD))
        broadcastDelayedMessages(
            listOf(
                Component.text("[!] 해당 플러그인과 맵은 BEOBMA에 의해 개발되었습니다.").decorate(TextDecoration.BOLD),
                Component.text("[!] 해당 플러그인에 대한 무단 수정, 배포 등을 금지합니다.").decorate(TextDecoration.BOLD),
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
        val game = Info.game ?: return
        ProjectTurnGame.instance.server.scheduler.cancelTasks(ProjectTurnGame.instance)

        game.players.forEach { player ->
            player.isGlowing = false
            player.inventory.clear()
            player.gameMode = GameMode.ADVENTURE
            val tags = player.scoreboardTags.toList()
            tags.forEach { tag ->
                player.removeScoreboardTag(tag)
            }
            CardConfig()
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard players reset @a")
            player.teleport(Location(player.world, 0.5, -60.0, 0.5))
        }

        game.gameEnemys.forEach { it.entity.remove() }

        Info.game = null
    }

    override fun Game.battleStart() {
        val game = Info.game ?: return
        val battleManager = BattleManager(DefaultBattleManager())
        val field = game.gameField
        battleManager.run {
            this.spawnNormalEnemy(field)
            this.playerLocationRetake()
            this.enemyLocationRetake()
        }
        game.playerDatas.forEach { player ->
            player.battleStartReset()
        }
        allTurnStart()
    }

    override fun Game.battleStop() {
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            compensationManager.run {
                playerData.normalReward()
            }
        }
    }

    override fun Game.hardBattleStart() {
        val game = Info.game ?: return
        val battleManager = BattleManager(DefaultBattleManager())
        val field = game.gameField
        battleManager.run {
            this.spawnNormalEnemy(field)
            this.playerLocationRetake()
            this.enemyLocationRetake()
        }
        game.playerDatas.forEach { player ->
            player.battleStartReset()
        }
        allTurnStart()
    }

    override fun Game.hardBattleStop() {
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            compensationManager.run {
                playerData.eliteReward()
            }
        }
    }

    override fun Game.bossStart() {
        val game = Info.game ?: return
        val battleManager = BattleManager(DefaultBattleManager())
        val field = game.gameField
        battleManager.run {
            this.spawnNormalEnemy(field)
            this.playerLocationRetake()
            this.enemyLocationRetake()
        }
        game.playerDatas.forEach { player ->
            player.battleStartReset()
        }
        allTurnStart()
    }

    override fun Game.bossStop() {
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            compensationManager.run {
                playerData.relicsReward()
            }
        }
    }

    override fun Game.eventStart() {
        TODO("Not yet implemented")
    }

    override fun Game.restStart() {
        TODO("Not yet implemented")
    }

    override fun Game.nextSector() {
        this.tileStep = 0

        this.playerDatas.forEach { playerData ->
            healthManager.run { playerData.setHealth(playerData.maxHealth) }
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
        if (this is Player) {
            playerManager.run {
                this@turnStart.addMana(1)
            }
            this.player.run {
                sendMessage(Component.text("당신의 턴입니다.").decorate(TextDecoration.BOLD))
                sendMessage(Component.text("점프하면 턴을 종료합니다.").decorate(TextDecoration.BOLD))
                isGlowing = true
                playSound(location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 1.0F)
                cardManager.run { drow(5) }
                scoreboardTags.add("this_Turn")
            }
        }

        if (this is Enemy) {
            this.entity.run {
                isGlowing = true
                scoreboardTags.add("this_Turn")
                this@turnStart.turnEnd()
                // 적 행동 정리
            }
        }
    }

    override fun Entity.turnEnd() {
        val game = Info.game ?: return

        if (this is Player) {
            this.player.run {
                sendMessage(Component.text("턴을 종료했습니다.").decorate(TextDecoration.BOLD))
                isGlowing = false
                scoreboardTags.remove("this_Turn")
            }
        }

        if (this is Enemy) {
            this.entity.run {
                isGlowing = false
                scoreboardTags.remove("this_Turn")
            }
        }
        game.gameTurnOrder.remove(this)
        val firstEntry = game.gameTurnOrder.firstOrNull()
        if (firstEntry != null) {
            firstEntry.turnStart()
            game.gameTurnOrder.remove(firstEntry)
        } else {
            allTurnEnd()
        }
    }

    private fun allTurnStart() {
        val game = Info.game ?: return
        val speed: MutableMap<Int, MutableList<Entity>> = mutableMapOf()


        fun addEntitySpeed(entity: Entity, speedMap: MutableMap<Int, MutableList<Entity>>, speed: Int?) {
            speed?.let {
                speedMap.getOrPut(it) { mutableListOf() }.add(entity)
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

        game.gameTurnOrder.firstOrNull()?.let { firstEntity ->
            firstEntity.turnStart()
            game.gameTurnOrder.remove(firstEntity)
        } ?: allTurnEnd()
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
            maxHealthManager.run { playerData.setMaxHealth(40) }
            healthManager.run { playerData.setHealth(40) }
            playerManager.run { playerData.setMaxMana(3) }
            playerManager.run { playerData.setMana(3) }
            playerData.deck.addAll(startCardList)
        }

        game.gameSector.addAll(GameField.entries)
        game.gameField = when (game.gameDifficulty) {
            GameDifficulty.Easy -> GameField.Forest
            GameDifficulty.Normal -> GameField.Cave
            GameDifficulty.Hard -> GameField.Sea
        }
        game.gameSector.remove(game.gameField)

        moveTile()
    }

    private fun moveTile() {
        val game = Info.game ?: return
        val inventoryManager = InventoryManager(DefaultInventoryManager())

        game.tileStep++
        inventoryManager.run {
            game.players.forEach { player ->
                player.openMapInventory(InventoryOpenType.Choice)
            }
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
        playerManager.run {
            this@battleStartReset.setMana(this@battleStartReset.maxMana)
        }
    }

    private fun Player.battleEndReset() {
        playerManager.run {
            this@battleEndReset.setMana(this@battleEndReset.maxMana)
        }

        this.deck.addAll(this.hand)
        this.deck.addAll(this.graveyard)
        this.deck.addAll(this.banish)
        this.hand.clear()
        this.graveyard.clear()
        this.banish.clear()

        cardManager.run {
            this@battleEndReset.applyHotbar()
        }
    }
}

class GameManager(private val converter: GameHandler) {
    fun Game.start() {
        converter.run { this@start.start() }
    }

    fun Game.stop() {
        converter.run { this@stop.stop() }
    }

    fun Game.battleStart() {
        converter.run { this@battleStart.battleStart() }
    }

    fun Game.battleStop() {
        converter.run { this@battleStop.battleStop() }
    }

    fun Game.hardBattleStart() {
        converter.run { this@hardBattleStart.hardBattleStart() }
    }

    fun Game.hardBattleStop() {
        converter.run { this@hardBattleStop.hardBattleStop() }
    }

    fun Game.bossStart() {
        converter.run { this@bossStart.bossStart() }
    }

    fun Game.bossStop() {
        converter.run { this@bossStop.bossStop() }
    }

    fun Game.restStart() {
        converter.run { this@restStart.restStart() }
    }

    fun Game.eventStart() {
        converter.run { this@eventStart.eventStart() }
    }

    fun Game.nextSector() {
        converter.run { this@nextSector.nextSector() }
    }

    fun gameOver() {
        converter.run { gameOver() }
    }

    fun Entity.turnStart() {
        converter.run { this@turnStart.turnStart() }
    }

    fun Entity.turnEnd() {
        converter.run { this@turnEnd.turnEnd() }
    }
}