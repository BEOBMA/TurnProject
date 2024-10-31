package org.beobma.projectturngame.manager

import org.bukkit.Sound
import org.bukkit.entity.Player

interface SoundHandler {
    fun Player.playCardUsingFailSound()
    fun Player.playSweepSound()
}

object SoundManager : SoundHandler {
    override fun Player.playCardUsingFailSound() {
        this.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F)
    }

    override fun Player.playSweepSound() {
        this.playSound(this.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
    }
}