package org.example.zybot.begin.Modules.UsagePer.main.hard;

import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Permission.DPermission;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Permission.DPermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

//向权限表中添加要被禁用的功能
@Service
public class UsagePer {
    private final DPermissionRepository dPermissionRepository;

    @Autowired
    public UsagePer(DPermissionRepository dPermissionRepository) {
        this.dPermissionRepository = dPermissionRepository;
    }

    @Transactional
    public void addPermission(String bid, String fun) {
        DPermission permission = new DPermission();
        permission.setKey(bid);
        permission.setFun(fun);
        dPermissionRepository.save(permission);
    }

    public int deletePermission(String bid, String fun) {
        int rowsAffected = dPermissionRepository.deleteByBidAndFun(bid, fun);
        if (rowsAffected > 0) {//删除成功
            return 1;
        } else {//未找到匹配的记录
            return 0;
        }
    }

    public int queryPermission(String bid, String fun){
        Optional<DPermission> permission = dPermissionRepository.findByKeyAndFun(bid, fun);
        if (permission.isPresent()) {
            return 0; // 存在记录
        } else {
            return 1; // 不存在记录
        }
    }


}
