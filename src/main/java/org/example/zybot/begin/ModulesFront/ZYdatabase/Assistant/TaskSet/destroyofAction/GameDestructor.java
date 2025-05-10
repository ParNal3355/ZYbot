package org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.TaskSet.destroyofAction;


//销毁类接口 使用Action表的类必须要实现这个接口
public interface GameDestructor {
    //实现销毁对应游戏的内容
    void destroyGame(String Bid, String sid);
    //返回游戏的编号
    int getNum();//
}
