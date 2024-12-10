package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.config.CardConfig
import org.beobma.projectturngame.config.CardConfig.Companion.reforgeCardPair
import org.beobma.projectturngame.config.EventConfig.Companion.eventList
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.event.EntityTurnStartEvent
import org.beobma.projectturngame.event.GameBattleStartEvent
import org.beobma.projectturngame.game.Game
import org.beobma.projectturngame.game.GameDifficulty.*
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.BattleManager.enemyLocationRetake
import org.beobma.projectturngame.manager.BattleManager.playerLocationRetake
import org.beobma.projectturngame.manager.BattleManager.spawnBossEnemy
import org.beobma.projectturngame.manager.BattleManager.spawnHardEnemy
import org.beobma.projectturngame.manager.BattleManager.spawnNormalEnemy
import org.beobma.projectturngame.manager.CardManager.applyHotbar
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.CompensationManager.eliteReward
import org.beobma.projectturngame.manager.CompensationManager.normalReward
import org.beobma.projectturngame.manager.CompensationManager.relicsReward
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.HealthManager.setHealth
import org.beobma.projectturngame.manager.InventoryManager.openEventInventory
import org.beobma.projectturngame.manager.InventoryManager.openMapInventory
import org.beobma.projectturngame.manager.InventoryManager.openStartCardPackInventory
import org.beobma.projectturngame.manager.MaxHealthManager.setMaxHealth
import org.beobma.projectturngame.manager.ParticleAnimationManager.isPlay
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.damage
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.PlayerManager.setMana
import org.beobma.projectturngame.manager.PlayerManager.setMaxMana
import org.beobma.projectturngame.manager.StunManager.isStun
import org.beobma.projectturngame.manager.StunManager.removeStun
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.BattleType
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.ResetType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.scheduler.BukkitRunnable

interface GameHandler {
    fun Game.start()
    fun Game.stop()
    fun Game.clear()
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

    fun firstStart()
    fun Game.gameOver()

    fun Entity.turnStart()
    fun Entity.turnEnd()
}

object GameManager : GameHandler {

    override fun Game.start() {
        ProjectTurnGame.instance.logger.info("Game is starting.")
        Info.game = this@start

        broadcast(Component.text("[!] 잠시 후 모든 플레이어가 자신이 사용할 카드팩을 선택합니다.").decorate(TextDecoration.BOLD))

        object : BukkitRunnable() {
            override fun run() {
                players.forEach {
                    it.openStartCardPackInventory()
                }
            }
        }.runTaskLater(ProjectTurnGame.instance, 80L)
    }

    override fun Game.stop() {
        ProjectTurnGame.instance.logger.info("Game is stopping.")
        isPlay = false
        ProjectTurnGame.instance.server.scheduler.cancelTasks(ProjectTurnGame.instance)

        val players = this.players

        gameEnemys.forEach {
            it.entity.remove()
        }

        Info.game = null
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
    }

    override fun Game.clear() {
        ProjectTurnGame.instance.logger.info("Game is being cleared.")
        this.stop()
    }

    override fun Game.battleStart() {
        ProjectTurnGame.instance.logger.info("Battle is starting.")
        playerDatas.forEach { player ->
            player.battleStartReset()
        }

        val event = GameBattleStartEvent(BattleType.Normal)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            ProjectTurnGame.instance.logger.info("Battle start event cancelled.")
            moveTile()
            return
        }

        this.battleType = Easy
        val field = gameField

