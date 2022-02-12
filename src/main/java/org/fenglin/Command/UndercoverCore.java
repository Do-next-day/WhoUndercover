package org.fenglin.Command;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.User;
import org.fenglin.GameInfo;
import org.fenglin.WUData;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;


public class UndercoverCore implements Runnable {
    private final List<GameInfo> data = new ArrayList<>();
    Status c1 = Status.CREATE;
    private CommandSender sender_run = null;
    private Map<String, String> world_data;

    //另开线程时事检测游戏进程
    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//          描述阶段检测
            if (c1 == Status.DESCRIBE) {
                AtomicReference<Boolean> isD = new AtomicReference<>(false);
                data.forEach(v -> {
                    if (v.getDescription().equals("空")) {
                        isD.set(true);
                    }
                });
                if (!isD.get()) {
                    c1 = Status.VOTE;
                    sender_run.sendMessage("描述阶段结束，开始投票阶段");

                }
            }
            //          投票阶段检测
            if (c1 == Status.VOTE) {
                AtomicReference<Boolean> isD = new AtomicReference<>(false);
//                v.setIsVoted(false); 完成投票  全部为false即为投票完成 有一个ture跳过！
                data.forEach(v -> {
                    if (v.getIsVoted()) {
                        isD.set(true);
                    }
                });
                if (!isD.get()) {
                    System.out.println("投票完成！开始判决");
//                  寻找最高票
                    int max_v = 0;
                    int max_i = 0;
                    for (int i = 0; i < data.size(); i++) {
                        if (data.get(i).getPoll() > max_v) {
                            max_v = data.get(i).getPoll();
                            max_i = i;
                        }
                    }
//                  判断胜负


                    StringBuilder info = new StringBuilder("----本局战况\n----");
                    info.append("普通玩家胜利");
                    if (data.get(max_i).getIsUndercover()) {

                        for (Map.Entry<String, String> entry : world_data.entrySet()) {
                            info.append("玩家词条：").append(entry.getKey()).append(",卧底词条：").append(entry.getValue()).append("\n");
                        }
//                  卧底信息
                        for (GameInfo datum : data) {
                            if (datum.getIsUndercover()) {
                                info.append("本局卧底：").append(datum.getName());
                            }
                        }
                        sender_run.sendMessage(info.toString());
                        c1 = Status.CREATE;
                        data.clear();
                        return;
                    } else {
//                      删除查看剩余人数
                        data.remove(max_i);
                        if (data.size() > 1) {
                            StringBuilder infoW = new StringBuilder("----本局战况\n----");
                            infoW.append("普通玩家胜利");
                            for (Map.Entry<String, String> entry : world_data.entrySet()) {
                                infoW.append("玩家词条：").append(entry.getKey()).append(",卧底词条：").append(entry.getValue()).append("\n");
                            }
                            for (GameInfo datum : data) {
                                if (datum.getIsUndercover()) {
                                    infoW.append("本局卧底：").append(datum.getName());
                                }
                            }
                            sender_run.sendMessage(infoW.toString());
                            c1 = Status.CREATE;
                            data.clear();
                            return;
                        }
                    }
//                  转化描述状态！
                    sender_run.sendMessage("卧底存在!继续描述.");
                    c1 = Status.DESCRIBE;
                }
            }
        }
    }

    public void create(CommandSender sender) {
        if (c1 == Status.CREATE) {
            GameInfo admin = new GameInfo();
            admin.setName(sender.getName());
            admin.setId(Objects.requireNonNull(sender.getUser()).getId());
            admin.setIsAdmin(true);
            data.add(admin);
            c1 = Status.JOIN;
            sender.sendMessage("创建游戏成功");
        } else {
            sender.sendMessage("游戏已创建");
        }
    }

    public void join(CommandSender sender) {
        if (c1 == Status.JOIN) {
            boolean isBoot = false;
            for (GameInfo datum : data) {
                if (datum.getId() == Objects.requireNonNull(sender.getUser()).getId()) {
                    isBoot = true;
                }
            }
            if (isBoot) {
                sender.sendMessage("已加入到游戏中");
                return;
            }
            GameInfo admin = new GameInfo();
            admin.setName(sender.getName());
            admin.setId(Objects.requireNonNull(sender.getUser()).getId());
            admin.setIsAdmin(false);
            data.add(admin);
            sender.sendMessage("加入游戏成功！当前人数：(" + data.size() + ">=3)");
        } else {
            sender.sendMessage("未在加入游戏状态。");
        }

    }

    public void start(CommandSender sender) {
        if (!(c1 == Status.JOIN)) {
            sender.sendMessage("当前状态无法开始游戏");
        } else {
            if (data.size() >= 3) {
//          随机词条
                List<Map<String, String>> words = WUData.INSTANCE.getWords();
                if (words.isEmpty()) {
                    sender.sendMessage("无配置词库，无法开始");
                }
                Random random = new Random();
                world_data = words.get(random.nextInt(words.size()));
//          分配词条
                for (Map.Entry<String, String> entry : world_data.entrySet()) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    for (GameInfo datum : data) {
                        datum.setWords(entry.getKey());
                    }
                    //随机卧底
                    data.get(random.nextInt(data.size())).setWords(entry.getValue());
                    data.get(random.nextInt(data.size())).setIsUndercover(true);

                }
//            通告
                for (GameInfo datum : data) {
                    Objects.requireNonNull(Objects.requireNonNull(sender.getBot()).getFriend(datum.getId())).sendMessage("本局你的词条为：" + datum.getWords());
                }
//          更改状态
                c1 = Status.DESCRIBE;
                sender.sendMessage("开始成功！你们将收到词条！");
//          开启游戏检测线程
                sender_run = sender;
                new Thread(this).start();
            } else {
                sender.sendMessage("人数未满足");
            }
        }

    }

    public void stop(CommandSender sender) {

        AtomicReference<Boolean> isf = new AtomicReference<>(false);
        data.forEach(v -> {
            if (Objects.requireNonNull(sender.getUser()).getId() == v.getId() && v.getIsAdmin()) {

                isf.set(true);
            }
        });
        if (isf.get()) {
            sender.sendMessage("无管理权限，无法重置");
        } else {
            c1 = Status.CREATE;
            data.clear();
            sender.sendMessage("已重置游戏！");
        }
    }

    public void describe(CommandSender sender, String message) {
        if (c1 == Status.DESCRIBE) {
            AtomicReference<Boolean> isD = new AtomicReference<>(false);
            data.forEach(v -> {
                if (Objects.requireNonNull(sender.getUser()).getId() == v.getId() && v.getDescription().equals("空")) {
                    v.setDescription(message);
                    sender.sendMessage("描述成功你的本轮描述词为：" + message);
                    isD.set(true);
                }
            });
            if (isD.get()) {
                return;
            }
            sender.sendMessage("你已经描述过了或者未加入本局游戏");
        } else {
            sender.sendMessage("当前状态无法描述");
        }
    }

    public void vote(CommandSender sender, User user) {
        if (c1 == Status.VOTE) {
            AtomicReference<Boolean> vInfo = new AtomicReference<>(false);
            data.forEach(v -> {
                if (Objects.requireNonNull(sender.getUser()).getId() == v.getId() && v.getIsVoted()) {
                    v.setIsVoted(false);
                    data.forEach(v1 -> {
                        if (v1.getId() == user.getId()) {
                            v1.setPoll(v1.getPoll() + 1);
                            vInfo.set(true);
                        }
                    });
                }
            });
            if (vInfo.get()) {
                sender.sendMessage("投票完成!");
                return;
            }
            sender.sendMessage("错误：无法完成投票(可能是已经投票或者未加入本局游戏)");
        } else {
            sender.sendMessage("当前状态无法进行投票");
        }

    }

    public void queryDescriptions(CommandSender sender) {
        if (c1 == Status.DESCRIBE || c1 == Status.VOTE) {
            AtomicReference<String> a = new AtomicReference<>("");
            data.forEach(v -> a.set(a.get() + v.getName() + "描述是：" + v.getDescription() + "\n"));
            sender.sendMessage(a.get());
        } else {
            sender.sendMessage("当前状态无法查看描述情况");
        }
    }

    public void queryPoll(CommandSender sender) {
        if (c1 == Status.VOTE) {
            AtomicReference<String> a = new AtomicReference<>("");
            data.forEach(v -> a.set(a.get() + v.getName() + "票数是：" + v.getPoll() + "\n"));
            sender.sendMessage(a.get());
        } else {
            sender.sendMessage("当前状态无法查看投票情况");
        }
    }

    enum Status {
        CREATE,//创建游戏时
        JOIN,//游戏加入时
        DESCRIBE,//描述
        VOTE,//投票
    }
}
