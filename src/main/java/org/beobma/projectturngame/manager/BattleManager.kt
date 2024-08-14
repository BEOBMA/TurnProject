package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.game.GameDifficulty
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity


interface Battlehandler {
    fun spawnNormalEnemy(field: GameField)
    fun spawnHardEnemy(field: GameField)
    fun spawnBossEnemy(field: GameField)

    fun playerLocationRetake()
    fun enemyLocationRetake()
}

class DefaultBattleManager : Battlehandler {
    override fun spawnNormalEnemy(field: GameField) {
        val game = Info.game ?: return
        val enemyManager = EnemyManager(DefaultEnemyManager())
        val difficultyWeight = when (game.gameDifficulty) {
            GameDifficulty.Easy -> 0.75
            GameDifficulty.Normal -> 1.0
            GameDifficulty.Hard -> 1.25
        }

        val healthWeight = (game.tileStep) * difficultyWeight

        fun spawnEntity(entityType: EntityType): LivingEntity {
            val entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), entityType) as LivingEntity
            return entity
        }

        fun spawnEnemy(enemy: Enemy, healthBase: Int) {
            val health = (healthBase * healthWeight).toInt()

            enemyManager.run {
                enemy.set()
                enemy.setMaxHealth(health)
                enemy.setHealth(health)
            }
        }

        when (field) {
            GameField.Forest -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.Cave -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.Sea -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.End -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }
        }
    }

    override fun spawnHardEnemy(field: GameField) {
        val game = Info.game ?: return
        val enemyManager = EnemyManager(DefaultEnemyManager())
        val difficultyWeight = when (game.gameDifficulty) {
            GameDifficulty.Easy -> 0.8
            GameDifficulty.Normal -> 1.0
            GameDifficulty.Hard -> 1.2
        }

        val healthWeight = (game.tileStep) * difficultyWeight

        fun spawnEntity(entityType: EntityType): LivingEntity {
            val entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), entityType) as LivingEntity
            return entity
        }

        fun spawnEnemy(enemy: Enemy, healthBase: Int) {
            val health = (healthBase * healthWeight).toInt()

            enemyManager.run {
                enemy.set()
                enemy.setMaxHealth(health)
                enemy.setHealth(health)
            }
        }

        when (field) {
            GameField.Forest -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.Cave -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.Sea -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.End -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }
        }
    }

    override fun spawnBossEnemy(field: GameField) {
        val game = Info.game ?: return
        val enemyManager = EnemyManager(DefaultEnemyManager())
        val difficultyWeight = when (game.gameDifficulty) {
            GameDifficulty.Easy -> 1.0
            GameDifficulty.Normal -> 1.2
            GameDifficulty.Hard -> 1.4
        }

        val healthWeight = (game.tileStep) * difficultyWeight

        fun spawnEntity(entityType: EntityType): LivingEntity {
            val entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 8.5, -40.0, 0.5, 90F, 0F), entityType) as LivingEntity
            return entity
        }

        fun spawnEnemy(enemy: Enemy, healthBase: Int) {
            val health = (healthBase * healthWeight).toInt()

            enemyManager.run {
                enemy.set()
                enemy.setMaxHealth(health)
                enemy.setHealth(health)
            }
        }

        when (field) {
            GameField.Forest -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.Cave -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.Sea -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }

            GameField.End -> {
                spawnEnemy(Enemy("소", spawnEntity(EntityType.COW)), 25)
                spawnEnemy(Enemy("돼지", spawnEntity(EntityType.PIG)), 20)
                spawnEnemy(Enemy("닭", spawnEntity(EntityType.CHICKEN)), 10)
            }
        }
    }

    override fun playerLocationRetake() {
        val game = Info.game ?: return
        val world = Bukkit.getWorld("world")

        val players = game.playerDatas
        val playerOrigin = when (game.gameField) {
            GameField.Forest -> Location(world, -5.5, -40.0, 0.5, -90F, 0F)
            GameField.Cave, GameField.Sea, GameField.End -> TODO()
        }

        val numPlayers = players.size
        val middleIndex = numPlayers / 2
        for ((index, player) in players.withIndex()) {
            val offset =
                if (numPlayers % 2 == 1) index - middleIndex else if (index < middleIndex) -(middleIndex - index) + 1 else index - middleIndex + 1
            player.player.teleport(playerOrigin.clone().add(0.0, 0.0, offset.toDouble()))
        }
    }

    override fun enemyLocationRetake() {
        val game = Info.game ?: return
        val world = Bukkit.getWorld("world")

        val enemyOrigin = when (game.gameField) {
            GameField.Forest -> Location(world, 6.5, -40.0, 0.5, 90F, 0F)
            GameField.Cave, GameField.Sea, GameField.End -> TODO()
        }

        val enemies = game.gameEnemys
        val numEnemies = enemies.size
        val middleIndex = numEnemies / 2
        for ((index, enemy) in enemies.withIndex()) {
            val offset =
                if (numEnemies % 2 == 1) (index - middleIndex) * 3 else if (index < middleIndex) -(middleIndex - index) * 3 + 1 else (index - middleIndex) * 3 + 1
            enemy.entity.teleport(enemyOrigin.clone().add(0.0, 0.0, offset.toDouble()))
        }
    }

}

class BattleManager(private val converter: Battlehandler) {
    fun spawnNormalEnemy(field: GameField) {
        converter.run { spawnNormalEnemy(field) }
    }

    fun spawnHardEnemy(field: GameField) {
        converter.run { spawnHardEnemy(field) }
    }

    fun spawnBossEnemy(field: GameField) {
        converter.run { spawnBossEnemy(field) }
    }

    fun playerLocationRetake() {
        converter.run { playerLocationRetake() }
    }

    fun enemyLocationRetake() {
        converter.run { enemyLocationRetake() }
    }
}