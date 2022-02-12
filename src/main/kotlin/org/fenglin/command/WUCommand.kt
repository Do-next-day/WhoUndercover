package org.fenglin.command

import net.mamoe.mirai.console.command.CompositeCommand
import org.fenglin.WhoUndercover

object WUCommand : CompositeCommand(
    WhoUndercover, "wu", "谁是卧底",
    description = "谁是卧底管理指令"
)