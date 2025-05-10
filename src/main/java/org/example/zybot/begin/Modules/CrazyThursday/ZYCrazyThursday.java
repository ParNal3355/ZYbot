package org.example.zybot.begin.Modules.CrazyThursday;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.CrazyThursday.content.CrazyThursday;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//疯狂星期四 指令的策略类
@Service
//标记为所有事件使用
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})
public class ZYCrazyThursday implements EventStrategy {
    private final Container container;
    private final CrazyThursday crazyThursday;

    @Autowired
    public ZYCrazyThursday(Container container, CrazyThursday crazyThursday) {
        this.container = container;
        this.crazyThursday = crazyThursday;
    }


    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();//创建返回内容的类
        String[] message=person.getMessage();
        //如果不是，则返回-1
        if (!message[0].equals("疯狂星期四")) {
            rPerson.setText("-1");
            return rPerson;
        }

        //如果是，则自动调用内容
        int num=390;//拥有的文案总数，查找CrazyThursday表后输入。
        rPerson.setText(crazyThursday.getRandomText(num));

        return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("疯狂星期四");
    }
}

