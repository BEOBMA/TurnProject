package org.beobma.projectturngame.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

enum class KeywordType(val component: Component) {
    Mana(Component.text("마나", TextColorType.Blue.textColor).decorate(TextDecoration.BOLD)),
    Remnant(Component.text("잔존", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)),
    Banish(Component.text("제외", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD)),
    Graveyard(Component.text("묘지", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)),
    Extinction(Component.text("소멸", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD)),
    SameCardDisappears(Component.text("동일 카드 소멸", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD))
}