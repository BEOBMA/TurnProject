package org.beobma.projectturngame.entity.player

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.entity.Entity
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class Player(
    val player: Player,
    var mana: Int,
    var maxMana: Int,
    val hand: MutableList<Card>,
    val deck: MutableList<Card>,
    val graveyard: MutableList<Card>,
    val banish: MutableList<Card>,
    val turnStartUnit: MutableList<() -> Unit> = mutableListOf(),
    val turnEndUnit: MutableList<() -> Unit> = mutableListOf(),
    var diceWeight: Int = 0,
    val alchemYingredientsPile: MutableList<Card> = mutableListOf()

) : Entity(player.name, (player.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue).toInt(), (player.health).toInt(), 3)