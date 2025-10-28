package org.alter.interfaces.bank

import dev.openrune.types.ItemServerType
import org.alter.api.BonusSlot
import org.alter.api.ClientScript
import org.alter.api.InterfaceDestination
import org.alter.api.ext.InterfaceEvent
import org.alter.api.ext.closeInterface
import org.alter.api.ext.getBonus
import org.alter.api.ext.getMagicDamageBonus
import org.alter.api.ext.getPrayerBonus
import org.alter.api.ext.getRangedStrengthBonus
import org.alter.api.ext.getStrengthBonus
import org.alter.api.ext.message
import org.alter.api.ext.openInterface
import org.alter.api.ext.runClientScript
import org.alter.api.ext.sendItemContainer
import org.alter.api.ext.setComponentText
import org.alter.api.ext.setInterfaceEvents
import org.alter.api.ext.setInterfaceUnderlay
import org.alter.api.ext.setVarp
import org.alter.api.ext.transfer
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
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
        calculateBonuses()
        val texts = bonusTextMap()
        val components = listOf(
            BankComponents.worn_off_stab,
            BankComponents.worn_off_slash,
            BankComponents.worn_off_crush,
            BankComponents.worn_off_magic,
            BankComponents.worn_off_range,
            BankComponents.worn_speed_base,
            BankComponents.worn_speed,
            BankComponents.worn_def_stab,
            BankComponents.worn_def_slash,
            BankComponents.worn_def_crush,
            BankComponents.worn_def_range,
            BankComponents.worn_def_magic,
            BankComponents.worn_melee_str,
            BankComponents.worn_ranged_str,
            BankComponents.worn_magic_dmg,
            BankComponents.worn_prayer,
            BankComponents.worn_undead,
            BankComponents.worn_slayer,
        )

        components.zip(texts).forEach { (component, text) ->
            setComponentText(
                interfaceId = component.interfaceId,
                component = component.componentId,
                text = text,
            )
        }

        runClientScript(
            ClientScript(id = 7065),
            BankComponents.tooltip,
            BankComponents.tooltip,
            "Increases your effective accuracy and damage against undead creatures. For multi-target Ranged and Magic attacks, this applies only to the primary target. It does not stack with the Slayer multiplier.",
        )
    }

    private fun Player.bonusTextMap(): List<String> {
        val magicDamageBonus = getMagicDamageBonus().toDouble()
        return listOf(
            "Stab: ${formatBonus(getBonus(BonusSlot.ATTACK_STAB))}",
            "Slash: ${formatBonus(getBonus(BonusSlot.ATTACK_SLASH))}",
            "Crush: ${formatBonus(getBonus(BonusSlot.ATTACK_CRUSH))}",
            "Magic: ${formatBonus(getBonus(BonusSlot.ATTACK_MAGIC))}",
            "Range: ${formatBonus(getBonus(BonusSlot.ATTACK_RANGED))}",
            "Base: TODO",
            "Actual: TODO",
            "Stab: ${formatBonus(getBonus(BonusSlot.DEFENCE_STAB))}",
            "Slash: ${formatBonus(getBonus(BonusSlot.DEFENCE_SLASH))}",
            "Crush: ${formatBonus(getBonus(BonusSlot.DEFENCE_CRUSH))}",
            "Range: ${formatBonus(getBonus(BonusSlot.DEFENCE_RANGED))}",
            "Magic: ${formatBonus(getBonus(BonusSlot.DEFENCE_MAGIC))}",
            "Melee STR: ${formatBonus(getStrengthBonus())}",
            "Ranged STR: ${formatBonus(getRangedStrengthBonus())}",
            "Magic DMG: ${formatBonus(magicDamageBonus)}%",
            "Prayer: ${formatBonus(getPrayerBonus())}",
            "Undead: TODO",
            "Slayer: TODO",
        )
    }

    private fun formatBonus(bonus: Int): String = if (bonus >= 0) "+$bonus" else bonus.toString()

    private fun formatBonus(bonus: Double): String {
        val formatted = String.format("%.1f", bonus)
        return if (bonus < 0) formatted else "+$formatted"
    }

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
            emptyArray<Item?>(),
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

    private fun Player.createPlaceholder(def: ItemServerType, slot: Int) {
        if (def.placeholderLink <= 0) return
        bank[slot] = Item(def.placeholderLink, -2)
    }
}
