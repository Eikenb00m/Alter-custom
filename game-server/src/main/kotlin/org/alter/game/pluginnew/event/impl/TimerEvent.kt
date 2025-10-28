package org.alter.game.pluginnew.event.impl

import org.alter.game.model.entity.Pawn
import org.alter.game.model.timer.TimerKey
import org.alter.game.pluginnew.event.Event

class TimerEvent(val timer: TimerKey, val pawn: Pawn) : Event

