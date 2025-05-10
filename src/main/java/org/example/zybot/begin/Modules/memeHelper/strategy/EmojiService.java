package org.example.zybot.begin.Modules.memeHelper.strategy;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.Modules.memeHelper.emoji.EmojiDownloader;
import org.example.zybot.begin.Modules.memeHelper.emoji.EmojiKitchen;
import org.example.zybot.begin.other.Assistant.Container;
import org.example.zybot.begin.other.factory.EventType;
import org.example.zybot.begin.other.factory.SupportedEvents;
import org.example.zybot.begin.other.message.Person;
import org.example.zybot.begin.other.message.RPerson;
import org.example.zybot.begin.other.strategy.EventStrategy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

//emoji合成 策略类
@Service
//标记为全部事件可用
@SupportedEvents({EventType.CChannel_SE,EventType.Channel,EventType.GroupChat,EventType.GroupChat_SE})
//指定该bean依赖于emojiDownloader类
@DependsOn("emojiDownloader")
public class EmojiService implements EventStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmojiService.class);
    private final EmojiKitchen emojiKitchen;
    private final Container container;
    String directoryPath = "./data/memeHelper/emoji/temporary";//获取到的图片临时保存地址
    @Autowired
    public EmojiService(EmojiDownloader emojiDownloader, Container container) {
        // 初始化 EmojiKitchen 实例时使用从 EmojiDownloader 获取的 items 数据
        this.emojiKitchen = new EmojiKitchen(emojiDownloader.getItems());
        this.container = container;//向管理器注册自己的关键词
    }

    @Override
    public RPerson process(Person person) {
        RPerson rPerson = new RPerson();//创建返回内容的类
        String[] message=person.getMessage();
        if (!message[0].equals("emoji合成")) {
            rPerson.setText("-1");
            return rPerson;
        }

        //参数过少
        if (message.length<3){
            rPerson.setText("参数过少......该指令需要两个标准emoji奥~\n例：/emoji合成 \uD83D\uDE04 ☕");
            return rPerson;
        }

        //符合逻辑，尝试生成emoji合成的url
        EmojiKitchen.Pair<String, String> emojiUrlPair =emojiKitchen.cook(message[1],message[2]);
        if (emojiUrlPair == null) {
            emojiUrlPair =emojiKitchen.cook(message[2],message[1]);
            if (emojiUrlPair == null) {
                rPerson.setText("当前emoji暂时没有合成内容或输入非标准emoji(；へ：)");
                return rPerson;
            }
        }
            String url=Download(emojiUrlPair.getValue());
            if (url==null) {//获取本地图片url时出错
                rPerson.setText("错误：获取对应图片的本地URL时出错╥﹏╥");
                return rPerson;
            }
            rPerson.setMode(2);//修改模式为输出图片
            rPerson.setImageUrl(url);//添加图片的url
            return rPerson;
    }


    //返回图片的本地URL地址
    @Nullable
    private String Download(@NotNull String imageUrl){
        try {
            // 下载图片到指定目录
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            File outputFile = new File(directoryPath + "/" + fileName);
            downloadImage(imageUrl, outputFile);
            return outputFile.getPath();
        } catch (IOException e) {
           log.error("emoji合成：将网络地址转换为本地地址失败。{}",e.getMessage());
        }
        return null;
    }


    //清除缓存的临时文件
    // 每1小时执行一次
    @Scheduled(fixedRate = 3600000)
    private void clearDirectory() throws IOException {
        File directory= new File(directoryPath);
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    file.delete();
                }
            }
        }
    }

    //通过图片URL网址将内容下载到本地
    private void downloadImage(String imageUrl, File outputFile) throws IOException {
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }



    @PostConstruct
    public void initializeFeature() {
        // 注册启动该内容的字段
        container.addField("emoji合成");
    }
}