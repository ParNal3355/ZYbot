package org.example.zybot.begin.Modules.MCremake.JE;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.zybot.begin.Modules.MCremake.common.DetectingOfIP;
import org.example.zybot.begin.Modules.MCremake.common.MCPair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
//返回MC-JE服务器的状态信息
public class JEServeInformation {


    private final RestTemplate restTemplate;
    private final DetectingOfIP detectingOfIP;
    private MCPair<String, Integer> mcPair;

    public JEServeInformation(RestTemplate restTemplate, DetectingOfIP detectingOfIP, MCPair<String, Integer> mcPair) {
        this.restTemplate = restTemplate;
        this.detectingOfIP = detectingOfIP;
        this.mcPair = mcPair;
    }


    /**
     * 处理从指定地址和默认端口获取服务器信息的请求。
     *
     * @param address     服务器地址（可能包含端口号）
     */
    public String handle(String address) {
        // 解析地址和端口
        mcPair  =  detectingOfIP.parseAddress(address);
        String ip = mcPair.getFirst();
        int port = mcPair.getSecond();

        // 检查 IP 地址是否有效
        if (ip.isEmpty()) {
            return "域名或者IP格式不对哦";
        }
            // 尝试获取服务器数据并打印结果
            return fetchServerData(ip, port);
    }

    /**
     * 连接到 Minecraft 服务器并获取服务器数据。
     *
     * @param ip   服务器 IP 地址
     * @param port 服务器端口号
     * @return 包含服务器信息的字符串
     */
    public String fetchServerData(String ip, int port) {
        String url = "https://api.mcstatus.io/v2/status/java/" + ip + ":" + port;
        try {
            ResponseEntity<JavaServerStatus> response = restTemplate.getForEntity(url, JavaServerStatus.class);

            if (response.getStatusCode() == HttpStatus.OK) { // 如果响应状态码为200 OK
                JavaServerStatus server = response.getBody();
                if (server != null && server.isOnline()) { // 如果服务器在线

                    StringBuilder builder = new StringBuilder();
                    List<JsonNode> playerList = server.getPlayers().getList();
                    if (!playerList.isEmpty()) {
                        List<JsonNode> first20Players = playerList.stream()
                                .limit(20)
                                .toList();
                        // 检查玩家列表长度是否小于20
                        for (JsonNode playerJson : first20Players) {
                            // 提取name_clean属性
                            String nameClean = playerJson.path("name_clean").asText();
                            // 将name_clean添加到builder中
                            builder.append(nameClean).append(", ");
                        }

                        if (playerList.size() > 20) {
                            // 添加额外的说明
                            builder.append("\n只读取前20名玩家。");
                        }
                    }

                    return "\n成功查询到服务器！" + "\n服务器版本："+server.getVersion().getNameClean()+"\n服务器标语："
                            + server.getMotd().getClean() + "\n当前玩家数量：" + server.getPlayers().getOnline() +
                            "\n最大玩家数：" + server.getPlayers().getMax() + "\n玩家列表：" + builder + "\n状态延迟较高，实际情况可能略有出入。";
                } else {
                    return "服务器不在线或服务器类型错误。";
                }
            } else {
                return "错误：查询服务器状态的网站无响应或返回了错误状态码：" + response.getStatusCode();
            }
        } catch (RestClientException e) {
            // 处理RestClientException，可能是网络问题或mcstatus.io服务不可用
            return "错误：网络问题或网址服务不可用。" + e.getMessage();
        }
    }
}