package org.beobma.projectturngame.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

enum class KeywordType(val component: Component) {
    Mana(Component.text("마나", TextColorType.Blue.textColor).decorate(TextDecoration.BOLD)),
    Remnant(Component.text("잔존", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)),
    Banish(Component.text("제외", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD)),
    Graveyard(Component.text("묘지", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)),
    Extinction(Component.text("소멸", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD)),
    Volatilization(Component.text("휘발", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD)),
    SameCardDisappears(Component.text("동일 카드 소멸", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD)),
    Fix(Component.text("고정", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD)),
    NotAvailable(Component.text("사용 불가", TextColorType.Red.textColor).decorate(TextDecoration.BOLD)),
    Shield(Component.text("보호막", TextColorType.Aqua.textColor).decorate(TextDecoration.BOLD)),
    TrueDamage(Component.text("고정 피해", TextColorType.White.textColor).decorate(TextDecoration.BOLD)),
    AlchemYingredientsPile(Component.text("연금술 재료 더미", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD)),
    AlchemYingredients(Component.text("연금술 재료", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD)),
    Ductility(Component.text("연성", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD)),
    Burn(Component.text("화상", TextColorType.Red.textColor).decorate(TextDecoration.BOLD)),
    Weakness(Component.text("나약함", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)),
    Slowness(Component.text("구속", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD)),
    Blindness(Component.text("실명", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD))
}