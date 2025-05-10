package org.example.zybot.begin.Modules.ZYothers;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYothers.hard.others;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//其他指令
@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})
public class ZYothers implements EventStrategy {
    private final Container container;
    private final others others;

    @Autowired
    public ZYothers(Container container, others others) {
        this.container = container;
        this.others = others;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson=new RPerson();
        //若信息数组[0]不是对应内容，则返回-1 代表该内容不归该模块处理
        String[] message=person.getMessage();
        switch (message[0]) {
            case "其他" -> {
                rPerson.setText(others.helps(1));
                return rPerson;
            }
            case "反馈与建议" -> {
                rPerson.setText(others.helps(2));
                return rPerson;
            }
            case "项目帮助" -> {
                rPerson.setText(others.helps(3));
                return rPerson;
            }
            case "版本信息" -> {
                rPerson.setText(others.helps(4));
                return rPerson;
            }
        }
        rPerson.setText("-1");
        return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("其他");
        container.addField("反馈与建议");
        container.addField("项目帮助");
        container.addField("版本信息");
    }
}
