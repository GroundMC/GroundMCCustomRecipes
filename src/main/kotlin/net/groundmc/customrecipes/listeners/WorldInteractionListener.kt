package net.groundmc.customrecipes.listeners

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

/**
 * Listener that handles world interaction events and applies custom behaviour
 * to it.
 */
class WorldInteractionListener(plugin: Plugin) : Listener {

    private val seeds = MaterialSetTag(NamespacedKey(plugin, "seeds"))
        .add(
            Material.WHEAT_SEEDS,
            Material.POTATO,
            Material.CARROT,
            Material.BEETROOT_SEEDS,
            Material.NETHER_WART,
            Material.COCOA_BEANS
        )
        .ensureSize("SEEDS", 6)


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
            REPLANT_HOES.contains(event.material) &&
            block.type in CROPS &&
            block.blockData is Ageable
        ) {

            val crop = block.blockData as Ageable

            if (crop.age == crop.maximumAge) {
                val drops: Collection<ItemStack> = event.item?.let { block.getDrops(it, event.player) } ?: block.drops

                val location = block.location
                ParticleBuilder(Particle.BLOCK_CRACK)
                    .location(location.toCenterLocation())
                    .offset(0.1, 0.1, 0.1)
                    .count(48)
                    .allPlayers()
                    .data(block.blockData)
                    .spawn()

                location.world.playSound(location, block.blockData.soundGroup.breakSound, 1f, 1f)

                crop.age = 0
                block.blockData = crop

                when (event.hand) {
                    EquipmentSlot.HAND -> event.player.swingMainHand()
                    EquipmentSlot.OFF_HAND -> event.player.swingOffHand()
                    else -> Unit
                }

                drops.forEach { drop ->
                    if (seeds.isTagged(drop.type)) {
                        drop.amount--
                    }
                    if (drop.amount > 0) {
                        location.world.dropItemNaturally(location, drop)
                    }
                }
            }
            event.isCancelled = true
        }
    }

    companion object {
        private val REPLANT_HOES = arrayOf(Material.IRON_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE)
        private val CROPS = arrayOf(
            Material.WHEAT,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.CARROTS,
            Material.NETHER_WART,
            Material.COCOA
        )
    }
}
