package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Score

interface UtilHandler {
    fun Player.getScore(objective: String): Score
}

object UtilManager : UtilHandler {
    override fun Player.getScore(objective: String): Score {
        if (this.scoreboard.getObjective(objective) !is Objective) {
            scoreboard.registerNewObjective(objective, Criteria.DUMMY, Component.text(objective))
        }
        return this.scoreboard.getObjective(objective)!!.getScore(this.name)
    }
}