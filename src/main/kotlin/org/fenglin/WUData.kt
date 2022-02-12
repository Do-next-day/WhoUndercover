package org.fenglin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object WUData : AutoSavePluginData("words") {
    // 词条库
    val words by value(mutableListOf<Map<String, String>>())
}