package org.beobma.projectturngame.abnormalityStatus

import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.text.KeywordType

class AbnormalityStatus(
    val keywordType: KeywordType,
    val caster: MutableList<Entity> = mutableListOf(),
    var power: Int = 0
)