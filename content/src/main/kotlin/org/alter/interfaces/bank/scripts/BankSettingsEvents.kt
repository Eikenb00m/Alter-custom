package org.alter.interfaces.bank.scripts

import org.alter.api.ext.inputInt
import org.alter.api.ext.message
import org.alter.game.model.entity.Player
import org.alter.game.pluginnew.Script
import org.alter.game.pluginnew.event.impl.ButtonClickEvent
import org.alter.interfaces.bank.BankFillerMode
import org.alter.interfaces.bank.QuantityMode
import org.alter.interfaces.bank.TabDisplayMode
import org.alter.interfaces.bank.alwaysPlacehold
import org.alter.interfaces.bank.bankFillerMode
import org.alter.interfaces.bank.depositInvButton
import org.alter.interfaces.bank.depositWornButton
import org.alter.interfaces.bank.incinerator
import org.alter.interfaces.bank.insertMode
import org.alter.interfaces.bank.invItemOptions
import org.alter.interfaces.bank.lastQtyInput
import org.alter.interfaces.bank.leftClickQtyMode
import org.alter.interfaces.bank.tabDisplayMode
import org.alter.interfaces.bank.tutorialButton
import org.alter.interfaces.bank.withdrawCert
import org.alter.interfaces.bank.configs.BankComponents

class BankSettingsEvents : Script() {

    init {
        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.rearrange_mode_swap }
            then { player.insertMode = false }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.rearrange_mode_insert }
            then { player.insertMode = true }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.withdraw_mode_item }
            then { player.withdrawCert = false }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.withdraw_mode_note }
            then { player.withdrawCert = true }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.always_placehold }
            then { player.alwaysPlacehold = !player.alwaysPlacehold }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.quantity_1 }
            then { player.leftClickQtyMode = QuantityMode.One }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.quantity_5 }
            then { player.leftClickQtyMode = QuantityMode.Five }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.quantity_10 }
            then { player.leftClickQtyMode = QuantityMode.Ten }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.quantity_all }
            then { player.leftClickQtyMode = QuantityMode.All }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.quantity_x }
            then {
                val selectedOption = option
                if (selectedOption == 2) {
                    player.queue {
                        val amount = inputInt(player, "Enter amount:")
                        player.lastQtyInput = amount
                        player.leftClickQtyMode = if (amount == 0) QuantityMode.One else QuantityMode.X
                    }
                } else {
                    player.leftClickQtyMode = if (player.lastQtyInput == 0) QuantityMode.One else QuantityMode.X
                }
            }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_tab_display }
            then { player.tabDisplayMode = eventTabDisplayMode(slot) }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.incinerator_toggle }
            then { player.incinerator = !player.incinerator }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.tutorial_button_toggle }
            then { player.tutorialButton = !player.tutorialButton }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.inventory_item_options_toggle }
            then { player.invItemOptions = !player.invItemOptions }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.deposit_inv_toggle }
            then { player.depositInvButton = !player.depositInvButton }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.deposit_worn_toggle }
            then { player.depositWornButton = !player.depositWornButton }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.release_placehold }
            then { player.releasePlaceholders() }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_fillers_1 }
            then { player.bankFillerMode = BankFillerMode.One }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_fillers_10 }
            then { player.bankFillerMode = BankFillerMode.Ten }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_fillers_50 }
            then { player.bankFillerMode = BankFillerMode.Fifty }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_fillers_x }
            then { player.bankFillerMode = BankFillerMode.X }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_fillers_all }
            then { player.bankFillerMode = BankFillerMode.All }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.bank_fillers_fill }
            then { player.bankFillersNotImplemented() }
        }
    }

    private fun eventTabDisplayMode(slot: Int): TabDisplayMode =
        when (slot) {
            0 -> TabDisplayMode.Obj
            1 -> TabDisplayMode.Digit
            2 -> TabDisplayMode.Roman
            else -> TabDisplayMode.Obj
        }

    private fun Player.releasePlaceholders() {
        var removed = 0
        for (index in 0 until bank.capacity) {
            val item = bank[index] ?: continue
            if (item.amount == -2) {
                bank[index] = null
                removed++
            }
        }
        if (removed == 0) {
            message("You don't have any placeholders to release.")
        } else {
            bank.shift()
        }
    }

    private fun Player.bankFillersNotImplemented() {
        message("Bank filler support is not implemented yet.")
    }
}
