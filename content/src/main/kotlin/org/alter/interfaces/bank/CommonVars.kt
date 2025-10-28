package org.alter.interfaces.bank

import org.alter.api.ext.getVarbit
import org.alter.api.ext.setVarbit
import org.alter.api.ext.toggleVarbit
import org.alter.game.model.entity.Player
import org.alter.interfaces.bank.configs.BankVarbits

var Player.insertMode: Boolean
    get() = getVarbit(BankVarbits.rearrange_mode) == 1
    set(value) = setVarbit(BankVarbits.rearrange_mode, if (value) 1 else 0)

var Player.withdrawCert: Boolean
    get() = getVarbit(BankVarbits.withdraw_mode) == 1
    set(value) = setVarbit(BankVarbits.withdraw_mode, if (value) 1 else 0)

var Player.alwaysPlacehold: Boolean
    get() = getVarbit(BankVarbits.placeholders) == 1
    set(value) = setVarbit(BankVarbits.placeholders, if (value) 1 else 0)

var Player.lastQtyInput: Int
    get() = getVarbit(BankVarbits.last_quantity_input)
    set(value) = setVarbit(BankVarbits.last_quantity_input, value)

var Player.leftClickQtyMode: QuantityMode
    get() = QuantityMode.fromVarValue(getVarbit(BankVarbits.left_click_quantity))
    set(value) = setVarbit(BankVarbits.left_click_quantity, value.varValue)

var Player.tabDisplayMode: TabDisplayMode
    get() = TabDisplayMode.fromVarValue(getVarbit(BankVarbits.tab_display))
    set(value) = setVarbit(BankVarbits.tab_display, value.varValue)

var Player.incinerator: Boolean
    get() = getVarbit(BankVarbits.incinerator) == 1
    set(value) = setVarbit(BankVarbits.incinerator, if (value) 1 else 0)

var Player.tutorialButton: Boolean
    get() = getVarbit(BankVarbits.tutorial_button) == 1
    set(value) = setVarbit(BankVarbits.tutorial_button, if (value) 1 else 0)

var Player.invItemOptions: Boolean
    get() = getVarbit(BankVarbits.inventory_item_options) == 1
    set(value) = setVarbit(BankVarbits.inventory_item_options, if (value) 1 else 0)

var Player.depositInvButton: Boolean
    get() = getVarbit(BankVarbits.deposit_inventory_button) == 1
    set(value) = setVarbit(BankVarbits.deposit_inventory_button, if (value) 1 else 0)

var Player.depositWornButton: Boolean
    get() = getVarbit(BankVarbits.deposit_worn_items_button) == 1
    set(value) = setVarbit(BankVarbits.deposit_worn_items_button, if (value) 1 else 0)

var Player.bankFillerMode: BankFillerMode
    get() = BankFillerMode.fromVarValue(getVarbit(BankVarbits.bank_filler_quantity))
    set(value) = setVarbit(BankVarbits.bank_filler_quantity, value.varValue)

var Player.disableIfEvents: Boolean
    get() = getVarbit(BankVarbits.disable_ifevents) == 1
    set(value) = setVarbit(BankVarbits.disable_ifevents, if (value) 1 else 0)

fun Player.toggleAlwaysDepositToPotionStore() {
    toggleVarbit(BankVarbits.always_deposit_to_potion_store)
}
