package org.alter.plugins.content.interfaces.bank

import org.alter.api.*
import org.alter.api.cfg.*
import org.alter.api.dsl.*
import org.alter.api.ext.*
import org.alter.game.*
import org.alter.game.model.*
import org.alter.game.model.attr.*
import org.alter.game.model.attr.INTERACTING_COMPONENT_CHILD
import org.alter.game.model.attr.INTERACTING_ITEM_SLOT
import org.alter.game.model.attr.OTHER_ITEM_SLOT_ATTR
import org.alter.game.model.container.*
import org.alter.game.model.container.key.*
import org.alter.game.model.entity.*
import org.alter.game.model.item.*
import org.alter.game.model.priv.Privilege
import org.alter.game.model.queue.*
import org.alter.game.model.shop.*
import org.alter.game.model.timer.*
import org.alter.game.plugin.*
import org.alter.plugins.content.interfaces.bank.Bank.BANK_INTERFACE_ID
import org.alter.plugins.content.interfaces.bank.Bank.BANK_MAINTAB_COMPONENT
import org.alter.plugins.content.interfaces.bank.Bank.REARRANGE_MODE_VARBIT
import org.alter.plugins.content.interfaces.bank.Bank.insert
import org.alter.plugins.content.interfaces.bank.BankTabs.BANK_TABLIST_ID
import org.alter.plugins.content.interfaces.bank.BankTabs.SELECTED_TAB_VARBIT
import org.alter.plugins.content.interfaces.bank.BankTabs.dropToTab
import org.alter.plugins.content.interfaces.bank.BankTabs.insertionPoint
import org.alter.plugins.content.interfaces.bank.BankTabs.numTabsUnlocked
import org.alter.plugins.content.interfaces.bank.BankTabs.shiftTabs
import org.alter.plugins.content.interfaces.bank.BankTabs.sizeVarbit
import org.alter.plugins.content.interfaces.bank.BankTabs.startPoint

class BankTabsPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {
        
