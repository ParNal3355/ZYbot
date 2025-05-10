package org.example.zybot.begin.Modules.ZYTodayLuck.main.hard;

import org.example.zybot.begin.ModulesFront.ZYdatabase.other.AboutDailyLuck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;

@Service
public class TodayLuck {
    private final AboutDailyLuck aboutDailyLuck;

    @Autowired
    public TodayLuck(AboutDailyLuck aboutDailyLuck) {
        this.aboutDailyLuck = aboutDailyLuck;
    }

    public String goTodayLuck(String id) {
        StringBuilder s = new StringBuilder();
        Random random = new Random();
        int x = random.nextInt(101); // 生成0到100的随机数
        int k = aboutDailyLuck.qData(id);
        // k=0,没有使用过该功能 k=1 使用过该功能

        if (k == 0) {
            int x1 = x, x2=0;
            while (true) {
                if (x >= 90) {
                    s.append("让我看看，这命运的启示~\n是...").append(x).append("点！哇！运气这么好，要不要抽抽卡试试手气？");
                    break;
                }
                if (x >= 80) {
                    s.append("让我看看，这命运的启示~\n是...").append(x).append("点！哇！运气不错哎，走路会不会捡到钱？");
                    break;
                }
                if (x >= 55) {
                    s.append("让我看看，这命运的启示~\n是...").append(x).append("点！唔——小有所成，吃顿好的犒劳自己一下吧，哎嘿");
                    break;
                }
                if (x > 30) {
                    s.append("让我看看，这命运的启示~\n是...").append(x).append("点！唔——今天是平淡的一天呢，一起喝杯茶吗?");
                    break;
                } else {
                    int y = 50 + random.nextInt(51); // 生成50到100的随机数
                    x2 = y;
                    s.append("让我看看，这命运的启示~\n是...").append(x).append("点！唔——今天运气不行吗...没关系，发动魔法~~~让不好的气息消除吧~（时间重置，回溯...）\n");
                    x = y;
                }
            }
            aboutDailyLuck.wData(id, String.valueOf(x1), String.valueOf(x2));
            return s.toString();
        } else {
            String[] n = aboutDailyLuck.cData(id);
            s.append("让我看看，这命运的启示~\n是...\n唔...看样子，今天已经启示过了呢\n我看看...今天的运气是~").append(n[0]);
            if (!Objects.equals(n[1], "0")){
                s.append(" --→ ").append(n[1]);
            }
            return s.toString();
        }
    }
}