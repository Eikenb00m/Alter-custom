package org.alter.plugins.content.interfaces.bank

import org.alter.api.ClientScript
import org.alter.api.InterfaceDestination
import org.alter.api.ext.*
import org.alter.game.model.World
import org.alter.game.model.container.ItemContainer
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
import org.alter.plugins.content.interfaces.bank.BankTabs.SELECTED_TAB_VARBIT
import org.alter.plugins.content.interfaces.bank.BankTabs.getTabsItems
import org.alter.plugins.content.interfaces.bank.configs.BankComponents
import org.alter.plugins.content.interfaces.bank.configs.BankConstants
import org.alter.plugins.content.interfaces.bank.configs.BankInterfaces
import org.alter.plugins.content.interfaces.bank.configs.BankVarbits
import org.alter.plugins.content.interfaces.equipstats.EquipmentStats.bonusTextMap

object Bank {
    private val Int.child: Int
        get() = this and 0xFFFF

    const val BANK_INTERFACE_ID: Int = BankInterfaces.bank_main
    const val INV_INTERFACE_ID: Int = BankInterfaces.bank_side

    const val BANK_MAINTAB_COMPONENT: Int = BankComponents.main_inventory.child
    const val INV_INTERFACE_CHILD: Int = BankComponents.side_inventory.child

    const val WITHDRAW_AS_VARBIT: Int = BankVarbits.withdraw_mode
    const val REARRANGE_MODE_VARBIT: Int = BankVarbits.rearrange_mode
    const val ALWAYS_PLACEHOLD_VARBIT: Int = BankVarbits.placeholders
    const val LAST_X_INPUT: Int = BankVarbits.last_quantity_input
    const val QUANTITY_VARBIT: Int = BankVarbits.left_click_quantity
    const val INCINERATOR_VARBIT: Int = BankVarbits.incinerator

