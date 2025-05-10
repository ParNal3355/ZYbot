package org.example.zybot.begin.Modules.APIhitokoto.body;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HitokotoService {
    private static final String HITOKOTO_API_URL = "https://v1.hitokoto.cn/?c=a&b&l=c";

    /**
     * 获取一言正文
     *
     * @return 一言正文
     */
    public String getHitokoto() {
        RestTemplate restTemplate = new RestTemplate();
        // 调用API
        HitokotoResponse response = restTemplate.getForObject(HITOKOTO_API_URL, HitokotoResponse.class);
        return response != null ? response.getHitokoto() : "API超时，无法获取一言";
    }
}
