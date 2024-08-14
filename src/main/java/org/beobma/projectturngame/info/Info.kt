package org.beobma.projectturngame.info

import org.beobma.projectturngame.game.Game

object Info {
    var game: Game? = null

    fun isGaming(): Boolean {
        return game is Game
    }
}