package org.example.zybot.begin.Modules.ZYDaemonicDisc.main.hard;


import org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.TaskSet.destroyofAction.GameDestructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//恶魔轮盘 销毁类
@Service
public class DesDaemonicDisc implements GameDestructor {
    private final DaemonicDisc disc;

    @Autowired
    public DesDaemonicDisc(final DaemonicDisc disc) {
        this.disc = disc;
    }
    @Override
    //销毁逻辑
    public void destroyGame(String Bid, String sid) {
        disc.closeGame(Bid, sid);
    }

    //返回游戏编号
    public int getNum() {
        return 1;
    }
}
