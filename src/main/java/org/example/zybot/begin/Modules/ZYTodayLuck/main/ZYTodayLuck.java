package org.example.zybot.begin.Modules.ZYTodayLuck.main;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYTodayLuck.main.hard.TodayLuck;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.example.zybot.begin.other.message.Person;
import org.springframework.stereotype.Service;

//今日运气
@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})
public class ZYTodayLuck implements EventStrategy {
    private final Container container;
    private final TodayLuck todayLuck;

    public ZYTodayLuck(TodayLuck todayLuck, Container container) {
        this.container = container;
        this.todayLuck = todayLuck;
    }


    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();
        //若信息数组[0]不是今日天气，则返回-1 代表该内容不归该模块处理
        String[] message = person.getMessage();
        if (!message[0].equals("今日运气")) {
            rPerson.setText("-1");
            return rPerson;
        }

        rPerson.setText(todayLuck.goTodayLuck(person.getUid()));
       return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("今日运气");
    }
}
