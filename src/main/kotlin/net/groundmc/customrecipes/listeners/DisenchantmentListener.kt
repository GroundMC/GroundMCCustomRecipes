package net.groundmc.customrecipes.listeners

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.plugin.Plugin

/**
 * Listener that handles events directed at anvils and allows enchanted items
 * to be disenchanted.
 */
class DisenchantmentListener(private val plugin: Plugin) : Listener {

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
                book.type == Material.WRITABLE_BOOK) {

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

            Bukkit.getScheduler().runTask(plugin, Runnable {
                inventory.repairCost = enchantment.value * valueMultiplier
            })
        }
    }

    companion object {
        private const val valueMultiplier = 3
    }
}
