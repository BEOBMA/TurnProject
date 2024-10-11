package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

interface TextHandler {
    fun targetingFailText(): Component
    fun manaFailText(): Component
    fun cardUseFailText(): Component
    fun cardNotAvailableText(): Component
}

object TextManager : TextHandler {
    override fun targetingFailText(): Component {
        return Component.text("[!] 카드의 효과와 바라보는 대상의 효과 관계가 비정상적입니다.", TextColorType.Red.textColor).decorate(TextDecoration.BOLD)
    }

    override fun manaFailText(): Component {
        return Component.text("[!] ", TextColorType.Red.textColor).decorate(TextDecoration.BOLD).append(KeywordType.Mana.component.append(
            Component.text("가 부족하여 카드를 사용할 수 없습니다.", TextColorType.Red.textColor)
        ))
    }

    override fun cardUseFailText(): Component {
        return Component.text("[!] 카드 사용의 조건을 만족하지 않아 카드를 사용할 수 없습니다.", TextColorType.Red.textColor).decorate(TextDecoration.BOLD)
    }

    override fun cardNotAvailableText(): Component {
        return Component.text("[!] 사용 불가 효과가 적용된 카드는 사용할 수 없습니다.", TextColorType.Red.textColor).decorate(TextDecoration.BOLD)
    }
}