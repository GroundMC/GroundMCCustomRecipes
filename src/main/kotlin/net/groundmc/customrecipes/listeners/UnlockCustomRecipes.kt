package net.groundmc.customrecipes.listeners

import net.groundmc.customrecipes.CustomRecipes
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class UnlockCustomRecipes(
    private val plugin: CustomRecipes,
    private val recipes: Map<Material, Material>
) : Listener {
    @EventHandler
    fun unlockAllRecipesOnJoin(event: PlayerJoinEvent) {
        event.player.discoverRecipes(recipes.map { (source, target) ->
            NamespacedKey(plugin, "slabsToBlocks${source.name}${target.name}")
        })
    }
}
