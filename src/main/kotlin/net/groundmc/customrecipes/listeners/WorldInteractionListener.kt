package net.groundmc.customrecipes.listeners

import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

/**
 * Listener that handles world interaction events and applies custom behaviour
 * to it.
 */
class WorldInteractionListener : Listener {

    private val crops = arrayOf(
            Material.WHEAT,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.CARROTS,
            Material.NETHER_WART,
            Material.COCOA
    )
    private val replantHoes = arrayOf(Material.IRON_HOE, Material.DIAMOND_HOE)

    /**
     * Replants crops automatically when right-clicked with an iron or diamond
     * hoe.
     *
     * @param event the player interaction event to handle
     */
    @EventHandler
    fun replantCrops(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        if (event.player.hasPermission("customrecipes.replant_crops") &&
                event.action == Action.RIGHT_CLICK_BLOCK &&
                replantHoes.contains(event.material) &&
                block.type in crops &&
                block.blockData is Ageable) {

            val crop = block.blockData as Ageable

            if (crop.age == crop.maximumAge) {
                val drops = block.drops
                crop.age = 0
                block.blockData = crop

                val location = block.location
                drops.forEach { drop -> location.world.dropItemNaturally(location, drop) }
            }
        }
    }
}
