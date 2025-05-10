package org.example.zybot.begin.Modules.MCremake.common;

import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_BEName.DMC_BEName;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_BEName.DMC_BENameRepository;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_JEName.DMC_JEName;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_JEName.DMC_JENameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//绑定服务器与别名
@Service
public class BindingServices {

    private final DMC_BENameRepository dmcBeNameRepository;
    private final DMC_JENameRepository dmcJeNameRepository;
    private static final Logger log = LoggerFactory.getLogger(BindingServices.class);

    @Autowired
    public BindingServices(DMC_BENameRepository dmcBeNameRepository, DMC_JENameRepository dmcJeNameRepository) {
        this.dmcBeNameRepository = dmcBeNameRepository;
        this.dmcJeNameRepository = dmcJeNameRepository;
    }

    //@Transactional
    public String bindServer(String bid, String add, String name, int k) {

        String[] parts = add.split(":", 2); // 分割字符串，最多分割成两部分
        String ip;
        int post;
        if (parts.length == 2) {
            ip = parts[0];
            try {
                post = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                return "参数错误！端口号不是纯数字(｀⌒´メ)";
            }
        } else {
            ip = parts[0];
            post = 25565;
        }

        //检查别名是否包含小数点
        Pattern pattern = Pattern.compile("\\.");
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {//包含小数点
            return "绑定失败，别名中不可以包含小数点奥~";
        }

        // 向表中存入数据
        if (k == 1) {
            // 检查是否已存在相同的bid、ip、post
            DMC_BEName existingBeNameByServer = dmcBeNameRepository.findByBidAndIpAndPost(bid, ip, post);
            if (existingBeNameByServer != null) {
                return "绑定失败......群聊/频道中已存在对应的服务器\n(；´д｀)ゞ";
            }
            // 检查是否已存在相同的bid、name
            Optional<DMC_BEName> existingBeName = dmcBeNameRepository.findByBidAndName(bid, name);
            if (existingBeName.isPresent()) {
                return "绑定失败......群聊/频道中已存在对应的别名\n(；´д｀)ゞ";
            }
            DMC_BEName beName = new DMC_BEName(bid, ip, post, name);
            dmcBeNameRepository.save(beName);
            return "绑定成功~您可以使用该别名来快捷使用相关BE指令啦\n( • ̀ω•́ )✧";
        } else if (k == 2) {
            // 检查是否已存在相同的bid、ip、post
            DMC_JEName existingJeNameByServer = dmcJeNameRepository.findByBidAndIpAndPost(bid, ip, post);
            if (existingJeNameByServer != null) {
                return "绑定失败......群聊/频道中已存在对应的服务器\n(；´д｀)ゞ";
            }
            // 检查是否已存在相同的bid、name
            Optional<DMC_JEName> existingJeName = dmcJeNameRepository.findByBidAndName(bid, name);
            if (existingJeName.isPresent()) {
                return "绑定失败......群聊/频道中已存在对应的别名\n(；´д｀)ゞ";
            }
            DMC_JEName jeName = new DMC_JEName(bid, ip, post, name);
            dmcJeNameRepository.save(jeName);
            return "绑定成功~您可以使用该别名来快捷使用相关JE指令啦\n( • ̀ω•́ )✧";
        }
        log.error("MC-BindingServices:数据错误！状态码K只能为1或2。");
        return "数据错误！状态码K只能为1或2(；д；)请联系开发者修改对应代码...";
    }
}