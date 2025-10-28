package org.alter.interfaces.bank

import org.alter.api.ext.getVarbit
import org.alter.api.ext.setVarbit
import org.alter.game.model.attr.INTERACTING_ITEM_SLOT
import org.alter.game.model.container.ItemContainer
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
import org.alter.interfaces.bank.configs.BankVarbits

object BankTabs {

    private val TAB_VARBITS = intArrayOf(
        BankVarbits.tab_size1,
        BankVarbits.tab_size2,
        BankVarbits.tab_size3,
        BankVarbits.tab_size4,
        BankVarbits.tab_size5,
        BankVarbits.tab_size6,
        BankVarbits.tab_size7,
        BankVarbits.tab_size8,
        BankVarbits.tab_size9,
    )

    fun dropToTab(player: Player, dstTab: Int) {
        val container = player.bank
        val srcSlot = player.attr[INTERACTING_ITEM_SLOT] ?: return
        val curTab = getCurrentTab(player, srcSlot)
        if (dstTab == curTab) {
            return
        }

        if (dstTab == 0) {
            container.insert(srcSlot, container.nextFreeSlot - 1)
            adjustTabSize(player, curTab, -1)
            return
        }

        val insertionPoint = insertionPoint(player, dstTab)
        if (dstTab < curTab || curTab == 0) {
            container.insert(srcSlot, insertionPoint)
        } else {
            container.insert(srcSlot, insertionPoint - 1)
        }
        adjustTabSize(player, dstTab, +1)
        if (curTab != 0) {
            adjustTabSize(player, curTab, -1)
            if (getTabSize(player, curTab) == 0 && curTab <= numTabsUnlocked(player)) {
                shiftTabs(player, curTab)
            }
        }
    }

    fun getCurrentTab(player: Player, slot: Int): Int {
        var cumulative = 0
        TAB_VARBITS.forEachIndexed { index, varbit ->
            cumulative += player.getVarbit(varbit)
            if (slot < cumulative) {
                return index + 1
            }
        }
        return 0
    }

    fun numTabsUnlocked(player: Player): Int = TAB_VARBITS.count { player.getVarbit(it) > 0 }

    fun insertionPoint(player: Player, tabIndex: Int): Int {
        if (tabIndex == 0) {
            return player.bank.nextFreeSlot
        }
        var total = 0
        repeat(tabIndex) { idx ->
            total += player.getVarbit(TAB_VARBITS[idx])
        }
        while (total > 0 && player.bank[total - 1] == null) {
            total--
        }
        return total
    }

    fun shiftTabs(player: Player, fromTab: Int) {
        val tabsUnlocked = numTabsUnlocked(player)
        if (fromTab >= tabsUnlocked) {
            setTabSize(player, fromTab, 0)
            return
        }
        for (tab in fromTab until TAB_VARBITS.size) {
            val next = if (tab + 1 < TAB_VARBITS.size) getTabSize(player, tab + 1) else 0
            setTabSize(player, tab, next)
        }
    }

    fun getTabsItems(player: Player, tab: Int): List<Item?> {
        if (tab == 0) {
            return player.bank.rawItems.toList()
        }
        val start = insertionPoint(player, tab - 1)
        val size = getTabSize(player, tab)
        return (start until start + size).map { index ->
            if (index in 0 until player.bank.capacity) player.bank[index] else null
        }
    }

    private fun getTabSize(player: Player, tabIndex: Int): Int {
        if (tabIndex == 0) return player.bank.capacity
        return player.getVarbit(TAB_VARBITS.getOrNull(tabIndex - 1) ?: return 0)
    }

    private fun adjustTabSize(player: Player, tab: Int, delta: Int) {
        if (tab <= 0) return
        val index = tab - 1
        val varbit = TAB_VARBITS.getOrNull(index) ?: return
        val current = player.getVarbit(varbit)
        player.setVarbit(varbit, (current + delta).coerceAtLeast(0))
    }

    private fun setTabSize(player: Player, tab: Int, size: Int) {
        if (tab <= 0) return
        val varbit = TAB_VARBITS.getOrNull(tab - 1) ?: return
        player.setVarbit(varbit, size.coerceAtLeast(0))
    }
}

private fun ItemContainer.insert(from: Int, to: Int) {
    val fromItem = this[from] ?: return
    this[from] = null

    if (from < to) {
        for (index in from until to) {
            this[index] = this[index + 1]
        }
    } else {
        for (index in from downTo to + 1) {
            this[index] = this[index - 1]
        }
    }
    this[to] = fromItem
}
