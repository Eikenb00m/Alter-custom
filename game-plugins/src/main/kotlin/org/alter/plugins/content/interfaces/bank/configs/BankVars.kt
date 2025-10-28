@file:Suppress("ConstPropertyName")

package org.alter.plugins.content.interfaces.bank.configs

/**
 * Varbit and varp identifiers used by the bank interface.
 */
object BankVarbits {
    const val rearrange_mode: Int = 3959
    const val withdraw_mode: Int = 3958
    const val placeholders: Int = 3755
    const val last_quantity_input: Int = 3960
    const val left_click_quantity: Int = 6590
    const val bank_filler_quantity: Int = 6281
    const val tab_display: Int = 4170
    const val incinerator: Int = 5102
    const val tutorial_button: Int = 10336
    const val inventory_item_options: Int = 10079
    const val deposit_inventory_button: Int = 8352
    const val deposit_worn_items_button: Int = 5364
    const val always_deposit_to_potion_store: Int = 11437
    const val tutorial_current_page: Int = 10308
    const val tutorial_total_pages: Int = 10309

    const val tab_size1: Int = 4171
    const val tab_size2: Int = 4172
    const val tab_size3: Int = 4173
    const val tab_size4: Int = 4174
    const val tab_size5: Int = 4175
    const val tab_size6: Int = 4176
    const val tab_size7: Int = 4177
    const val tab_size8: Int = 4178
    const val tab_size9: Int = 4179
    const val tab_size_main: Int = 65531

    const val selected_tab: Int = 4150

    const val disable_ifevents: Int = 65530
}

object BankVarps {
    const val bank_serverside_vars: Int = 65532
}
