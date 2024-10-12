package org.beobma.projectturngame.config

import org.beobma.projectturngame.config.relics.BurnEnchantRelics
import org.beobma.projectturngame.config.relics.CrawfishRelics
import org.beobma.projectturngame.config.relics.MysteriousDeckRelics
import org.beobma.projectturngame.relics.Relics

class RelicsConfig {
    companion object {
        val relicsList: MutableSet<Relics> = mutableSetOf()
    }

    init {
        relicsList.clear()

        relicsConfig()
    }

    private fun relicsConfig() {
        CrawfishRelics()
        BurnEnchantRelics()
        MysteriousDeckRelics()
    }
}