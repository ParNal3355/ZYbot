package org.example.zybot.begin.Modules.ZYrandomnumber.main.hard;


import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class RandomNumber { // 取随机数
    public String randomNumbers(String a, String b) {

        // 将字符串型变量转换为整数
        int numA;
        int numB;
        try {
            numA = Integer.parseInt(a);
            numB = Integer.parseInt(b);
        } catch (NumberFormatException e) {
            return "/随机数 后面只能是整数啦\n举个栗子：/随机数 0 6";
        }

        // 如果数据b小于数据a，则将两个数据进行调换
        if (numA > numB) {
            int temp = numA;
            numA = numB;
            numB = temp;
        }

        // 在a到b之间取随机数（可以取到a、b所代表的值）
        Random random = new Random();
        int randomNumber = numA + random.nextInt(numB - numA + 1);

        return "那我就随机取一个数啦，我看看...\n是..." + randomNumber + "点！";
    }
}