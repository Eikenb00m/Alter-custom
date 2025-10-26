package org.alter.game.pluginnew.event.impl

import org.alter.game.model.entity.Player
import org.alter.game.pluginnew.event.PlayerEvent

class FriendListDeleteEvent(
    val name: String,
    player: Player
) : PlayerEvent(player)

