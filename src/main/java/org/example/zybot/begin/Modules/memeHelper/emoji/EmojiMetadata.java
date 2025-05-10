package org.example.zybot.begin.Modules.memeHelper.emoji;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class EmojiMetadata {
    @JsonProperty("knownSupportedEmoji")
    private List<String> knownSupportedEmoji;

    @JsonProperty("data")
    private Map<String, EmojiKitchenItem> data;

    public List<String> getKnownSupportedEmoji() {
        return knownSupportedEmoji;
    }
    public void setKnownSupportedEmoji(List<String> knownSupportedEmoji) {
        this.knownSupportedEmoji = knownSupportedEmoji;
    }
    public Map<String, EmojiKitchenItem> getData() {
        return data;
    }
    public void setData(Map<String, EmojiKitchenItem> data) {
        this.data = data;
    }



}