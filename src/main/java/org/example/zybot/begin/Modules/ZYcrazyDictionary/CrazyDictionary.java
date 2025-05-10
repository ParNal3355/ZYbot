package org.example.zybot.begin.Modules.ZYcrazyDictionary;


import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYcrazyDictionary.body.JsonParser;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
//标记为所有事件可用
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})
//指定该bean依赖于JsonParser类
@DependsOn("jsonParser")
public class CrazyDictionary implements EventStrategy {
    private final Container container;
    private final List<String> Dictionary;//存储解析后的发电语录

    @Autowired
    public CrazyDictionary(Container container, JsonParser jsonParser) {
        this.container=container;
        this.Dictionary=jsonParser.getMessages();
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();
        //若信息数组[0]不是随机数，则返回-1 代表该内容不归该模块处理
        String[] message = person.getMessage();
        if (!message[0].equals("发电")) {
            rPerson.setText("-1");
            return rPerson;
        }

        if (message.length==1){//没有指定发电对象
            rPerson.setText("参数不足...指令/发电 xxx  其中xxx为发电对象奥~");
            return rPerson;
        }

        Random random = new Random();
        int index = random.nextInt(Dictionary.size());
        String m = Dictionary.get(index);
        // 替换所有{0}占位符
        String replacedMessage = m.replace("{0}",message[1]);
        rPerson.setText(replacedMessage);
        return rPerson;

    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("发电");
    }
}
