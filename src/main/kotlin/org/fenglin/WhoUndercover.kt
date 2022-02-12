package org.fenglin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.firstIsInstanceOrNull
import net.mamoe.mirai.utils.info
import org.fenglin.command.WUCommand

object WhoUndercover : KotlinPlugin(
    JvmPluginDescription(
        id = "org.fenglin.Undercover",
        version = "1.0",
        name = "谁是卧底"
    ) {
        author("枫叶秋林")
    }
) {
    override fun onEnable() {
        WUCommand.register()
        WUData.reload()
        println(WUData.words[0])
        logger.info { "谁是卧底，数据加载成功！" }

        globalEventChannel().subscribeGroupMessages {
            startsWith("谁是卧底") {
                when {
                    it.contains("创建游戏") -> {
                        UndercoverCore.create(toCommandSender())
                        globalEventChannel().filter { e -> e is GroupMessageEvent && e.group.id == this.group.id }
                            .subscribeGroupMessages {
                                "加入游戏" {
                                    UndercoverCore.join(toCommandSender())
                                }
                                "开始游戏" {
                                    UndercoverCore.start(toCommandSender())
                                }
                                startsWith("投票") Vote@{
                                    val foo = message.firstIsInstanceOrNull<At>()?.let { at -> subject[at.target] } ?: return@Vote
                                    UndercoverCore.vote(toCommandSender(), foo)
                                }
                                "停止游戏" {
                                    UndercoverCore.stop(toCommandSender())
                                }
                                startsWith("描述") { des ->
                                    UndercoverCore.describe(toCommandSender(), des)
                                }
                                "查看描述" {
                                    UndercoverCore.queryDescriptions(toCommandSender())
                                }
                                "查看投票" {
                                    UndercoverCore.queryPoll(toCommandSender())
                                }
                            }
                    }
                    else -> subject.sendMessage("""
                        游戏帮助
                    """.trimIndent())
                }
            }
        }
    }
}