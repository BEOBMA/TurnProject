package org.beobma.projectturngame.config.relics

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.config.RelicsConfig.Companion.relicsList
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.relics.Relics
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.BattleType
import org.beobma.projectturngame.util.EffectTime

class MysteriousDeckRelics {
    init {
        relicsConfig()
    }

    private fun relicsConfig() {
        val relics = Relics("신비한 덱",listOf(
            Component.text("전투 시작 시 덱에서 카드를 1장 추가로 뽑는다.", TextColorType.Gray.textColor)
        ), EffectTime.OnBattleStart)
        { player, inputList ->
            val battleType = inputList[0]

            if (battleType !is BattleType) return@Relics 0
            player.drow(1)
            return@Relics 0
        }
        relicsList.add(relics)
    }
}