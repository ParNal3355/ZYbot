package org.example.zybot.begin.Modules.APIhitokoto;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.APIhitokoto.body.HitokotoService;
import org.example.zybot.begin.Modules.ZYDaemonicDisc.main.hard.DaemonicDisc;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})//标注可以使用该模块的事件
public class ZYAPIhitokoto implements EventStrategy {
    private final Container container;
    private final HitokotoService hitokotoService;

    @Autowired
    public ZYAPIhitokoto(Container container,HitokotoService hitokotoService) {
        this.container=container;
        this.hitokotoService=hitokotoService;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();
        //若信息数组[0]不是随机数，则返回-1 代表该内容不归该模块处理
        String[] message = person.getMessage();
        if (!message[0].equals("一言")) {
            rPerson.setText("-1");
            return rPerson;
        }

        rPerson.setText(hitokotoService.getHitokoto());
        return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("一言");
    }
}
