package net.groundmc.customrecipes.listeners

import org.bukkit.CropState
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.material.Crops

/**
 * Listener that handles world interaction events and applies custom behaviour
 * to it.
 */
class WorldInteractionListener : Listener {

    private val replantHoes = listOf(Material.IRON_HOE, Material.DIAMOND_HOE)

    /**
     * Replants crops automatically when right-clicked with an iron or diamond
     * hoe.
     *
     * @param event the player interaction event to handle
     */
    @EventHandler
    fun replantCrops(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        if (event.player.hasPermission("customrecipes.replant_crops") &&
                event.action == Action.RIGHT_CLICK_BLOCK &&
                replantHoes.contains(event.material) &&
                block.state.data is Crops) {

            val crop = block.state.data as Crops

            if (crop.state == CropState.RIPE) {
                val drops = block.drops
                crop.state = CropState.SEEDED

                block.data = crop.data

                val location = block.location
                drops.forEach { drop -> location.world.dropItemNaturally(location, drop) }
            }
        }
    }
}
