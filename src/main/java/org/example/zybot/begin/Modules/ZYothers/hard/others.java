package org.example.zybot.begin.Modules.ZYothers.hard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class others {
    private final ResourceLoader resourceLoader;
    private final String helpFilePath="./data/others/help.txt";//帮助文档
    private final String FeedFilePath="./data/others/FeedbackandSuggestions.txt";//反馈与建议
    private final String ProjectHelpFilePath="./data/others/ProjectHelp.txt";//推荐
    private final String versionFilePath="./data/others/version.txt";//版本信息
    private static final Logger log = LoggerFactory.getLogger(others.class);

    @Autowired
    public others(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String helps(int a) {
        String FilePath;
        switch (a) {
            case 1://帮助
                FilePath = helpFilePath;
                break;
            case 2://反馈与建议
                FilePath = FeedFilePath;
                break;
            case 3://项目帮助
                FilePath = ProjectHelpFilePath;
                break;
            case 4://版本信息
                FilePath = versionFilePath;
                break;
            default: {
                log.error("ZYothers.hard.help.class:switch索引超出,请检查索引内容是否正确。");
                return "错误，switch索引超出";
            }
        }

        StringBuilder content = new StringBuilder();
        try {
            Resource resource = resourceLoader.getResource("file:" + FilePath);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append("\n");
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            return "读取帮助内容时出错啦呜呜呜QAQ\n我也不知道发生了什么...总之，再试几次吧，\n要是没有解决，就请跟我的开发者联系一下啦，谢谢你~";
        }
        return content.toString();
    }
}
