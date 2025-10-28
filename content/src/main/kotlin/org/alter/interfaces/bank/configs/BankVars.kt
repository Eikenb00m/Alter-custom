@file:Suppress("ConstPropertyName")

package org.alter.interfaces.bank.configs

import org.alter.rscm.RSCM.asRSCM

/**
 * Varbit and varp identifiers used by the bank interface.
 *
 * Replace the placeholder values with the correct IDs from the cache.
 */
object BankVarbits {
    val rearrange_mode: Int = "varbits.bank_insertmode".asRSCM()
    val withdraw_mode: Int = "varbits.bank_withdrawnotes".asRSCM()
    val placeholders: Int = "varbits.bank_leaveplaceholders".asRSCM()
    val last_quantity_input: Int = "varbits.bank_requestedquantity".asRSCM()
    val left_click_quantity: Int = "varbits.bank_quantity_type".asRSCM()
    val bank_filler_quantity: Int = "varbits.bank_fillermode".asRSCM()
    val tab_display: Int = "varbits.bank_tab_display".asRSCM()
    val incinerator: Int = "varbits.bank_showincinerator".asRSCM()
    val tutorial_button: Int = "varbits.bank_hidebanktut".asRSCM()
    val inventory_item_options: Int = "varbits.bank_hidesideops".asRSCM()
    val deposit_inventory_button: Int = "varbits.bank_hidedepositinv".asRSCM()
    val deposit_worn_items_button: Int = "varbits.bank_hidedepositworn".asRSCM()
    val always_deposit_to_potion_store: Int = "varbits.bank_depositpotion".asRSCM()
    val tutorial_current_page: Int = "varbits.hnt_hint_step".asRSCM()
    val tutorial_total_pages: Int = "varbits.hnt_hint_max_step".asRSCM()

    val tab_size1: Int = "varbits.bank_tab_1".asRSCM()
    val tab_size2: Int = "varbits.bank_tab_2".asRSCM()
    val tab_size3: Int = "varbits.bank_tab_3".asRSCM()
    val tab_size4: Int = "varbits.bank_tab_4".asRSCM()
    val tab_size5: Int = "varbits.bank_tab_5".asRSCM()
    val tab_size6: Int = "varbits.bank_tab_6".asRSCM()
    val tab_size7: Int = "varbits.bank_tab_7".asRSCM()
    val tab_size8: Int = "varbits.bank_tab_8".asRSCM()
    val tab_size9: Int = "varbits.bank_tab_9".asRSCM()
    val tab_size_main: Int = "varbits.bank_tab_main".asRSCM()

    val selected_tab: Int = "varbits.bank_currenttab".asRSCM()

    val disable_ifevents: Int = "varbits.bank_disable_ifevents".asRSCM()
}

object BankVarps {
    val bank_serverside_vars: Int = "varps.bank_serverside_vars".asRSCM() //TO-DO: Need to build this varp to the cache!
}
