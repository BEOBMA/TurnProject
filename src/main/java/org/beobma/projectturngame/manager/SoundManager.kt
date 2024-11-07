package org.beobma.projectturngame.manager

import org.bukkit.Sound
import org.bukkit.entity.Player

interface SoundHandler {
    fun Player.playCardUsingFailSound()
}

object SoundManager : SoundHandler {
    override fun Player.playCardUsingFailSound() {
        this.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F)
    }
}