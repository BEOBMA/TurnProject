package org.beobma.projectturngame

import org.beobma.projectturngame.command.Command
import org.beobma.projectturngame.config.CardConfig
import org.beobma.projectturngame.listener.*
import org.bukkit.plugin.java.JavaPlugin

class ProjectTurnGame : JavaPlugin() {
    companion object {
        lateinit var instance: ProjectTurnGame
    }

    override fun onEnable() {
        instance = this

        eventRegister()
        CardConfig()
        loggerInfo("Enabling ProjectTurn")
    }

    override fun onDisable() {
        loggerInfo("Disabling ProjectTurn")
    }

    private fun eventRegister() {
        server.getPluginCommand("pt")?.setExecutor(Command())

        server.pluginManager.registerEvents(Command(), this)
        server.pluginManager.registerEvents(OnCreatureSpawnEvent(), this)
        server.pluginManager.registerEvents(OnEntityDamageByEntityEvent(), this)
        server.pluginManager.registerEvents(OnInventoryClickEvent(), this)
        server.pluginManager.registerEvents(OnInventoryCloseEvent(), this)
        server.pluginManager.registerEvents(OnPlayerDropItemEvent(), this)
        server.pluginManager.registerEvents(OnPlayerQuitEvent(), this)
        server.pluginManager.registerEvents(OnPlayerInteractEvent(), this)
        server.pluginManager.registerEvents(OnPlayerMoveEvent(), this)
        server.pluginManager.registerEvents(OnPlayerJumpEvent(), this)
        server.pluginManager.registerEvents(OnPlayerSwapHandItemsEvent(), this)
        server.pluginManager.registerEvents(OnEntityTurnStartEvent(), this)
        server.pluginManager.registerEvents(OnEntityTurnEndEvent(), this)
        server.pluginManager.registerEvents(OnDamageEvent(), this)
    }

    fun loggerInfo(string: String) {
        logger.info("[ProjectTurn] $string")
    }
}
