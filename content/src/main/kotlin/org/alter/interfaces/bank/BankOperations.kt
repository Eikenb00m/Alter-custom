package org.alter.interfaces.bank

import org.alter.api.BonusSlot
import org.alter.api.ClientScript
import org.alter.api.InterfaceDestination
import org.alter.api.ext.InterfaceEvent
import org.alter.api.ext.closeInterface
import org.alter.api.ext.message
import org.alter.api.ext.openInterface
import org.alter.api.ext.runClientScript
import org.alter.api.ext.sendItemContainer
import org.alter.api.ext.setComponentText
import org.alter.api.ext.setInterfaceEvents
import org.alter.api.ext.setInterfaceUnderlay
import org.alter.api.ext.setVarp
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
import org.alter.game.model.item.ItemDef
import org.alter.game.model.item.getDef
import org.alter.interfaces.bank.configs.BankComponents
import org.alter.interfaces.bank.configs.BankInterfaces
import org.alter.interfaces.bank.configs.BankSubComponents
import org.alter.interfaces.bank.configs.BankVarps
import org.alter.interfaces.bank.alwaysPlacehold
import org.alter.interfaces.bank.withdrawCert
import org.alter.interfaces.bank.util.componentId
import org.alter.interfaces.bank.util.interfaceId

object BankOperations {

    fun Player.openBank() {
        setInterfaceUnderlay(-1, -2)
        openInterface(BankInterfaces.bank_main, InterfaceDestination.MAIN_SCREEN)
        openInterface(BankInterfaces.bank_side, InterfaceDestination.TAB_AREA)
        setVarp(BankVarps.bank_serverside_vars, -1)
        setComponentText(
            interfaceId = BankInterfaces.bank_main,
            component = BankComponents.capacity_text.componentId,
            text = bank.capacity.toString(),
        )
        runClientScript(
            ClientScript(id = 1495),
            "Non-members' capacity: 400<br>Become a member for 400 more.<br>A banker can sell you up to 360 more.<br>+20 for your PIN.<br>Set an Authenticator for 20 more.",
            BankComponents.capacity_container,
            BankComponents.tooltip,
        )

        sendBonuses()
        setInventoryInterfaceEvents()
        setBankInterfaceEvents()
        sendSideInventory()
    }

    private fun Player.sendBonuses() {
        val bonuses = equipment.getBonuses()
        val keys = listOf(
            BonusSlot.ATTACK_STAB,
            BonusSlot.ATTACK_SLASH,
            BonusSlot.ATTACK_CRUSH,
            BonusSlot.ATTACK_MAGIC,
            BonusSlot.ATTACK_RANGED,
            BonusSlot.DEFENCE_STAB,
            BonusSlot.DEFENCE_SLASH,
            BonusSlot.DEFENCE_CRUSH,
            BonusSlot.DEFENCE_MAGIC,
            BonusSlot.DEFENCE_RANGED,
            BonusSlot.STRENGTH_MELEE,
            BonusSlot.STRENGTH_RANGED,
            BonusSlot.STRENGTH_MAGIC,
            BonusSlot.PRAYER,
        )

        val components = listOf(
            BankComponents.worn_off_stab,
            BankComponents.worn_off_slash,
            BankComponents.worn_off_crush,
            BankComponents.worn_off_magic,
            BankComponents.worn_off_range,
            BankComponents.worn_def_stab,
            BankComponents.worn_def_slash,
            BankComponents.worn_def_crush,
            BankComponents.worn_def_magic,
            BankComponents.worn_def_range,
            BankComponents.worn_melee_str,
            BankComponents.worn_ranged_str,
            BankComponents.worn_magic_dmg,
            BankComponents.worn_prayer,
        )

        components.zip(keys).forEach { (component, key) ->
            val value = bonuses[key.id]
            setComponentText(
                interfaceId = BankInterfaces.bank_main,
                component = component.componentId,
                text = value.signedBonus(),
            )
        }
    }

    private fun Int.signedBonus(): String = if (this >= 0) "+$this" else toString()

