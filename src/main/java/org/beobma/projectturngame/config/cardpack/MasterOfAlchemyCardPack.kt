package org.beobma.projectturngame.config.cardpack

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.continueeffect.ContinueEffect
import org.beobma.projectturngame.continueeffect.ContinueEffectHandler
import org.beobma.projectturngame.entity.Entity
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.BlindnessManager.increaseBlindness
import org.beobma.projectturngame.manager.BurnManager.getBurn
import org.beobma.projectturngame.manager.BurnManager.increaseBurn
import org.beobma.projectturngame.manager.CardManager.addCard
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.ParticleManager.spawnSphereParticles
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.addShield
import org.beobma.projectturngame.manager.SelectionFactordManager.allEnemyMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.manager.WeaknessManager.increaseWeakness
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.EffectTime
import org.bukkit.Particle
import org.bukkit.Sound

class MasterOfAlchemyCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("<gray>연금술의 대가",
            listOf(
                "<gray>각기 다른 카드를 조합하여 새로운 카드를 만든다."
            ), mutableListOf(), mutableListOf(), CardPackType.Limitation
        )


        //region attack Common Initialization
        val attack = Card(
            "공격", listOf(
                "<gray>바라보는 적에게 7의 피해를 입힌다."
            ), CardRarity.Common, 1, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(7, usePlayerData)
                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                return@Card true
            }
        )
        //endregion

        //region defense Common Initialization
        val defense = Card(
            "수비", listOf(
                "<gray>10의 피해를 막는 ${KeywordType.Shield.string}을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Shield]!!
            ), CardRarity.Common, 1, { usePlayerData, _ ->
                val player = usePlayerData.player

                usePlayerData.addShield(10)
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                return@Card true
            }
        )
        //endregion

        //region rest Common Initialization
        val rest = Card(
            "휴식", listOf(
                "${KeywordType.Mana.string}를 1 회복한다."
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player

                usePlayerData.addMana(1)
                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                return@Card true
            }
        )
        //endregion

        //region drow Common Initialization
        val drow = Card(
            "뽑기", listOf(
                "<gray>덱에서 카드를 1장 뽑는다."
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player

                usePlayerData.drow(1)

                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                return@Card true
            }
        )
        //endregion

        //region alchemy ingredients Initialization
        val water = Card(
            "물", listOf(
        KeywordType.NotAvailable.string,
        KeywordType.AlchemYingredients.string,
        "",
        "<dark_gray>그 누구도 물 없이 살 수는 없었다.",
        "",
        dictionary.dictionaryList[KeywordType.NotAvailable]!!,
        dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
        ), CardRarity.Common, 0
        )

        val fire = Card(
            "불", listOf(
                KeywordType.NotAvailable.string,
                KeywordType.AlchemYingredients.string,
                "",
                "<dark_gray>불을 발견한 사람은 인류를 창조했다.",
                "",
                dictionary.dictionaryList[KeywordType.NotAvailable]!!,
                dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
            ), CardRarity.Common, 0
        )

        val dirt = Card(
            "흙", listOf(
                KeywordType.NotAvailable.string,
                KeywordType.AlchemYingredients.string,
                "",
                "<dark_gray>흙은 모든 생명의 어머니이자 보호자다.",
                "",
                dictionary.dictionaryList[KeywordType.NotAvailable]!!,
                dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
            ), CardRarity.Common, 0
        )

        val air = Card(
            "공기", listOf(
                KeywordType.NotAvailable.string,
                KeywordType.AlchemYingredients.string,
                "",
                "<dark_gray>공기는 우리의 첫 번째 음식이다.",
                "",
                dictionary.dictionaryList[KeywordType.NotAvailable]!!,
                dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
            ), CardRarity.Common, 0
        )


        val river = Card(
            "강", listOf(
                KeywordType.Volatilization.string,
                "",
                "<blue><bold>마나</bold><gray>를 2 회복한다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.addMana(2)
                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                return@Card true
            }
        )

        val sun = Card(
            "태양", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>모든 적에게 <red><bold>화상</bold><gray>을 10 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!,
                dictionary.dictionaryList[KeywordType.Burn]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()

                player.world.playSound(player.location, Sound.ITEM_FIRECHARGE_USE, 1.0F, 0.5F)

                targets.forEach {
                    player.world.spawnParticle(Particle.FLAME, it.entity.location, 30, 0.0, 0.0, 0.0, 0.2)
                    it.increaseBurn(10, usePlayerData)
                }

                return@Card true
            }
        )

        val earth = Card(
            "대지", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>50의 피해를 막는 <aqua><bold>보호막</bold><gray>을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!,
                dictionary.dictionaryList[KeywordType.Shield]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.addShield(50)

                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                spawnSphereParticles(player, Particle.END_ROD, 2.0, 300)
                return@Card true
            }
        )

        val wind = Card(
            "바람", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>덱에서 카드를 4장 뽑는다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.drow(4)
                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                return@Card true
            }
        )

        val steam = Card(
            "증기", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>바라보는 적에게 <dark_gray><bold>나약함</bold><gray>을 7 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!,
                dictionary.dictionaryList[KeywordType.Weakness]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                player.world.playSound(player.location, Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 1.0F)
                spawnSphereParticles(target.entity, Particle.ASH, 2.0, 300)
                target.increaseWeakness(7, usePlayerData)
                return@Card true
            }
        )

        val mud = Card(
            "진흙", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>모든 적의 속도가 3 감소한다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()

                targets.forEach {
                    it.speed -= 3
                    spawnSphereParticles(it.entity, Particle.ASH, 2.0, 100)
                }

                player.world.playSound(player.location, Sound.BLOCK_SLIME_BLOCK_STEP, 1.0F, 0.5F)
                return@Card true
            }
        )

        val fog = Card(
            "안개", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>모든 적에게 <dark_gray><bold>실명</bold><gray>을 7 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!,
                dictionary.dictionaryList[KeywordType.Blindness]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()

                player.world.playSound(player.location, Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 0.5F)
                targets.forEach {
                    it.increaseBlindness(7, usePlayerData)
                    spawnSphereParticles(it.entity, Particle.CLOUD, 2.0, 50)
                }

                return@Card true
            }
        )

        val lava = Card(
            "용암", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>모든 적에게 20의 피해를 입힌다. 대상에게 <red><bold>화상</bold><gray>이 있었다면 추가로 20의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!,
                dictionary.dictionaryList[KeywordType.Burn]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()

                player.world.playSound(player.location, Sound.ITEM_BUCKET_FILL_LAVA, 1.0F, 0.5F)
                targets.forEach {
                    it.damage(20, usePlayerData)

                    if (it.getBurn() is AbnormalityStatus) {
                        it.damage(20, usePlayerData)
                        player.world.spawnParticle(Particle.FLAME, it.entity.location, 40, 0.0, 0.0, 0.0, 0.1)
                    }
                }

                return@Card true
            }
        )

        val lightning = Card(
            "번개", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>무작위 적에게 6의 피해를 입힌다.",
                "<gray>위 효과는 3번 사용하며, 첫 번째 대상에게 여러번 적중할 때마다 추가로 6의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()
                val firstTarget = targets.random()

                player.world.playSound(player.location, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 0.4F, 0.5F)
                firstTarget.damage(6, usePlayerData)
                repeat(2) {
                    if (targets.isEmpty()) return@Card true
                    val target = targets.random()

                    player.world.spawnParticle(Particle.WAX_ON, target.entity.location, 30, 0.0, 0.0, 0.0, 0.2)
                    target.damage(6, usePlayerData)
                    if (target == firstTarget) {
                        target.damage(6, usePlayerData)
                    }
                }
                return@Card true
            }
        )

        val dust = Card(
            "먼지", listOf(
                KeywordType.Volatilization.string,
                "",
                "<gray>바라보는 적에게 <dark_gray><bold>실명</bold><gray>을 10 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Volatilization]!!,
                dictionary.dictionaryList[KeywordType.Blindness]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 1.0F)
                spawnSphereParticles(target.entity, Particle.ASH, 2.0, 300)
                target.increaseBlindness(10, usePlayerData)

                return@Card true
            }
        )
        //endregion


        //region lesserConjugation Common Initialization
        val lesserConjugation = Card(
            "하급 연성", listOf(
                "<gold><bold>연금술 재료 더미</bold><gray>의 카드들 중, 무작위 카드 2장을 <gold><bold>연성</bold><gray>한다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredientsPile]!!,
                dictionary.dictionaryList[KeywordType.Ductility]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val ingredients = usePlayerData.alchemYingredientsPile.shuffled().take(2)
                val (cardA, cardB) = if (ingredients.size < 2) {
                    return@Card false
                } else ingredients

                val resultCard = when (listOf(cardA.name, cardB.name).sorted()) {
                    listOf("물", "물") -> river
                    listOf("불", "불") -> sun
                    listOf("흙", "흙") -> earth
                    listOf("공기", "공기") -> wind
                    listOf("물", "불") -> steam
                    listOf("물", "흙") -> mud
                    listOf("물", "공기") -> fog
                    listOf("불", "흙") -> lava
                    listOf("불", "공기") -> lightning
                    listOf("흙", "공기") -> dust
                    else -> null
                }

                if (resultCard == null) return@Card false

                player.world.playSound(player.location, Sound.BLOCK_BREWING_STAND_BREW, 1.0F, 0.7F)
                usePlayerData.addCard(resultCard)
                usePlayerData.alchemYingredientsPile.removeAll(listOf(cardA, cardB))

                return@Card true

            }
        )
        //endregion

        //region kilnOfCreation Common Initialization
        val kilnOfCreation = Card(
            "창조의 가마", listOf(
                "<gray>'연금술의 대가' 카드팩에 존재하는 <gold><bold>연금술 재료 </bold><gray>카드들 중, 무작위 3장을 생성하고 패에 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cardList = listOf(water, fire, dirt, air)

                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                usePlayerData.addCard(cardList.random())
                usePlayerData.addCard(cardList.random())
                usePlayerData.addCard(cardList.random())
                return@Card true
            }
        )
        //endregion

        //region materialReproduction Common Initialization
        val materialReproduction = Card(
            "재료 복제", listOf(
                "<gold><bold>연금술 재료 더미</bold><gray>의 무작위 카드 1장과 동일한 카드를 생성하고 <gold><bold>연금술 재료 더미</bold><gray>에 2장 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredientsPile]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cards = usePlayerData.alchemYingredientsPile

                if (cards.isEmpty()) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                val card = cards.random()
                usePlayerData.alchemYingredientsPile.add(card)
                usePlayerData.alchemYingredientsPile.add(card)
                return@Card true
            }
        )
        //endregion


        //region intermediateSoftness Uncommon Initialization
        val intermediateSoftness = Card(
            "중급 연성", listOf(
                "<gold><bold>연금술 재료 더미</bold><gray>의 카드들 중, 무작위 카드 2장을 <gold><bold>연성</bold><gray>한다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredientsPile]!!,
                dictionary.dictionaryList[KeywordType.Ductility]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val ingredients = usePlayerData.alchemYingredientsPile.shuffled().take(2)
                if (ingredients.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val (cardA, cardB) = ingredients

                val resultCard = when (listOf(cardA.name, cardB.name).sorted()) {
                    listOf("물", "물") -> river
                    listOf("불", "불") -> sun
                    listOf("흙", "흙") -> earth
                    listOf("공기", "공기") -> wind
                    listOf("물", "불") -> steam
                    listOf("물", "흙") -> mud
                    listOf("물", "공기") -> fog
                    listOf("불", "흙") -> lava
                    listOf("불", "공기") -> lightning
                    listOf("흙", "공기") -> dust
                    else -> null
                }

                if (resultCard == null) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_BREWING_STAND_BREW, 1.0F, 0.7F)
                usePlayerData.addCard(resultCard)
                usePlayerData.alchemYingredientsPile.removeAll(listOf(cardA, cardB))

                return@Card true

            }
        )
        //endregion\

        //region disappearanceOfMaterials Uncommon Initialization
        val disappearanceOfMaterials = Card(
            "재료의 소멸", listOf(
                "<gold><bold>연금술 재료 더미</bold><gray>의 카드들 중, 무작위 카드 1장을 소멸시키고 발동할 수 있다.",
                "<gray>덱에서 카드를 2장 뽑고 <blue><bold>마나</bold><gray>를 3 회복한다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredientsPile]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.isEmpty()) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0F, 0.5F)
                usePlayerData.alchemYingredientsPile.remove(usePlayerData.alchemYingredientsPile.random())
                usePlayerData.addMana(3)
                usePlayerData.drow(2)

                return@Card true
            }
        )
        //endregion

        //region deactivateConjugation Uncommon Initialization
        val deactivateConjugation = Card(
            "연성 해제", listOf(
                "<gray>패에 <gold><bold>연성</bold><gray>을 통해 생성된 카드를 소멸시키고 발동할 수 있다.",
                "<gray>덱에서 카드를 2장 뽑고 <blue><bold>마나</bold><gray>를 3 회복한다.",
                "<gray>또한, 소멸시킨 카드의 재료가 되는 카드를 생성하고 <gold><bold>연금술 재료 더미</bold><gray>에 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.Ductility]!!,
                dictionary.dictionaryList[KeywordType.AlchemYingredientsPile]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cards = listOf(river, sun, earth, wind, steam, mud, fog, lava, lightning, dust)

                if (usePlayerData.hand.none { cards.contains(it) }) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                val cardList = usePlayerData.hand.filter { cards.contains(it) }
                val card = cardList.random()

                player.world.playSound(player.location, Sound.ENTITY_WIND_CHARGE_WIND_BURST, 1.0F, 0.5F)
                when (card) {
                    river -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(water)
                    }
                    sun -> {
                        usePlayerData.alchemYingredientsPile.add(sun)
                        usePlayerData.alchemYingredientsPile.add(sun)
                    }
                    earth -> {
                        usePlayerData.alchemYingredientsPile.add(dirt)
                        usePlayerData.alchemYingredientsPile.add(dirt)
                    }
                    wind -> {
                        usePlayerData.alchemYingredientsPile.add(air)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    steam -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(fire)
                    }
                    mud -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(dirt)
                    }
                    fog -> {
                        usePlayerData.alchemYingredientsPile.add(water)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    lava -> {
                        usePlayerData.alchemYingredientsPile.add(fire)
                        usePlayerData.alchemYingredientsPile.add(dirt)
                    }
                    lightning -> {
                        usePlayerData.alchemYingredientsPile.add(fire)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    dust -> {
                        usePlayerData.alchemYingredientsPile.add(dirt)
                        usePlayerData.alchemYingredientsPile.add(air)
                    }
                    else -> {
                        return@Card false
                    }
                }
                usePlayerData.addMana(3)
                usePlayerData.drow(2)

                return@Card true
            }
        )
        //endregion


        //region urgentDelivery Rare Initialization
        val urgentDelivery = Card(
            "긴급 배달", listOf(
                "<gray>'연금술의 대가' 카드팩에 존재하는 <gold><bold>연금술 재료</bold><gray> 카드들 중, 무작위 카드를 가능한 만큼 생성하고 패에 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cardList = listOf(water, fire, dirt, air)

                player.world.playSound(player.location, Sound.ENTITY_SNIFFER_STEP, 1.0F, 2.0F)
                while (usePlayerData.hand.size < 9) {
                    usePlayerData.addCard(cardList.random())
                }
                return@Card true
            }
        )
        //endregion


        //region substitutionDuctility Legend Initialization
        val substitutionDuctility = Card(
            "치환 연성", listOf(
                "<gold><bold>연금술 재료 더미</bold><gray>의 카드들 중, 무작위 카드 2장을 <gold><bold>연성</bold><gray>한 것으로 간주하고 <gold><bold>연성</bold><gray>한 카드를 패에 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.AlchemYingredientsPile]!!,
                dictionary.dictionaryList[KeywordType.Ductility]!!
            ), CardRarity.Legend, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                if (usePlayerData.alchemYingredientsPile.size < 2) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val cardA = usePlayerData.alchemYingredientsPile.random()
                val cardB = usePlayerData.alchemYingredientsPile
                    .filter { it != cardA }
                    .randomOrNull() ?: cardA.copy()

                val resultCard = when (listOf(cardA.name, cardB.name).sorted()) {
                    listOf("물", "물") -> river
                    listOf("불", "불") -> sun
                    listOf("흙", "흙") -> earth
                    listOf("공기", "공기") -> wind
                    listOf("물", "불") -> steam
                    listOf("물", "흙") -> mud
                    listOf("물", "공기") -> fog
                    listOf("불", "흙") -> lava
                    listOf("불", "공기") -> lightning
                    listOf("흙", "공기") -> dust
                    else -> null
                }

                if (resultCard == null) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_BREWING_STAND_BREW, 1.0F, 0.7F)
                usePlayerData.addCard(resultCard)

                return@Card true
            }
        )
        //endregion

        //region continuousDelivery Legend Initialization
        val continuousDelivery = Card(
            "지속 배달", listOf(
                KeywordType.Continue.string,
                "",
                "<gray>전투 종료 시까지 매 턴을 시작할 때 '연금술의 대가' 카드팩에 존재하는 <gold><bold>연금술 재료</bold><gray> 카드들 중, 무작위 카드를 생성하고 패에 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.Continue]!!,
                dictionary.dictionaryList[KeywordType.AlchemYingredients]!!
            ), CardRarity.Legend, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val game = Info.game ?: return@Card false
                val cardList = listOf(water, fire, dirt, air)

                player.world.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.END_ROD, player.location, 10, 0.0, 0.0, 0.0, 0.3)
                game.continueEffects.add(ContinueEffect(usePlayerData, EffectTime.TurnStart, { entity: Entity ->
                    usePlayerData.addCard(cardList.random())
                    player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                } as ContinueEffectHandler
                ))

                return@Card true
            }
        )
        //endregion

        //region ultimateDuctility Legend Initialization
        val ultimateDuctility = Card(
            "궁극적 연성", listOf(
                "<gray>'연금술의 대가' 카드팩에 존재하는 <gold><bold>연성</bold><gray>을 통해 생성되는 카드들 중, 무작위 카드를 3장 생성하고 패에 넣는다.",
                "",
                dictionary.dictionaryList[KeywordType.Ductility]!!
            ), CardRarity.Legend, 3,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val cardList = listOf(river, sun, earth, wind, steam, mud, fog, lava, lightning, dust)

                usePlayerData.addCard(cardList.random())
                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)

                return@Card true
            }
        )
        //endregion

        cardPack.cardList.addAll(
            listOf(
                lesserConjugation,
                lesserConjugation,
                lesserConjugation,
                kilnOfCreation,
                kilnOfCreation,
                kilnOfCreation,
                materialReproduction,
                materialReproduction,
                materialReproduction,
                intermediateSoftness,
                intermediateSoftness,
                intermediateSoftness,
                disappearanceOfMaterials,
                disappearanceOfMaterials,
                disappearanceOfMaterials,
                deactivateConjugation,
                deactivateConjugation,
                deactivateConjugation,
                urgentDelivery,
                urgentDelivery,
                urgentDelivery,
                substitutionDuctility,
                continuousDelivery,
                ultimateDuctility
            )
        )

        cardPackList.add(
            cardPack
        )

        cardPack.startCardList.addAll(
            listOf(
                lesserConjugation,
                lesserConjugation,
                lesserConjugation,
                kilnOfCreation,
                kilnOfCreation,
                kilnOfCreation,
                attack,
                attack,
                attack,
                defense,
                defense,
                defense,
                rest,
                rest,
                drow,
                drow
            )
        )
        cardList.addAll(cardPack.cardList)
        cardList.addAll(cardPack.startCardList)
        cardList.addAll(listOf(water, fire, air, dirt, river, sun, earth, wind, steam, mud, fog, lava, lightning, dust))
    }
}