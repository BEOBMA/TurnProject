package org.beobma.projectturngame.localization

import net.kyori.adventure.text.Component
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType

class Dictionary {
    val dictionaryList: HashMap<String, Component> = hashMapOf(
        Pair(
            "마나", KeywordType.Mana.component.append(Component.text(": 카드를 사용할 떄 소모된다.", TextColorType.Gray.textColor))
        ), Pair(
            "잔존", KeywordType.Remnant.component.append(Component.text(": 사용 시 묘지로 보내지지 않고 패로 되돌아온다.", TextColorType.Gray.textColor))
        ), Pair(
            "제외", KeywordType.Banish.component.append(Component.text(": 제외된 카드는 이번 전투동안 사용할 수 없다.", TextColorType.Gray.textColor))
        ), Pair(
            "묘지", KeywordType.Graveyard.component.append(Component.text(": 카드를 사용하거나 버리면 묘지로 보내진다. 덱에서 카드를 뽑을 때, 덱에 남은 카드가 없다면 묘지에 있는 카드를 덱으로 되돌린다.", TextColorType.Gray.textColor))
        ), Pair(
            "소멸", KeywordType.Extinction.component.append(Component.text(": 사용 시 묘지로 보내지지 않고, 제외된다.", TextColorType.Gray.textColor))
        ), Pair(
            "휘발", KeywordType.Volatilization.component.append(Component.text(": 사용 시 묘지로 보내지지 않고, 게임에서 완전히 소멸한다.", TextColorType.Gray.textColor))
        ), Pair(
            "동일 카드 소멸", KeywordType.SameCardDisappears.component.append(Component.text(": 사용 시 덱, 묘지에 있는 사용된 카드와 동일한 카드를 모두 제외한다.", TextColorType.Gray.textColor))
        ), Pair(
            "고정", KeywordType.Fix.component.append(Component.text(": 이 카드명, 다른 카드명을 가진 카드의 효과로 카드가 이동되지 않는다. (만약, 카드 사용의 조건으로 이동시켜야할 경우 이동되지 않으며 만족한 것으로 간주한다.)", TextColorType.Gray.textColor))
        ), Pair(
            "사용 불가", KeywordType.NotAvailable.component.append(Component.text(": 사용할 수 없다.", TextColorType.Gray.textColor))
        ), Pair(
            "보호막", KeywordType.Shield.component.append(Component.text(": 피해를 받을 때, 수치만큼 피해를 경감하고 경감한 수치 만큼 보호막을 제거한다.", TextColorType.Gray.textColor))
        ), Pair(
            "고정 피해", KeywordType.TrueDamage.component.append(Component.text(": 모든 피해 계산을 무시하고 수치 그대로의 피해를 입힌다.", TextColorType.Gray.textColor))
        ), Pair(
            "연금술 재료 더미", KeywordType.AlchemYingredientsPile.component.append(Component.text(": 연금술 재료 카드를 들고 Q를 누르면 이 더미에 카드를 넣는다. 더미에 들어간 카드를 꺼낼 수는 없다.", TextColorType.Gray.textColor))
        ), Pair(
            "연금술 재료", KeywordType.AlchemYingredients.component.append(Component.text(": Q를 누르면 연금술 재료 더미에 넣을 수 있으며, 새로운 카드를 연성하는데 사용할 수 있다. 다시 꺼낼 수는 없다.", TextColorType.Gray.textColor))
        ), Pair(
            "연성", KeywordType.Ductility.component.append(Component.text(": 연금술 재료 더미에 있는 카드들을 게임에서 소멸시키고 그 카드들로 만들 수 있는 카드를 생성하고 패에 넣는다.", TextColorType.Gray.textColor))
        )
    )
}