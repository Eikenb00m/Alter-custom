package org.alter.plugins.content.interfaces.bank

import org.alter.api.BonusSlot
import org.alter.api.ClientScript
import org.alter.api.InterfaceDestination
import org.alter.api.ext.*
import org.alter.game.model.World
import org.alter.game.model.container.ItemContainer
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
import org.alter.plugins.content.interfaces.bank.BankTabs.SELECTED_TAB_VARBIT
import org.alter.plugins.content.interfaces.bank.BankTabs.getTabsItems
import org.alter.plugins.content.interfaces.equipstats.EquipmentStats
import org.alter.plugins.content.interfaces.equipstats.EquipmentStats.bonusTextMap

/**
 * @author Tom <rspsmods@gmail.com>
 */
object Bank {
    const val BANK_INTERFACE_ID = 12
    const val BANK_MAINTAB_COMPONENT = 13
    const val BANK_TABS_COMPONENT = 11
    const val BANK_CAPACITY_LAYER_COMPONENT = 7
    const val BANK_CAPACITY_TEXT_COMPONENT = 9
    const val BANK_TOOLTIP_COMPONENT = 130
    const val BANK_INCINERATOR_CONFIRM_COMPONENT = 50
    const val BANK_TAB_DISPLAY_COMPONENT = 140

    const val INV_INTERFACE_ID = 15
    const val INV_INTERFACE_CHILD = 3
    const val INV_WORN_COMPONENT = 4
    const val INV_LOOTING_BAG_COMPONENT = 11
    const val INV_LEAGUE_SECOND_INV_COMPONENT = 18

    const val WITHDRAW_AS_VARBIT = 3958
    const val REARRANGE_MODE_VARBIT = 3959
    const val ALWAYS_PLACEHOLD_VARBIT = 3755
    const val LAST_X_INPUT = 3960
    const val QUANTITY_VARBIT = 6590
    const val INCINERATOR_VARBIT = 5102

    /**
     * Visual varbit for the "Bank your loot" tab area interface when storing
     * items from a looting bag into the bank.
     */
    private const val BANK_YOUR_LOOT_VARBIT = 4139

    fun withdraw(
        p: Player,
        id: Int,
        amt: Int,
        slot: Int,
        placehold: Boolean,
    ) {
        var withdrawn = 0
        val from = p.bank
        val to = p.inventory
        val amount = Math.min(from.getItemCount(id), amt)
        val note = p.getVarbit(WITHDRAW_AS_VARBIT) == 1
        for (i in slot until from.capacity) {
            val item = from[i] ?: continue
            if (item.id != id) {
                continue
            }
            if (withdrawn >= amount) {
                break
            }
            val left = amount - withdrawn
            val copy = Item(item.id, Math.min(left, item.amount))
            if (copy.amount >= item.amount) {
                copy.copyAttr(item)
            }
            val transfer = from.transfer(to, item = copy, fromSlot = i, note = note, unnote = false)
            withdrawn += transfer?.completed ?: 0
            if (from[i] == null) {
                if (placehold || p.getVarbit(ALWAYS_PLACEHOLD_VARBIT) == 1) {
                    val def = item.getDef()
                    /**
                     * Make sure the item has a valid placeholder item in its
                     * definition.
                     */
                    if (def.placeholderLink > 0) {
                        p.bank[i] = Item(def.placeholderLink, -2)
                    }
                }
            }
        }
        if (withdrawn == 0) {
            p.message("You don't have enough inventory space.")
        } else if (withdrawn != amount) {
            p.message("You don't have enough inventory space to withdraw that many.")
        }
    }

