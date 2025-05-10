package org.example.zybot.begin.Modules.ZYhelp;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//机器人关于群聊/频道的帮助菜单
@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat})//标注为群聊/频道事件
public class helpOfGroup implements EventStrategy {
    private final Container container;
    private final String helpPath="./data/help/helpOfGroup.jpg";
    @Autowired
    public helpOfGroup(Container container) {
        this.container = container;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson=new RPerson();
        //若信息数组[0]不是帮助/help，则返回-1 代表该内容不归该模块处理
        String[] message=person.getMessage();
        if (!message[0].equals("帮助") && !message[0].equals("help")) {
            rPerson.setText("-1");
            return rPerson;
        }

        rPerson.setMode(2);
        rPerson.setImageUrl(helpPath);
        return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("帮助");
        container.addField("help");
    }
}
