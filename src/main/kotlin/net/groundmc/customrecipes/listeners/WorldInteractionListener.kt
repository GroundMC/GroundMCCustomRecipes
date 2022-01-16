package net.groundmc.customrecipes.listeners

import com.destroystokyo.paper.MaterialSetTag
import com.destroystokyo.paper.ParticleBuilder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Particle
import org.bukkit.block.BlockFace
import org.bukkit.block.data.Ageable
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
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

    private val goodHoes = MaterialSetTag(NamespacedKey(plugin, "good_hoes"))
        .add(
            Material.IRON_HOE,
            Material.DIAMOND_HOE,
            Material.NETHERITE_HOE
        )
        .ensureSize("GOOD_HOES", 3)

    private val replantableCrops = MaterialSetTag(NamespacedKey(plugin, "replantable_crops"))
        .add(
            Material.WHEAT,
            Material.POTATOES,
            Material.BEETROOTS,
            Material.CARROTS,
            Material.NETHER_WART,
            Material.COCOA
        )
        .ensureSize("REPLANTABLE_CROPS", 6)


    /**
     * Replants crops automatically when right-clicked with an iron or diamond
     * hoe.
     *
     * @param event the player interaction event to handle
     */
    @EventHandler
    fun replantCrops(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val item = event.item ?: return
        if (event.player.hasPermission("customrecipes.replant_crops") &&
            event.action == Action.RIGHT_CLICK_BLOCK &&
            goodHoes.isTagged(item)
        ) {
            val efficiency = item.getEnchantmentLevel(Enchantment.DIG_SPEED)

            val rotation = event.player.facing
            val shape = when (efficiency) {
                0 -> Shapes.ONE_BY_ONE
                1 -> Shapes.ONE_BY_TWO
                2 -> Shapes.TWO_BY_TWO
                3 -> Shapes.TWO_BY_THREE
                4 -> Shapes.THREE_BY_THREE
                5 -> Shapes.THREE_BY_FOUR
                else -> Shapes.ONE_BY_ONE
            }.map { it.rotate(rotation) }

            for ((x, y) in shape) {
                val blockAt = block.getRelative(x, 0, y)

                if (replantableCrops.isTagged(blockAt) &&
                    blockAt.blockData is Ageable
                ) {

                    val crop = blockAt.blockData as Ageable

                    if (crop.age == crop.maximumAge) {
                        val drops = event.item?.let { blockAt.getDrops(it, event.player) } ?: blockAt.drops

                        val location = blockAt.location
                        ParticleBuilder(Particle.BLOCK_CRACK)
                            .location(location.toCenterLocation())
                            .offset(0.1, 0.1, 0.1)
                            .count(48)
                            .allPlayers()
                            .data(blockAt.blockData)
                            .spawn()

                        location.world.playSound(location, blockAt.blockData.soundGroup.breakSound, 1f, 1f)

                        crop.age = 0
                        blockAt.blockData = crop

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
        }
    }

    companion object {
        data class Offset(val x: Int, val y: Int) {
            fun rotate(facing: BlockFace): Offset {
                return when (facing) {
                    BlockFace.NORTH -> Offset(-x, -y)
                    BlockFace.EAST -> Offset(y, -x)
                    BlockFace.SOUTH -> this
                    BlockFace.WEST -> Offset(-y, x)
                    else -> this
                }
            }
        }

        object Shapes {
            internal val ONE_BY_ONE = setOf(Offset(0, 0))
            internal val ONE_BY_TWO = setOf(
                Offset(0, 0),
                Offset(0, 1)
            )
            internal val TWO_BY_TWO = setOf(
                Offset(-1, 0), Offset(0, 0),
                Offset(-1, 1), Offset(0, 1)
            )
            internal val TWO_BY_THREE = setOf(
                Offset(-1, 0), Offset(0, 0), Offset(1, 0),
                Offset(-1, 1), Offset(0, 1), Offset(1, 1),
            )
            internal val THREE_BY_THREE = setOf(
                Offset(-1, -1), Offset(0, -1), Offset(1, -1),
                Offset(-1, 0), Offset(0, 0), Offset(1, 0),
                Offset(-1, 1), Offset(0, 1), Offset(1, 1),
            )
            internal val THREE_BY_FOUR = setOf(
                Offset(-1, -1), Offset(0, -1), Offset(1, -1),
                Offset(-1, 0), Offset(0, 0), Offset(1, 0),
                Offset(-1, 1), Offset(0, 1), Offset(1, 1),
                Offset(-1, 2), Offset(0, 2), Offset(1, 2),
            )
        }
    }
}
