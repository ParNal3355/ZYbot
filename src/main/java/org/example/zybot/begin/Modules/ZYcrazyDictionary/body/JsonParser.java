package org.example.zybot.begin.Modules.ZYcrazyDictionary.body;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class JsonParser {
    private List<String> messages;
    private static final String jsonPath = "./data/CrazyDictionary/messages.json";
    private static final Logger log = LoggerFactory.getLogger(JsonParser.class);

    public JsonParser() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 确保使用 UTF-8 编码读取文件
            String jsonContent = Files.readString(Paths.get(jsonPath));
            this.messages = mapper.readValue(jsonContent, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            // 捕获JSON处理异常，这可能是由于JSON格式不正确引起的
            log.error("JSON解析错误：{}", e.getMessage(), e);
        } catch (IOException e) {
            log.error("解析JSON文件失败：{}", e.getMessage(), e);
        }
    }

    public List<String> getMessages() {
        return messages;
    }
}