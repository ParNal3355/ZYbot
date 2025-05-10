package org.example.zybot.begin.Modules.ZYSleepAndMorn.body;

import org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL.DSleepMornStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
//定时任务类 每天0点清除标记
public class CleanSleepMornServer {
    private DSleepMornStatisticsRepository statisticsRepository;

    @Autowired
    public void setStatisticsGroupRepository(DSleepMornStatisticsRepository statisticsRepository) {
        this.statisticsRepository=statisticsRepository;
    }

    /**
     * 每天0点执行的定时任务，清除统计数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanStatistics() {
        // 将SleepMornStatistics表中Fuc1、Fuc2、fuc3列都设置为0
        statisticsRepository.resetFucColumns();
    }
}