package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info


interface SelectionFactordHandler {
    fun Player.focusOn(): Entity?
    fun Player.allTeamMembers(excludeSelf: Boolean, includeDeceased: Boolean): List<Player>
    fun Player.allEnemyMembers(): List<Enemy>
    fun Player.enemyDiffusion(target: Enemy): List<Enemy>

    fun Enemy.allTeamMembers(excludeSelf: Boolean): List<Enemy>
    fun Enemy.allEnemyMembers(): List<Player>
}

object SelectionFactordManager : SelectionFactordHandler {
    override fun Player.focusOn(): Entity? {
        val game = Info.game ?: return null
        val world = this.player.world

        val rayTraceResult = world.rayTraceEntities(
            this.player.eyeLocation, this.player.eyeLocation.direction, 100.0, 0.1
        ) {
            it != this.player
        }

        if (rayTraceResult?.hitEntity is org.bukkit.entity.Entity && rayTraceResult.hitEntity !is org.bukkit.entity.Player) {
            val enemyData = game.gameEnemys.find { it.entity == rayTraceResult.hitEntity }
            return enemyData
        }

        if (rayTraceResult?.hitEntity is org.bukkit.entity.Player) {
            val playerData = game.playerDatas.find { it.player == rayTraceResult.hitEntity }
            return playerData
        }

        return null
    }

    override fun Player.allTeamMembers(excludeSelf: Boolean, includeDeceased: Boolean): List<Player> {
        val game = Info.game ?: return listOf()
        val teamList = game.playerDatas.toMutableList()

        if (excludeSelf) {
            teamList.remove(this)
        }

        if (includeDeceased) {
            teamList.removeAll(teamList.filter { it.isDead })
        }

        return teamList.toList()
    }

    override fun Player.allEnemyMembers(): List<Enemy> {
        val game = Info.game ?: return listOf()
        val enemyList = game.gameEnemys.toMutableList()

        return enemyList.toList()
    }

    override fun Player.enemyDiffusion(target: Enemy): List<Enemy> {
        val game = Info.game ?: return emptyList()
        val allEnemyList = game.gameEnemys
        val index = allEnemyList.indexOf(target)

        if (index != -1) {
            val finalTargets = mutableListOf<Enemy>()
            val left = if (index > 0) allEnemyList[index - 1] else null
            val right = if (index < allEnemyList.size - 1) allEnemyList[index + 1] else null

            if (left is Enemy) finalTargets.add(left)
            finalTargets.add(target)
            if (right is Enemy) finalTargets.add(right)


            return finalTargets
        } else {
            return emptyList()
        }
    }

    override fun Enemy.allTeamMembers(excludeSelf: Boolean): List<Enemy> {
        val game = Info.game ?: return listOf()
        val teamList = game.gameEnemys.toMutableList()

        if (excludeSelf) {
            teamList.remove(this)
        }

        return teamList.toList()
    }

    override fun Enemy.allEnemyMembers(): List<Player> {
        val game = Info.game ?: return listOf()
        val enemyList = game.playerDatas.toMutableList()

        return enemyList.toList()
    }
}