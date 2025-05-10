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


//通过别名获取对应服务器ip
@Service
public class GainIPofAlias {

    private final DMC_BENameRepository dmcBeNameRepository;
    private final DMC_JENameRepository dmcJeNameRepository;
    private static final Logger log = LoggerFactory.getLogger(GainIPofAlias.class);

    // 自动注入两个repository
    @Autowired
    public GainIPofAlias(DMC_BENameRepository dmcBeNameRepository, DMC_JENameRepository dmcJeNameRepository) {
        this.dmcBeNameRepository = dmcBeNameRepository;
        this.dmcJeNameRepository = dmcJeNameRepository;
    }

    // k=1 时查询BE服务器，k=2 时查询JE服务器
    public MCPair<String, Integer> madeIP(String bid, String name, int k) {
        if (k == 1) {
            // 从MC_BEName表中查询
            Optional<DMC_BEName> beNameOptional = dmcBeNameRepository.findByBidAndName(bid, name);
            return beNameOptional.map(beName -> new MCPair<>(beName.getIp(), beName.getPost()))
                    .orElseGet(() -> new MCPair<>("", null));
        } else if (k == 2) {
            // 从MC_JEName表中查询
            Optional<DMC_JEName> jeNameOptional = dmcJeNameRepository.findByBidAndName(bid, name);
            return jeNameOptional.map(jeName -> new MCPair<>(jeName.getIp(), jeName.getPost()))
                    .orElseGet(() -> new MCPair<>("", null));
        } else {
            log.error("MC-GainIPofAlias:数据错误！状态码K只能为1或2。");
            return new MCPair<>("", -1);
        }
    }
}
