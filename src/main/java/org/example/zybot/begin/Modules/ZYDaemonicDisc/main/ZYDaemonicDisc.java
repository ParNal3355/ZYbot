package org.example.zybot.begin.Modules.ZYDaemonicDisc.main;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYDaemonicDisc.main.hard.DaemonicDisc;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.example.zybot.begin.other.message.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

////恶魔轮盘赌
//启动该模块的函数，需继承EventStrategy接口
@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat})//标注可以使用该模块的事件
public class ZYDaemonicDisc implements EventStrategy {
    private final DaemonicDisc daemonicDisc;
    private final Container container;
    final static String helpFilePath="./data/DaemonicDisc/help.jpg";

    @Autowired
    public ZYDaemonicDisc( DaemonicDisc daemonicDisc,Container container) {
        this.daemonicDisc=daemonicDisc;
        this.container=container;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson=new RPerson();
        //若信息数组[0]不是随机数，则返回-1 代表该内容不归该模块处理
        String[] message=person.getMessage();
        if (!message[0].equals("轮盘")) {
            rPerson.setText("-1");
            return rPerson;
        }

        String a="",b="";
        int m= message.length;//至少为1
        if (m>1) {
            if (m == 2)
                a = message[1];
            else {
                a = message[1];
                b = message[2];
            }
        }

        if(a.equals("帮助") || a.equals("help")){
            rPerson.setMode(2);
            rPerson.setImageUrl(helpFilePath);
        }

        rPerson.setText(daemonicDisc.select(a,b,person.getBid(),person.getSid(),person.getUid()));
        return rPerson;
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("轮盘");
    }
}
