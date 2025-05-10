package org.example.zybot.begin.Modules.memeHelper.emoji;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;


//执行分析json数据

//更新表情包数据xxx
@Service
public class EmojiDownloader {

//    private static final String LAST_UPDATE = "2023-10-02T11:19:09.000-07:00";//手动填写最后时间戳
//    private static final String EMOJI_REGEX = "/* 正则表达式 */";//表情包对应正则表达式
//    private static final String ZIP_FILE_NAME = "emoji-kitchen-main.zip";//指定下载后保存的zip文件名称

    private static final String FOLDER_NAME = "./data/memeHelper/emoji/emoji-kitchen-backend-main/app";//指定文件路径
    private  Map<String, EmojiKitchenItem> items; // 动态变量，用于存储解析后的 JSON 数据
    private static final Logger log = LoggerFactory.getLogger(EmojiDownloader.class);

    /*@PostConstruct
    public void load() {
        File jsonFile = new File(FOLDER_NAME, "metadata.json");

        if (jsonFile.exists() && jsonFile.isFile()) {
            ObjectMapper mapper = new ObjectMapper();
            try (InputStream inputStream = new FileInputStream(jsonFile);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                items = mapper.readValue(reader, new com.fasterxml.jackson.core.type.TypeReference<Map<String, EmojiKitchenItem>>() {});
            } catch (IOException e) {
                log.error("读取和解析JSON文件失败。", e);
            }
        } else {
            log.error("JSON文件不存在。");
        }
    }*/

    @PostConstruct
    public void load() {
        File jsonFile = new File(FOLDER_NAME, "metadata.json");

        if (jsonFile.exists() && jsonFile.isFile()) {
            ObjectMapper mapper = new ObjectMapper();
            try (InputStream inputStream = new FileInputStream(jsonFile);
                 InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                // 假设EmojiMetadata类有一个名为items的字段，类型为Map<String, EmojiKitchenItem>
                EmojiMetadata metadata = mapper.readValue(reader, EmojiMetadata.class);
                this.items = metadata.getData();
            } catch (IOException e) {
                log.error("emoji合成：读取和解析JSON文件失败。", e);
            }
        } else {
            log.error("emoji合成：JSON文件不存在。");
        }
    }


    public Map<String, EmojiKitchenItem> getItems() {
        Map<String, EmojiKitchenItem> item=items;
        items=null;
        return item;
    }
}