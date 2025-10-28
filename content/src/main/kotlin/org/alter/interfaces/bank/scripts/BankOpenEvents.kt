package org.alter.interfaces.bank.scripts

import org.alter.api.ext.isInterfaceVisible
import org.alter.api.ext.setVarbit
import org.alter.game.pluginnew.MenuOption
import org.alter.game.pluginnew.Script
import org.alter.game.pluginnew.event.impl.ButtonClickEvent
import org.alter.interfaces.bank.BankTabs
import org.alter.interfaces.bank.configs.BankComponents
import org.alter.interfaces.bank.configs.BankInterfaces
import org.alter.interfaces.bank.configs.BankSubComponents
import org.alter.interfaces.bank.configs.BankVarbits

class BankOpenEvents : Script() {

    init {
        on<ButtonClickEvent> {
            where { component.combinedId == BankComponents.tabs }
            then { handleTabClick(this) }
        }
    }

    private fun handleTabClick(event: ButtonClickEvent) {
        val player = event.player
        if (!player.isInterfaceVisible(BankInterfaces.bank_main)) {
            return
        }
        val tabIndex = event.slot - BankSubComponents.main_tab
        if (tabIndex < 0) {
            return
        }
        val menuOption = runCatching { MenuOption.fromId(event.option) }.getOrNull() ?: return
        when (menuOption) {
            MenuOption.OP1 -> {
                if (tabIndex <= BankTabs.numTabsUnlocked(player)) {
                    player.setVarbit(BankVarbits.selected_tab, tabIndex)
                }
            }
            else -> Unit
        }
    }
}
