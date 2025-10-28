package org.alter.interfaces.bank.util

val Int.interfaceId: Int
    get() = this ushr 16

val Int.componentId: Int
    get() = this and 0xFFFF
