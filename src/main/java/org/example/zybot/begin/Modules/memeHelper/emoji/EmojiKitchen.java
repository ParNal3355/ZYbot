package org.example.zybot.begin.Modules.memeHelper.emoji;

import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


//实现类 通过输入的两个emoji查找对应的合成emoji
@Service
public class EmojiKitchen {
    private static final String FOLDER_NAME = "./data/memeHelper/emoji/emoji-kitchen-backend-main/app";//指定文件路径
    private final Map<String, EmojiKitchenItem> items;

    public EmojiKitchen(Map<String, EmojiKitchenItem> items) {
        this.items = items;
    }

    public Pair<String, String> cook(String left, String right) {
        String l = unicode(left);
        String r = unicode(right);

            List<EmojiKitchenCombination> combinationsLists = items.get(l) != null ? items.get(l).getCombinations().values().stream().flatMap(List::stream).collect(Collectors.toList()) : null;
            if (combinationsLists == null) {
                combinationsLists = items.get(r) != null ? items.get(r).getCombinations().values().stream().flatMap(List::stream).collect(Collectors.toList()) : null;
            }
            if (combinationsLists == null) {
                return null;
            }

            EmojiKitchenCombination item = combinationsLists.stream()
                    .filter(it -> it.getLeftEmoji().equals(left) && it.getRightEmoji().equals(right))
                    .findFirst()
                    .orElse(null);
            if (item == null) {
                return null;
            }

        return new Pair<>(item.getFilename(), item.getgStaticUrl());
    }

    private String unicode(String emoji) {
        StringBuilder unicodeStringBuilder = new StringBuilder();
        for (int i = 0; i < emoji.length(); ) {
            int codePoint = emoji.codePointAt(i);
            // 将代码点转换为小写十六进制字符串
            String hex = Integer.toHexString(codePoint).toLowerCase();
            // 确保十六进制字符串长度为4，不足前面补0
            while (hex.length() < 4) {
                hex = "0" + hex;
            }
            unicodeStringBuilder.append(hex);
            i += Character.charCount(codePoint);
        }
        return unicodeStringBuilder.toString();
    }


    public static class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> {
        public Pair(K key, V value) {
            super(key, value);
        }
    }
}