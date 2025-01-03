package org.beobma.projectturngame.entity.player

import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.relics.Relics
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

class Player(
    val player: Player,
    var mana: Int,
    var maxMana: Int,
    var cardPack: CardPack,
    var hand: MutableList<Card>,
    var deck: MutableList<Card>,
    var graveyard: MutableList<Card>,
    var banish: MutableList<Card>,
    val relics: MutableList<Relics> = mutableListOf(),
    var emerald: Int = 0,
    val turnStartUnit: MutableList<() -> Unit> = mutableListOf(),
    val turnEndUnit: MutableList<() -> Unit> = mutableListOf(),
    var diceWeight: Int = 0,
    val alchemYingredientsPile: MutableList<Card> = mutableListOf()

) : Entity(player.name, (player.getAttribute(Attribute.MAX_HEALTH)!!.baseValue).toInt(), (player.health).toInt(), 3)