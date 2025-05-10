package org.example.zybot.begin.Modules.ZYrandomnumber.main;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYrandomnumber.main.hard.RandomNumber;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.example.zybot.begin.other.message.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//随机数
@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})
public class ZYrandomnumber implements EventStrategy {
    private final RandomNumber randomNumber;
    private final Container container;

    @Autowired
    public ZYrandomnumber(RandomNumber randomNumber, Container container) {
        this.randomNumber = randomNumber;
        this.container = container;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();

        //若信息数组[0]不是随机数，则返回-1 代表该内容不归该模块处理
        String[] message=person.getMessage();
        if (!message[0].equals("随机数")){
            rPerson.setText("-1");
            return rPerson;
        }

        //错误处理逻辑
        if (message.length<3) {
            rPerson.setText("\"\\\"数据不足...我做不到..（趴）\\\\n举个栗子：/随机数 0 6\\\"\"");
            return rPerson;
        }
        String a=message[1];
        String b=message[2];
        // 检查输入是否为空或格式不正确
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) {
            rPerson.setText("\"数据不足...我做不到..（趴）\\n举个栗子：/随机数 0 6\"");
            return rPerson;
        }
        // 检查是否为整数
        if (!a.matches("-?\\d+") || !b.matches("-?\\d+")) {
            rPerson.setText("/随机数 后面只能是整数啦\n举个栗子：/随机数 0 6");
            return rPerson;
        }
        rPerson.setText(randomNumber.randomNumbers(a,b));
        return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("随机数");
    }
}
