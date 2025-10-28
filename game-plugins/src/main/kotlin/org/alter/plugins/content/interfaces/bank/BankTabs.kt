package org.alter.plugins.content.interfaces.bank

import org.alter.api.ext.getVarbit
import org.alter.api.ext.setVarbit
import org.alter.game.model.attr.INTERACTING_ITEM_SLOT
import org.alter.game.model.entity.Player
import org.alter.game.model.item.Item
import org.alter.plugins.content.interfaces.bank.Bank.insert
import org.alter.plugins.content.interfaces.bank.configs.BankComponents
import org.alter.plugins.content.interfaces.bank.configs.BankVarbits

object BankTabs {
    private val Int.child: Int
        get() = this and 0xFFFF

    const val BANK_TABLIST_ID: Int = BankComponents.tabs.child
    const val SELECTED_TAB_VARBIT: Int = BankVarbits.selected_tab

    private val tabSizeVarbits = intArrayOf(
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
            val lastFree = if (container.nextFreeSlot == 0) 0 else container.nextFreeSlot - 1
            container.insert(srcSlot, lastFree)
            if (curTab != 0) {
                decrementTabSize(player, curTab)
            }
            return
        }
        val insertion = insertionPoint(player, dstTab)
        val insertIndex = if (dstTab < curTab || curTab == 0) insertion else insertion - 1
        container.insert(srcSlot, insertIndex)
        incrementTabSize(player, dstTab)
        if (curTab != 0) {
            decrementTabSize(player, curTab)
        }
    }

    fun dropToTab(player: Player, dstTab: Int, srcSlot: Int, hasEmptySlot: Boolean) {
        val container = player.bank
        val curTab = getCurrentTab(player, srcSlot)
        if (dstTab == curTab) {
            return
        }
        if (dstTab == 0) {
            val lastFree = if (container.nextFreeSlot == 0) 0 else container.nextFreeSlot - 1
            container.insert(srcSlot, lastFree)
            if (curTab != 0) {
                decrementTabSize(player, curTab)
            }
            return
        }
        val (insertion, replacesNull) = newInsertionPoint(player, dstTab)
        val insertIndex = if (dstTab < curTab || curTab == 0) insertion else insertion - 1
        container.insert(srcSlot, insertIndex)
        if (!hasEmptySlot && !replacesNull) {
            incrementTabSize(player, dstTab)
        }
        if (curTab != 0) {
            decrementTabSize(player, curTab)
        }
    }

    fun getCurrentTab(player: Player, slot: Int): Int {
        var current = 0
        for (tab in 1..tabSizeVarbits.size) {
            current += player.getVarbit(tabSizeVarbit(tab))
            if (slot < current) {
                return tab
            }
        }
        return 0
    }

    fun numTabsUnlocked(player: Player): Int {
        var unlocked = 0
        for (tab in 1..tabSizeVarbits.size) {
            if (player.getVarbit(tabSizeVarbit(tab)) > 0) {
                unlocked++
            }
        }
        return unlocked
    }

    fun insertionPoint(player: Player, tabIndex: Int = 0): Int {
        if (tabIndex == 0) {
            return player.bank.nextFreeSlot
        }
        var prevDex = 0
        var dex = 0
        for (tab in 1..tabIndex) {
            prevDex = dex
            dex += player.getVarbit(tabSizeVarbit(tab))
        }
        while (dex != 0 && player.bank[dex - 1] == null && dex > prevDex) {
            dex--
        }
        return dex
    }

    fun newInsertionPoint(player: Player, tabIndex: Int = 0): Pair<Int, Boolean> {
        var replacesNull = false
        if (tabIndex == 0) {
            return player.bank.nextFreeSlot to replacesNull
        }
        var prevDex = 0
        var dex = 0
        for (tab in 1..tabIndex) {
            prevDex = dex
            dex += player.getVarbit(tabSizeVarbit(tab))
        }
        while (dex != 0 && player.bank[dex - 1] == null && dex > prevDex) {
            dex--
        }
        if (dex in 0 until player.bank.capacity && player.bank[dex] == null) {
            replacesNull = true
        }
        return dex to replacesNull
    }

    fun startPoint(player: Player, tabIndex: Int = 0): Int {
        var dex = 0
        if (tabIndex == 0) {
            for (tab in 1..tabSizeVarbits.size) {
                dex += player.getVarbit(tabSizeVarbit(tab))
            }
        } else {
            for (tab in 1 until tabIndex) {
                dex += player.getVarbit(tabSizeVarbit(tab))
            }
        }
        return dex
    }

    fun shiftTabs(player: Player, emptyTabIdx: Int) {
        val lastTab = tabSizeVarbits.size
        for (tab in emptyTabIdx..lastTab) {
            val currentVarbit = tabSizeVarbit(tab)
            val nextValue = if (tab < lastTab) {
                player.getVarbit(tabSizeVarbit(tab + 1))
            } else {
                0
            }
            player.setVarbit(currentVarbit, nextValue)
        }
        val selected = player.getVarbit(SELECTED_TAB_VARBIT)
        if (selected == emptyTabIdx) {
            player.setVarbit(SELECTED_TAB_VARBIT, 0)
        } else if (selected > emptyTabIdx) {
            player.setVarbit(SELECTED_TAB_VARBIT, selected - 1)
        }
    }

    fun getTabsItems(player: Player, tab: Int): List<Item?> {
        var remaining = player.bank.toMutableList()
        var tabItems = mutableListOf<Item?>()
        for (currentTab in 1..tab) {
            val size = player.getVarbit(tabSizeVarbit(currentTab))
            if (currentTab == tab) {
                tabItems = remaining.take(size).toMutableList()
            } else {
                remaining = remaining.drop(size).toMutableList()
            }
        }
        return tabItems
    }

    private fun tabSizeVarbit(tabIndex: Int): Int {
        require(tabIndex in 1..tabSizeVarbits.size) { "Invalid bank tab index: $tabIndex" }
        return tabSizeVarbits[tabIndex - 1]
    }

    fun sizeVarbit(tabIndex: Int): Int = tabSizeVarbit(tabIndex)

    fun collapseTab(player: Player, tabIndex: Int): Boolean {
        require(tabIndex in 1..tabSizeVarbits.size) { "Invalid bank tab index: $tabIndex" }
        val sizeVarbitId = sizeVarbit(tabIndex)
        val tabSize = player.getVarbit(sizeVarbitId)
        if (tabSize == 0) {
            return false
        }
        val container = player.bank
        val start = startPoint(player, tabIndex)
        val items = mutableListOf<Item>()
        for (offset in 0 until tabSize) {
            val slot = start + offset
            if (slot >= container.capacity) {
                break
            }
            val item = container[slot] ?: continue
            items.add(item)
            container[slot] = null
        }
        player.setVarbit(sizeVarbitId, 0)
        shiftTabs(player, tabIndex)
        container.shift()
        if (items.isEmpty()) {
            return false
        }
        var insertSlot = container.capacity - container.freeSlotCount
        items.forEach { item ->
            if (insertSlot in 0 until container.capacity) {
                container[insertSlot++] = item
            }
        }
        container.shift()
        if (player.getVarbit(SELECTED_TAB_VARBIT) == tabIndex) {
            player.setVarbit(SELECTED_TAB_VARBIT, 0)
        }
        return true
    }

    fun removePlaceholders(player: Player, tabIndex: Int): Boolean {
        require(tabIndex in 1..tabSizeVarbits.size) { "Invalid bank tab index: $tabIndex" }
        val container = player.bank
        val sizeVarbitId = sizeVarbit(tabIndex)
        val size = player.getVarbit(sizeVarbitId)
        if (size == 0) {
            return false
        }
        val start = startPoint(player, tabIndex)
        var removed = 0
        for (offset in 0 until size) {
            val slot = start + offset
            if (slot >= container.capacity) {
                break
            }
            val item = container[slot] ?: continue
            if (item.amount == -2) {
                container[slot] = null
                removed++
            }
        }
        if (removed == 0) {
            return false
        }
        val newSize = (size - removed).coerceAtLeast(0)
        player.setVarbit(sizeVarbitId, newSize)
        container.shift()
        if (newSize == 0) {
            shiftTabs(player, tabIndex)
            if (player.getVarbit(SELECTED_TAB_VARBIT) == tabIndex) {
                player.setVarbit(SELECTED_TAB_VARBIT, 0)
            }
        }
        return true
    }

    fun removeAllPlaceholders(player: Player): Boolean {
        val container = player.bank
        val removedByTab = IntArray(tabSizeVarbits.size + 1)
        var removedAny = false
        for (slot in 0 until container.capacity) {
            val item = container[slot] ?: continue
            if (item.amount != -2) {
                continue
            }
            val tabIndex = getCurrentTab(player, slot)
            if (tabIndex in 1..tabSizeVarbits.size) {
                removedByTab[tabIndex]++
            }
            container[slot] = null
            removedAny = true
        }
        if (!removedAny) {
            return false
        }
        container.shift()
        for (tab in tabSizeVarbits.size downTo 1) {
            val removed = removedByTab[tab]
            if (removed == 0) {
                continue
            }
            val varbit = sizeVarbit(tab)
            val current = player.getVarbit(varbit)
            val newSize = (current - removed).coerceAtLeast(0)
            player.setVarbit(varbit, newSize)
            if (newSize == 0) {
                shiftTabs(player, tab)
            }
        }
        return true
    }

    private fun incrementTabSize(player: Player, tab: Int) {
        val varbit = tabSizeVarbit(tab)
        player.setVarbit(varbit, player.getVarbit(varbit) + 1)
    }

    private fun decrementTabSize(player: Player, tab: Int) {
        val varbit = tabSizeVarbit(tab)
        val newSize = player.getVarbit(varbit) - 1
        player.setVarbit(varbit, newSize)
        if (newSize <= 0 && tab <= numTabsUnlocked(player)) {
            shiftTabs(player, tab)
        }
    }

}
