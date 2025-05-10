package org.example.zybot.begin.Modules.memeHelper.emoji;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmojiKitchenItem {
    private String alt;
    private String emoji;
    private String emojiCodepoint;
    private int gBoardOrder;
    private List<String> keywords;
    private String category;
    private String subcategory;
    private Map<String, List<EmojiKitchenCombination>> combinations;

    // 构造函数、getter 和 setter 方法
    public EmojiKitchenItem() {
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getEmojiCodepoint() {
        return emojiCodepoint;
    }

    public void setEmojiCodepoint(String emojiCodepoint) {
        this.emojiCodepoint = emojiCodepoint;
    }

    public int getGBoardOrder() {
        return gBoardOrder;
    }

    public void setGBoardOrder(int gBoardOrder) {
        this.gBoardOrder = gBoardOrder;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public Map<String, List<EmojiKitchenCombination>> getCombinations() {
        return combinations;
    }

    public void setCombinations(Map<String, List<EmojiKitchenCombination>> combinations) {
        this.combinations = combinations;
    }
}