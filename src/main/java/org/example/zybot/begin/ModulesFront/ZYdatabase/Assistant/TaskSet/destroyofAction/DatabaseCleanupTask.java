package org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.TaskSet.destroyofAction;

import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DAction;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

//定时任务，每1h执行一次获取action表中以存在1h的数据
@Component
public class DatabaseCleanupTask {

    private final DActionRepository dactionRepository;
    private final ThreadPoolTaskExecutor dbCleanupExecutor;
    private final GameDestructorManager gameDestructorManager;// 注入线程池

    @Autowired
    public DatabaseCleanupTask(DActionRepository dactionRepository,ThreadPoolTaskExecutor dbCleanupExecutor
    , GameDestructorManager gameDestructorManager) {
        this.dactionRepository = dactionRepository;
        this.dbCleanupExecutor = dbCleanupExecutor;
        this.gameDestructorManager = gameDestructorManager;
    }

    @Scheduled(fixedRate = 3600000) // 每小时执行一次
    public void cleanupOldActions() {
        //将任务放入线性池
        dbCleanupExecutor.execute(() -> {
            // 获得1小时前的时间
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            // 调用DactionRepository获得小于1小时的所有数据集合
            List<DAction> oldActions = dactionRepository.findActionsOlderThanOneHour(oneHourAgo);
            //将获得到的数据交给管理器类处理
            for (DAction daction : oldActions) {
                int num = daction.getNum();
                gameDestructorManager.executeDestructor(num, daction.getId().getId(), daction.getId().getRoom());
            }
        });
    }
}