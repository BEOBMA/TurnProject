package org.beobma.projectturngame.text

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

enum class KeywordType(val component: Component) {
    Mana(Component.text("마나", TextColorType.Blue.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Remnant(Component.text("잔존", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Banish(Component.text("제외", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Graveyard(Component.text("묘지", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Extinction(Component.text("소멸", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Volatilization(Component.text("휘발", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    SameCardDisappears(Component.text("동일 카드 소멸", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Fix(Component.text("고정", TextColorType.DarkGray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    NotAvailable(Component.text("사용 불가", TextColorType.Red.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Shield(Component.text("보호막", TextColorType.Aqua.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    TrueDamage(Component.text("고정 피해", TextColorType.White.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    AlchemYingredientsPile(Component.text("연금술 재료 더미", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    AlchemYingredients(Component.text("연금술 재료", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Ductility(Component.text("연성", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Burn(Component.text("화상", TextColorType.Red.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Weakness(Component.text("나약함", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Blindness(Component.text("실명", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Emerald(Component.text("에메랄드", TextColorType.Green.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Continue(Component.text("지속", TextColorType.Gray.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Time(Component.text("시간", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Stun(Component.text("기절", TextColorType.Yellow.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Reforge(Component.text("재련", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Reforged(Component.text("재련됨", TextColorType.Gold.textColor).decorate(TextDecoration.BOLD).append(Component.text())),
    Protect(Component.text("보호", TextColorType.Blue.textColor).decorate(TextDecoration.BOLD).append(Component.text()))
}