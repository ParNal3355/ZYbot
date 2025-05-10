package org.example.zybot.begin.Modules.ZYSleepAndMorn;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYSleepAndMorn.body.GoodMorningServer;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//注册为群聊/频道事件
@SupportedEvents({EventType.Channel,EventType.GroupChat})
//早安指令的策略类
public class ZYGoodMorningServer implements EventStrategy {
    private final Container container;
    private final GoodMorningServer goodMorningServer;

    @Autowired
    public ZYGoodMorningServer(Container container, GoodMorningServer goodMorningServer) {
        this.container = container;
        this.goodMorningServer = goodMorningServer;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();
        //若信息数组[0]不是今日天气，则返回-1 代表该内容不归该模块处理
        String[] message = person.getMessage();
        if (!message[0].equals("早安")) {
            rPerson.setText("-1");
            return rPerson;
        }

        rPerson.setText(goodMorningServer.processGoodMorning(person.getBid(),person.getUid()));
        return rPerson;
    }


    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("早安");
    }

}
