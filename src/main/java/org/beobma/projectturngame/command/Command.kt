package org.beobma.projectturngame.command

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.beobma.projectturngame.config.CardConfig.Companion.cardList
import org.beobma.projectturngame.game.Game
import org.beobma.projectturngame.game.GameDifficulty
import org.beobma.projectturngame.game.GameField
import org.beobma.projectturngame.game.GameType
import org.beobma.projectturngame.info.Info
import org.beobma.projectturngame.localization.Dictionary
import org.beobma.projectturngame.manager.CardManager.addCard
import org.beobma.projectturngame.manager.GameManager.start
import org.beobma.projectturngame.manager.GameManager.stop
import org.beobma.projectturngame.manager.InventoryManager.openAlchemYingredientsPileInfoInventory
import org.beobma.projectturngame.manager.InventoryManager.openBanishInfoInventory
import org.beobma.projectturngame.manager.InventoryManager.openDeckInfoInventory
import org.beobma.projectturngame.manager.InventoryManager.openGraveyardInfoInventory
import org.beobma.projectturngame.manager.InventoryManager.openMyInfoInventory
import org.beobma.projectturngame.manager.InventoryManager.openTurnOtherInfoInventory
import org.beobma.projectturngame.text.TextColorType
import org.beobma.projectturngame.util.CardPosition
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

class Command : Listener, CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (cmd.name.equals("pt", ignoreCase = true) && args.isNotEmpty()) {
            if (sender !is Player) {
                sender.sendMessage(
                    Component.text("[!] 이 명령어는 플레이어만 사용할 수 있습니다.", TextColorType.Red.textColor)
                        .decorate(TextDecoration.BOLD)
                )
                return false
            }

            when (args[0].lowercase(Locale.getDefault())) {
                "start", "시작" -> {
                    if (!sender.isOp) {
                        sender.sendMessage(
                            Component.text("[!] 이 명령어를 사용할 권한이 없습니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    if (args.size < 3) {
                        sender.sendMessage(
                            Component.text("[!] 올바른 인수를 제공해 주세요.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val gameType = GameType.entries.find { it.name.equals(args[1], ignoreCase = true) }
                    val gameDifficulty = GameDifficulty.entries.find { it.name.equals(args[2], ignoreCase = true) }

                    if (gameType == null || gameDifficulty == null) {
                        sender.sendMessage(
                            Component.text("[!] 유효한 게임 타입이나 난이도를 입력해 주세요.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val world = Bukkit.getWorld("world")
                    if (world?.seed != 8971917449433682803) {
                        sender.sendMessage(
                            Component.text("[!] 이 플러그인은 전용 맵과 함께 사용해야 합니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        sender.sendMessage(
                            Component.text("[!] 게임을 강제로 종료합니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return true
                    }
                    Game(
                        Bukkit.getOnlinePlayers().toMutableList(),
                        gameType,
                        gameDifficulty,
                        mutableListOf(),
                        GameField.Forest
                    ).start()
                }

                "stop", "종료" -> {
                    if (!sender.isOp) {
                        sender.sendMessage(
                            Component.text("[!] 이 명령어를 사용할 권한이 없습니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val game = Info.game

                    if (game !is Game) {
                        sender.sendMessage(
                            Component.text("[!] 진행중인 게임이 없습니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    game.stop()
                    return true
                }

                "dictionary", "사전" -> {
                    if (args.size < 2) {
                        sender.sendMessage(
                            Component.text("[!] 사전에서 검색할 키워드를 제공해주세요.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val key = args.drop(1).joinToString(" ").trim()
                    val dictionaryList = Dictionary().dictionaryList

                    val definition = dictionaryList[key]
                    if (definition == null) {
                        sender.sendMessage(
                            Component.text("[!] 용어 '$key' 가 사전에 없습니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    sender.sendMessage(definition)
                    return true
                }

                "info", "정보" -> {
                    if (args.size < 2) {
                        sender.sendMessage(
                            Component.text("[!] 올바른 인수를 제공해주세요.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val key = args.drop(1).joinToString(" ").trim()

                    when (key) {
                        "덱" -> {
                            sender.openDeckInfoInventory()
                        }

                        "묘지" -> {
                            sender.openGraveyardInfoInventory()
                        }

                        "제외" -> {
                            sender.openBanishInfoInventory()
                        }

                        "연금술 재료 더미" -> {
                            sender.openAlchemYingredientsPileInfoInventory()
                        }

                        "자신 정보" -> {
                            sender.openMyInfoInventory()
                        }

                        "턴 순서" -> {
                            sender.openTurnOtherInfoInventory()
                        }


                        else -> {
                            sender.sendMessage(
                                Component.text("[!] 올바르지 않은 인수입니다.", TextColorType.Red.textColor)
                                    .decorate(TextDecoration.BOLD)
                            )
                            return false
                        }
                    }
                    return true
                }

                // 이하 디버그 전용 명령어
                "get" -> {
                    if (args.size < 2) {
                        sender.sendMessage(
                            Component.text("[!] 가져올 카드의 이름을 제공해주세요.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    if (args.size < 3) {
                        sender.sendMessage(
                            Component.text("[!] 카드를 넣을 위치를 제공해주세요.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val cardName = args[1].trim().replace(" ", "")
                    val cardPosition = args[2].trim()

                    val definition = cardList.find { it.name.trim().replace(" ", "") == cardName }
                    if (definition == null) {
                        sender.sendMessage(
                            Component.text("[!] '$cardName' 이름을 가진 카드가 존재하지 않습니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }

                    val position = CardPosition.entries.find { it.name == cardPosition }
                    if (position !is CardPosition) {
                        sender.sendMessage(
                            Component.text("[!] 카드를 넣을 위치가 올바르지 않습니다.", TextColorType.Red.textColor)
                                .decorate(TextDecoration.BOLD)
                        )
                        return false
                    }


                    val playerData = Info.game?.playerDatas?.find { it.player == sender } ?: return false

                    playerData.addCard(definition, position)
                    return true
                }

                else -> {
                    sender.sendMessage(
                        Component.text("[!] 알 수 없는 명령어: ${args[0]}.", TextColorType.Red.textColor)
                            .decorate(TextDecoration.BOLD)
                    )
                    return false
                }
            }
            return false
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String> {
        if (command.name.equals("pt", ignoreCase = true)) {
            return when (args.size) {
                1 -> listOf("start", "stop", "dictionary", "info", "정보", "사전")
                2 -> when (args[0].lowercase(Locale.getDefault())) {
                    "start" -> GameType.entries.map { it.name }
                    "dictionary", "사전" -> Dictionary().dictionaryList.keys.toList()
                    "info", "정보" -> listOf("덱", "묘지", "제외", "연금술 재료 더미", "턴 순서", "자신 정보")
                    else -> emptyList()
                }

                3 -> if (args[0].equals(
                        "start", ignoreCase = true
                    )
                ) Difficulty.entries.map { it.name } else emptyList()

                else -> emptyList()
            }
        }
        return emptyList()
    }
}