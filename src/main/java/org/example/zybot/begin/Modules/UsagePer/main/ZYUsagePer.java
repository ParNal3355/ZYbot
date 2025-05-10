package org.example.zybot.begin.Modules.UsagePer.main;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.UsagePer.main.hard.UsagePer;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

//权限 指令的策略类
@Service
//标记为群聊事件、频道事件使用
@SupportedEvents({EventType.Channel,EventType.GroupChat})
public class ZYUsagePer implements EventStrategy {
    private final UsagePer usagePer;
    private final Container container;
    private final List<String> x= Arrays.asList("频道主", "超级管理员", "分组管理员","子频道/版块管理员");

    @Autowired
    public ZYUsagePer(Container container, UsagePer usagePer) {
        this.usagePer = usagePer;
        this.container = container;
    }


    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();//创建返回内容的类
        String[] message=person.getMessage();
        if (!message[0].equals("权限")) {
            rPerson.setText("-1");
            return rPerson;
        }

        if (!person.getSid().equals("0")){
            if (x.stream().noneMatch(person.getIdentity()::contains)){
                rPerson.setText("该指令仅为管理员可用奥~");
                return rPerson;
            }
        }

        int x;//表明需要的功能 1：启用 2：禁用
        //检测要选择的功能是否为 启用 禁用
        int m=message.length;
        if (m<3) {//参数不足
            rPerson.setText("参数过少......该指令格式为 /权限 A B 其中A为启用or禁用，B为已存在的所有一级指令（除了权限指令） \n例：/权限 禁用 今日运气");
            return rPerson;
        }
        //参数足够，判断内容是否正确
        switch (message[1]) {
            case "启用": {
                x = 1;
                break;
            }
            case "禁用": {
                x = 2;
                break;
            }
            default: {
                rPerson.setText("输入错误，/权限 A B 其中的A为“启用”或“禁用”才可以奥~");
                return rPerson;
            }
        }

        //检测[2]内容是否是当前存在的指令
        if (!container.containsField(message[2])) {//不存在
            rPerson.setText("错误，需要操作的指令不存在，/权限 A B 中B为已存在指令的名字。\n例：/权限 禁用 今日运气");
            return rPerson;
        }

        //检测是否为 权限 指令
        if (message[2].equals("权限")) {
            rPerson.setText("权限 指令只能是启用状态奥~");
            return rPerson;
        }


        if (x==2){//禁用
            if (usagePer.queryPermission(person.getBid(),message[2])==0){
                rPerson.setText("禁用失败，该指令已被禁用了奥~");
                return rPerson;
            }
            usagePer.addPermission(person.getBid(),message[2]);
            rPerson.setText("禁用成功~指令"+message[2]+"在该群/频道已禁用");
            return rPerson;
        }else {//启用
            if (usagePer.deletePermission(person.getBid(),message[2])==1){
                rPerson.setText("启用成功~指令"+message[2]+"在该群/频道已启用");
                return rPerson;
            }else {
                rPerson.setText("错误：该指令并未禁用，不需要启用指令奥~");
                return rPerson;
            }
        }
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("权限");
    }
}
