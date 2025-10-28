package org.alter.interfaces.bank

import org.alter.game.model.entity.Player

fun Player.openBankInterface() {
    BankOperations.openBank(this)
}
