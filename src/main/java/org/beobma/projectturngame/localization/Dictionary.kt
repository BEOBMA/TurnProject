package org.beobma.projectturngame.localization

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class Dictionary {
    val dictionaryList: HashMap<String, Component> = hashMapOf(
        Pair(
            "마나", KeywordType.Mana.component.append(Component.text(": 대부분의 카드를 사용할 때 소모되는 자원입니다.", TextColorType.Gray.textColor))
        ), Pair(
            "잔존", KeywordType.Remnant.component.append(Component.text(": 이 효과가 붙은 카드 사용 시 패로 되돌아옵니다.", TextColorType.Gray.textColor))
        ), Pair(
            "제외", KeywordType.Banish.component.append(Component.text(": 일부 효과로 인해 카드가 제외될 수 있습니다. 제외된 카드는 이번 전투동안 사용할 수 없습니다.", TextColorType.Gray.textColor))
        ), Pair(
            "묘지", KeywordType.Graveyard.component.append(Component.text(": 카드를 사용하거나 버리면 묘지로 보내집니다. 카드를 뽑을 때, 덱에 남은 카드가 없다면 묘지에 있는 카드를 덱으로 되돌립니다.", TextColorType.Gray.textColor))
        ), Pair(
            "소멸", KeywordType.Extinction.component.append(Component.text(": 이 효과가 붙은 카드 사용 시 이 카드가 제외됩니다.", TextColorType.Gray.textColor))
        ), Pair(
            "동일 카드 소멸", KeywordType.SameCardDisappears.component.append(Component.text(": 이 효과가 붙은 카드 사용 시 덱, 묘지에 있는 동일한 카드를 모두 제외합니다.", TextColorType.Gray.textColor))
        )
    )
}