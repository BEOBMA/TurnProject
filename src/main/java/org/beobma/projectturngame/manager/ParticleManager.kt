package org.beobma.projectturngame.manager

import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Player
import kotlin.math.cos
import kotlin.math.sin

interface ParticleHandler {
    fun spawnSphereParticles(player: Player, particle: Particle, radius: Double, points: Int)
}

object ParticleManager : ParticleHandler {
    override fun spawnSphereParticles(player: Player, particle: Particle, radius: Double, points: Int) {
        val playerLocation: Location = player.location.add(0.0, 1.0, 0.0)

        for (i in 0 until points) {
            val theta = Math.toRadians((i * 360.0 / points))
            for (j in 0 until points) {
                val phi = Math.toRadians((j * 180.0 / points))

                val x = radius * sin(phi) * cos(theta)
                val y = radius * cos(phi)
                val z = radius * sin(phi) * sin(theta)
                val particleLocation = playerLocation.clone().add(x, y, z)
                player.world.spawnParticle(particle, particleLocation, 1, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }
}