    private fun Player.setInventoryInterfaceEvents() {
        setInterfaceEvents(
            interfaceId = BankInterfaces.bank_side,
            component = BankComponents.side_inventory.componentId,
            range = 0 until inventory.capacity,
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

        setInterfaceEvents(
            interfaceId = BankInterfaces.bank_main,
            component = BankComponents.incinerator_confirm.componentId,
            range = 1..bank.capacity,
            InterfaceEvent.ClickOp1,
        )
    }

    private fun Player.setBankInterfaceEvents() {
        val mainComponent = BankComponents.main_inventory.componentId
        setInterfaceEvents(
            interfaceId = BankInterfaces.bank_main,
            component = mainComponent,
            range = 0 until bank.capacity,
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

        val extendedRange = (bank.capacity + BankSubComponents.tab_extended_slots_offset.first)..(bank.capacity + BankSubComponents.tab_extended_slots_offset.last)
        setInterfaceEvents(
            interfaceId = BankInterfaces.bank_main,
            component = mainComponent,
            range = extendedRange,
            InterfaceEvent.DragTargetable,
        )

        setInterfaceEvents(
            interfaceId = BankInterfaces.bank_main,
            component = BankComponents.tabs.componentId,
            range = BankSubComponents.main_tab..BankSubComponents.main_tab,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.DragTargetable,
        )

        setInterfaceEvents(
            interfaceId = BankInterfaces.bank_main,
            component = BankComponents.tabs.componentId,
            range = BankSubComponents.other_tabs,
            InterfaceEvent.ClickOp1,
            InterfaceEvent.ClickOp6,
            InterfaceEvent.ClickOp7,
            InterfaceEvent.DRAG_DEPTH1,
            InterfaceEvent.DragTargetable,
        )
    }

    private fun Player.sendSideInventory() {
        // TODO: hook up bankside containers once cache enums are available.
        sendItemContainer(
            BankComponents.side_inventory.interfaceId,
            BankComponents.side_inventory.componentId,
            emptyArray(),
        )
    }

    fun Player.closeBankInterfaces() {
        closeInterface(BankInterfaces.bank_main)
        closeInterface(BankInterfaces.bank_side)
        closeInterface(dest = InterfaceDestination.TAB_AREA)
        closeInterface(dest = InterfaceDestination.OVERLAY)
    }

    fun Player.deposit(id: Int, amount: Int) {
        val from = inventory
        val to = bank
        var deposited = 0
        val target = amount.coerceAtMost(from.getItemCount(id))
        for (slot in 0 until from.capacity) {
            val item = from[slot] ?: continue
            if (item.id != id) continue
            if (deposited >= target) break
            val remaining = target - deposited
            val copy = Item(item.id, remaining.coerceAtMost(item.amount)).also { candidate ->
                if (candidate.amount >= item.amount) {
                    candidate.copyAttr(item)
                }
            }
            val result = from.transfer(to, copy, fromSlot = slot, note = false, unnote = true)
            deposited += result?.completed ?: 0
        }
        if (deposited == 0) {
            message("Bank full.")
        }
    }

    fun Player.withdraw(id: Int, amount: Int, slot: Int, placehold: Boolean) {
        val from = bank
        val to = inventory
        val maxAmount = amount.coerceAtMost(from.getItemCount(id))
        var withdrawn = 0
        val note = withdrawCert
        for (index in slot until from.capacity) {
            val item = from[index] ?: continue
            if (item.id != id) continue
            if (withdrawn >= maxAmount) break
            val remaining = maxAmount - withdrawn
            val copy = Item(item.id, remaining.coerceAtMost(item.amount)).also { candidate ->
                if (candidate.amount >= item.amount) {
                    candidate.copyAttr(item)
                }
            }
            val transfer = from.transfer(to, copy, fromSlot = index, note = note, unnote = false)
            withdrawn += transfer?.completed ?: 0
            if (from[index] == null && (placehold || alwaysPlacehold)) {
                createPlaceholder(item.getDef(), index)
            }
        }
        if (withdrawn == 0) {
            message("You don't have enough inventory space.")
        } else if (withdrawn != maxAmount) {
            message("You don't have enough inventory space to withdraw that many.")
        }
    }

    private fun Player.createPlaceholder(def: ItemDef?, slot: Int) {
        if (def == null || def.placeholderLink <= 0) return
        bank[slot] = Item(def.placeholderLink, -2)
    }
}
