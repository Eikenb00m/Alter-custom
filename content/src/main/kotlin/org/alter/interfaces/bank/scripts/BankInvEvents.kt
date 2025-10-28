package org.alter.interfaces.bank.scripts

import org.alter.api.ext.inputInt
import org.alter.api.ext.isInterfaceVisible
import org.alter.api.ext.message
import org.alter.game.model.entity.Player
import org.alter.game.pluginnew.MenuOption
import org.alter.game.pluginnew.event.impl.ButtonClickEvent
import org.alter.interfaces.bank.BankOperations
import org.alter.interfaces.bank.QuantityMode
import org.alter.interfaces.bank.lastQtyInput
import org.alter.interfaces.bank.leftClickQtyMode
import org.alter.interfaces.bank.configs.BankComponents
import org.alter.interfaces.bank.configs.BankInterfaces
import org.alter.game.pluginnew.Script

class BankInvEvents : Script() {

    init {
        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.main_inventory }
            then { handleWithdraw(this) }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.side_inventory }
            then { handleDeposit(this) }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.deposit_inventory }
            then { player.depositEntireInventory() }
        }

        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.deposit_worn }
            then { player.depositAllWorn() }
        }
    }

    private suspend fun handleWithdraw(event: ButtonClickEvent) {
        val player = event.player
        if (!player.isInterfaceVisible(BankInterfaces.bank_main)) {
            return
        }
        val slot = event.slot
        if (slot !in 0 until player.bank.capacity) {
            return
        }
        val item = player.bank[slot] ?: return
        val menuOption = runCatching { MenuOption.fromId(event.option) }.getOrNull() ?: return

        if (item.amount == -2) {
            player.bank[slot] = null
            player.bank.shift()
            return
        }

        val amount = when (menuOption) {
            MenuOption.OP1 -> player.resolveLeftClickAmount(item.amount)
            MenuOption.OP2 -> 1
            MenuOption.OP3 -> 5
            MenuOption.OP4 -> 10
            MenuOption.OP5 -> player.lastQtyInput.takeIf { it > 0 } ?: 0
            MenuOption.OP6 -> {
                player.queue {
                    val input = inputInt(player, "How many would you like to withdraw?")
                    if (input > 0) {
                        player.lastQtyInput = input
                        BankOperations.withdraw(player, item.id, input, slot, placehold = false)
                    }
                }
                return
            }
            MenuOption.OP7 -> item.amount
            MenuOption.OP8 -> (item.amount - 1).coerceAtLeast(0)
            MenuOption.OP9 -> item.amount
            MenuOption.OP10 -> return
            else -> return
        }

        if (amount <= 0) {
            return
        }

        val placehold = menuOption == MenuOption.OP9
        BankOperations.withdraw(player, item.id, amount, slot, placehold)
    }

    private suspend fun handleDeposit(event: ButtonClickEvent) {
        val player = event.player
        if (!player.isInterfaceVisible(BankInterfaces.bank_main)) {
            return
        }
        val slot = event.slot
        if (slot !in 0 until player.inventory.capacity) {
            return
        }
        val item = player.inventory[slot] ?: return
        val menuOption = runCatching { MenuOption.fromId(event.option) }.getOrNull() ?: return

        if (menuOption == MenuOption.OP10) {
            return
        }

        val amount = when (menuOption) {
            MenuOption.OP1 -> player.resolveLeftClickAmount(item.amount)
            MenuOption.OP2 -> 1
            MenuOption.OP3 -> 5
            MenuOption.OP4 -> 10
            MenuOption.OP5 -> player.lastQtyInput.takeIf { it > 0 } ?: 0
            MenuOption.OP6 -> {
                player.queue {
                    val input = inputInt(player, "How many would you like to bank?")
                    if (input > 0) {
                        player.lastQtyInput = input
                        BankOperations.deposit(player, item.id, input)
                    }
                }
                return
            }
            MenuOption.OP7 -> player.inventory.getItemCount(item.id)
            MenuOption.OP8 -> (player.inventory.getItemCount(item.id) - 1).coerceAtLeast(0)
            else -> return
        }

        if (amount <= 0) {
            return
        }

        BankOperations.deposit(player, item.id, amount)
    }

    private fun Player.resolveLeftClickAmount(max: Int): Int = when (leftClickQtyMode) {
        QuantityMode.One -> 1
        QuantityMode.Five -> 5
        QuantityMode.Ten -> 10
        QuantityMode.All -> max
        QuantityMode.X -> if (lastQtyInput == 0) 1 else lastQtyInput
    }

    private fun Player.depositEntireInventory() {
        for (slot in 0 until inventory.capacity) {
            val item = inventory[slot] ?: continue
            BankOperations.deposit(this, item.id, inventory.getItemCount(item.id))
        }
    }

    private fun Player.depositAllWorn() {
        var depositedAny = false
        for (slot in 0 until equipment.capacity) {
            val item = equipment[slot] ?: continue
            val count = item.amount
            BankOperations.deposit(this, item.id, count)
            if (equipment[slot] == null) {
                depositedAny = true
            }
        }
        if (!depositedAny) {
            message("You have no equipment to deposit.")
        }
    }
}
