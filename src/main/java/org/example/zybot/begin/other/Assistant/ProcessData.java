package org.example.zybot.begin.other.Assistant;

import org.springframework.stereotype.Component;

//消息处理逻辑，将接收到的信息进行切割保存
@Component
public class ProcessData {
    public String[] processMessage(String rawMessage) {
        // 使用正则表达式分割字符串，\s+ 表示一个或多个空白字符
        String[] parts = rawMessage.split("\\s+");

        // 创建一个新的数组来保存处理后的结果
        String[] processedParts = new String[parts.length];

        for (int i = 0; i < parts.length; i++) {
            // 检查字符串是否以"/"开头，如果是，则去掉它
            if (parts[i].startsWith("/")) {
                processedParts[i] = parts[i].substring(1);
            } else {
                processedParts[i] = parts[i];
            }
        }
        return processedParts;
    }
}
