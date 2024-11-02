package org.beobma.projectturngame.config.cardpack

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.beobma.projectturngame.card.Card
import org.beobma.projectturngame.card.CardRarity
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.entity.enemy.Enemy
import org.beobma.projectturngame.manager.EnemyManager.damage
import org.beobma.projectturngame.manager.ParticleAnimationManager.play
import org.beobma.projectturngame.manager.PlayerManager.addMana
import org.beobma.projectturngame.manager.PlayerManager.heal
import org.beobma.projectturngame.manager.SelectionFactordManager.focusOn
import org.beobma.projectturngame.manager.SoundManager.playCardUsingFailSound
import org.beobma.projectturngame.manager.SoundManager.playSweepSound
import org.beobma.projectturngame.manager.TextManager.targetingFailText
import org.beobma.projectturngame.particle.ParticleAnimation
import org.beobma.projectturngame.text.KeywordType
import org.beobma.projectturngame.text.TextColorType
import org.bukkit.Particle
import org.bukkit.Sound
import org.checkerframework.checker.units.qual.min

class StartCardPack {
    companion object {
        val startCardList: MutableList<Card> = mutableListOf()
    }

    init {
        cardConfig()
    }

    private fun cardConfig() {
        //region attack Initialization
        val attack = Card(
            "공격", listOf(
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적에게 5의 피해를 입힌다.</gray>")
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                player.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                target.damage(5, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region heal Initialization
        val heal = Card(
            "가벼운 치유", listOf(
                MiniMessage.miniMessage().deserialize("<gray>체력을 5 회복한다.</gray>"),
            ), CardRarity.Common, 1,
            { usePlayerData, _ ->
                val player = usePlayerData.player

                player.spawnParticle(Particle.HEART, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                usePlayerData.heal(5, usePlayerData)
                return@Card true
            }
        )
        //endregion

        //region powerAttack Initialization
        val powerAttack = Card(
            "강공", listOf(
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적에게 12의 피해를 입힌다.</gray>"),
            ), CardRarity.Common, 2,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }

                player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0F, 0.5F)
                player.spawnParticle(Particle.CRIT, target.entity.location, 10, 0.5, 0.5, 0.5, 0.0)
                target.damage(12, usePlayerData)

                return@Card true
            }
        )
        //endregion

        //region rest Initialization
        val rest = Card(
            "휴식", listOf(
                MiniMessage.miniMessage().deserialize("<blue><bold>마나</bold><gray>를 1 회복한다.</gray>")
            ), CardRarity.Common, 0,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                player.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0F, 2.0F)
                player.spawnParticle(Particle.WAX_OFF, player.location, 10, 0.5, 0.5, 0.5, 1.0)
                usePlayerData.addMana(1)
                return@Card true
            }
        )
        //endregion

        //region continuousAttack Initialization
        val continuousAttack = Card(
            "연공", listOf(
                MiniMessage.miniMessage().deserialize("<gray>바라보는 적에게 7의 피해를 3번 입힌다.</gray>")
            ), CardRarity.Common, 3,
            { usePlayerData, _ ->
                val player = usePlayerData.player
                val target = usePlayerData.focusOn()

                if (target !is Enemy) {
                    player.sendMessage(targetingFailText())
                    player.playCardUsingFailSound()
                    return@Card false
                }
                ParticleAnimation(
                    listOf(
                        {
                            player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                            player.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                            target.damage(7, usePlayerData)
                        },
                        {},
                        {},
                        {},
                        {
                            player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                            player.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                            target.damage(7, usePlayerData)
                        },
                        {},
                        {},
                        {},
                        {
                            player.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 0.5F)
                            player.spawnParticle(Particle.SWEEP_ATTACK, target.entity.location, 1, 0.0, 0.0, 0.0, 1.0)
                            target.damage(7, usePlayerData)
                        }
                    )
                ).play()
                return@Card true
            }
        )
        //endregion


        startCardList.addAll(
            listOf(
                attack, attack, attack, heal, heal, powerAttack, powerAttack, rest, rest, continuousAttack
            )
        )

        cardList.addAll(startCardList)
    }
}