package org.beobma.projectturngame.text

enum class  KeywordType(val string: String, val keywordName: String) {
    Mana("<blue><bold>마나</bold><gray>", "마나"),
    Remnant("<dark_gray><bold>잔존</bold><gray>", "잔존"),
    Banish("<dark_gray><bold>제외</bold><gray>", "제외"),
    Graveyard("<dark_gray><bold>묘지</bold><gray>", "묘지"),
    Extinction("<dark_gray><bold>소멸</bold><gray>", "소멸"),
    Volatilization("<dark_gray><bold>휘발</bold><gray>", "휘발"),
    SameCardDisappears("<dark_gray><bold>동일 카드 소멸</bold><gray>", "동일 카드 소멸"),
    Fix("<dark_gray><bold>고정</bold><gray>", "고정"),
    NotAvailable("<red><bold>사용 불가</bold><gray>", "사용 불가"),
    Shield("<aqua><bold>보호막</bold><gray>", "보호막"),
    TrueDamage("<white><bold>고정피해</bold><gray>", "고정피해"),
    AlchemYingredientsPile("<gold><bold>연금술 재료 더미</bold><gray>", "연금술 재료 더미"),
    AlchemYingredients("<gold><bold>연금술 재료</bold><gray>", "연금술 재료"),
    Ductility("<gold><bold>연성</bold><gray>", "연성"),
    Burn("<red><bold>화상</bold><gray>", "화상"),
    Weakness("<dark_gray><bold>나약함</bold><gray>", "나약함"),
    Blindness("<dark_gray><bold>실명</bold><gray>", "실명"),
    Emerald("<green><bold>에메랄드</bold><gray>", "에메랄드"),
    Continue("<dark_gray><bold>지속</bold><gray>", "지속"),
    Time("<gold><bold>시간</bold><gray>", "시간"),
    Stun("<yellow><bold>기절</bold><gray>", "기절"),
    Reforge("<gold><bold>재련</bold><gray>", "재련"),
    Reforged("<gold><bold>재련됨</bold><gray>", "재련됨"),
    Protect("<blue><bold>보호</bold><gray>", "보호"),
    Bleeding("<dark_red><bold>출혈</bold><gray>", "출혈")
}