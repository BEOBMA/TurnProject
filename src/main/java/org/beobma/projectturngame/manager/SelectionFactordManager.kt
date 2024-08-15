package org.beobma.projectturngame.manager

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.entity.player.Player
import org.beobma.projectturngame.info.Info


interface SelectionFactordHandler {
    fun Player.focusOn(): Entity?
    fun Player.allTeamMembers(excludeSelf: Boolean, includeDeceased: Boolean): List<Player>
    fun Player.allEnemyMembers(): List<Enemy>
}

class DefaultSelectionFactordManager : SelectionFactordHandler {
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
}


class SelectionFactordManager(private val converter: SelectionFactordHandler) {
    fun Player.focusOn(): Entity? {
        return converter.run { this@focusOn.focusOn() }
    }

    fun Player.allTeamMembers(excludeSelf: Boolean, includeDeceased: Boolean): List<Player> {
        return converter.run { this@allTeamMembers.allTeamMembers(excludeSelf, includeDeceased) }
    }

    fun Player.allEnemyMembers(): List<Enemy> {
        return converter.run { this@allEnemyMembers.allEnemyMembers() }
    }
}