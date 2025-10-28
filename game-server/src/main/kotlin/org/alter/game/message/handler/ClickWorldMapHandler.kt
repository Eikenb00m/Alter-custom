package org.alter.game.message.handler

import net.rsprot.protocol.game.incoming.misc.user.ClickWorldMap
import org.alter.game.message.MessageHandler
import org.alter.game.model.Tile
import org.alter.game.model.entity.Client
import org.alter.game.model.move.moveTo
import org.alter.game.model.priv.Privilege
import org.alter.game.pluginnew.event.impl.ClickWorldMapEvent

/**
 * @author HolyRSPS <dagreenrs@gmail.com>
 */
class ClickWorldMapHandler : MessageHandler<ClickWorldMap> {
    override fun consume(
        client: Client,
        message: ClickWorldMap,
    ) {
        ClickWorldMapEvent(Tile(message.x, message.z), client).post()
    }
}