    init {

        /**
         * Handles setting the current selected tab varbit on tab selection.
         *
         * When you take out to inv from bank -> It leaves empty gaps -> But when you put everything via Bank All -> The empty gaps get subtracted.
         */
        onButton(BANK_INTERFACE_ID, BANK_TABLIST_ID) {
            val dstTab = player.getInteractingSlot() - 10
            val opt = player.getInteractingOption()
            when (opt) {
                1 -> {
                    if (dstTab <= numTabsUnlocked(player)) {
                        player.setVarbit(SELECTED_TAB_VARBIT, dstTab)
                    }
                }
                5 -> {
                    player.message("Not implemented [Bank1]")
                }
                6 -> {
                    // @TODO Remove placeholders for that tab
                    // If no placeholders Text: You don't have any placeholders to release. else Nothing xd
                    player.message("Not implemented [Bank2]")
                }
                else -> {
                    player.printAndMessageIfHasPower(
                        ("Unknown option from component: [$BANK_INTERFACE_ID:$BANK_TABLIST_ID]: $opt"),
                        Privilege.ADMIN_POWER,
                    )
                }
            }
        }

        onButton(BANK_INTERFACE_ID, 113) {
            // player.setVarbit(386, 1)
            // player.closeInterface(dest = InterfaceDestination.TAB_AREA)
            // player.openInterface(INV_INTERFACE_ID, 4)
        }

        /**
         * Moving items to tabs via the top tabs bar.
         */
        onComponentToComponentItemSwap(
            srcInterfaceId = BANK_INTERFACE_ID,
            srcComponent = BANK_MAINTAB_COMPONENT,
            dstInterfaceId = BANK_INTERFACE_ID,
            dstComponent = BANK_TABLIST_ID,
        ) {
            val srcComponent = player.attr[INTERACTING_COMPONENT_CHILD]!!
            if (srcComponent == BANK_TABLIST_ID) { // attempting to drop tab on bank!!
                return@onComponentToComponentItemSwap
            } else { // perform drop to tab
                val dstSlot = player.attr[OTHER_ITEM_SLOT_ATTR]!!
                dropToTab(player, dstSlot - 10)
            }
        }

        /**
         * Moving tabs via the top tabs bar to swap/insert their order.
         */
        onComponentToComponentItemSwap(
            srcInterfaceId = BANK_INTERFACE_ID,
            srcComponent = BANK_TABLIST_ID,
            dstInterfaceId = BANK_INTERFACE_ID,
            dstComponent = BANK_TABLIST_ID,
        ) {
            val container = player.bank
            val srcTab = player.attr[INTERACTING_ITEM_SLOT]!!
            val dstTab = player.attr[OTHER_ITEM_SLOT_ATTR]!!
            if (dstTab == 0) {
                var item = startPoint(player, srcTab)
                var end = insertionPoint(player, srcTab)
                while (item != end) {
                    container.insert(item, container.nextFreeSlot - 1)
                    end--
                    val srcVarbit = sizeVarbit(srcTab)
                    player.setVarbit(srcVarbit, player.getVarbit(srcVarbit) - 1)
                    if (player.getVarbit(srcVarbit) == 0 && srcTab <= numTabsUnlocked(player)) {
                        shiftTabs(player, srcTab)
                    }
                }
                return@onComponentToComponentItemSwap
            }
            val srcVarbit = sizeVarbit(srcTab)
            val dstVarbit = sizeVarbit(dstTab)
            val srcSize = player.getVarbit(srcVarbit)
            val dstSize = player.getVarbit(dstVarbit)
            val insertMode = player.getVarbit(REARRANGE_MODE_VARBIT) == 1
            if (insertMode) {
                if (dstTab < srcTab) { // insert each of the items in srcTab directly before dstTab moving index up each time to account for shifts
                    var destination = startPoint(player, dstTab)
                    for (item in startPoint(player, srcTab) until insertionPoint(player, srcTab))
                        container.insert(item, destination++)
                    // update tab size varbits according to insertion location
                    var holder = player.getVarbit(dstVarbit)
                    player.setVarbit(dstVarbit, srcSize)
                    for (tab in dstTab + 1..srcTab) {
                        val tabVarbit = sizeVarbit(tab)
                        val temp = player.getVarbit(tabVarbit)
                        player.setVarbit(tabVarbit, holder)
                        holder = temp
                    }
                } else { // insert each item in srcTab before dstTab consuming index move in the shifts already in insert()
                    if (dstTab == srcTab + 1) {
                        return@onComponentToComponentItemSwap
                    }
                    val destination = startPoint(player, dstTab) - 1
                    val srcStart = startPoint(player, srcTab)
                    for (item in 1..srcSize)
                        container.insert(srcStart, destination)
                    var holder = player.getVarbit(dstVarbit)
                    player.setVarbit(dstVarbit, srcSize)
                    for (tab in dstTab - 2 downTo srcTab) {
                        val tabVarbit = sizeVarbit(tab)
                        val temp = player.getVarbit(tabVarbit)
                        player.setVarbit(tabVarbit, holder)
                        holder = temp
                    }
                }
            } else { // swap tabs in place
                val smallerTab = if (dstSize <= srcSize) dstTab else srcTab
                val smallVarbit = sizeVarbit(smallerTab)
                val smallSize = player.getVarbit(smallVarbit)
                val largerTab = if (dstSize > srcSize) dstTab else srcTab
                val largeVarbit = sizeVarbit(largerTab)
                val largeSize = player.getVarbit(largeVarbit)
                val smallStart = startPoint(player, smallerTab)
                val largeStart = startPoint(player, largerTab)

                // direct swap those that will easily fit
                var dex = largeStart
                for (item in smallStart until insertionPoint(player, smallerTab)) {
                    container.swap(item, dex++)
                }
                // insert left overs from larger tab into smaller tab's end
                var insertDex = insertionPoint(player, smallerTab)
                var largeEnd = insertionPoint(player, largerTab)
                while (dex != largeEnd) {
                    if (largerTab < smallerTab) { // not size but tab order
                        container.insert(dex, insertDex - 1)
                        largeEnd--
                    } else {
                        container.insert(dex++, insertDex++)
                    }
                }
                // update each tab's size to reflect new contents
                player.setVarbit(smallVarbit, largeSize)
                player.setVarbit(largeVarbit, smallSize)
            }
        }
    }
}
