package org.alter.interfaces.bank.scripts

import org.alter.api.ext.message
import org.alter.game.pluginnew.Script
import org.alter.game.pluginnew.event.impl.ButtonClickEvent
import org.alter.interfaces.bank.configs.BankComponents

class BankTutorialEvents : Script() {

    init {
        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.tutorial_button }
            then { player.message("The bank tutorial is not implemented yet.") }
        }
    }
}
