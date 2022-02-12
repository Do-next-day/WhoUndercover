package org.fenglin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object WUData : AutoSavePluginData("words") {
    // 词条库
    val words by value(mutableListOf(
        mapOf("玩家词条1" to "卧底词条1", "玩家词条2" to "卧底词条2")
    ))
}