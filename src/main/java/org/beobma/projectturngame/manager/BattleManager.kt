package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.enemy.EnemyAction
import org.beobma.projectturngame.game.GameDifficulty
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CustomStackManager.getStack
import org.beobma.projectturngame.manager.CustomStackManager.increaseStack
import org.beobma.projectturngame.manager.CustomStackManager.setStack
import org.beobma.projectturngame.manager.EnemyManager.addShield
import org.beobma.projectturngame.manager.EnemyManager.heal
import org.beobma.projectturngame.manager.EnemyManager.set
import org.beobma.projectturngame.manager.EnemyManager.setHealth
import org.beobma.projectturngame.manager.EnemyManager.setMaxHealth
import org.beobma.projectturngame.manager.PlayerManager.damage
import org.beobma.projectturngame.manager.SelectionFactordManager.allEnemyMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.allTeamMembers
import org.beobma.projectturngame.manager.StunManager.addStun
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.ActionType
import org.beobma.projectturngame.util.DamageType
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import kotlin.math.sqrt


interface Battlehandler {
    fun spawnNormalEnemy(field: GameField)
    fun spawnHardEnemy(field: GameField)
    fun spawnBossEnemy(field: GameField)

    fun playerLocationRetake()
    fun enemyLocationRetake()
}
object BattleManager : Battlehandler {
    private val dictionary = Dictionary()

