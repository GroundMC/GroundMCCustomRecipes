package gtlp.groundmc.customrecipes.listeners

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

/**
 * Listener that handles events directed at anvils and allows enchanted items
 * to be disenchanted.
 */
class DisenchantmentListener : Listener {

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
                !firstItem.enchantments.isEmpty() &&
                book != null &&
                book.type == Material.BOOK_AND_QUILL) {

            val enchantment = firstItem.enchantments.entries.iterator().next()

            val enchantedBook = ItemStack(Material.ENCHANTED_BOOK)
            val meta = enchantedBook.itemMeta as EnchantmentStorageMeta
            meta.addStoredEnchant(
                    enchantment.key,
                    enchantment.value,
                    false
            )
            enchantedBook.itemMeta = meta
            event.result = enchantedBook
            inventory.repairCost = enchantment.value * 2
        }
    }

    /**
     * Actually disenchants the item and gives the player with the permission
     * to do so an enchanted book with the enchantment on it.
     * Removes the enchantment from the item in the first slot and converts an
     * ordinary book and quill into an enchanted book.
     *
     * @param event the event to handle
     */
    @EventHandler
    fun disenchant(event: InventoryClickEvent) {
        val player = event.whoClicked as Player
        if (!player.hasPermission("customrecipes.disenchant")) {
            event.isCancelled = true
            return
        }
        if (event.action == InventoryAction.NOTHING &&
                event.isLeftClick &&
                event.clickedInventory is AnvilInventory) {
            val inventory = event.clickedInventory as AnvilInventory
            val firstItem = inventory.getItem(0)
            val book = inventory.getItem(1)
            if (firstItem != null &&
                    !firstItem.enchantments.isEmpty() &&
                    book != null &&
                    book.type == Material.BOOK_AND_QUILL) {
                val enchantment = firstItem.enchantments.entries.iterator().next()

                if (player.level < enchantment.value * 2) {
                    event.isCancelled = true
                    return
                }

                book.amount--

                firstItem.removeEnchantment(enchantment.key)

                inventory.setItem(0, firstItem)
                inventory.repairCost = enchantment.value * 2
                player.level = player.level - enchantment.value * 2

                val enchantedBook = ItemStack(Material.ENCHANTED_BOOK)
                val meta = enchantedBook.itemMeta as EnchantmentStorageMeta
                meta.addStoredEnchant(
                        enchantment.key,
                        enchantment.value,
                        false
                )
                enchantedBook.itemMeta = meta

                event.currentItem = enchantedBook
                event.view.cursor = enchantedBook
                event.isCancelled = true
            }
        }
    }
}
