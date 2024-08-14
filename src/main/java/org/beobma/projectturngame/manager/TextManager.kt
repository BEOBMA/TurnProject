package org.beobma.projectturngame.manager

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

interface TextHandler {
    fun targetingFailText(): Component
    fun manaFailText(): Component
}

class DefaultTextManager : TextHandler {
    override fun targetingFailText(): Component {
        return Component.text("[!] 카드의 효과와 바라보는 대상의 효과 관계가 비정상적입니다.", TextColorType.Red.textColor).decorate(TextDecoration.BOLD)
    }

    override fun manaFailText(): Component {
        return Component.text("[!] ", TextColorType.Red.textColor).decorate(TextDecoration.BOLD).append(KeywordType.Mana.component.append(
            Component.text("가 부족하여 카드를 사용할 수 없습니다.", TextColorType.Red.textColor)
        ))
    }
}

class TextManager(private val converter: TextHandler) {
    fun targetingFailText(): Component {
        return converter.run { targetingFailText() }
    }

    fun manaFailText(): Component {
        return converter.run { manaFailText() }
    }
}