package gtlp.groundmc.customrecipes

import gtlp.groundmc.customrecipes.listeners.DisenchantmentListener
import gtlp.groundmc.customrecipes.listeners.WorldInteractionListener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.plugin.java.JavaPlugin

/**
 * Plugin to add some custom recipes
 */
class CustomRecipes : JavaPlugin() {

    /**
     * A list of recipes to be added to the game.
     */
    private val recipes = listOf<Recipe>(
            /*
            Shapeless recipes:
             */
            /*
             * Shapeless recipe to convert three wheat to bread.
             * Allows crafting bread without a crafting table.
             */
            ShapelessRecipe(
                    NamespacedKey(this, "wheatToBreadShapeless"),
                    ItemStack(Material.BREAD))
                    .addIngredient(3, Material.WHEAT),

            /*
             * Shapeless recipe to convert an enchanted book back to a normal book.
             * Recycled paper is still good paper.
             */
            ShapelessRecipe(
                    NamespacedKey(this, "enchantmentBookToBookShapeless"),
                    ItemStack(Material.BOOK))
                    .addIngredient(Material.ENCHANTED_BOOK),

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
            ShapedRecipe(
                    NamespacedKey(this, "logToStick"),
                    ItemStack(Material.STICK, 16))
                    .shape("L", "L")
                    .setIngredient('L', Material.LOG, WILDCARD),
            /*
             * 2 Logs to 16 sticks, new logs.
             * Recipe:
             * L
             * L
             *
             * Where "L" is a log.
             */
            ShapedRecipe(
                    NamespacedKey(this, "log2ToStick"),
                    ItemStack(Material.STICK, 16))
                    .shape("L", "L")
                    .setIngredient('L', Material.LOG_2, WILDCARD),
            /*
             * Logs in the shape of planks for a chest to 4 chests, old logs.
             * Recipe:
             * L L L
             * L   L
             * L L L
             *
             * Where "L" is a log.
             */
            ShapedRecipe(
                    NamespacedKey(this, "logToChest"),
                    ItemStack(Material.CHEST, 4))
                    .shape("LLL", "L L", "LLL")
                    .setIngredient('L', Material.LOG, WILDCARD),
            /*
             * Logs in the shape of planks for a chest to 4 chests, new logs.
             * Recipe:
             * L L L
             * L   L
             * L L L
             *
             * Where "L" is a log.
             */
            ShapedRecipe(
                    NamespacedKey(this, "log2ToChest"),
                    ItemStack(Material.CHEST, 4))
                    .shape("LLL", "L L", "LLL")
                    .setIngredient('L', Material.LOG_2, WILDCARD),

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
            ShapedRecipe(
                    NamespacedKey(this, "rottenFleshToLeather"),
                    ItemStack(Material.LEATHER))
                    .shape("RRR", "R R", "RRR")
                    .setIngredient('R', Material.ROTTEN_FLESH)
            /*
            Furnace Recipes:
             */

    )

    override fun onEnable() {
        getCommand("customrecipes").executor = CustomRecipesCommand()

        Bukkit.getPluginManager().registerEvents(WorldInteractionListener(), this)
        Bukkit.getPluginManager().registerEvents(DisenchantmentListener(), this)
        recipes.forEach { Bukkit.addRecipe(it) }
    }

    private inner class CustomRecipesCommand : CommandExecutor {

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            if (sender.hasPermission(Bukkit.getPluginManager().getPermission("customrecipes.admin"))) {
                sender.sendMessage("Running CustomRecipes commit " + description.version)
            }
            return true
        }
    }

    companion object {

        /**
         * Constant wildcard value for recipes
         */
        private const val WILDCARD = java.lang.Short.MAX_VALUE.toInt()
    }
}