        spawnNormalEnemy(field)
        playerLocationRetake()
        enemyLocationRetake()
        allTurnStart()
    }

    override fun Game.battleStop() {
        ProjectTurnGame.instance.logger.info("Battle is stopping.")
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            when (this.battleType) {
                Easy ->  playerData.normalReward()
                Normal ->  playerData.eliteReward()
                Hard -> playerData.relicsReward()
                null -> playerData.normalReward()
            }
        }
        this.battleType = null
    }

    override fun Game.hardBattleStart() {
        ProjectTurnGame.instance.logger.info("Hard battle is starting.")
        playerDatas.forEach { player ->
            player.battleStartReset()
        }

        val event = GameBattleStartEvent(BattleType.Hard)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            ProjectTurnGame.instance.logger.info("Hard battle start event cancelled.")
            moveTile()
            return
        }

        this.battleType = Normal
        val field = gameField

        spawnHardEnemy(field)
        playerLocationRetake()
        enemyLocationRetake()

        allTurnStart()
    }

    override fun Game.hardBattleStop() {
        ProjectTurnGame.instance.logger.info("Hard battle is stopping.")
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            playerData.eliteReward()
        }
    }

    override fun Game.bossStart() {
        ProjectTurnGame.instance.logger.info("Boss battle is starting.")
        playerDatas.forEach { player ->
            player.battleStartReset()
        }

        val event = GameBattleStartEvent(BattleType.Boss)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            ProjectTurnGame.instance.logger.info("Boss battle start event cancelled.")
            moveTile()
            return
        }

        this.battleType = Hard
        val field = gameField

        spawnBossEnemy(field)
        playerLocationRetake()
        enemyLocationRetake()

        allTurnStart()
    }

    override fun Game.bossStop() {
        ProjectTurnGame.instance.logger.info("Boss battle is stopping.")
        this.playerDatas.forEach { playerData ->
            playerData.battleEndReset()
            playerData.relicsReward()
        }
    }

    override fun Game.eventStart() {
        ProjectTurnGame.instance.logger.info("Event is starting.")
        if (eventList.isEmpty()) {
            ProjectTurnGame.instance.logger.info("No events available, moving to the next tile.")
            moveTile()
            return
        }

        val event = eventList.random()

        players.forEach {
            it.openEventInventory(event)
        }
    }

    override fun Game.restStart() {
        ProjectTurnGame.instance.logger.info("Resting phase is starting.")
        this.playerDatas.forEach {
            if (!it.isDead) {
                it.heal(20, it)
            }
        }

        moveTile()
    }

    override fun Game.nextSector() {
        ProjectTurnGame.instance.logger.info("Moving to the next sector.")
        this.clear()

//        this.stop()
//        this.tileStep = 0
//
//        this.playerDatas.forEach { playerData ->
//            playerData.setHealth(playerData.maxHealth)
//        }
//        this.gameSector.remove(this.gameField)
//        this.gameMapInventory = null
//        moveTile()
    }

    override fun Game.gameOver() {
        ProjectTurnGame.instance.logger.info("Game over.")
        this.stop()
    }

    override fun Entity.turnStart() {
        ProjectTurnGame.instance.logger.info("Entity ${this.name} is starting its turn.")
        val game = Info.game ?: return
        game.gameTurnOrder.remove(this)
        val event = EntityTurnStartEvent(this)
        ProjectTurnGame.instance.server.pluginManager.callEvent(event)
        if (event.isCancelled) {
            ProjectTurnGame.instance.logger.info("Turn start event cancelled for ${this.name}.")
            this.turnEnd()
            return
        }

        if (this is Player) {
            ProjectTurnGame.instance.logger.info("Player ${this.player.name} is starting their turn.")
            this@turnStart.addMana(1)

            this.player.run {
                isGlowing = true
                playSound(location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 1.0F)
                drow(1)
                scoreboardTags.add("this_Turn")
                turnStartUnit.forEach {
                    it.invoke()
                }
                turnStartUnit.clear()
            }
            if (this.isStun()) {
                ProjectTurnGame.instance.logger.info("Player ${this.player.name} is stunned.")
                this.removeStun()
                this.turnEnd()
            }
        }
        else if (this is Enemy) {
            ProjectTurnGame.instance.logger.info("Enemy ${this.entity.name} is starting its turn.")
            this.entity.run {
                isGlowing = true
                scoreboardTags.add("this_Turn")
            }

            object : BukkitRunnable() {
                override fun run() {
                    if (isStun()) {
                        ProjectTurnGame.instance.logger.info("Enemy ${this@turnStart.entity.name} is stunned.")
                        removeStun()
                        turnEnd()
                        cancel()
                        return
                    }

                    this@turnStart.actionList.forEach {
                        when (game.gameDifficulty) {
                            Easy -> if (it.difficulty != Easy) {
                                return@forEach
                            }

                            Normal -> if (it.difficulty != Easy && it.difficulty != Normal) {
                                return@forEach
                            }

                            Hard -> if (it.difficulty != Easy && it.difficulty != Normal && it.difficulty != Hard) {
                                return@forEach
                            }
                        }
                        if (it.actionCondition.invoke(this@turnStart) == true) {
                            bleedingHandler(this@turnStart)
                            if (this@turnStart.isDead) {
                                ProjectTurnGame.instance.logger.info("Enemy ${this@turnStart.entity.name} has died.")
                                if (game.gameEnemys.isEmpty()) return
                                this@turnStart.turnEnd()
                                cancel()
                                return
                            }

                            broadcast(MiniMessage.miniMessage().deserialize("<gray>${this@turnStart.name}이(가) ${it.actionName}을 발동합니다."))
                            object : BukkitRunnable() {
                                override fun run() {
                                    it.action.invoke(this@turnStart)

                                    object : BukkitRunnable() {
                                        override fun run() {
                                            this@turnStart.turnEnd()
                                        }
                                    }.runTaskLater(ProjectTurnGame.instance, 40L)
                                }
                            }.runTaskLater(ProjectTurnGame.instance, 40L)
                            return
                        }
                    }
                }
            }.runTaskLater(ProjectTurnGame.instance, 60L)
        }
    }

    override fun Entity.turnEnd() {
        ProjectTurnGame.instance.logger.info("Entity ${this.name} is ending its turn.")
        val game = Info.game ?: return

        if (this is Player) {
            ProjectTurnGame.instance.logger.info("Player ${this.player.name} is ending their turn.")
            this.player.run {
                isGlowing = false
                scoreboardTags.remove("this_Turn")
                turnEndUnit.forEach {
                    it.invoke()
                }
                turnEndUnit.clear()
            }
        }
        else if (this is Enemy) {
            ProjectTurnGame.instance.logger.info("Enemy ${this.entity.name} is ending its turn.")
            this.entity.run {
                isGlowing = false
                scoreboardTags.remove("this_Turn")
            }
        }

        val firstEntry = game.gameTurnOrder.firstOrNull()
        if (firstEntry != null) {
            firstEntry.turnStart()
        } else {
            ProjectTurnGame.instance.logger.info("All turns have ended, starting new turn cycle.")
            allTurnEnd()
        }
    }

    override fun firstStart() {
        ProjectTurnGame.instance.logger.info("Initializing the first start of the game.")
        val game = Info.game ?: return

        game.playerDatas.forEach { playerData ->
            playerData.setMaxHealth(40)
            playerData.setHealth(40)
            playerData.setMaxMana(3)
            playerData.setMana(3)
            playerData.deck.addAll(playerData.cardPack.cardList)
        }

        game.gameSector.addAll(GameField.entries)
        game.gameField = GameField.Forest
        game.gameSector.remove(game.gameField)
        playerLocationRetake()

        game.moveTile()
    }

    private fun allTurnStart() {
        ProjectTurnGame.instance.logger.info("All turns are starting.")
        val game = Info.game ?: return
        val speed: MutableMap<Int, MutableList<Entity>> = mutableMapOf()
        val diceSides = 12

        fun rollDice(sides: Int): Int {
            return (1..sides).random()
        }

        fun addEntitySpeed(entity: Entity, speedMap: MutableMap<Int, MutableList<Entity>>, baseSpeed: Int?) {
            baseSpeed?.let {
                val finalSpeed = it + rollDice(diceSides)
                ProjectTurnGame.instance.logger.info("Entity ${entity::class.simpleName} rolled speed $finalSpeed.")
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
        ProjectTurnGame.instance.logger.info("Turn order determined.")
        game.gameTurnOrder.firstOrNull()?.turnStart() ?: allTurnEnd()
    }

    private fun allTurnEnd() {
        ProjectTurnGame.instance.logger.info("All turns have ended, starting new cycle.")
        allTurnStart()
    }

    override fun Game.moveTile() {
        ProjectTurnGame.instance.logger.info("Moving tile.")
        if (!this.playerDatas.none { it.player.scoreboardTags.contains("inventory_MapChoice") }) return

        tileStep++

        if (tileStep > 8) {
            ProjectTurnGame.instance.logger.info("Tile step exceeds 8, moving to next sector.")
            nextSector()
            return
        }
        players.forEach { player ->
            player.openMapInventory(InventoryOpenType.Choice)
        }
    }

    private fun broadcast(message: Component) {
        Bukkit.broadcast(message)
    }

    private fun Player.battleStartReset() {
        ProjectTurnGame.instance.logger.info("Resetting player ${this.player.name} for battle start.")
        this.deck.shuffle()
        this.drow(5)
        this@battleStartReset.setMana(this@battleStartReset.maxMana)
    }

    private fun Player.battleEndReset() {
        ProjectTurnGame.instance.logger.info("Resetting player ${this.player.name} for battle end.")
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
        this.player.scoreboardTags.remove("this_Turn")
        this.player.isGlowing = false

        if (this.deck.any { it.description.contains(KeywordType.Reforged.string.toString()) }) {
            val playerDeckList = this.deck.toMutableList()

            playerDeckList.forEachIndexed { index, card ->
                val newCard = reforgeCardPair.entries.find { it.value.description == card.description }?.key
                if (newCard is Card) {
                    ProjectTurnGame.instance.logger.info("Replacing reforged card in player ${this.player.name}'s deck.")
                    playerDeckList[index] = newCard
                }
            }
            this.deck = playerDeckList
        }

        this.applyHotbar()
    }

    private fun bleedingHandler(entity: Entity) {
        ProjectTurnGame.instance.logger.info("Handling bleeding for entity ${entity::class.simpleName}.")
        val bleeding = entity.abnormalityStatus.find { it.keywordType == KeywordType.Bleeding }

        if (bleeding !is AbnormalityStatus) return

        if (entity is Enemy) {
            ProjectTurnGame.instance.logger.info("Enemy ${entity.entity.name} takes bleeding damage: ${bleeding.power}.")
            entity.damage(bleeding.power, null, DamageType.AbnormalStatus)
        }

        if (entity is Player) {
            ProjectTurnGame.instance.logger.info("Player ${entity.player.name} takes bleeding damage: ${bleeding.power}.")
            entity.damage(bleeding.power, null, DamageType.AbnormalStatus)
        }

        bleeding.power = (bleeding.power / 2).toInt()

        if (bleeding.power <= 1) {
            ProjectTurnGame.instance.logger.info("Bleeding effect removed from entity ${entity::class.simpleName}.")
            entity.abnormalityStatus.remove(bleeding)
        }
    }
}