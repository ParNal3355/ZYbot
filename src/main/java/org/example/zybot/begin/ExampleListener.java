package org.example.zybot.begin;

import love.forte.simbot.common.collectable.Collectables;
import love.forte.simbot.component.qguild.event.QGAtMessageCreateEvent;
import love.forte.simbot.component.qguild.event.QGC2CMessageCreateEvent;
import love.forte.simbot.component.qguild.event.QGDirectMessageCreateEvent;
import love.forte.simbot.component.qguild.event.QGGroupAtMessageCreateEvent;
import love.forte.simbot.component.qguild.role.QGRole;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.OfflineImage;
import love.forte.simbot.message.Text;
import love.forte.simbot.qguild.api.message.MessageSendApi;
import love.forte.simbot.quantcat.common.annotations.ContentTrim;
import love.forte.simbot.quantcat.common.annotations.Filter;
import love.forte.simbot.quantcat.common.annotations.FilterValue;
import love.forte.simbot.quantcat.common.annotations.Listener;
import love.forte.simbot.resource.Resources;
import org.example.zybot.begin.other.Assistant.PerService;
import org.example.zybot.begin.other.Assistant.ProcessData;
import org.example.zybot.begin.other.factory.EventStrategyFactory;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.Assistant.Container;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static love.forte.simbot.resource.Resources.*;

//监听类
@Component
public class ExampleListener {
    private final EventStrategyFactory strategyFactory;
    private final Container container;
    private final ProcessData processData;
    private final PerService perService;


    @Autowired
    public ExampleListener(ProcessData processData, EventStrategyFactory strategyFactory, Container container, PerService perService) {
        this.processData = processData;
        this.strategyFactory = strategyFactory;
        this.container = container;
        this.perService = perService;
    }

    //QQ群聊事件监视类
    @Listener
    @Filter(value = "{{value,.*}}", targets = @Filter.Targets(atBot = true))
    @ContentTrim
    public CompletableFuture<?> onChannelMessageQq(QGGroupAtMessageCreateEvent event, @FilterValue("value") String value) {
        // 将消息内容交给processMessage，根据处理过后的内容进行回复

        String[] parts = processData.processMessage(value);
        //检测对应指令是否为功能中的内容
        String x = parts[0];
        if (!container.containsField(x))
            return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");

        String uid = event.getAuthorId().toString();//用户id
        String bid = event.getContent().getId().toString();//群聊id
        //获取频道用于的自定义昵称   xxxxxx  群聊无法获得
        //String name=event.getAuthor().getNick();
        String sid = "0";//房间id 群聊没有，定义为0 表示该消息来自群聊

        //检测对应指令在该群聊中是否关闭
        if (perService.checkPermission(bid, x) == 0)
            return event.replyAsync("该指令已关闭，请联系管理员使用 /权限 启用 " + x + "指令开启叭~");

        //将得到的内容编写到Person类中
        Person person = new Person(parts, bid, sid, uid);

        //定义枚举内容
        EventType eventType = EventType.GroupChat;
        //从策略工厂获取关联对象列表
        List<EventStrategy> strategies = strategyFactory.getStrategies(eventType);
        for (EventStrategy strategy : strategies) {//将Person类传入各策略类
            RPerson response = strategy.process(person);
            if (!"-1".equals(response.getText())) {//返回类型不为-1，代表找到了对应的策略类
                switch (response.getMode()) {
                    case 1: {//文本
                        return event.replyAsync(response.getText());
                    }
                    case 2: {//图片
                        var path = Path.of(response.getImageUrl());
                        var resource =Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(offlineImage);
                    }
                    case 3: {//图片+文本
                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(Messages.of(Text.of(response.getText()), offlineImage));
                    }
                    default:
                        return event.replyAsync("错误，无法识别的输出模式O.o？");
                }
            }
        }
        // 如果没有策略处理该事件
        return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");
        //不带返回值的回复方法
        //event.getContent().sendBlocking("1");*/
    }