    override fun spawnNormalEnemy(field: GameField) {
        val game = Info.game ?: return
        val difficultyWeight = when (game.gameDifficulty) {
            GameDifficulty.Easy -> 0.75
            GameDifficulty.Normal -> 1.0
            GameDifficulty.Hard -> 1.25
        }

        val healthWeight = sqrt(game.tileStep.toDouble() * difficultyWeight)

        fun spawnEntity(entityType: EntityType): LivingEntity {
            val entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 0.5, -40.0, 0.5, 90F, 0F), entityType) as LivingEntity
            return entity
        }

        fun spawnEnemy(enemy: Enemy, healthBase: Int) {
            val health = (healthBase * healthWeight).toInt()

            enemy.set()
            enemy.setMaxHealth(health)
            enemy.setHealth(enemy.maxHealth)
        }

        when (field) {
            GameField.Forest -> {
                spawnEnemy(
                    Enemy(
                        "소",
                        spawnEntity(EntityType.COW),
                        listOf(
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 모든 아군의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.allTeamMembers(true).forEach {
                                        it.heal(5, enemy)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 자신의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.heal(3, enemy)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 8의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(8, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 25
                )
                spawnEnemy(
                    Enemy(
                        "돼지",
                        spawnEntity(EntityType.PIG),
                        listOf(
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>아군이 2명 이하일 경우, 모든 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 2
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(3, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 20
                )
                spawnEnemy(
                    Enemy(
                        "닭",
                        spawnEntity(EntityType.CHICKEN),
                        listOf(
                            EnemyAction(
                                "부화",
                                listOf("<gray>아군이 5명 이하인 경우, '새끼 닭'을 소환한다."),
                                ActionType.Summon,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 5
                                },
                                { enemy ->
                                    spawnEnemy(
                                        Enemy(
                                            "새끼 닭",
                                            spawnEntity(EntityType.CHICKEN),
                                            listOf(
                                                EnemyAction(
                                                    "쪼기",
                                                    listOf("<gray>무작위 적에게 1의 피해를 입힌다."),
                                                    ActionType.Attack,
                                                    { enemy ->
                                                        return@EnemyAction true
                                                    },
                                                    { enemy ->
                                                        val targets = enemy.allEnemyMembers()

                                                        targets.random().damage(1, enemy, DamageType.Normal)
                                                    },
                                                    difficulty = GameDifficulty.Easy
                                                )
                                            )
                                        ), 3
                                    )
                                    enemyLocationRetake()
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 2의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(2, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 1의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(1, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 7
                )
            }

            GameField.Cave -> {
                spawnEnemy(
                    Enemy(
                        "소",
                        spawnEntity(EntityType.COW),
                        listOf(
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 모든 아군의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.allTeamMembers(true).forEach {
                                        it.heal(5, enemy)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 자신의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.heal(3, enemy)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 8의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(8, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 25
                )
                spawnEnemy(
                    Enemy(
                        "돼지",
                        spawnEntity(EntityType.PIG),
                        listOf(
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>아군이 2명 이하일 경우, 모든 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 2
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(3, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 20
                )
                spawnEnemy(
                    Enemy(
                        "닭",
                        spawnEntity(EntityType.CHICKEN),
                        listOf(
                            EnemyAction(
                                "부화",
                                listOf("<gray>아군이 5명 이하인 경우, '새끼 닭'을 소환한다."),
                                ActionType.Summon,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 6
                                },
                                { enemy ->
                                    spawnEnemy(
                                        Enemy(
                                            "새끼 닭",
                                            spawnEntity(EntityType.CHICKEN),
                                            listOf(
                                                EnemyAction(
                                                    "쪼기",
                                                    listOf("<gray>무작위 적에게 1의 피해를 입힌다."),
                                                    ActionType.Attack,
                                                    { enemy ->
                                                        return@EnemyAction true
                                                    },
                                                    { enemy ->
                                                        val targets = enemy.allEnemyMembers()

                                                        targets.random().damage(1, enemy, DamageType.Normal)
                                                    },
                                                    difficulty = GameDifficulty.Easy
                                                )
                                            )
                                        ), 3
                                    )
                                    enemyLocationRetake()
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 2의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(2, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 1의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(1, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 7
                )
            }

            GameField.Sea -> {
                spawnEnemy(
                    Enemy(
                        "소",
                        spawnEntity(EntityType.COW),
                        listOf(
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 모든 아군의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.allTeamMembers(true).forEach {
                                        it.heal(5, enemy)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 자신의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.heal(3, enemy)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 8의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(8, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 25
                )
                spawnEnemy(
                    Enemy(
                        "돼지",
                        spawnEntity(EntityType.PIG),
                        listOf(
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>아군이 2명 이하일 경우, 모든 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 2
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(3, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 20
                )
                spawnEnemy(
                    Enemy(
                        "닭",
                        spawnEntity(EntityType.CHICKEN),
                        listOf(
                            EnemyAction(
                                "부화",
                                listOf("<gray>아군이 5명 이하인 경우, '새끼 닭'을 소환한다."),
                                ActionType.Summon,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 6
                                },
                                { enemy ->
                                    spawnEnemy(
                                        Enemy(
                                            "새끼 닭",
                                            spawnEntity(EntityType.CHICKEN),
                                            listOf(
                                                EnemyAction(
                                                    "쪼기",
                                                    listOf("<gray>무작위 적에게 1의 피해를 입힌다."),
                                                    ActionType.Attack,
                                                    { enemy ->
                                                        return@EnemyAction true
                                                    },
                                                    { enemy ->
                                                        val targets = enemy.allEnemyMembers()

                                                        targets.random().damage(1, enemy, DamageType.Normal)
                                                    },
                                                    difficulty = GameDifficulty.Easy
                                                )
                                            )
                                        ), 3
                                    )
                                    enemyLocationRetake()
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 2의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(2, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 1의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(1, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 7
                )
            }

            GameField.End -> {
                spawnEnemy(
                    Enemy(
                        "소",
                        spawnEntity(EntityType.COW),
                        listOf(
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 모든 아군의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.allTeamMembers(true).forEach {
                                        it.heal(5, enemy)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "휴식",
                                listOf("<gray>자신의 체력이 50% 미만일 경우, 자신의 체력을 5 회복시킨다."),
                                ActionType.Heal, { enemy ->
                                    return@EnemyAction enemy.maxHealth / 2 > enemy.health

                                },
                                { enemy ->
                                    enemy.heal(3, enemy)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 8의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(8, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 25
                )
                spawnEnemy(
                    Enemy(
                        "돼지",
                        spawnEntity(EntityType.PIG),
                        listOf(
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>아군이 2명 이하일 경우, 모든 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 2
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 5의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(5, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "몸통 박치기",
                                listOf("<gray>무작위 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction

                                    game.playerDatas.random().damage(3, enemy, DamageType.Normal)
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 20
                )
                spawnEnemy(
                    Enemy(
                        "닭",
                        spawnEntity(EntityType.CHICKEN),
                        listOf(
                            EnemyAction(
                                "부화",
                                listOf("<gray>아군이 5명 이하인 경우, '새끼 닭'을 소환한다."),
                                ActionType.Summon,
                                { enemy ->
                                    val game = Info.game ?: return@EnemyAction false

                                    return@EnemyAction game.gameEnemys.size <= 6
                                },
                                { enemy ->
                                    spawnEnemy(
                                        Enemy(
                                            "새끼 닭",
                                            spawnEntity(EntityType.CHICKEN),
                                            listOf(
                                                EnemyAction(
                                                    "쪼기",
                                                    listOf("<gray>무작위 적에게 1의 피해를 입힌다."),
                                                    ActionType.Attack,
                                                    { enemy ->
                                                        return@EnemyAction true
                                                    },
                                                    { enemy ->
                                                        val targets = enemy.allEnemyMembers()

                                                        targets.random().damage(1, enemy, DamageType.Normal)
                                                    },
                                                    difficulty = GameDifficulty.Easy
                                                )
                                            )
                                        ), 3
                                    )
                                    enemyLocationRetake()
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 2의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(2, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "알 뿌리기",
                                listOf("<gray>무작위 적에게 1의 피해를 입힌다. 이 효과는 3번 발동한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    repeat(3) {
                                        targets.random().damage(1, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Easy
                            )
                        )
                    ), 7
                )
            }
        }
    }

    override fun spawnHardEnemy(field: GameField) {
        val game = Info.game ?: return
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

            enemy.set()
            enemy.setMaxHealth(health)
            enemy.setHealth(enemy.maxHealth)
        }

        when (field) {
            GameField.Forest -> {
                spawnEnemy(
                    Enemy(
                        "스니퍼",
                        spawnEntity(EntityType.SNIFFER),
                        listOf(
                            EnemyAction(
                                "경계",
                                listOf("<gray>자신이 경계 상태가 아니라면, 경계 상태가 된다."),
                                ActionType.Special, { enemy ->
                                    if (enemy.entity.scoreboardTags.contains("not_boundary")) return@EnemyAction false

                                    return@EnemyAction !enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    enemy.entity.scoreboardTags.add("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 회복",
                                listOf(
                                    "<gray>자신이 경계 상태이고 체력이 25% 미만이라면, 자신의 체력을 최대 체력의 20% 만큼 회복한다. 이후 경계 상태를 해제하며 더 이상 경계 상태가 될 수 없다."),
                                ActionType.Heal, { enemy ->
                                    if (!enemy.entity.scoreboardTags.contains("boundary")) return@EnemyAction false
                                    if (enemy.maxHealth / 4 <= enemy.health) return@EnemyAction false

                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.heal(enemy.maxHealth / 5, enemy)
                                    enemy.entity.scoreboardTags.remove("boundary")
                                    enemy.entity.scoreboardTags.add("not_boundary")
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 7의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(7, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 5의 피해를 입힌다. 이후 경계 상태를 해제한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "저항",
                                listOf("<gray>모든 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(3, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                        )
                    ), 50
                )
            }

            GameField.Cave -> {
                spawnEnemy(
                    Enemy(
                        "스니퍼",
                        spawnEntity(EntityType.SNIFFER),
                        listOf(
                            EnemyAction(
                                "경계",
                                listOf("<gray>자신이 경계 상태가 아니라면, 경계 상태가 된다."),
                                ActionType.Special, { enemy ->
                                    if (enemy.entity.scoreboardTags.contains("not_boundary")) return@EnemyAction false

                                    return@EnemyAction !enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    enemy.entity.scoreboardTags.add("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 회복",
                                listOf("<gray>자신이 경계 상태이고 체력이 25% 미만이라면, 자신의 체력을 최대 체력의 20% 만큼 회복한다. 이후 경계 상태를 해제하며 더 이상 경계 상태가 될 수 없다."),
                                ActionType.Heal, { enemy ->
                                    if (!enemy.entity.scoreboardTags.contains("boundary")) return@EnemyAction false
                                    if (enemy.maxHealth / 4 <= enemy.health) return@EnemyAction false

                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.heal(enemy.maxHealth / 5, enemy)
                                    enemy.entity.scoreboardTags.remove("boundary")
                                    enemy.entity.scoreboardTags.add("not_boundary")
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 5의 피해를 입힌다. 이후 경계 상태를 해제한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 7의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(7, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "저항",
                                listOf("<gray>모든 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(3, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                        )
                    ), 50
                )
            }

            GameField.Sea -> {
                spawnEnemy(
                    Enemy(
                        "스니퍼",
                        spawnEntity(EntityType.SNIFFER),
                        listOf(
                            EnemyAction(
                                "경계",
                                listOf("<gray>자신이 경계 상태가 아니라면, 경계 상태가 된다."),
                                ActionType.Special, { enemy ->
                                    if (enemy.entity.scoreboardTags.contains("not_boundary")) return@EnemyAction false

                                    return@EnemyAction !enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    enemy.entity.scoreboardTags.add("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 회복",
                                listOf("<gray>자신이 경계 상태이고 체력이 25% 미만이라면, 자신의 체력을 최대 체력의 20% 만큼 회복한다. 이후 경계 상태를 해제하며 더 이상 경계 상태가 될 수 없다."),
                                ActionType.Heal, { enemy ->
                                    if (!enemy.entity.scoreboardTags.contains("boundary")) return@EnemyAction false
                                    if (enemy.maxHealth / 4 <= enemy.health) return@EnemyAction false

                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.heal(enemy.maxHealth / 5, enemy)
                                    enemy.entity.scoreboardTags.remove("boundary")
                                    enemy.entity.scoreboardTags.add("not_boundary")
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 5의 피해를 입힌다. 이후 경계 상태를 해제한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 7의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(7, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "저항",
                                listOf("<gray>모든 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(3, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                        )
                    ), 50
                )
            }

            GameField.End -> {
                spawnEnemy(
                    Enemy(
                        "스니퍼",
                        spawnEntity(EntityType.SNIFFER),
                        listOf(
                            EnemyAction(
                                "경계",
                                listOf("<gray>자신이 경계 상태가 아니라면, 경계 상태가 된다."),
                                ActionType.Special, { enemy ->
                                    if (enemy.entity.scoreboardTags.contains("not_boundary")) return@EnemyAction false

                                    return@EnemyAction !enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    enemy.entity.scoreboardTags.add("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 회복",
                                listOf("<gray>자신이 경계 상태이고 체력이 25% 미만이라면, 자신의 체력을 최대 체력의 20% 만큼 회복한다. 이후 경계 상태를 해제하며 더 이상 경계 상태가 될 수 없다."),
                                ActionType.Heal, { enemy ->
                                    if (!enemy.entity.scoreboardTags.contains("boundary")) return@EnemyAction false
                                    if (enemy.maxHealth / 4 <= enemy.health) return@EnemyAction false

                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.heal(enemy.maxHealth / 5, enemy)
                                    enemy.entity.scoreboardTags.remove("boundary")
                                    enemy.entity.scoreboardTags.add("not_boundary")
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 5의 피해를 입힌다. 이후 경계 상태를 해제한다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(5, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "경계 - 공격",
                                listOf("<gray>자신이 경계 상태라면, 모든 적에게 7의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction enemy.entity.scoreboardTags.contains("boundary")
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(7, enemy, DamageType.Normal)
                                    }
                                    enemy.entity.scoreboardTags.remove("boundary")
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "저항",
                                listOf("<gray>모든 적에게 3의 피해를 입힌다."),
                                ActionType.Attack,
                                { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    val targets = enemy.allEnemyMembers()

                                    targets.forEach {
                                        it.damage(3, enemy, DamageType.Normal)
                                    }
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                        )
                    ), 50
                )
            }
        }
    }

    override fun spawnBossEnemy(field: GameField) {
        val game = Info.game ?: return
        val difficultyWeight = when (game.gameDifficulty) {
            GameDifficulty.Easy -> 1.0
            GameDifficulty.Normal -> 1.2
            GameDifficulty.Hard -> 1.4
        }

        val healthWeight = (game.tileStep) * difficultyWeight

        fun spawnEntity(entityType: EntityType): LivingEntity {
            val entity = Bukkit.getWorld("world")!!
                .spawnEntity(Location(Bukkit.getWorld("world"), 0.5, -40.0, 0.5, 90F, 0F), entityType) as LivingEntity
            return entity
        }

        fun spawnEnemy(enemy: Enemy, healthBase: Int) {
            val health = (healthBase * healthWeight).toInt()

            enemy.set()
            enemy.setMaxHealth(health)
            enemy.setHealth(enemy.maxHealth)
        }

        when (field) {
            GameField.Forest -> {
                spawnEnemy(
                    Enemy(
                        "파괴수",
                        spawnEntity(EntityType.RAVAGER),
                        listOf(
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 2 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만든다."
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 2
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 10의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(10, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시키고 10의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(10)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시키고 5의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(5)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시킨다."
                                ),
                                ActionType.Special, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                        )
                    ), 70
                )
            }

            GameField.Cave -> {
                spawnEnemy(
                    Enemy(
                        "파괴수",
                        spawnEntity(EntityType.RAVAGER),
                        listOf(
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 2 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만든다."
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 2
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 10의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(10, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시키고 10의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(10)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시키고 5의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(5)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시킨다."
                                ),
                                ActionType.Special, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                        )
                    ), 70
                )
            }

            GameField.Sea -> {
                spawnEnemy(
                    Enemy(
                        "파괴수",
                        spawnEntity(EntityType.RAVAGER),
                        listOf(
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 2 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만든다."
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 2
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 10의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(10, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시키고 10의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(10)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "자신의 기력 수치를 1 증가시키고 5의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(5)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시킨다."
                                ),
                                ActionType.Special, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                        )
                    ), 70
                )
            }

            GameField.End -> {
                spawnEnemy(
                    Enemy(
                        "파괴수",
                        spawnEntity(EntityType.RAVAGER),
                        listOf(
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 2 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만든다."
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 2
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 20의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(20, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "돌진",
                                listOf(
                                    "<gray>자신의 기력 수치가 3 이상이라면, 모든 적에게 10의 피해를 입힌다. 이후 기력을 0으로 만들며 자신은 ${KeywordType.Stun.string} 상태가 된다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Stun]!!
                                ),
                                ActionType.Attack, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.allEnemyMembers().forEach {
                                        it.damage(10, enemy, DamageType.Normal)
                                    }
                                    enemy.setStack("energy", 0)
                                    enemy.addStun()
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시키고 10의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(10)
                                },
                                difficulty = GameDifficulty.Hard
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "자신의 기력 수치를 1 증가시키고 5의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                                    "",
                                    dictionary.dictionaryList[KeywordType.Shield]!!
                                ),
                                ActionType.Defense, { enemy ->
                                    return@EnemyAction true
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                    enemy.addShield(5)
                                },
                                difficulty = GameDifficulty.Normal
                            ),
                            EnemyAction(
                                "기모으기",
                                listOf(
                                    "<gray>자신의 기력 수치를 1 증가시킨다."
                                ),
                                ActionType.Special, { enemy ->
                                    return@EnemyAction enemy.getStack("energy").score >= 3
                                },
                                { enemy ->
                                    enemy.increaseStack("energy", 1)
                                },
                                difficulty = GameDifficulty.Easy
                            ),
                        )
                    ), 70
                )
            }
        }
    }

    override fun playerLocationRetake() {
        val game = Info.game ?: return
        val world = Bukkit.getWorld("world")

        val players = game.playerDatas
        val playerOrigin = when (game.gameField) {
            GameField.Forest -> Location(world, -5.5, -40.0, 0.5, -90F, 0F)
            GameField.Cave, GameField.Sea, GameField.End -> Location(world, -5.5, -40.0, 0.5, -90F, 0F)
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
            GameField.Forest -> Location(world, 0.5, -40.0, 0.5, 90F, 0F)
            GameField.Cave, GameField.Sea, GameField.End -> Location(world, 6.5, -40.0, 0.5, 90F, 0F)
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