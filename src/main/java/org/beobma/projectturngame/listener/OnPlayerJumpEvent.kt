package org.beobma.projectturngame.listener

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.manager.GameManager.turnEnd
import org.beobma.projectturngame.manager.ParticleAnimationManager.isPlay
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class OnPlayerJumpEvent : Listener {

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val game = Info.game ?: return

        if (player in game.players) {
            if (player.scoreboardTags.contains("this_Turn")) {
                if (isPlay) {
                    player.sendMessage(Component.text("[!] 카드 사용 연출이 재생중이므로 일시적으로 행동을 수행할 수 없습니다.", TextColorType.Red.textColor)
                        .decorate(TextDecoration.BOLD))
                    event.isCancelled = true
                    return
                }
                val playerData = game.playerDatas.find { it.player == player }

                playerData?.turnEnd()
            }
            event.isCancelled = true
        }
    }
}