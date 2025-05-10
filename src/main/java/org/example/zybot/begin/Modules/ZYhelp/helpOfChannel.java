package org.example.zybot.begin.Modules.ZYhelp;

import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.stereotype.Service;

//机器人关于群聊/频道单聊的帮助菜单
@Service
@SupportedEvents({EventType.CChannel_SE,EventType.GroupChat_SE})//标注为QQ/频道单聊事件
public class helpOfChannel implements EventStrategy {
    private final String helpPath="./data/help/helpOfChannel.jpg";

    @Override
    public RPerson process(Person person) {
        RPerson rPerson=new RPerson();
        //若信息数组[0]不是随机数，则返回-1 代表该内容不归该模块处理
        String[] message=person.getMessage();
        if (!message[0].equals("帮助") && !message[0].equals("help")) {
            rPerson.setText("-1");
            return rPerson;
        }

        rPerson.setMode(2);
        rPerson.setImageUrl(helpPath);
        return rPerson;
    }
}
