package org.example.zybot.begin.Modules.CrazyThursday.content;


import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.CrazyThursday.DCrazyThursday;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.CrazyThursday.DCrazyThursdayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//疯狂星期四获取文本类
@Service
public class CrazyThursday {

    private final DCrazyThursdayRepository crazyThursdayRepository;

    @Autowired
    public CrazyThursday(DCrazyThursdayRepository crazyThursdayRepository) {
        this.crazyThursdayRepository = crazyThursdayRepository;
    }

    public String getRandomText(int totalTexts) {
        if (totalTexts <= 0) {
            return "疯狂星期四：数值错误，文本id不可能为小于0的数字！";
        }
        // 生成一个1到totalTexts之间的随机数
        int randomId = 1 + (int) (Math.random() * totalTexts);
        // 根据id从数据库中获取CrazyThursday对象
        Optional<DCrazyThursday> optionalCrazyThursday = crazyThursdayRepository.findById(randomId);
        // 如果找到了对象，返回text字段，否则返回null
        return optionalCrazyThursday.map(DCrazyThursday::getText).orElse(null);
    }
}