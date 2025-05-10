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

@Service
public class DeleteAlias {

    private final DMC_BENameRepository dmcBeNameRepository;
    private final DMC_JENameRepository dmcJeNameRepository;
    private static final Logger log = LoggerFactory.getLogger(DeleteAlias.class);

    @Autowired
    public DeleteAlias(DMC_BENameRepository dmcBeNameRepository, DMC_JENameRepository dmcJeNameRepository) {
        this.dmcBeNameRepository = dmcBeNameRepository;
        this.dmcJeNameRepository = dmcJeNameRepository;
    }

    public String deleteByName(String bid, String name, int k) {
        if (k == 1) {
            // 使用DMC_BEName检查和删除
            Optional<DMC_BEName> beNameOptional = dmcBeNameRepository.findByBidAndName(bid, name);
            if (beNameOptional.isPresent()) {
                dmcBeNameRepository.delete(beNameOptional.get());
                return "删除成功~";
            } else {
                return "别名不存在，是不是输入错了喵？";
            }
        } else if (k == 2) {
            // 使用DMC_JEName检查和删除
            Optional<DMC_JEName> jeNameOptional = dmcJeNameRepository.findByBidAndName(bid, name);
            if (jeNameOptional.isPresent()) {
                dmcJeNameRepository.delete(jeNameOptional.get());
                return "删除成功~";
            } else {
                return "别名不存在，是不是输入错了喵？";
            }
        } else {
            log.error("MC-DeleteAlias:数据错误！状态码K只能为1或2。");
            return "数据错误！状态码K只能为1或2(；д；)请联系开发者修改对应代码...";
        }
    }
}