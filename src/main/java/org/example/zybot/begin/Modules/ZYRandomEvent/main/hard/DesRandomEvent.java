package org.example.zybot.begin.Modules.ZYRandomEvent.main.hard;

import org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.TaskSet.destroyofAction.GameDestructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//世间轮回 销毁类
@Service
public class DesRandomEvent implements GameDestructor {
    private final RandomEvent randomEvent;

    @Autowired
    public DesRandomEvent(RandomEvent randomEvent) {
        this.randomEvent = randomEvent;
    }

    @Override
    //关闭游戏逻辑
    public void destroyGame(String Bid, String sid) {
        int []k={0};
        randomEvent.deleteFolder(Bid,sid,k);
    }

    //返回游戏编号
    public int getNum(){
        return 2;
    }
}
