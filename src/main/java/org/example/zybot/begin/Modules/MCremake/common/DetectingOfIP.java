package org.example.zybot.begin.Modules.MCremake.common;

import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

//检测输入内容是否是ip地址
//是则返回 不是则pair串
@Service
public class DetectingOfIP {

    // 正则表达式，用于匹配纯 IP 地址或域名
    private static final Pattern IP_REGEX = Pattern.compile("^([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|\\d{1,3}(\\.\\d{1,3}){3})$");
    // 正则表达式，用于匹配带有端口号的 IP 地址或域名
    private static final Pattern IP_WITH_PORT_REGEX = Pattern.compile("^([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|\\d{1,3}(\\.\\d{1,3}){3}):(\\d{1,5})$");

    /**
     * 解析输入的地址字符串，确定 IP 地址和端口号。
     *
     * @param address     服务器地址（可能包含端口号）
     * @return 包含 IP 地址和端口号的 Pair 对象
     */
     public MCPair<String, Integer> parseAddress(String address) {
        int defaultPort=25565;
        // 如果地址包含端口号，分割字符串并返回 IP 地址和端口号
        if (IP_WITH_PORT_REGEX.matcher(address).matches()) {
            String[] parts = address.split(":");
            return new MCPair<>(parts[0], Integer.parseInt(parts[1]));
        } else if (IP_REGEX.matcher(address).matches()) {
            // 如果地址不包含端口号，返回 IP 地址和默认端口号
            return new MCPair<>(address, defaultPort);
        } else {
            // 如果地址格式不正确，返回一个空 IP 地址和 -1 端口号
            return new MCPair<>("", -1);
        }
    }
}
