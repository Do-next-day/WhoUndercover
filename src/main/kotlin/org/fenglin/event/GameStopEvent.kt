package org.fenglin.event

import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.AbstractEvent

data class GameStopEvent(
    val group: Group
) : AbstractEvent()