package gtlp.groundmc.customrecipes.listeners;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Map;

/**
 * Listener that handles events directed at anvils and allows enchanted items
 * to be disenchanted.
 */
public class DisenchantmentListener implements Listener {

    /**
     * Prepares the disenchantment of an item.
     *
     * @param event the event to handle
     */
    @EventHandler
    public void prepareDisenchant(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack book = inventory.getItem(1);
        if (firstItem != null &&
                !firstItem.getEnchantments().isEmpty() &&
                book != null &&
                book.getType() == Material.BOOK_AND_QUILL) {

            Map.Entry<Enchantment, Integer> enchantment =
                    firstItem.getEnchantments().entrySet().iterator().next();

            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta =
                    (EnchantmentStorageMeta) enchantedBook.getItemMeta();
            meta.addStoredEnchant(
                    enchantment.getKey(),
                    enchantment.getValue(),
                    false
            );
            enchantedBook.setItemMeta(meta);
            event.setResult(enchantedBook);
            inventory.setRepairCost(enchantment.getValue() * 2);
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
    public void disenchant(InventoryClickEvent event) {
        Player player;
        if (event.getWhoClicked() instanceof Player) {
            player = (Player) event.getWhoClicked();
        } else {
            return;
        }
        if (!player.hasPermission("customrecipes.disenchant")) {
            return;
        }
        if (event.getAction() == InventoryAction.NOTHING &&
                event.isLeftClick() &&
                event.getClickedInventory() instanceof AnvilInventory) {
            AnvilInventory inventory =
                    (AnvilInventory) event.getClickedInventory();
            ItemStack firstItem = inventory.getItem(0);
            ItemStack book = inventory.getItem(1);
            if (firstItem != null &&
                    !firstItem.getEnchantments().isEmpty() &&
                    book != null &&
                    book.getType() == Material.BOOK_AND_QUILL) {
                Map.Entry<Enchantment, Integer> enchantment =
                        firstItem.getEnchantments().entrySet().iterator().next();

                if (player.getLevel() < enchantment.getValue() * 2) {
                    return;
                }

                book.setAmount(book.getAmount() - 1);

                firstItem.removeEnchantment(enchantment.getKey());

                inventory.setItem(0, firstItem);
                inventory.setRepairCost(enchantment.getValue() * 2);
                player.setLevel(player.getLevel() - (enchantment.getValue() * 2));

                ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta =
                        (EnchantmentStorageMeta) enchantedBook.getItemMeta();
                meta.addStoredEnchant(
                        enchantment.getKey(),
                        enchantment.getValue(),
                        false
                );
                enchantedBook.setItemMeta(meta);

                event.setCurrentItem(enchantedBook);
                event.getView().setCursor(enchantedBook);
                event.setCancelled(true);
            }
        }
    }
}
