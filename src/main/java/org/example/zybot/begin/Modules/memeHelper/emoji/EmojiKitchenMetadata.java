package org.example.zybot.begin.Modules.memeHelper.emoji;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class EmojiKitchenMetadata {
    @JsonProperty("data")
    private Map<String, List<EmojiKitchenItem>> data;

    // getter 和 setter 方法
    public Map<String, List<EmojiKitchenItem>> getData() {
        return data;
    }

    public void setData(Map<String, List<EmojiKitchenItem>> data) {
        this.data = data;
    }
}