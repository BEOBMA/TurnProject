package org.beobma.projectturngame.manager

import org.bukkit.Sound
import org.bukkit.entity.Player

interface SoundHandler {
    fun Player.playTargetingFailSound()
}

class DefaultSoundManager : SoundHandler {
    override fun Player.playTargetingFailSound() {
        this.playSound(this.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F)
    }
}

class SoundManager(private val converter: SoundHandler) {
    fun Player.playTargetingFailSound() {
        converter.run { this@playTargetingFailSound.playTargetingFailSound() }
    }
}