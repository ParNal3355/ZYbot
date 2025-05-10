package org.example.zybot.begin.Modules.MCremake.BE;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.zybot.begin.Modules.MCremake.common.DetectingOfIP;
import org.example.zybot.begin.Modules.MCremake.common.MCPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;

@Service
public class BEServeInformation {
    private final RestTemplate restTemplate;
    private MCPair<String,Integer> mcPair;
    private final DetectingOfIP detectingOfIP;

    @Autowired
    public BEServeInformation(RestTemplate restTemplate, MCPair<String,Integer> mcpair, DetectingOfIP detectingOfIP) {
        this.restTemplate = restTemplate;
        this.mcPair = mcpair;
        this.detectingOfIP = detectingOfIP;
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

    public String fetchServerData(String ip, int port) {
        String url = "https://api.mcstatus.io/v2/status/bedrock/" + ip + ":" + port;
        try {
            ResponseEntity<BedrockServerStatus> response = restTemplate.getForEntity(url, BedrockServerStatus.class);

            if (response.getStatusCode() == HttpStatus.OK) { // 如果响应状态码为200 OK
                BedrockServerStatus server = response.getBody();
                if (server != null && server.isOnline()) { // 如果服务器在线
                    return "\n成功查询到服务器！\n服务器版本: " + server.getVersion().getName()  + "\n服务器标语: " +
                            server.getMotd().getClean()+ "\n当前玩家数量: " + server.getPlayers().getOnline() +
                            "\n最大玩家数：" + server.getPlayers().getMax()+"\n该指令状态延迟较高，实际情况可能略有出入。";
                } else {
                    return "服务器不在线或服务器类型错误。\n该指令状态延迟较高，实际情况可能略有出入。";
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