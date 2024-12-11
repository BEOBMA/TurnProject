package org.beobma.projectturngame.config.cardpack

import org.beobma.projectturngame.abnormalityStatus.AbnormalityStatus
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardPack
import org.beobma.projectturngame.card.CardPackType
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.config.CardConfig.Companion.cardPackList
import org.beobma.projectturngame.config.CardConfig.Companion.reforgeCardPair
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.BleedingManager.getBleeding
import org.beobma.projectturngame.manager.BleedingManager.increaseBleeding
import org.beobma.projectturngame.manager.CustomStackManager.getStack
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.PlayerManager.addShield
import org.beobma.projectturngame.manager.ProtectManager.increaseProtect
import org.beobma.projectturngame.manager.SelectionFactordManager.allEnemyMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.allTeamMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.enemyDiffusion
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.StunManager.addStun
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.text.KeywordType
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound

class HammerOfReforgingCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("<gray>재련의 망치",
            listOf(
                "<gray>마나를 추가로 소모하여 카드를 재련하고 추가 효과를 얻을 수 있다."
            ), mutableListOf(), mutableListOf(), CardPackType.Limitation
        )

        //region materialReproduction Common Initialization
        val materialReproduction = Card(
            "쐐기 창", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>바라보는 적에게 5의 피해를 입힌다.",
                "<gold><bold>재련</bold><gray>하면 대신 7의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)

                target.damage(5, usePlayerData)
                return@Card true
            }
        )
        //endregion
        //region materialReproductionReforged Common Initialization
        val materialReproductionReforged = Card(
            "쐐기 창", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>바라보는 적에게 <gold><bold>7</bold><gray>의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 5, 0.1, 0.1, 0.1, 1.0)

                target.damage(7, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region bayonet Common Initialization
        val bayonet = Card(
            "대검", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>바라보는 적과 그 양옆 적에게 7의 피해를 입힌다.",
                "<gold><bold>재련</bold><gray>하면 대신 15의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val targets = usePlayerData.enemyDiffusion(target)

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND, 1.0F, 1.5F)

                targets.forEach {
                    it.damage(7, usePlayerData)
                    player.world.spawnParticle(Particle.SWEEP_ATTACK, it.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                }

                return@Card true
            }
        )
        //endregion
        //region bayonetReforged Common Initialization
        val bayonetReforged = Card(
            "대검", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>바라보는 적과 그 양옆 적에게 <gold><bold>15</bold><gray>의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                val targets = usePlayerData.enemyDiffusion(target)

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND, 1.0F, 0.5F)

                targets.forEach {
                    it.damage(15, usePlayerData)
                    player.world.spawnParticle(Particle.SWEEP_ATTACK, it.entity.location, 20, 0.1, 0.1, 0.1, 1.0)
                }

                return@Card true
            }
        )
        //endregion

        //region bayonetProtection Common Initialization
        val bayonetProtection = Card(
            "대검 방호", listOf(
                KeywordType.Reforge.string,
                "",
                "<blue><bold>보호 </bold><gray>1을 얻는다.",
                "<gold><bold>재련</bold><gray>하면 대신 모든 아군이 3을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!,
                dictionary.dictionaryList[KeywordType.Protect]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                usePlayerData.increaseProtect(1, usePlayerData)
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)

                return@Card true
            }
        )
        //endregion
        //region bayonetProtectionRefirged Common Initialization
        val bayonetProtectionRefirged = Card(
            "대검 방호", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>모든 아군이 <blue><bold>보호 </bold><gray>3을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!,
                dictionary.dictionaryList[KeywordType.Protect]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allTeamMembers(true, false)

                targets.forEach {
                    it.increaseProtect(3, usePlayerData)
                }
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 0.7F)

                return@Card true
            }
        )
        //endregion


        //region hammer Uncommon Initialization
        val hammer = Card(
            "망치", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>바라보는 적에게 10의 피해를 입힌다.",
                "<gold><bold>재련</bold><gray>하면 대신 15의 피해를 입히고 추가로 <yellow><bold>기절</bold><gray> 상태로 만든다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!,
                dictionary.dictionaryList[KeywordType.Stun]!!
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1.0F, 1.5F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(10, usePlayerData)
                return@Card true
            }
        )
        //endregion
        //region hammerReforged Uncommon Initialization
        val hammerReforged = Card(
            "망치", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>바라보는 적에게 <gold><bold>15</bold><gray>의 피해를 입히고 <yellow><bold>기절</bold><gray> 상태로 만든다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!,
                dictionary.dictionaryList[KeywordType.Stun]!!
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(15, usePlayerData)
                target.addStun()
                return@Card true
            }
        )
        //endregion

        //region longSword Uncommon Initialization
        val longSword = Card(
            "장검", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>모든 적에게 7의 피해를 입힌다.",
                "<gold><bold>재련</bold><gray>하면 대신 12의 피해를 입히고 추가로 <dark_red><bold>출혈</bold><gray>을 3 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!,
                dictionary.dictionaryList[KeywordType.Bleeding]!!
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F)
                targets.forEach {
                    it.damage(7, usePlayerData)
                    player.world.spawnParticle(Particle.SWEEP_ATTACK, it.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                }
                return@Card true
            }
        )
        //endregion
        //region longSwordReforged Uncommon Initialization
        val longSwordReforged = Card(
            "장검", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>모든 적에게 <gold><bold>12</bold><gray>의 피해를 입히고 <dark_red><bold>출혈</bold><gray>을 3 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!,
                dictionary.dictionaryList[KeywordType.Bleeding]!!
            ), CardRarity.Uncommon, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F)

                targets.forEach {
                    it.damage(12, usePlayerData)
                    it.increaseBleeding(3, usePlayerData)
                    player.world.spawnParticle(Particle.SWEEP_ATTACK, it.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                    player.world.spawnParticle(Particle.CRIT, it.entity.location, 10, 0.0, 0.0, 0.0, 1.0)
                }
                return@Card true
            }
        )
        //endregion

        //region dagger Uncommon Initialization
        val dagger = Card(
            "단검", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>바라보는 적에게 10의 피해를 입힌다.",
                "<gray>이 카드가 <gold><bold>재련</bold><gray>되어있고, 대상의 체력이 최대체력의 20% 이하라면 추가로 30의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!
            ), CardRarity.Uncommon, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(10, usePlayerData)
                return@Card true
            }
        )
        //endregion
        //region daggerReforged Uncommon Initialization
        val daggerReforged = Card(
            "단검", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>바라보는 적에게 10의 피해를 입힌다.",
                "<gray>대상의 체력이 최대체력의 20% 이하라면 추가로 30의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!
            ), CardRarity.Uncommon, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(10, usePlayerData)
                if (target.maxHealth / 5 >= target.health) {
                    target.damage(30, usePlayerData)
                    player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 2.0F)
                    player.world.spawnParticle(Particle.END_ROD, target.entity.location, 50, 0.0, 0.0, 0.0, 0.4)
                }
                return@Card true
            }
        )
        //endregion


        //region bigAx Rare Initialization
        val bigAx = Card(
            "대도끼", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>바라보는 적에게 15의 피해를 입힌다.",
                "<gray>이 카드가 <gold><bold>재련</bold><gray>되어있고, 대상이 <dark_red><bold>출혈 </bold><gray>상태라면 추가로 7의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!,
                dictionary.dictionaryList[KeywordType.Bleeding]!!
            ), CardRarity.Rare, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(15, usePlayerData)
                return@Card true
            }
        )
        //endregion
        //region bigAxReforged Rare Initialization
        val bigAxReforged = Card(
            "대도끼", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>바라보는 적에게 15의 피해를 입힌다.",
                "<gray>대상이 <dark_red><bold>출혈 </bold><gray>상태라면 추가로 7의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!,
                dictionary.dictionaryList[KeywordType.Bleeding]!!
            ), CardRarity.Rare, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(15, usePlayerData)
                if (target.getBleeding() is AbnormalityStatus) {
                    target.damage(7, usePlayerData)
                    player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND, 1.0F, 2.0F)
                    player.world.spawnParticle(Particle.CRIT, target.entity.location, 30, 0.0, 0.0, 0.0, 1.0)
                }
                return@Card true
            }
        )
        //endregion

        //region shield Rare Initialization
        val shield = Card(
            "방패", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>10의 피해를 막는 <aqua><bold>보호막</bold><gray>을 얻는다.",
                "<gold><bold>재련</bold><gray>하면 대신 20의 피해를 막는다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!,
                dictionary.dictionaryList[KeywordType.Shield]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                usePlayerData.addShield(10)
                return@Card true
            }
        )
        //endregion
        //region shieldReforged Rare Initialization
        val shieldReforged = Card(
            "방패", listOf(
                KeywordType.Reforged.string,
                "",
                "<gold><bold>20</bold><gray>의 피해를 막는 <aqua><bold>보호막</bold><gray>을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!,
                dictionary.dictionaryList[KeywordType.Shield]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 0.5F)
                usePlayerData.addShield(20)
                return@Card true
            }
        )
        //endregion

        //region knuckles Rare Initialization
        val knuckles = Card(
            "너클", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>바라보는 적에게 7의 피해를 입힌다.",
                "<gold><bold>재련</bold><gray>하면 대신 10의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!
            ), CardRarity.Rare, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_AIR, 1.0F, 2.0F)

                target.damage(7, usePlayerData)
                return@Card true
            }
        )
        //endregion
        //region knucklesReforged Rare Initialization
        val knucklesReforged = Card(
            "너클", listOf(
                KeywordType.Reforged.string,
                "",
                "<gray>바라보는 적에게 <gold><bold>10</bold><gray>의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Reforged]!!
            ), CardRarity.Rare, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_MACE_SMASH_AIR, 1.0F, 1.5F)
                player.world.spawnParticle(Particle.CRIT, target.entity.location, 10, 0.0, 0.0, 0.0, 1.0)
                target.damage(10, usePlayerData)
                return@Card true
            }
        )
        //endregion


        //region finalBlow Legend Initialization
        val finalBlow = Card(
            "최후의 일격", listOf(
                KeywordType.Reforge.string,
                "",
                "<gray>이 카드는 여러번 <gold><bold>재련</bold><gray>할 수 있다.",
                "<gray>이 카드는 마나를 소모하여 <gold><bold>재련</bold><gray>할 수 없다. 대신, 다른 카드를 <gold><bold>재련</bold><gray>하면 이 카드 또한 <gold><bold>재련</bold><gray>한다.",
                "",
                "<gray>바라보는 적에게 7의 피해를 입힌다. 이 효과는 이 카드를 <gold><bold>재련</bold><gray>한 횟수만큼 발동한다.",
                "<gray>위 효과를 발동하는 도중 대상이 사망하면 무작위 적 대상에게 이어서 사용한다.",
                "<gray>이 카드는 항상 <gold><bold>재련</bold><gray>되지 않은 것으로 간주한다."
                "",
                dictionary.dictionaryList[KeywordType.Reforge]!!
            ), CardRarity.Legend, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                repeat(usePlayerData.getStack("ReforgeStack").score) {
                    if (target.isDead) {
                        val allEnemy = usePlayerData.allEnemyMembers()

                        if (allEnemy.isEmpty()) return@Card true
                        allEnemy.random().damage(20, usePlayerData)
                        return@repeat
                    }

                    target.damage(7, usePlayerData)
                }
                return@Card true
            }
        )
        //endregion


        cardPack.cardList.addAll(
            listOf(
                materialReproduction,
                materialReproduction,
                materialReproduction,
                bayonet,
                bayonet,
                bayonet,
                bayonetProtection,
                bayonetProtection,
                bayonetProtection,
                hammer,
                hammer,
                hammer,
                longSword,
                longSword,
                longSword,
                dagger,
                dagger,
                dagger,
                bigAx,
                bigAx,
                bigAx,
                shield,
                shield,
                shield,
                knuckles,
                knuckles,
                knuckles,
                finalBlow
            )
        )

        cardPackList.add(
            cardPack
        )

        reforgeCardPair[materialReproduction] = materialReproductionReforged
        reforgeCardPair[bayonet] = bayonetReforged
        reforgeCardPair[bayonetProtection] = bayonetProtectionRefirged
        reforgeCardPair[hammer] = hammerReforged
        reforgeCardPair[longSword] = longSwordReforged
        reforgeCardPair[dagger] = daggerReforged
        reforgeCardPair[bigAx] = bigAxReforged
        reforgeCardPair[shield] = shieldReforged
        reforgeCardPair[knuckles] = knucklesReforged
        cardList.addAll(cardPack.cardList)
        cardList.addAll(listOf(materialReproductionReforged,bayonetReforged, bayonetProtectionRefirged, hammerReforged,
            longSwordReforged, daggerReforged, bigAxReforged, shieldReforged, knucklesReforged))
    }
}