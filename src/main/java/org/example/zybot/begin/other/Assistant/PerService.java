package org.example.zybot.begin.other.Assistant;

import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Permission.DPermission;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Permission.DPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

//检测对应功能是否启动，如果没有则返回0，有则返回1
@Service
public class PerService {

    private final DPermissionRepository dPermissionRepository;

    @Autowired
    public PerService(DPermissionRepository dPermissionRepository) {
        this.dPermissionRepository = dPermissionRepository;
    }

    public int checkPermission(String bid, String fun) {
        Optional<DPermission> permission = dPermissionRepository.findByKeyAndFun(bid, fun);
        if (permission.isPresent()) {
            return 0; // 存在记录
        } else {
            return 1; // 不存在记录
        }
    }
}
