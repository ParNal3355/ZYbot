package org.example.zybot.begin.Modules.ZYRandomEvent.main;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.ZYRandomEvent.main.hard.RandomEvent;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DAction;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DActionRepository;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.example.zybot.begin.other.message.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//世间轮回
@Service
@SupportedEvents({EventType.Channel,EventType.GroupChat})
public class ZYRandomEvent implements EventStrategy {
    private final RandomEvent randomEvent;
    private final Container container;
    private final DActionRepository dActionRepository;
    final static String helpFilePath="./data/RandomEvent/help.jpg";

    @Autowired
    public ZYRandomEvent(RandomEvent randomEvent,DActionRepository dActionRepository, Container container) {
        this.randomEvent = randomEvent;
        this.dActionRepository = dActionRepository;
        this.container = container;
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();
        //若信息数组[0]不是世间轮回，则返回-1 代表该内容不归该模块处理
        String[] message=person.getMessage();
        if (!message[0].equals("世间轮回")) {
            rPerson.setText("-1");
            return rPerson;
        }

        //赋值逻辑，防止出现读取过界的错误
        int[] k = new int[2];
        int m= message.length;//至少为1
        String[] f=new String[2];
        if (m==1) {
            rPerson.setText("唔—— 指令少东西了奥，是不知道该填写什么吗？\nemmm......可以使用 /世间轮回 帮助 来获取帮助奥");
            return rPerson;
        }
        else {
            if (m == 2) {
                f[0] = message[1];
                //不需要检测逻辑的指令直接通过
                if (f[0].equals("help") || f[0].equals("帮助")){
                    rPerson.setMode(2);
                    rPerson.setImageUrl(helpFilePath);
                    return rPerson;
                }
                if (f[0].equals("查看事件") || f[0].equals("查看惩罚")) {
                    rPerson.setText(randomEvent.RandomEvents(f, person.getBid(), person.getSid(), person.getName(), person.getUid(), k));
                    return rPerson;
                }
            }
            else {
                    f[0] = message[1];
                    f[1] = message[2];
            }
        }

        //检验逻辑
        //检查游戏是否存在
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(person.getBid(),
                person.getSid());
        if (actionOptional.isEmpty()) {
            //游戏还未创建
            k[0]=1;
            //return  "该游戏本房间还没有创建呢，无法使用该指令奥~";
        }
        if (actionOptional.isPresent()) {
            DAction action = actionOptional.get();
            String n = action.getNum().toString();
            if ("2".equals(n)) {
                //游戏已存在
                k[0]=2;
                if (f[0].equals("关闭游戏")||f[0].equals("查看成员")) {
                    rPerson.setText(randomEvent.RandomEvents(f, person.getBid(), person.getSid(), person.getName(), person.getUid(), k));
                    return rPerson;
                }
                //return "本房间的《世间轮回》小游戏已经创建啦，不需要再次创建~";
            }else {
                rPerson.setText("指令不可用...(；д；)\n存在其他小游戏占用了该房间资源");
                return rPerson;
            }
        }

        //检查游戏是否开始
        if (k[0]==2) {
            DAction action = actionOptional.get();
            if (action.getAct() == null) {
                k[1]=1;//还没有开始游戏
                //return "该房间还没有开始游戏呢，请先开始游戏奥";
            }else
                k[1]=2;//以开始游戏
        }

        rPerson.setText(randomEvent.RandomEvents(f,person.getBid(),person.getSid(),person.getName(),person.getUid(),k));
        return rPerson;
    }


    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("世间轮回");
    }
}
