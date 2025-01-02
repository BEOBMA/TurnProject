package org.beobma.projectturngame.config.relics

import org.beobma.projectturngame.config.RelicsConfig.Companion.relicsList
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.BurnManager.increaseBurn
import org.beobma.projectturngame.relics.Relics
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.EffectTime

class BurnEnchantRelics {
    init {
        relicsConfig()
    }

    private fun relicsConfig() {
        val dictionary = Dictionary()
        val relics = Relics("인챈트 - 화상",
            listOf(
            "<gray>공격 적중 시 대상에게 ${KeywordType.Burn.string}</bold><gray>을 1 부여한다.",
            "",
            dictionary.dictionaryList[KeywordType.Burn]!!
            ), EffectTime.OnHit)
        { player, inputList ->
            val entity = inputList[0]
            val damage = inputList[1]
            val damageType = inputList[2]

            if (entity !is Entity) return@Relics 0
            if (damage !is Int) return@Relics 0
            if (damageType !is DamageType) return@Relics 0

            entity.increaseBurn(1, player)
            return@Relics 0
        }
        relicsList.add(relics)
    }
}