package org.beobma.projectturngame.manager

import org.beobma.projectturngame.ProjectTurnGame
import org.beobma.projectturngame.particle.ParticleAnimation
import org.bukkit.scheduler.BukkitRunnable

interface ParticleAnimationHandler{
    fun ParticleAnimation.play()
}

object ParticleAnimationManager : ParticleAnimationHandler {
    private val projectTurnGame = ProjectTurnGame.instance
    var isPlay = false

    override fun ParticleAnimation.play() {
        var index = 0
        isPlay = true

        object : BukkitRunnable() {
            override fun run() {
                if (index >= animation.size) {
                    isPlay = false
                    cancel()
                    return
                }

                animation[index].invoke()
                index++
            }
        }.runTaskTimer(projectTurnGame, 0L, 1L)
    }
}