    fun deposit(
        player: Player,
        id: Int,
        amt: Int,
    ) {
        println("Deposit method executed ====")
        val from = player.inventory
        val to = player.bank
        val amount = from.getItemCount(id).coerceAtMost(amt)
        var deposited = 0
        for (i in 0 until from.capacity) {
            val item = from[i] ?: continue
            if (item.id != id) {
                continue
            }
            if (deposited >= amount) {
                break
            }
            val curTab = player.getVarbit(SELECTED_TAB_VARBIT)
            val hasEmptySlot = getTabsItems(player, curTab).contains(null)

            val left = amount - deposited
            val copy = Item(item.id, Math.min(left, item.amount))
            if (copy.amount >= item.amount) {
                copy.copyAttr(item)
            }
            /**
             * @TODO Add handling if curTab is not selected --> We load items into main tab. Even if other tabs have empty slots.
             * @TODO When taking tabs first item it will do shift. --> Empty slots are moved not removed.
             */
            var toSlot = to.removePlaceholder(player.world, copy)
            var placeholderOrExistingStack = true
            if (toSlot == -1 && !to.contains(item.id)) {
                placeholderOrExistingStack = false
                //toSlot = to.getLastFreeSlot()
            }
            val transaction = from.transfer(to, item = copy, fromSlot = i, toSlot = toSlot, note = false, unnote = true)
            if (transaction != null) {
                deposited += transaction.completed
            }

            if (deposited > 0) {
                if (curTab != 0 && !placeholderOrExistingStack) {
                    BankTabs.dropToTab(player, curTab, to.getLastFreeSlotReversed() - 1, hasEmptySlot)
                }
            }
        }
        if (deposited == 0) {
            player.message("Bank full.")
        }
    }

