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
import org.beobma.projectturngame.manager.CardManager.drow
import org.beobma.projectturngame.manager.DeathResistanceManager.increaseDeathResistance
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.GameManager.turnEnd
import org.beobma.projectturngame.manager.HealthManager.addHealth
import org.beobma.projectturngame.manager.HealthManager.setHealth
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.addShield
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.SelectionFactordManager.allEnemyMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.allTeamMembers
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.StunManager.addStun
import org.beobma.projectturngame.manager.TextManager.cardUseFailText
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.manager.TimeManager.decreaseTime
import org.beobma.projectturngame.manager.TimeManager.getTime
import org.beobma.projectturngame.manager.TimeManager.increaseTime
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.util.DamageType
import org.beobma.projectturngame.util.EffectTime
import org.bukkit.Particle
import org.bukkit.Sound

class RelativityOfTimeCardPack {
    private val dictionary = Dictionary()

    init {
        cardConfig()
    }

    private fun cardConfig() {
        val cardPack = CardPack("<gray>시간의 상대성",
            listOf(
                "<gray>여러 카드의 효과로 시간 수치를 쌓아 추가 턴을 얻는 등. 강력한 효과를 얻는다."
            ), mutableListOf(), mutableListOf(), CardPackType.Limitation
        )


        //region attack Common Initialization
        val attack = Card(
            "공격", listOf(
                "<gray>바라보는 적에게 7의 피해를 입히고 ${KeywordType.Time.string}을 5 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Common, 1, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(7, usePlayerData)
                usePlayerData.increaseTime(5, usePlayerData)
                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                return@Card true
            }
        )
        //endregion

        //region defense Common Initialization
        val defense = Card(
            "수비", listOf(
                "<gray>10의 피해를 막는 ${KeywordType.Shield.string}을 얻고 ${KeywordType.Time.string}을 5 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Shield]!!,
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Common, 1, { usePlayerData, _ ->
                val player = usePlayerData.player

                usePlayerData.addShield(10)
                usePlayerData.increaseTime(5, usePlayerData)
                player.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                return@Card true
            }
        )
        //endregion

        //region rest Common Initialization
        val rest = Card(
            "휴식", listOf(
                "${KeywordType.Mana.string}를 1 회복한다.",
                "${KeywordType.Time.string}이 5 이상이면 5 만큼 소모하여 ${KeywordType.Mana.string}를 추가로 1 회복한다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()

                usePlayerData.addMana(1)

                if (time is AbnormalityStatus && time.power >= 5) {
                    usePlayerData.addMana(1)
                    usePlayerData.decreaseTime(5, usePlayerData)
                }
                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                return@Card true
            }
        )
        //endregion

        //region drow Common Initialization
        val drow = Card(
            "뽑기", listOf(
                "<gray>덱에서 카드를 1장 뽑는다.",
                "${KeywordType.Time.string}이 5 이상이면 5 만큼 소모하여 추가로 1장 더 뽑는다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Uncommon, 0, { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()

                usePlayerData.drow(1)

                if (time is AbnormalityStatus && time.power >= 5) {
                    usePlayerData.drow(1)
                    usePlayerData.decreaseTime(5, usePlayerData)
                }

                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                return@Card true
            }
        )
        //endregion

        //region powerAttack Common Initialization
        val powerAttack = Card(
            "강공", listOf(
                "<gray>바라보는 적에게 10의 피해를 입힌다.",
                "${KeywordType.Time.string}이 5 이상이면 5 만큼 소모하여 추가로 10의 피해를 입힌다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Uncommon, 2, { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()
                val time = usePlayerData.getTime()

                if (cardList.isEmpty()) {
                    player.sendMessage(cardUseFailText())
                    return@Card false
                }

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                target.damage(10, usePlayerData)

                if (time is AbnormalityStatus && time.power >= 5) {
                    target.damage(10, usePlayerData)
                    usePlayerData.decreaseTime(5, usePlayerData)
                }

                player.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                return@Card true
            }
        )
        //endregion


        //region saveTime Common Initialization
        val saveTime = Card(
            "시간 저장", listOf(
                KeywordType.Continue.string,
                "",
                "<gray>전투 종료 시까지 어느 대상이든 턴을 종료할 때마다 자신은 <gold><bold>시간</bold><gray>을 10 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Continue]!!,
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val game = Info.game ?: return@Card false

                player.world.playSound(player.location, Sound.BLOCK_BEACON_POWER_SELECT, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.END_ROD, player.location, 10, 0.0, 0.0, 0.0, 0.3)
                game.continueEffects.add(ContinueEffect(usePlayerData, EffectTime.TurnEnd, { entity: Entity ->
                    usePlayerData.increaseTime(10, usePlayerData)
                } as ContinueEffectHandler
                ))
                return@Card true
            }
        )
        //endregion

        //region resourceAcceleration Common Initialization
        val resourceAcceleration = Card(
            "자원 가속", listOf(
                "<gold><bold>시간</bold><gray>을 5 소모하고 발동할 수 있다.",
                "<gray>덱에서 카드를 1장 뽑고 <blue><bold>마나 </bold><gray>1을 회복한다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()

                if (time !is AbnormalityStatus || time.power < 5) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.ITEM_BOOK_PAGE_TURN, 1.0F, 1.5F)
                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)

                usePlayerData.decreaseTime(5, usePlayerData)
                usePlayerData.drow(1)
                usePlayerData.addMana(1)

                return@Card true
            }
        )
        //endregion

