package net.groundmc.customrecipes.listeners

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.block.data.Directional
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.plugin.Plugin
import java.util.*

/**
 * Listener that handles events directed at anvils and allows enchanted items
 * to be disenchanted.
 */
class DisenchantmentListener(private val plugin: Plugin) : Listener {

    private val lastAnvilInteraction = WeakHashMap<Player, Location>()

    /**
     * Prepares the disenchantment of an item.
     *
     * @param event the event to handle
     */
    @EventHandler
    fun prepareDisenchant(event: PrepareAnvilEvent) {
        val inventory = event.inventory
        val firstItem = inventory.getItem(0)
        val book = inventory.getItem(1)
        if (firstItem != null &&
            firstItem.enchantments.isNotEmpty() &&
            book != null &&
            book.type == Material.WRITABLE_BOOK &&
            event.inventory.renameText?.isEmpty() == true
        ) {

            val enchantment = firstItem.enchantments.entries.first()

            val enchantedBook = ItemStack(Material.ENCHANTED_BOOK)
            val meta = enchantedBook.itemMeta as EnchantmentStorageMeta
            meta.addStoredEnchant(
                enchantment.key,
                enchantment.value,
                false
            )
            enchantedBook.itemMeta = meta
            event.result = enchantedBook

            inventory.repairCost = disenchantmentCost(enchantment)
        }
    }

    @EventHandler
    fun extractEnchantment(event: InventoryClickEvent) {
        val inventory = event.clickedInventory
        val whoClicked = event.whoClicked

        // Check whether left-clicking the result of the result prepared above
        if (event.isLeftClick &&
            whoClicked is Player &&
            inventory is AnvilInventory &&
            event.slotType == InventoryType.SlotType.RESULT
        ) {
            // Checking if there are all the required items and there is still an applicable enchantment
            val firstItem = inventory.getItem(0)
            val book = inventory.getItem(1)
            if (firstItem != null &&
                firstItem.enchantments.isNotEmpty() &&
                book != null &&
                book.type == Material.WRITABLE_BOOK &&
                inventory.renameText?.isEmpty() == true
            ) {


                val enchantment = firstItem.enchantments.entries.first()
                // Check if the player has enough levels to actually commence disenchanting
                if (whoClicked.level < disenchantmentCost(enchantment)) {
                    return
                }

                whoClicked.giveExpLevels(-disenchantmentCost(enchantment))

                // Build the enchanted book
                val enchantedBook = ItemStack(Material.ENCHANTED_BOOK)
                val meta = enchantedBook.itemMeta as EnchantmentStorageMeta
                meta.addStoredEnchant(
                    enchantment.key,
                    enchantment.value,
                    false
                )
                enchantedBook.itemMeta = meta

                // Actually cancel the original event,
                event.result = Event.Result.DENY

                if (event.isShiftClick) {
                    val result = whoClicked.inventory.addItem(enchantedBook)
                    if (result.isNotEmpty()) {
                        event.view.cursor = enchantedBook
                    }
                } else {
                    event.view.cursor = enchantedBook
                }
                firstItem.removeEnchantment(enchantment.key)

                inventory.setItem(0, firstItem)
                inventory.setItem(1, null)

                val anvilLocation = lastAnvilInteraction[whoClicked]
                if (anvilLocation != null) {
                    val anvil = anvilLocation.block.state
                    val index = anvilTypes.indexOf(anvil.type)
                    val world = anvilLocation.world
                    world.spawnParticle(
                        Particle.BLOCK_CRACK,
                        anvilLocation,
                        100,
                        0.25,
                        0.0,
                        0.25,
                        anvil.blockData
                    )
                    world.playSound(
                        anvilLocation,
                        Sound.BLOCK_ANVIL_DESTROY,
                        0.75f,
                        1.0f
                    )
                    if (index in 0 until anvilTypes.lastIndex) {
                        val directional = anvil.blockData as Directional
                        anvil.type = anvilTypes[index + 1]
                        val anvilDirection = anvil.blockData as Directional
                        anvilDirection.facing = directional.facing
                        anvil.blockData = anvilDirection
                        anvil.update(true)
                    } else if (index == anvilTypes.lastIndex) {
                        anvil.type = Material.AIR
                        anvil.update(true)
                    }
                }
            }
        }
    }

    private fun disenchantmentCost(enchantment: MutableMap.MutableEntry<Enchantment, Int>) =
        enchantment.value * valueMultiplier

    @EventHandler
    fun useAnvil(event: PlayerInteractEvent) {
        val block = event.clickedBlock
        val type = block?.type
        if (type != null && type in anvilTypes) {
            lastAnvilInteraction[event.player] = block.location
        }
    }

    companion object {
        private const val valueMultiplier = 4
        private val anvilTypes = arrayOf(
            Material.ANVIL,
            Material.CHIPPED_ANVIL,
            Material.DAMAGED_ANVIL
        )
    }
}
