package org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.TaskSet.destroyofAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//销毁类的管理器类
@Component
public class GameDestructorManager {
    private static final Logger log = LoggerFactory.getLogger(GameDestructorManager.class);
    private final Map<Integer, GameDestructor> destructors = new HashMap<>();

    public GameDestructorManager(List<GameDestructor> destructors) {
        //将游戏代号与对应销毁类创建映射
        for (GameDestructor destructor : destructors) {
            // 假设您的销毁类都有一个名为 getNum 的方法来返回num值
            int num = destructor.getNum();
            this.destructors.put(num, destructor);
        }
    }

    public void executeDestructor(int num, String id, String room) {

        GameDestructor destructor = destructors.get(num);
        if (destructor != null) {
            destructor.destroyGame(id, room);
        } else {
            log.error("找不到对应功能的销毁类，功能编号：{}", num);
        }
    }
}