        //region acceleration Common Initialization
        val acceleration = Card(
            "가속", listOf(
                KeywordType.Continue.string,
                "",
                "<gold><bold>시간 </bold><gray>1을 소모하고 발동할 수 있다.",
                "<gray>전투 종료 시까지 자신의 속도가 3 증가한다.",
                "",
                dictionary.dictionaryList[KeywordType.Continue]!!,
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()
                val game = Info.game ?: return@Card false

                if (time !is AbnormalityStatus || time.power < 1) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                usePlayerData.decreaseTime(1, usePlayerData)
                player.world.playSound(player.location, Sound.ENTITY_SNIFFER_STEP, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.ENCHANT, player.location, 10, 0.3, 0.3, 0.3, 1.0)

                usePlayerData.speed += 3
                game.battleEndUnit.add {
                    usePlayerData.speed -= 3
                }

                return@Card true
            }
        )
        //endregion


        //region temporarySuspension Uncommon Initialization
        val temporarySuspension = Card(
            "일시적 중단", listOf(
                "<gold><bold>시간</bold><gray>을 50 얻는다. 이후 자신의 턴을 종료한다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                usePlayerData.increaseTime(50, usePlayerData)
                usePlayerData.turnEnd()
                return@Card true
            }
        )
        //endregion

        //region accumulatedMoments Uncommon Initialization
        val accumulatedMoments = Card(
            "축적된 순간", listOf(
                "<gray>바라보는 적에게 자신의 <gold><bold>(시간 수치)</bold><gray> 만큼의 피해를 입힌다.",
                "<gray>이후 자신의 <gold><bold>시간 </bold><gray>수치를 절반으로 만든다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Uncommon, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()
                val time = usePlayerData.getTime()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                if (time !is AbnormalityStatus) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 1.0F)
                player.world.spawnParticle(Particle.END_ROD, target.entity.location, 30, 0.0, 0.0, 0.0, 0.5)
                target.damage(time.power, usePlayerData, DamageType.Normal)

                usePlayerData.decreaseTime(time.power / 2, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region timeTheft Uncommon Initialization
        val timeTheft = Card(
            "시간 절도", listOf(
                "<gray>바라보는 적의 (속도 * 10) 만큼 <gold><bold>시간</bold><gray>을 얻는다.",
                "<gray>이후 대상의 속도를 0으로 만든다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Uncommon, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                val targetSpeed = target.speed.toInt()

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.WAX_OFF, target.entity.location, 10, 0.5, 0.5, 0.5, 1.0)
                usePlayerData.increaseTime(targetSpeed * 10, usePlayerData)
                target.speed = 0

                return@Card true
            }
        )
        //endregion


        //region extensionOfTime Rare Initialization
        val extensionOfTime = Card(
            "시간의 연장선", listOf(
                "<gold><bold>시간 </bold><gray>25를 소모하고 발동할 수 있다.",
                "<gray>이번 라운드의 턴 순서 마지막에 추가 턴을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()
                val game = Info.game ?: return@Card false

                if (time !is AbnormalityStatus || time.power < 25) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.WAX_OFF, player.location, 50, 0.0, 0.0, 0.0, 1.0)
                usePlayerData.decreaseTime(25, usePlayerData)
                game.gameTurnOrder.add(usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region timeLifeIntersection Rare Initialization
        val timeLifeIntersection = Card(
            "시간 생명 교차", listOf(
                "<gray>자신의 체력을 최대 10까지 소모하고 소모한 (체력 * 5) 만큼 <gold><bold>시간</bold><gray>을 얻는다.",
                "<gray>이 효과로는 체력이 1 미만으로 내려가지 않으며, 이 경우 체력을 10 소모한 것으로 간주한다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                if (usePlayerData.health - 10 < 1) {
                    usePlayerData.setHealth(1)
                    usePlayerData.increaseTime(50, usePlayerData)
                    return@Card true
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 1.0F)
                player.world.spawnParticle(Particle.DAMAGE_INDICATOR, player.location, 10, 0.5, 0.5, 0.5, 1.0)

                usePlayerData.addHealth(-10)
                usePlayerData.increaseTime(50, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region lifeTimeIntersection Rare Initialization
        val lifeTimeIntersection = Card(
            "생명 시간 교차", listOf(
                "<gold><bold>시간 </bold><gray>수치만큼 체력을 회복하고 수치를 절반으로 만든다.",
                "<gray>만약, 자신의 체력이 1이었다면 <gold><bold>시간 </bold><gray>수치를 소모하지 않는다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Rare, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val time = usePlayerData.getTime()
                val isHealthZero = usePlayerData.health == 1

                if (time !is AbnormalityStatus) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.world.spawnParticle(Particle.HEART, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                usePlayerData.heal(time.power, usePlayerData)
                if (!isHealthZero) {
                    usePlayerData.decreaseTime(time.power / 2, usePlayerData)
                }
                return@Card true
            }
        )
        //endregion


        //region parallelTime Legend Initialization
        val parallelTime = Card(
            "평행 시간", listOf(
                "<gold><bold>시간 </bold><gray>50을 소모하고 발동할 수 있다.",
                "<gray>이번 라운드의 턴 순서 마지막에 모든 아군이 추가 턴을 얻는다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!
            ), CardRarity.Legend, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val playerTeam = usePlayerData.allTeamMembers(true, false)
                val time = usePlayerData.getTime()
                val game = Info.game ?: return@Card false

                if (time !is AbnormalityStatus || time.power < 50) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0F, 0.5F)
                player.world.spawnParticle(Particle.END_ROD, player.location, 50, 0.1, 0.1, 0.1, 0.4)
                usePlayerData.decreaseTime(50, usePlayerData)
                game.gameTurnOrder.addAll(playerTeam)
                return@Card true
            }
        )
        //endregion

        //region timeStop Legend Initialization
        val timeStop = Card(
            "시간 정지", listOf(
                "${KeywordType.Time.string} 50을 소모하고 발동할 수 있다.",
                "<gray>모든 적을 ${KeywordType.Stun.string} 상태로 만든다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!,
                dictionary.dictionaryList[KeywordType.Stun]!!
            ), CardRarity.Legend, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val targets = usePlayerData.allEnemyMembers()
                val time = usePlayerData.getTime()

                if (time !is AbnormalityStatus || time.power < 50) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0F, 0.5F)

                targets.forEach {
                    it.addStun()
                    player.world.spawnParticle(Particle.END_ROD, it.entity.location, 50, 0.0, 0.0, 0.0, 0.4)
                }
                usePlayerData.decreaseTime(50, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region preservationOfLife Legend Initialization
        val preservationOfLife = Card(
            "생명 보존", listOf(
                "${KeywordType.Time.string} 50을 소모하고 발동할 수 있다.",
                "<gray>모든 아군에게 ${KeywordType.DeathResistance.string} 3회를 부여한다.",
                "",
                dictionary.dictionaryList[KeywordType.Time]!!,
                dictionary.dictionaryList[KeywordType.DeathResistance]!!
            ), CardRarity.Legend, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val playerTeam = usePlayerData.allTeamMembers(true, false)
                val time = usePlayerData.getTime()

                if (time !is AbnormalityStatus || time.power < 50) {
                    player.sendMessage(cardUseFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.world.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.0F, 0.5F)

                playerTeam.forEach {
                    it.increaseDeathResistance(3, usePlayerData)
                }
                usePlayerData.decreaseTime(50, usePlayerData)
                return@Card true
            }
        )
        //endregion

        cardPack.startCardList.addAll(
            listOf(
                attack,
                attack,
                attack,
                defense,
                defense,
                defense,
                rest,
                rest,
                drow,
                drow,
                powerAttack,
                powerAttack
            )
        )

        cardPack.cardList.addAll(
            listOf(
                saveTime,
                saveTime,
                saveTime,
                resourceAcceleration,
                resourceAcceleration,
                resourceAcceleration,
                acceleration,
                acceleration,
                acceleration,
                temporarySuspension,
                temporarySuspension,
                temporarySuspension,
                accumulatedMoments,
                accumulatedMoments,
                accumulatedMoments,
                timeTheft,
                timeTheft,
                timeTheft,
                extensionOfTime,
                extensionOfTime,
                extensionOfTime,
                timeLifeIntersection,
                timeLifeIntersection,
                timeLifeIntersection,
                lifeTimeIntersection,
                lifeTimeIntersection,
                lifeTimeIntersection,
                parallelTime,
                timeStop,
                preservationOfLife
            )
        )

        cardPackList.add(
            cardPack
        )

        cardList.addAll(cardPack.cardList)
        cardList.addAll(cardPack.startCardList)
    }
}