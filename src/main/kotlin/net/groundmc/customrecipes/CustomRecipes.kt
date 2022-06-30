package net.groundmc.customrecipes

import net.groundmc.customrecipes.listeners.DisenchantmentListener
import net.groundmc.customrecipes.listeners.UnlockCustomRecipes
import net.groundmc.customrecipes.listeners.WorldInteractionListener
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.inventory.*
import org.bukkit.plugin.java.JavaPlugin

/**
 * Plugin to add some custom recipes
 */
class CustomRecipes : JavaPlugin() {

    /**
     * A list of recipes to be added to the game.
     */
    private val recipes = mutableListOf<Recipe>(
        /*
        Shapeless recipes:
         */
        /*
         * Shapeless recipe to convert three wheat to bread.
         * Allows crafting bread without a crafting table.
         */
        ShapelessRecipe(
            NamespacedKey(this, "wheatToBreadShapeless"),
            ItemStack(Material.BREAD)
        )
            .addIngredient(3, Material.WHEAT),

        /*
         * Shapeless recipe to convert an enchanted book back to a normal book.
         * Recycled paper is still good paper.
         */
        ShapelessRecipe(
            NamespacedKey(this, "enchantmentBookToBookShapeless"),
            ItemStack(Material.BOOK)
        )
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
            ItemStack(Material.STICK, 16)
        )
            .shape("L", "L")
            .setIngredient('L', logChoice),

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
            ItemStack(Material.CHEST, 4)
        )
            .shape("LLL", "L L", "LLL")
            .setIngredient('L', logChoice)

        /*
        Furnace Recipes:
         */

    )

    override fun onEnable() {
        getCommand("customrecipes")?.setExecutor(CustomRecipesCommand())

        Bukkit.getPluginManager().registerEvents(WorldInteractionListener(this), this)
        Bukkit.getPluginManager().registerEvents(DisenchantmentListener(this), this)
        Bukkit.getPluginManager().registerEvents(UnlockCustomRecipes(this, slabsToPlanks), this)


        addDynamicRecipes()

        recipes.forEach { Bukkit.addRecipe(it) }
    }

    private fun addDynamicRecipes() {
        slabsToPlanks.forEach { (source, target) ->
            recipes += ShapedRecipe(
                NamespacedKey(this, "slabsToBlocks${source.name}${target.name}"),
                ItemStack(target, 1)
            )
                .shape("SS")
                .setIngredient('S', source)
                .apply {
                    group = "slabsToBlocks"
                }
        }
    }

    private inner class CustomRecipesCommand : CommandExecutor {

        override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
            if (sender.hasPermission(Bukkit.getPluginManager().getPermission("customrecipes.admin")!!)) {
                sender.sendMessage("Running CustomRecipes commit " + description.version)
            }
            return true
        }
    }

    companion object {
        private val logChoice = RecipeChoice.MaterialChoice(
            Material.OAK_LOG,
            Material.BIRCH_LOG,
            Material.SPRUCE_LOG,
            Material.JUNGLE_LOG,
            Material.DARK_OAK_LOG,
            Material.ACACIA_LOG,

            // 1.16
            Material.CRIMSON_STEM,
            Material.WARPED_STEM,

            // 1.19
            Material.MANGROVE_LOG,
        )

        private val slabsToPlanks = mapOf(
            Material.OAK_SLAB to Material.OAK_PLANKS,
            Material.BIRCH_SLAB to Material.BIRCH_PLANKS,
            Material.SPRUCE_SLAB to Material.SPRUCE_PLANKS,
            Material.DARK_OAK_SLAB to Material.DARK_OAK_PLANKS,
            Material.JUNGLE_SLAB to Material.JUNGLE_PLANKS,
            Material.ACACIA_SLAB to Material.ACACIA_PLANKS,
            Material.STONE_SLAB to Material.STONE,
            Material.COBBLESTONE_SLAB to Material.COBBLESTONE,
            Material.SMOOTH_STONE_SLAB to Material.SMOOTH_STONE,
            Material.SANDSTONE_SLAB to Material.SANDSTONE,
            Material.CUT_SANDSTONE_SLAB to Material.CUT_SANDSTONE,
            Material.BRICK_SLAB to Material.BRICKS,
            Material.STONE_BRICK_SLAB to Material.STONE_BRICKS,
            Material.NETHER_BRICK_SLAB to Material.NETHER_BRICKS,
            Material.QUARTZ_SLAB to Material.QUARTZ_BLOCK,
            Material.RED_SANDSTONE_SLAB to Material.RED_SANDSTONE,
            Material.CUT_RED_SANDSTONE_SLAB to Material.CUT_RED_SANDSTONE,
            Material.PURPUR_SLAB to Material.PURPUR_BLOCK,
            Material.PRISMARINE_SLAB to Material.PRISMARINE,
            Material.PRISMARINE_BRICK_SLAB to Material.PRISMARINE_BRICKS,
            Material.DARK_PRISMARINE_SLAB to Material.DARK_PRISMARINE,
            Material.POLISHED_GRANITE_SLAB to Material.POLISHED_GRANITE,
            Material.POLISHED_ANDESITE_SLAB to Material.POLISHED_ANDESITE,
            Material.POLISHED_DIORITE_SLAB to Material.POLISHED_DIORITE,
            Material.GRANITE_SLAB to Material.GRANITE,
            Material.DIORITE_SLAB to Material.DIORITE,
            Material.ANDESITE_SLAB to Material.ANDESITE,
            Material.SMOOTH_SANDSTONE_SLAB to Material.SMOOTH_SANDSTONE,
            Material.SMOOTH_RED_SANDSTONE_SLAB to Material.SMOOTH_RED_SANDSTONE,
            Material.MOSSY_STONE_BRICK_SLAB to Material.MOSSY_STONE_BRICKS,
            Material.MOSSY_COBBLESTONE_SLAB to Material.MOSSY_COBBLESTONE,
            Material.END_STONE_BRICK_SLAB to Material.END_STONE_BRICKS,
            Material.SMOOTH_QUARTZ_SLAB to Material.SMOOTH_QUARTZ,
            Material.RED_NETHER_BRICK_SLAB to Material.RED_NETHER_BRICKS,

            //1.16
            Material.CRIMSON_SLAB to Material.CRIMSON_PLANKS,
            Material.WARPED_SLAB to Material.WARPED_PLANKS,
            Material.BLACKSTONE_SLAB to Material.BLACKSTONE,
            Material.POLISHED_BLACKSTONE_SLAB to Material.POLISHED_BLACKSTONE,
            Material.POLISHED_BLACKSTONE_BRICK_SLAB to Material.POLISHED_BLACKSTONE_BRICKS,

            //1.17
            Material.DEEPSLATE_BRICK_SLAB to Material.DEEPSLATE_BRICKS,
            Material.COBBLED_DEEPSLATE_SLAB to Material.COBBLED_DEEPSLATE,
            Material.POLISHED_DEEPSLATE_SLAB to Material.POLISHED_DEEPSLATE,
            Material.DEEPSLATE_TILE_SLAB to Material.DEEPSLATE_TILES,

            Material.CUT_COPPER_SLAB to Material.CUT_COPPER,
            Material.EXPOSED_CUT_COPPER_SLAB to Material.EXPOSED_CUT_COPPER,
            Material.OXIDIZED_CUT_COPPER_SLAB to Material.OXIDIZED_CUT_COPPER,
            Material.WEATHERED_CUT_COPPER_SLAB to Material.WEATHERED_CUT_COPPER,
            Material.WAXED_CUT_COPPER_SLAB to Material.WAXED_CUT_COPPER,
            Material.WAXED_EXPOSED_CUT_COPPER_SLAB to Material.WAXED_EXPOSED_CUT_COPPER,
            Material.WAXED_OXIDIZED_CUT_COPPER_SLAB to Material.WAXED_OXIDIZED_CUT_COPPER,
            Material.WAXED_WEATHERED_CUT_COPPER_SLAB to Material.WAXED_WEATHERED_CUT_COPPER,

            //1.19
            Material.MANGROVE_SLAB to Material.MANGROVE_PLANKS,
        )
    }
}
