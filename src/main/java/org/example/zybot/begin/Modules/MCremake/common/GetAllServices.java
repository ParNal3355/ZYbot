package org.example.zybot.begin.Modules.MCremake.common;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_BEName.DMC_BEName;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_BEName.DMC_BENameRepository;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_JEName.DMC_JEName;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.MC_JEName.DMC_JENameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetAllServices {

    private final DMC_BENameRepository dmcBeNameRepository;
    private final DMC_JENameRepository dmcJeNameRepository;
    private static final Logger log = LoggerFactory.getLogger(GetAllServices.class);

    @Autowired
    public GetAllServices(DMC_BENameRepository dmcBeNameRepository, DMC_JENameRepository dmcJeNameRepository) {
        this.dmcBeNameRepository = dmcBeNameRepository;
        this.dmcJeNameRepository = dmcJeNameRepository;
    }

    @Transactional(readOnly = true)
    public String getIpsAndPortsByBid(String bid, int k) {
        if (k == 1) {
            return "服务器列表：\n由于QQAPI限制，已将所有“.”换成“/”以保证可以输出。格式：\n地址 别名\n"+processQuery1(dmcBeNameRepository.findByBid(bid)).replace(".", "/");
        } else if (k == 2) {
            return "服务器列表：\n由于QQAPI限制，已将所有“.”换成“/”以保证可以输出。格式：\n地址 别名\n"+processQuery2(dmcJeNameRepository.findByBid(bid)).replace(".", "/");
        } else {
            log.error("MC-GetAllServices:数据错误！状态码K只能为1或2。");
            return "数据错误！状态码K只能为1或2(；д；)请联系开发者修改对应代码...";
        }
    }

    private String processQuery1(List<DMC_BEName> beNames) {
        return beNames.stream()
                .map(beName -> beName.getIp() + ":" + beName.getPost() + " " + beName.getName())
                .collect(Collectors.joining("\n"));
    }

    private String processQuery2(List<DMC_JEName> jeNames) {
        return jeNames.stream()
                .map(jeName -> jeName.getIp() + ":" + jeName.getPost() + " " + jeName.getName())
                .collect(Collectors.joining("\n"));
    }
}