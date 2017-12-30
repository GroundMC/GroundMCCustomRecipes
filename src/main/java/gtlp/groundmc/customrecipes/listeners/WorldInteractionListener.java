package gtlp.groundmc.customrecipes.listeners;

import com.google.common.collect.Lists;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import java.util.Collection;
import java.util.List;

/**
 * Listener that handles world interaction events and applies custom behaviour
 * to it.
 */
public class WorldInteractionListener implements Listener {

    private final List<Material> replantHoes =
            Lists.newArrayList(Material.IRON_HOE, Material.DIAMOND_HOE);

    /**
     * Replants crops automatically when right-clicked with an iron or diamond
     * hoe.
     *
     * @param event the player interaction event to handle
     */
    @EventHandler
    public void replantCrops(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (event.getPlayer().hasPermission("customrecipes.replant_crops") &&
                event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                replantHoes.contains(event.getMaterial()) &&
                block.getState().getData() instanceof Crops) {

            Crops crop = (Crops) block.getState().getData();

            if (crop.getState() == CropState.RIPE) {
                Collection<ItemStack> drops = block.getDrops();
                crop.setState(CropState.SEEDED);

                BlockState state = block.getState();
                state.setData(crop);
                //noinspection deprecation
                block.setData(crop.getData());

                Location location = block.getLocation();
                drops.forEach(drop -> location.getWorld().dropItemNaturally(location, drop));
            }
        }
    }
}
