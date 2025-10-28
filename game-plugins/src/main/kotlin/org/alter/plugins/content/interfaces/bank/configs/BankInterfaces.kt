@file:Suppress("ConstPropertyName")

package org.alter.plugins.content.interfaces.bank.configs

import org.alter.rscm.RSCM.asRSCM

/**
 * Interface and component identifiers used throughout the bank module.
 *
 * Every entry is initialised with a placeholder so the proper values can
 * be filled in later while keeping call-sites intact.
 */
object BankInterfaces {
    val bank_main: Int = "interfaces.bankmain".asRSCM()
    val bank_side: Int = "interfaces.bankside".asRSCM()
    val tutorial_overlay: Int = "interfaces.screenhighlight".asRSCM()
}

object BankComponents {
    val tutorial_button: Int = "interfaces.bankmain:bank_tut".asRSCM()
    val capacity_container: Int = "interfaces.bankmain:capacity_layer".asRSCM()
    val capacity_text: Int = "interfaces.bankmain:capacity".asRSCM()
    val main_inventory: Int = "interfaces.bankmain:items".asRSCM()
    val tabs: Int = "interfaces.bankmain:tabs".asRSCM()
    val incinerator_confirm: Int = "interfaces.bankmain:incinerator_confirm".asRSCM()
    val potionstore_items: Int = "interfaces.bankmain:potionstore_items".asRSCM()
    val worn_off_stab: Int = "interfaces.bankmain:stabatt".asRSCM()
    val worn_off_slash: Int = "interfaces.bankmain:slashatt".asRSCM()
    val worn_off_crush: Int = "interfaces.bankmain:crushatt".asRSCM()
    val worn_off_magic: Int = "interfaces.bankmain:magicatt".asRSCM()
    val worn_off_range: Int = "interfaces.bankmain:rangeatt".asRSCM()
    val worn_speed_base: Int = "interfaces.bankmain:attackspeedbase".asRSCM()
    val worn_speed: Int = "interfaces.bankmain:attackspeedactual".asRSCM()
    val worn_def_stab: Int = "interfaces.bankmain:stabdef".asRSCM()
    val worn_def_slash: Int = "interfaces.bankmain:slashdef".asRSCM()
    val worn_def_crush: Int = "interfaces.bankmain:crushdef".asRSCM()
    val worn_def_range: Int = "interfaces.bankmain:rangedef".asRSCM()
    val worn_def_magic: Int = "interfaces.bankmain:magicdef".asRSCM()
    val worn_melee_str: Int = "interfaces.bankmain:meleestrength".asRSCM()
    val worn_ranged_str: Int = "interfaces.bankmain:rangestrength".asRSCM()
    val worn_magic_dmg: Int = "interfaces.bankmain:magicdamage".asRSCM()
    val worn_prayer: Int = "interfaces.bankmain:prayer".asRSCM()
    val worn_undead: Int = "interfaces.bankmain:typemultiplier".asRSCM()
    val worn_slayer: Int = "interfaces.bankmain:slayermultiplier".asRSCM()
    val tutorial_overlay_target: Int = "interfaces.bankmain:bank_highlight".asRSCM()
    val confirmation_overlay_target: Int = "interfaces.bankmain:popup".asRSCM()
    val tooltip: Int = "interfaces.bankmain:tooltip".asRSCM()

    val rearrange_mode_swap: Int = "interfaces.bankmain:swap".asRSCM()
    val rearrange_mode_insert: Int = "interfaces.bankmain:insert".asRSCM()
    val withdraw_mode_item: Int = "interfaces.bankmain:item".asRSCM()
    val withdraw_mode_note: Int = "interfaces.bankmain:note".asRSCM()
    val always_placehold: Int = "interfaces.bankmain:placeholder".asRSCM()
    val deposit_inventory: Int = "interfaces.bankmain:depositinv".asRSCM()
    val deposit_worn: Int = "interfaces.bankmain:depositworn".asRSCM()
    val quantity_1: Int = "interfaces.bankmain:quantity1".asRSCM()
    val quantity_5: Int = "interfaces.bankmain:quantity5".asRSCM()
    val quantity_10: Int = "interfaces.bankmain:quantity10".asRSCM()
    val quantity_x: Int = "interfaces.bankmain:quantityx".asRSCM()
    val quantity_all: Int = "interfaces.bankmain:quantityall".asRSCM()

    val incinerator_toggle: Int = "interfaces.bankmain:incinerator_toggle".asRSCM()
    val tutorial_button_toggle: Int = "interfaces.bankmain:banktut_toggle".asRSCM()
    val inventory_item_options_toggle: Int = "interfaces.bankmain:sideops_toggle".asRSCM()
    val deposit_inv_toggle: Int = "interfaces.bankmain:depositinv_toggle".asRSCM()
    val deposit_worn_toggle: Int = "interfaces.bankmain:depositworn_toggle".asRSCM()
    val release_placehold: Int = "interfaces.bankmain:release_placeholders".asRSCM()
    val bank_fillers_1: Int = "interfaces.bankmain:bank_filler_1".asRSCM()
    val bank_fillers_10: Int = "interfaces.bankmain:bank_filler_10".asRSCM()
    val bank_fillers_50: Int = "interfaces.bankmain:bank_filler_50".asRSCM()
    val bank_fillers_x: Int = "interfaces.bankmain:bank_filler_x".asRSCM()
    val bank_fillers_all: Int = "interfaces.bankmain:bank_filler_all".asRSCM()
    val bank_fillers_fill: Int = "interfaces.bankmain:bank_filler_confirm".asRSCM()
    val bank_tab_display: Int = "interfaces.bankmain:dropdown_content".asRSCM()

    val side_inventory: Int = "interfaces.bankside:items".asRSCM()
    val worn_inventory: Int = "interfaces.bankside:wornops".asRSCM()
    val lootingbag_inventory: Int = "interfaces.bankside:lootingbag_items".asRSCM()
    val league_inventory: Int = "interfaces.bankside:league_secondinv_items".asRSCM()
    val bankside_highlight: Int = "interfaces.bankside:bankside_highlight".asRSCM()

    val tutorial_close_button: Int = "interfaces.screenhighlight:pausebutton".asRSCM()
    val tutorial_next_page: Int = "interfaces.screenhighlight:continue".asRSCM()
    val tutorial_prev_page: Int = "interfaces.screenhighlight:previous".asRSCM()
}

object BankSubComponents {
    val main_tab: Int = 10
    val other_tabs: IntRange = 11..19

    val tab_extended_slots_offset: IntRange = 19..28
}
