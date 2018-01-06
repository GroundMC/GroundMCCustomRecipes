package gtlp.groundmc.customrecipes;

import gtlp.groundmc.customrecipes.listeners.DisenchantmentListener;
import gtlp.groundmc.customrecipes.listeners.WorldInteractionListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Plugin to add some custom recipes
 */
@SuppressWarnings("unused")
public class CustomRecipes extends JavaPlugin {

    /**
     * Constant wildcard value for recipes
     */
    private static final short WILDCARD = Short.MAX_VALUE;

    /**
     * A list of recipes to be added to the game.
     */
    @SuppressWarnings("deprecation")
    private final List<Recipe> recipes = List.of(
            /*
            Shapeless recipes:
             */
            /*
             * Shapeless recipe to convert three wheat to bread.
             * Allows crafting bread without a crafting table.
             */
            new ShapelessRecipe(
                    new NamespacedKey(this, "wheatToBreadShapeless"),
                    new ItemStack(Material.BREAD)).
                    addIngredient(3, Material.WHEAT),

            /*
             * Shapeless recipe to convert an enchanted book back to a normal book.
             * Recycled paper is still good paper.
             */
            new ShapelessRecipe(
                    new NamespacedKey(this, "enchantmentBookToBookShapeless"),
                    new ItemStack(Material.BOOK)).
                    addIngredient(Material.ENCHANTED_BOOK),

            /*
            Shaped recipes:
             */
            /*
             * 2 Logs to 16 sticks, old logs.
             * Recipe:
             * L
             * L
             *
             * Where "L" is a log.
             */
            new ShapedRecipe(
                    new NamespacedKey(this, "logToStick"),
                    new ItemStack(Material.STICK, 16)).
                    shape("L", "L").
                    setIngredient('L', Material.LOG, WILDCARD),
            /*
             * 2 Logs to 16 sticks, new logs.
             * Recipe:
             * L
             * L
             *
             * Where "L" is a log.
             */
            new ShapedRecipe(
                    new NamespacedKey(this, "log2ToStick"),
                    new ItemStack(Material.STICK, 16)).
                    shape("L", "L").
                    setIngredient('L', Material.LOG_2, WILDCARD),
            /*
             * Logs in the shape of planks for a chest to 4 chests, old logs.
             * Recipe:
             * L L L
             * L   L
             * L L L
             *
             * Where "L" is a log.
             */
            new ShapedRecipe(
                    new NamespacedKey(this, "logToChest"),
                    new ItemStack(Material.CHEST, 4)).
                    shape("LLL", "L L", "LLL").
                    setIngredient('L', Material.LOG, WILDCARD),
            /*
             * Logs in the shape of planks for a chest to 4 chests, new logs.
             * Recipe:
             * L L L
             * L   L
             * L L L
             *
             * Where "L" is a log.
             */
            new ShapedRecipe(
                    new NamespacedKey(this, "log2ToChest"),
                    new ItemStack(Material.CHEST, 4)).
                    shape("LLL", "L L", "LLL").
                    setIngredient('L', Material.LOG_2, WILDCARD),

            /*
             * A circle of rotten flesh to a single peace of leather.
             * Another use of rotten flesh.
             * Recipe:
             * R R R
             * R   R
             * R R R
             *
             * Where "R" is rotten flesh.
             */
            new ShapedRecipe(
                    new NamespacedKey(this, "rottenFleshToLeather"),
                    new ItemStack(Material.LEATHER)).
                    shape("RRR", "R R", "RRR").
                    setIngredient('R', Material.ROTTEN_FLESH)
            /*
            Furnace Recipes:
             */

    );

    @Override
    public void onEnable() {
        getCommand("customrecipes").setExecutor(new CustomRecipesCommand());

        Bukkit.getPluginManager().registerEvents(new WorldInteractionListener(), this);
        Bukkit.getPluginManager().registerEvents(new DisenchantmentListener(), this);
        recipes.forEach(Bukkit::addRecipe);
    }

    private class CustomRecipesCommand implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (sender.hasPermission(Bukkit.getPluginManager().getPermission("customrecipes.admin"))) {
                sender.sendMessage("Running CustomRecipes commit " + getDescription().getVersion());
            }
            return true;
        }
    }
}