    fun open(p: Player) {
        p.setInterfaceUnderlay(-1, -2)
        p.openInterface(BANK_INTERFACE_ID, InterfaceDestination.MAIN_SCREEN)
        p.openInterface(INV_INTERFACE_ID, InterfaceDestination.TAB_AREA)
        p.setVarp(262, -1)
        val bankCapacity = p.bank.capacity
        p.setComponentText(interfaceId = BANK_INTERFACE_ID, component = BANK_CAPACITY_TEXT_COMPONENT, text = bankCapacity.toString())
        p.runClientScript(
            ClientScript(id = 1495),
            "Non-members' capacity: 400<br>Become a member for 400 more.<br>A banker can sell you up to 360 more.<br>+20 for your PIN.<br>Set an Authenticator for 20 more.",
            packComponent(BANK_INTERFACE_ID, BANK_CAPACITY_LAYER_COMPONENT),
            packComponent(BANK_INTERFACE_ID, BANK_TOOLTIP_COMPONENT),
        )
        sendBonuses(p)
        p.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = INV_INTERFACE_CHILD,
            0..27,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp2,
            InterfaceEvent.ClickOp3,
            InterfaceEvent.ClickOp4,
            InterfaceEvent.ClickOp5,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.ClickOp8,
            InterfaceEvent.ClickOp9,
            InterfaceEvent.ClickOp10,
            InterfaceEvent.DRAG_DEPTH1,
            InterfaceEvent.DragTargetable,
        )
        if (bankCapacity > 0) {
            p.setInterfaceEvents(
                interfaceId = BANK_INTERFACE_ID,
                component = BANK_INCINERATOR_CONFIRM_COMPONENT,
                1..bankCapacity,
                InterfaceEvent.ClickOp1,
            )
        }
        p.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = INV_LOOTING_BAG_COMPONENT,
            0..27,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp2,
            InterfaceEvent.ClickOp3,
            InterfaceEvent.ClickOp4,
            InterfaceEvent.ClickOp5,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.ClickOp10,
        )
        if (bankCapacity > 0) {
            val bankSlotRange = 0 until bankCapacity
            val lastBankSlot = bankCapacity - 1
            p.setInterfaceEvents(
                interfaceId = BANK_INTERFACE_ID,
                component = BANK_MAINTAB_COMPONENT,
                bankSlotRange,
                InterfaceEvent.ClickOp1,
                InterfaceEvent.ClickOp2,
                InterfaceEvent.ClickOp3,
                InterfaceEvent.ClickOp4,
                InterfaceEvent.ClickOp5,
                InterfaceEvent.ClickOp6,
                InterfaceEvent.ClickOp7,
                InterfaceEvent.ClickOp8,
                InterfaceEvent.ClickOp9,
                InterfaceEvent.ClickOp10,
                InterfaceEvent.DRAG_DEPTH2,
                InterfaceEvent.DragTargetable,
            )
            p.setInterfaceEvents(
                interfaceId = BANK_INTERFACE_ID,
                component = BANK_MAINTAB_COMPONENT,
                (lastBankSlot + 10)..(lastBankSlot + 18),
                InterfaceEvent.ClickOp1,
            )
            p.setInterfaceEvents(
                interfaceId = BANK_INTERFACE_ID,
                component = BANK_MAINTAB_COMPONENT,
                (lastBankSlot + 19)..(lastBankSlot + 28),
                InterfaceEvent.DragTargetable,
            )
        }
        p.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            BANK_TABS_COMPONENT,
            10..10,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.DragTargetable,
        )
        p.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            BANK_TABS_COMPONENT,
            11..19,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.DRAG_DEPTH1,
            InterfaceEvent.DragTargetable,
        )
        p.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = INV_WORN_COMPONENT,
            0..27,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp9,
            InterfaceEvent.ClickOp10,
            InterfaceEvent.DRAG_DEPTH1,
            InterfaceEvent.DragTargetable,
        )
        p.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = INV_LEAGUE_SECOND_INV_COMPONENT,
            0..27,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp2,
            InterfaceEvent.ClickOp3,
            InterfaceEvent.ClickOp4,
            InterfaceEvent.ClickOp5,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.ClickOp10,
        )
        p.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            component = BANK_TAB_DISPLAY_COMPONENT,
            0..8,
            InterfaceEvent.ClickOp1,
        )
        p.setVarbit(BANK_YOUR_LOOT_VARBIT, 0)
    }

    fun sendBonuses(p: Player) {

        with(p) {
            BANK_BONUS_COMPONENTS.forEachIndexed { index, component ->
                setBankEquipCompText(component = component, text = bonusTextMap()[index])
            }
        }
        p.runClientScript(
            ClientScript(id = 7065),
            packComponent(BANK_INTERFACE_ID, BANK_TOOLTIP_COMPONENT),
            packComponent(BANK_INTERFACE_ID, BANK_UNDEAD_BONUS_COMPONENT),
            "Increases your effective accuracy and damage against undead creatures. For multi-target Ranged and Magic attacks, this applies only to the primary target. It does not stack with the Slayer multiplier.",
        )
        p.setComponentText(interfaceId = BANK_INTERFACE_ID, component = BANK_SLAYER_BONUS_COMPONENT, text = "Slayer: 0%") // @TODO
    }
    private fun Player.setBankEquipCompText(component: Int, text: String) {
        this.setComponentText(interfaceId = BANK_INTERFACE_ID, component = component, text = text)
    }

    private fun packComponent(interfaceId: Int, componentId: Int): Int = (interfaceId shl 16) or componentId

    private val BANK_BONUS_COMPONENTS = intArrayOf(
        101,
        102,
        103,
        104,
        105,
        135,
        136,
        107,
        108,
        109,
        111,
        110,
        113,
        114,
        115,
        116,
        118,
        119,
    )

    private const val BANK_UNDEAD_BONUS_COMPONENT = 118
    private const val BANK_SLAYER_BONUS_COMPONENT = 119

    fun ItemContainer.removePlaceholder(
        world: World,
        item: Item,
    ): Int {
        val def = item.toUnnoted().getDef()
        val slot = if (def.placeholderLink > 0) indexOfFirst { it?.id == def.placeholderLink && it.amount == -2 } else -1
        if (slot != -1) {
            this[slot] = null
        }
        return slot
    }

    fun ItemContainer.insert(
        from: Int,
        to: Int,
    ) {
        val fromItem = this[from]!! // Shouldn't be null

        this[from] = null

        if (from < to) {
            for (i in from until to) {
                this[i] = this[i + 1]
            }
        } else {
            for (i in from downTo to + 1) {
                this[i] = this[i - 1]
            }
        }
        this[to] = fromItem
    }
}
