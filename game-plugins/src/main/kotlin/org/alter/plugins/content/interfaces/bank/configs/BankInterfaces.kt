@file:Suppress("ConstPropertyName")

package org.alter.plugins.content.interfaces.bank.configs

/**
 * Interface and component identifiers used throughout the bank module.
 *
 * Every entry is initialised with a placeholder so the proper values can
 * be filled in later while keeping call-sites intact.
 */
object BankInterfaces {
    const val bank_main: Int = -1
    const val bank_side: Int = -1
    const val tutorial_overlay: Int = -1
}

object BankComponents {
    const val tutorial_button: Int = -1
    const val capacity_container: Int = -1
    const val capacity_text: Int = -1
    const val main_inventory: Int = -1
    const val tabs: Int = -1
    const val incinerator_confirm: Int = -1
    const val potionstore_items: Int = -1
    const val worn_off_stab: Int = -1
    const val worn_off_slash: Int = -1
    const val worn_off_crush: Int = -1
    const val worn_off_magic: Int = -1
    const val worn_off_range: Int = -1
    const val worn_speed_base: Int = -1
    const val worn_speed: Int = -1
    const val worn_def_stab: Int = -1
    const val worn_def_slash: Int = -1
    const val worn_def_crush: Int = -1
    const val worn_def_range: Int = -1
    const val worn_def_magic: Int = -1
    const val worn_melee_str: Int = -1
    const val worn_ranged_str: Int = -1
    const val worn_magic_dmg: Int = -1
    const val worn_prayer: Int = -1
    const val worn_undead: Int = -1
    const val worn_slayer: Int = -1
    const val tutorial_overlay_target: Int = -1
    const val confirmation_overlay_target: Int = -1
    const val tooltip: Int = -1

    const val rearrange_mode_swap: Int = -1
    const val rearrange_mode_insert: Int = -1
    const val withdraw_mode_item: Int = -1
    const val withdraw_mode_note: Int = -1
    const val always_placehold: Int = -1
    const val deposit_inventory: Int = -1
    const val deposit_worn: Int = -1
    const val quantity_1: Int = -1
    const val quantity_5: Int = -1
    const val quantity_10: Int = -1
    const val quantity_x: Int = -1
    const val quantity_all: Int = -1

    const val incinerator_toggle: Int = -1
    const val tutorial_button_toggle: Int = -1
    const val inventory_item_options_toggle: Int = -1
    const val deposit_inv_toggle: Int = -1
    const val deposit_worn_toggle: Int = -1
    const val release_placehold: Int = -1
    const val bank_fillers_1: Int = -1
    const val bank_fillers_10: Int = -1
    const val bank_fillers_50: Int = -1
    const val bank_fillers_x: Int = -1
    const val bank_fillers_all: Int = -1
    const val bank_fillers_fill: Int = -1
    const val bank_tab_display: Int = -1

    const val side_inventory: Int = -1
    const val worn_inventory: Int = -1
    const val lootingbag_inventory: Int = -1
    const val league_inventory: Int = -1
    const val bankside_highlight: Int = -1

    const val tutorial_close_button: Int = -1
    const val tutorial_next_page: Int = -1
    const val tutorial_prev_page: Int = -1
}

object BankSubComponents {
    const val main_tab: Int = -1
    val other_tabs: IntRange = -1..-1

    val tab_extended_slots_offset: IntRange = -1..-1
}
