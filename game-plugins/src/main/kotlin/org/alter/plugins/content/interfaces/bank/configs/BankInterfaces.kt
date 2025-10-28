@file:Suppress("ConstPropertyName")

package org.alter.plugins.content.interfaces.bank.configs

/**
 * Interface and component identifiers used throughout the bank module.
 */
object BankInterfaces {
    const val bank_main: Int = 12
    const val bank_side: Int = 15
    const val tutorial_overlay: Int = 664
}

object BankComponents {
    const val tutorial_button: Int = 786520
    const val capacity_container: Int = 786552
    const val capacity_text: Int = 786466
    const val main_inventory: Int = 786452
    const val tabs: Int = 786461
    const val incinerator_confirm: Int = 786572
    const val potionstore_items: Int = 786507
    const val worn_off_stab: Int = 786571
    const val worn_off_slash: Int = 786478
    const val worn_off_crush: Int = 786539
    const val worn_off_magic: Int = 786432
    const val worn_off_range: Int = 786476
    const val worn_speed_base: Int = 786541
    const val worn_speed: Int = 786559
    const val worn_def_stab: Int = 786481
    const val worn_def_slash: Int = 786451
    const val worn_def_crush: Int = 786526
    const val worn_def_range: Int = 786534
    const val worn_def_magic: Int = 786565
    const val worn_melee_str: Int = 786530
    const val worn_ranged_str: Int = 786454
    const val worn_magic_dmg: Int = 786439
    const val worn_prayer: Int = 786491
    const val worn_undead: Int = 786535
    const val worn_slayer: Int = 786513
    const val wornslot0: Int = 786485
    const val wornslot1: Int = 786487
    const val wornslot2: Int = 786472
    const val wornslot3: Int = 786473
    const val wornslot4: Int = 786475
    const val wornslot5: Int = 786477
    const val wornslot7: Int = 786467
    const val wornslot9: Int = 786470
    const val wornslot10: Int = 786554
    const val wornslot12: Int = 786553
    const val wornslot13: Int = 786551
    const val tutorial_overlay_target: Int = 786490
    const val confirmation_overlay_target: Int = 786568
    const val tooltip: Int = 786433

    const val rearrange_mode_swap: Int = 786521
    const val rearrange_mode_insert: Int = 786544
    const val withdraw_mode_item: Int = 786443
    const val withdraw_mode_note: Int = 786538
    const val always_placehold: Int = 786555
    const val deposit_inventory: Int = 786549
    const val deposit_worn: Int = 786519
    const val quantity_1: Int = 786516
    const val quantity_5: Int = 786527
    const val quantity_10: Int = 786436
    const val quantity_x: Int = 786560
    const val quantity_all: Int = 786493

    const val incinerator_toggle: Int = 786435
    const val tutorial_button_toggle: Int = 786498
    const val inventory_item_options_toggle: Int = 786501
    const val deposit_inv_toggle: Int = 786562
    const val deposit_worn_toggle: Int = 786482
    const val release_placehold: Int = 786529
    const val bank_fillers_1: Int = 786518
    const val bank_fillers_10: Int = 786500
    const val bank_fillers_50: Int = 786437
    const val bank_fillers_x: Int = 786486
    const val bank_fillers_all: Int = 786499
    const val bank_fillers_fill: Int = 786464
    const val bank_tab_display: Int = 786542

    const val side_inventory: Int = 983061
    const val worn_inventory: Int = 983135
    const val lootingbag_inventory: Int = 983130
    const val league_inventory: Int = 983127
    const val bankside_highlight: Int = 983055

    const val tutorial_close_button: Int = 43515926
    const val tutorial_next_page: Int = 43515919
    const val tutorial_prev_page: Int = 43515923
}

object BankSubComponents {
    const val main_tab: Int = 10
    val other_tabs: IntRange = 11..19

    val tab_extended_slots_offset: IntRange = 19..28
}
