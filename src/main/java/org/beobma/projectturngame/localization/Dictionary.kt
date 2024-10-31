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
            "동일 카드 소멸", KeywordType.SameCardDisappears.component.append(Component.text(": 사용 시 패, 덱, 묘지에 있는 카드명이 같은 카드를 모두 제외한다.", TextColorType.Gray.textColor))
        ), Pair(
            "고정", KeywordType.Fix.component.append(Component.text(": 이 카드를 제외한 다른 카드의 효과로 카드가 이동되지 않는다. (만약, 다른 카드 사용의 조건으로 이 카드를 이동시켜야 할 경우 이동되지 않으며 조건을 만족한 것으로 간주한다.)", TextColorType.Gray.textColor))
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
        ), Pair(
            "화상", KeywordType.Burn.component.append(Component.text(": 턴 시작 시 수치만큼 피해를 입고 수치를 절반으로 만든다.", TextColorType.Gray.textColor))
        ), Pair(
            "나약함", KeywordType.Weakness.component.append(Component.text(": 턴 종료 시까지 가하는 피해가 수치만큼 감소한다.", TextColorType.Gray.textColor))
        ), Pair(
            "실명", KeywordType.Blindness.component.append(Component.text(": 공격 적중 시 (수치 x 5)%로 공격이 빗나간다. 이 효과가 발동한 후, 실명을 제거한다.", TextColorType.Gray.textColor))
        ), Pair(
            "에메랄드", KeywordType.Emerald.component.append(Component.text(": 게임 중 재화로서 사용된다.", TextColorType.Gray.textColor))
        ), Pair(
            "지속", KeywordType.Continue.component.append(Component.text(": 카드의 효과가 카드 효과에 명시된 시점까지 지속된다.", TextColorType.Gray.textColor))
        ), Pair(
            "시간", KeywordType.Time.component.append(Component.text(": 특정 카드의 효과로 소모된다.", TextColorType.Gray.textColor))
        ), Pair(
            "기절", KeywordType.Stun.component.append(Component.text(": 턴 시작 시 강제로 턴이 종료된다. 이 효과가 발동한 후, 기절을 제거한다.", TextColorType.Gray.textColor))
        ), Pair(
            "재련", KeywordType.Reforge.component.append(Component.text(": Q를 누르면 마나를 추가로 1 소모하고 같은 카드명을 가진 카드들을 이번 전투동안 재련된 상태로 만든다.", TextColorType.Gray.textColor))
        ), Pair(
            "재련됨", KeywordType.Reforged.component.append(Component.text(": 재련되어 강화된 상태의 카드로서, 전투 종료 시 모든 카드는 재련되기 전으로 돌아간다.", TextColorType.Gray.textColor))
        ), Pair(
            "보호", KeywordType.Reforged.component.append(Component.text(": 피격 시 받는 피해가 수치만큼 감소한다. 턴 시작 시 보호를 제거한다.", TextColorType.Gray.textColor))
        )
    )
}