package org.beobma.projectturngame.config

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.config.RelicsConfig.Companion.relicsList
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.relics.Relics
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.EffectTime

class CrawfishRelics {
    init {
        relicsConfig()
    }

    private fun relicsConfig() {
        val relics = Relics("가재",listOf(
            Component.text("")
        ), EffectTime.WhenHit)
        { player, inputList ->
            val attacker = inputList[0]
            val damage = inputList[1]
            val damageType = inputList[2]

            if (attacker !is Entity) return@Relics 0
            if (damage !is Int) return@Relics 0
            if (damageType !is DamageType) return@Relics 0

            if (damageType == DamageType.True) return@Relics 0
            return@Relics damage - 1
        }
        relicsList.add(relics)
    }
}