package org.beobma.projectturngame.text

import net.kyori.adventure.text.format.TextColor

enum class TextColorType(val textColor: TextColor) {
    Black(TextColor.color(0, 0, 0)),
    White(TextColor.color(255, 255, 255)),
    Gray(TextColor.color(170, 170, 170)),
    DarkGray(TextColor.color(85, 85, 85)),
    Blue(TextColor.color(85, 85, 255)),
    Aqua(TextColor.color(85, 255, 255)),
    Green(TextColor.color(85, 255, 85)),
    Yellow(TextColor.color(255, 255, 85)),
    Gold(TextColor.color(255, 170, 0)),
    DarkPurple(TextColor.color(170, 0, 170)),
    Red(TextColor.color(255, 85, 85)),
    DarkRed(TextColor.color(170, 0, 0))
}