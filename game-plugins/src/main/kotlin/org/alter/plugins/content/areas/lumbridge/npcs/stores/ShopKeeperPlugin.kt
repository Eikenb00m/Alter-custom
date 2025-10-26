package org.alter.plugins.content.areas.lumbridge.npcs.stores

import org.alter.api.ext.*
import org.alter.game.Server
import org.alter.game.model.Direction
import org.alter.game.model.World
import org.alter.game.model.entity.Player
import org.alter.game.model.queue.QueueTask
import org.alter.game.model.shop.PurchasePolicy
import org.alter.game.model.shop.ShopItem
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository
import org.alter.plugins.content.mechanics.shops.CoinCurrency
import org.alter.rscm.RSCM.getRSCM

class ShopKeeperPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
    private val shopkeepers = listOf("npcs.generalassistant1", "npcs.generalshopkeeper1")

    private val dialogOptions: List<String> = listOf(
        "Yes please. What are you selling?",
        "No thanks.",
    )

    private val storeItems = listOf(
        ShopItem(getRSCM("items.pot_empty"), 5, 1, 0),
        ShopItem(getRSCM("items.jug_empty"), 2, 1, 0),
        ShopItem(getRSCM("items.pack_jug_empty"), 5, 182, 56),
        ShopItem(getRSCM("items.shears"), 2, 1, 0),
        ShopItem(getRSCM("items.knife"), 5, 7, 0),
        ShopItem(getRSCM("items.bucket_empty"), 3, 2, 0),
        ShopItem(getRSCM("items.pack_bucket"), 15, 650, 200),
        ShopItem(getRSCM("items.bowl_empty"), 2, 5, 1),
        ShopItem(getRSCM("items.cake_tin"), 2, 13, 4),
        ShopItem(getRSCM("items.tinderbox"), 2, 1, 0),
        ShopItem(getRSCM("items.chisel"), 2, 1, 0),
        ShopItem(getRSCM("items.spade"), 1, 3, 1),
        ShopItem(getRSCM("items.hammer"), 5, 1, 0),
        ShopItem(getRSCM("items.newcomer_map"), 5, 1, 0),
        ShopItem(getRSCM("items.sos_security_book"), 5, 2, 0),
    )

    init {
        spawnNpc("npcs.generalshopkeeper1", 3211, 3246, 0, 3, Direction.EAST)
        spawnNpc("npcs.generalassistant1", 3211, 3247, 0, 3, Direction.EAST)

        createShop("Lumbridge General Store", CoinCurrency(), purchasePolicy = PurchasePolicy.BUY_TRADEABLES) {
            storeItems.forEachIndexed { index, item ->
                items[index] = item
            }
        }

        shopkeepers.forEach {
            onNpcOption(it, option = "talk-to") { player.queue { dialog(player) } }

            onNpcOption(it, option = "trade") { player.shop() }
        }
    }

    fun Player.shop() = this.openShop("Lumbridge General Store")

    suspend fun QueueTask.dialog(player: Player) {
        chatNpc(player, "Can I help you at all?")

        when (options(player, *dialogOptions.toTypedArray())) {
            1 -> player.shop()
            2 -> chatPlayer(player, "No thanks.")
        }
    }
}
