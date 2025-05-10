package org.example.zybot.begin.Modules.MCremake.common;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//读取mcHelp.txt文档的内容
@Service
public class readMCHelp {
    private static final Logger log = LoggerFactory.getLogger(readMCHelp.class);
    private static final String FilePath="./data/MC/";
    private static final String File="/help.txt";

    @NotNull
    public String readFile(int k) {//读取存储 事件 文档的内容
        String add;
        if (k==1)
            add=FilePath+"BE"+File;
        else if (k==2)
            add=FilePath+"JE"+File;
        else {
            log.error("MC-GetAllServices:数据错误！状态码K只能为1或2。");
            return "数据错误！状态码K只能为1或2(；д；)请联系开发者修改对应代码...";
        }
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(add))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        } catch (IOException e) {
            log.error("读取MC指令的帮助文档时出错。", e);
            return "获取MC帮助文档时出错啦呜呜呜QAQ\n我也不知道发生了什么...总之，再试几次吧，\n要是没有解决，就请跟我的开发者联系一下啦，谢谢你~";
        }
        return content.toString();
    }
}