    private const val BANK_YOUR_LOOT_VARBIT: Int = 4139

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
        val amount = minOf(from.getItemCount(id), amt)
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
            val copy = Item(item.id, minOf(left, item.amount))
            if (copy.amount >= item.amount) {
                copy.copyAttr(item)
            }
            val transfer = from.transfer(to, item = copy, fromSlot = i, note = note, unnote = false)
            withdrawn += transfer?.completed ?: 0
            if (from[i] == null) {
                if (placehold || p.getVarbit(ALWAYS_PLACEHOLD_VARBIT) == 1) {
                    val def = item.getDef()
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
            val copy = Item(item.id, minOf(left, item.amount))
            if (copy.amount >= item.amount) {
                copy.copyAttr(item)
            }

            var toSlot = to.removePlaceholder(player.world, copy)
            var placeholderOrExistingStack = true
            if (toSlot == -1 && !to.contains(item.id)) {
                placeholderOrExistingStack = false
            }
            val transaction = from.transfer(to, item = copy, fromSlot = i, toSlot = toSlot, note = false, unnote = true)
            if (transaction != null) {
                deposited += transaction.completed
            }

            if (deposited > 0) {
                if (curTab != 0 && !placeholderOrExistingStack) {
                    BankTabs.dropToTab(player, curTab, to.nextFreeSlot - 1, hasEmptySlot)
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
        p.setComponentText(BANK_INTERFACE_ID, BankComponents.capacity_text.child, p.bank.capacity.toString())
        p.runClientScript(
            ClientScript(id = 1495),
            "Members' capacity: ${BankConstants.default_capacity}<br>A banker can sell you up to ${BankConstants.purchasable_capacity} more.",
            BankComponents.capacity_container,
            BankComponents.tooltip,
        )
        sendBonuses(p)
        setBankEvents(p)
        p.setVarbit(BANK_YOUR_LOOT_VARBIT, 0)
    }

    private fun setBankEvents(player: Player) {
        val bankRange = 0 until player.bank.capacity
        player.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            component = BANK_MAINTAB_COMPONENT,
            range = bankRange,
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
        player.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            component = BankComponents.tabs.child,
            range = BankSubComponents.main_tab..BankSubComponents.main_tab,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.DragTargetable,
        )
        player.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            component = BankComponents.tabs.child,
            range = BankSubComponents.other_tabs,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.DRAG_DEPTH1,
            InterfaceEvent.DragTargetable,
        )
        val invRange = 0 until player.inventory.capacity
        player.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = BankComponents.side_inventory.child,
            range = invRange,
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
        player.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = BankComponents.worn_inventory.child,
            range = invRange,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp9,
            InterfaceEvent.ClickOp10,
            InterfaceEvent.DRAG_DEPTH1,
            InterfaceEvent.DragTargetable,
        )
        player.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = BankComponents.lootingbag_inventory.child,
            range = invRange,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp2,
            InterfaceEvent.ClickOp3,
            InterfaceEvent.ClickOp4,
            InterfaceEvent.ClickOp5,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.ClickOp10,
        )
        player.setInterfaceEvents(
            interfaceId = INV_INTERFACE_ID,
            component = BankComponents.league_inventory.child,
            range = invRange,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp2,
            InterfaceEvent.ClickOp3,
            InterfaceEvent.ClickOp4,
            InterfaceEvent.ClickOp5,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.ClickOp10,
        )
        player.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            component = BankComponents.incinerator_confirm.child,
            range = 1..player.bank.capacity,
            InterfaceEvent.ClickOp1,
        )
        player.setInterfaceEvents(
            interfaceId = BANK_INTERFACE_ID,
            component = BankComponents.bank_tab_display.child,
            range = 0..8,
            InterfaceEvent.ClickOp1,
        )
    }

    fun sendBonuses(p: Player) {
        val bonuses = p.bonusTextMap()
        with(p) {
            setBankEquipCompText(BankComponents.worn_off_stab.child, bonuses[0])
            setBankEquipCompText(BankComponents.worn_off_slash.child, bonuses[1])
            setBankEquipCompText(BankComponents.worn_off_crush.child, bonuses[2])
            setBankEquipCompText(BankComponents.worn_off_magic.child, bonuses[3])
            setBankEquipCompText(BankComponents.worn_off_range.child, bonuses[4])
            setBankEquipCompText(BankComponents.worn_speed_base.child, bonuses[5])
            setBankEquipCompText(BankComponents.worn_speed.child, bonuses[6])
            setBankEquipCompText(BankComponents.worn_def_stab.child, bonuses[7])
            setBankEquipCompText(BankComponents.worn_def_slash.child, bonuses[8])
            setBankEquipCompText(BankComponents.worn_def_crush.child, bonuses[9])
            setBankEquipCompText(BankComponents.worn_def_range.child, bonuses[10])
            setBankEquipCompText(BankComponents.worn_def_magic.child, bonuses[11])
            setBankEquipCompText(BankComponents.worn_melee_str.child, bonuses[12])
            setBankEquipCompText(BankComponents.worn_ranged_str.child, bonuses[13])
            setBankEquipCompText(BankComponents.worn_magic_dmg.child, bonuses[14])
            setBankEquipCompText(BankComponents.worn_prayer.child, bonuses[15])
            setBankEquipCompText(BankComponents.worn_undead.child, bonuses[16])
            setBankEquipCompText(BankComponents.worn_slayer.child, bonuses[17])
        }
        p.runClientScript(
            ClientScript(id = 7065),
            BankComponents.tooltip,
            BankComponents.worn_magic_dmg,
            "Increases your effective accuracy and damage against undead creatures. For multi-target Ranged and Magic attacks, this applies only to the primary target. It does not stack with the Slayer multiplier.",
        )
    }

    private fun Player.setBankEquipCompText(component: Int, text: String) {
        setComponentText(BANK_INTERFACE_ID, component, text)
    }

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
        val fromItem = this[from] ?: return
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