    //QQ频道监听函数
    @Listener//标记函数为监听函数
    @Filter(value = "{{value,/.*}}", targets = @Filter.Targets(atBot = true))
    @ContentTrim // 当匹配被at时，将'at'这个特殊消息移除后，剩余的文本消息大概率存在前后空格，通过此注解在匹配的时候忽略前后空格
    public CompletableFuture<?> onChannelMessageQQ(QGAtMessageCreateEvent event, @FilterValue("value") String value) {
        // 将要监听的事件类型放在参数里，即代表监听此类型的消息
        // 将 CompletableFuture 作为返回值，simbot会以非阻塞的形式处理它
        // 将消息内容交给processMessage，根据处理过后的内容进行回复
        String[] parts = processData.processMessage(value);

        //检测对应指令是否为功能中的内容
        String x = parts[0];
        if (!container.containsField(x))
            return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");

        //获取用户所在别野的id
        String bid = event.getSource().getId().toString();
        //获取用户的id
        String uid = event.getAuthor().getId().toString();
        //将频道内自定义用户名称
        String name = event.getAuthor().getNick();
        //将用户所在房间的id
        String sid = event.getContent().getId().toString();

        Person person = new Person(parts, bid, sid, uid, name);

        // 获取用户身份组列表
        List<String> identity = new ArrayList<>();
        var rolesCollectable = event.getAuthor().getRoles();
        CompletableFuture<List<QGRole>> rolesFuture = Collectables.toListAsync(rolesCollectable);

        rolesFuture.thenAccept(roles -> {
            for (QGRole role : roles) {
                identity.add(role.getName());
            }
            person.setIdentity(identity);
        }).join(); // 等待异步操作完成

        //检测对应指令在该频道中是否关闭
        if (perService.checkPermission(bid, x) == 0)
            return event.replyAsync("该指令已关闭，请联系管理员使用 /权限 启用 " + x + "指令开启叭~");

        //定义枚举内容
        EventType eventType = EventType.Channel;
        //从策略工厂获取关联对象列表
        List<EventStrategy> strategies = strategyFactory.getStrategies(eventType);
        for (EventStrategy strategy : strategies) {//将Person类传入各策略类
            RPerson response = strategy.process(person);
            if (!"-1".equals(response.getText())) {//返回类型不为-1，代表找到了对应的策略类
                switch (response.getMode()) {
                    case 1: {//文本
                        return event.replyAsync(response.getText());
                    }
                    case 2: {//图片

                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(offlineImage);
                    }
                    case 3: {//图片+文本
                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(Messages.of(Text.of(response.getText()), offlineImage));
                    }
                    default:
                        return event.replyAsync("错误，无法识别的输出模式O.o？");
                }
            }
        }
        // 如果没有策略处理该事件
        return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");

        //不带返回值的回复方法
        //event.getContent().sendBlocking("1");

        //p频道发送带有图片的信息的方法
        // 你想要发送消息的目标 QGTextChanenl
        // 只有文字子频道 QGTextChanenl 才能发送消息
        // 其他类型的 QGChannel 无法发送消息
        /*QGTextChannel channel = event.getContent();

        // 获取到一个 Resource, 此处以 Path 为参考
        var path = Path.of("本地图片/地址/image.png");
        var resource = Resources.valueOf(path);
        var offlineImage = OfflineImage.ofResource(resource);

        channel.sendAsync(offlineImage);
        // 或配合其他消息元素发送，比如文字
        channel.sendAsync(Messages.of(Text.of("你好"), offlineImage));*/
    }

    //QGC2CMessageCreateEvent QQ单聊对应的事件
    //QQ单聊监听函数
    @Listener//标记函数为监听函数
    @Filter
    public CompletableFuture<?> C2CChannelMessageQQ(QGC2CMessageCreateEvent event) {

        //获取消息内容
        String value = event.getMessageContent().getSourceContent();

        //获取单聊id
        String id = event.getMessageContent().getId().toString();

        //分割消息内容
        String[] parts = processData.processMessage(value);

        //检测对应指令是否为功能中的内容
        String x = parts[0];
        if (!container.containsField(x))
            return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");


        Person person = new Person();
        person.setMessage(parts);

        //定义枚举内容
        EventType eventType = EventType.GroupChat_SE;
        //从策略工厂获取关联对象列表
        List<EventStrategy> strategies = strategyFactory.getStrategies(eventType);
        for (EventStrategy strategy : strategies) {//将Person类传入各策略类
            RPerson response = strategy.process(person);
            if (!"-1".equals(response.getText())) {//返回类型不为-1，代表找到了对应的策略类
                switch (response.getMode()) {
                    case 1: {//文本
                        return event.replyAsync(response.getText());
                    }
                    case 2: {//图片
                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(offlineImage);
                    }
                    case 3: {//图片+文本
                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(Messages.of(Text.of(response.getText()), offlineImage));
                    }
                    default:
                        return event.replyAsync("错误，无法识别的输出模式O.o？");
                }
            }
        }

        // 如果没有策略处理该事件
        return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");
    }


    //频道单聊监听函数
    @Listener//标记函数为监听函数
    @Filter
    public CompletableFuture<?> DirectChannelMessageQQ1(QGDirectMessageCreateEvent event) {
        //获取消息内容
        String value = event.getMessageContent().getSourceContent();

        //获取单聊id
        String id = event.getMessageContent().getId().toString();

        //分割消息内容
        String[] parts = processData.processMessage(value);

        //检测对应指令是否为功能中的内容
        String x = parts[0];
        if (!container.containsField(x))
            return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");


        Person person = new Person();
        person.setMessage(parts);

        //定义枚举内容
        EventType eventType = EventType.CChannel_SE;
        //从策略工厂获取关联对象列表
        List<EventStrategy> strategies = strategyFactory.getStrategies(eventType);
        for (EventStrategy strategy : strategies) {//将Person类传入各策略类
            RPerson response = strategy.process(person);
            if (!"-1".equals(response.getText())) {//返回类型不为-1，代表找到了对应的策略类
                switch (response.getMode()) {
                    case 1: {//文本
                        return event.replyAsync(response.getText());
                    }
                    case 2: {//图片
                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(offlineImage);
                    }
                    case 3: {//图片+文本
                        var path = Path.of(response.getImageUrl());
                        var resource = Resources.valueOf(path);
                        var offlineImage = OfflineImage.ofResource(resource);
                        return event.replyAsync(Messages.of(Text.of(response.getText()), offlineImage));
                    }
                    default:
                        return event.replyAsync("错误，无法识别的输出模式O.o？");
                }
            }
        }

        // 如果没有策略处理该事件
        return event.replyAsync("唔...这条指令我不认识呢，要不使用/帮助 查看一下支持的指令叭");
    }
}


