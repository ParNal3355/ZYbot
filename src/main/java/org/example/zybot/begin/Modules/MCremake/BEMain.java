package org.example.zybot.begin.Modules.MCremake;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.MCremake.BE.BEServeInformation;
import org.example.zybot.begin.Modules.MCremake.common.*;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


//MC-BE服务器指令 策略类
@Service
//标注为所有事件可用
@SupportedEvents({EventType.Channel,EventType.GroupChat,EventType.CChannel_SE,EventType.GroupChat_SE})
public class BEMain implements EventStrategy {
    private final readMCHelp readMCHelp;
    private final Container container;
    private final GetAllServices getAllServices;
    private final GainIPofAlias gainIPofAlias;
    private final BindingServices bindingServices;
    private final DeleteAlias deleteAlias;
    private final BEServeInformation beServeInformation;

    @Autowired
    public BEMain(Container container, GetAllServices getAllServices, GainIPofAlias gainIPofAlias
    , BindingServices bindingServices, DeleteAlias deleteAlias, readMCHelp readMCHelp,BEServeInformation beServeInformation) {
        this.readMCHelp = readMCHelp;
        this.container = container;
        this.getAllServices = getAllServices;
        this.gainIPofAlias = gainIPofAlias;
        this.bindingServices = bindingServices;
        this.deleteAlias = deleteAlias;
        this.beServeInformation = beServeInformation;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();
        //若信息数组[0]不是随机数，则返回-1 代表该内容不归该模块处理
        String[] message = person.getMessage();
        if (!message[0].equals("BE")) {
            rPerson.setText("-1");
            return rPerson;
        }

        if (message.length ==1) {
            rPerson.setText("参数不足，我不知道要做什么奥...是不知道指令吗？可以使用 /BE 帮助 指令查询奥~");
            return rPerson;
        }

        switch (message[1]) {
            case "查看全部" -> {
                rPerson.setText(getAllServices.getIpsAndPortsByBid(person.getBid(), 1));
                return rPerson;
            }
            case "帮助", "help" -> {
                rPerson.setText(readMCHelp.readFile(1));
                return rPerson;
            }
            case "绑定" -> {
                if (message.length < 4) {
                    rPerson.setText("参数不足...可使用 /BE help 指令查看帮助奥~");
                    return rPerson;
                }
                rPerson.setText(bindingServices.bindServer(person.getBid(), message[2], message[3], 1));
                return rPerson;
            }
        }

        //确保参数足够：/BE 指令名 内容
        if (message.length < 3) {//参数不足 只有/BE xxx
            rPerson.setText("参数不足...可使用 /BE help 指令查看帮助奥~");
            return rPerson;
        }

        //检查输入的地址是否符合规范
        Pattern pattern = Pattern.compile("\\.");
        Matcher matcher = pattern.matcher(message[2]);
        boolean found1 = matcher.find();//是：地址 否：别名

        if (message[1].equals("删除")) {
            if (found1){//输入的是地址
                rPerson.setText("删除绑定的服务器只能使用别名删除奥~");
                return rPerson;
            }
            rPerson.setText(deleteAlias.deleteByName(person.getBid(),message[2],1));
            return rPerson;
        }

        //指令的内容，当前只有一个，所以就用if了
        if (message[1].equals("服务器状态")){
            if (found1){//输入的是地址
                rPerson.setText(beServeInformation.handle(message[2]));
                return rPerson;
            }else {//输入的是别名
                MCPair<String,Integer> a= gainIPofAlias.madeIP(person.getBid(),message[2],1);
                if (a.getSecond()==null){
                    rPerson.setText("错误，此群聊/频道中不存在该别名，是输入错误了吗？(´｀;)？");
                }
                if (a.getSecond()==-1){
                    rPerson.setText("数据错误！状态码K只能为1或2(；д；)请联系开发者修改对应代码...");
                    return rPerson;
                }
                String add=a.getFirst()+":"+a.getSecond();
                rPerson.setText(beServeInformation.handle(add));
                return rPerson;
            }
        }else {
            rPerson.setText("该指令我不认识奥，是输入错误了吗？可以使用/BE help 指令查看帮助奥~");
            return rPerson;
        }
    }

    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("BE");
    }
}
