package org.alter.interfaces.bank

/**
 * Enum wrappers for the various bank interface settings stored in varbits.
 */
enum class QuantityMode(val varValue: Int) {
    One(0),
    Five(1),
    Ten(2),
    X(3),
    All(4);

    companion object {
        fun fromVarValue(value: Int): QuantityMode = entries.firstOrNull { it.varValue == value } ?: One
    }
}

enum class TabDisplayMode(val varValue: Int) {
    Obj(0),
    Digit(1),
    Roman(2);

    companion object {
        fun fromVarValue(value: Int): TabDisplayMode = entries.firstOrNull { it.varValue == value } ?: Obj
    }
}

enum class BankFillerMode(val varValue: Int) {
    All(0),
    One(1),
    Ten(2),
    Fifty(3),
    X(4);

    companion object {
        fun fromVarValue(value: Int): BankFillerMode = entries.firstOrNull { it.varValue == value } ?: All
    }
}
