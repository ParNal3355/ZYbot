package org.example.zybot.begin.Modules.ZYSleepAndMorn.body;

import org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
//晚安 指令的处理类
public class SleepServer {

    private final DSleepMornRecordsRepository recordsRepository;
    private final DSleepMornStatisticsRepository statisticsRepository;
    private final DSleepMornStatisticsGroupRepository statisticsGroupRepository;

    @Autowired
    public SleepServer(DSleepMornRecordsRepository recordsRepository,DSleepMornStatisticsRepository statisticsRepository,
                       DSleepMornStatisticsGroupRepository statisticsGroupRepository) {
        this.recordsRepository = recordsRepository;
        this.statisticsRepository = statisticsRepository;
        this.statisticsGroupRepository = statisticsGroupRepository;
    }

    /**
     * 处理睡眠记录的业务逻辑
     * @param bid 群聊/频道编号
     * @param uid 人员编号
     * @return 处理结果的字符串
     */
    public String processSleep(String bid, String uid) {

        // 向SleepMornStatistics表中查找有没有Bid所在的行，如果没有则添加该行，并将该行的num设置为1
        DSleepMornStatistics statistics = statisticsRepository.findByUidAndBid(uid,bid);
        if (statistics == null) {
            statistics = new DSleepMornStatistics();
            statistics.setUid(uid);
            statistics.setNum(0);
            statistics.setFuc1(0);
            statistics.setFuc2(0);
            statistics.setFuc3(0);
            statisticsRepository.save(statistics);
        }

        // 获取当前时间并确定睡眠消息
        LocalTime now = LocalTime.now();
        StringBuilder message=new StringBuilder();//创建储存返回语句内容的变量
        message.append(determineSleepMessage(now));//根据当前时间返回对应语句

        // 根据当前时间更新Fuc1或Fuc2
        if (now.isAfter(LocalTime.of(11, 0)) && now.isBefore(LocalTime.of(18, 0))) {//午睡时间
            if (statistics.getFuc2() != 0) {
                return "已经午睡过啦~一般而言，应该没有人会在一天内午睡好几次叭...(=ω=；)";
            }
            statistics.setFuc2(1);//代表今天午睡了
        } else {//非午睡时间
            if (statistics.getFuc1() != 0) {
                return "已经说过晚安啦，早睡奥~ヽ(•ω•。)ノ";
            }
            statistics.setFuc1(1);//代表今天睡觉了
            //updateGroupStatistics(bid, "Snum");//更新组统计信息
        }
        statisticsRepository.save(statistics);

        // 向SleepMornRecords表中添加该行
        DSleepMornRecords record = recordsRepository.findByUid(uid);
        if (record == null) {//没有记录，添加记录
            DSleepMornRecords record1 = new DSleepMornRecords();
            record1.setUid(uid);
            record1.setTime(java.time.LocalDateTime.now());
            record1.setBid(bid);
            recordsRepository.save(record1);
        }else {//有记录，代表之前用过早安指令，修改记录中的内容以便能使用早安指令
            record.setTime(java.time.LocalDateTime.now());
            record.setK(2);
            recordsRepository.save(record);
        }

        if (now.isAfter(LocalTime.of(11, 0)) && now.isBefore(LocalTime.of(18, 0))) {//11-18点
            return message.toString();
        }

        // 如果当前时间不是11-18点，则更新消息与组统计信息
        int Gnum=1;//记录组内今日睡觉人数 减少数据库的读取
        if (!now.isAfter(LocalTime.of(11, 0)) || !now.isBefore(LocalTime.of(18, 0))) {
            DSleepMornStatisticsGroup groupStatistics = statisticsGroupRepository.findByBid(bid);
            if (groupStatistics == null) {
                groupStatistics = new DSleepMornStatisticsGroup();
                groupStatistics.setBid(bid);
                groupStatistics.setSnum(1);
                groupStatistics.setGnum(0);
                statisticsGroupRepository.save(groupStatistics);
            } else {
                Gnum=groupStatistics.getSnum()+1;
                groupStatistics.setSnum(Gnum);
                statisticsGroupRepository.save(groupStatistics);
            }

            //查找statistic列（特殊列，统计所有使用该bot早安与晚安人数）
            int Snum=1;//记录全部人员今日睡觉人数 减少数据库操作
            DSleepMornStatisticsGroup totalStatistics = statisticsGroupRepository.findByBid("statistic");
            if (totalStatistics == null) {
                totalStatistics = new DSleepMornStatisticsGroup();
                totalStatistics.setBid("statistic");
                totalStatistics.setSnum(1);
                totalStatistics.setGnum(0);
                statisticsGroupRepository.save(totalStatistics);
            } else {
                Snum=totalStatistics.getSnum()+1;
                totalStatistics.setSnum(Snum);
                statisticsGroupRepository.save(totalStatistics);
            }

            message.append("\uD83C\uDF19你是本群聊/频道第").append(Gnum).append("位睡眠的，也是今天第").append(Snum).append("位睡眠的奥~");
        }


        return message.toString();
    }

    /**
     * 根据当前时间确定睡眠消息
     * @param now 当前时间
     * @return 睡眠消息
     */
    private String determineSleepMessage(LocalTime now) {
        if (now.isAfter(LocalTime.of(23, 0)) || now.isBefore(LocalTime.of(3, 0))) {//23-3点
            return "唔——已经很晚啦！不要熬夜奥，下次可不要这么晚了奥~\n";
        } else if (now.isAfter(LocalTime.of(3, 0)) && now.isBefore(LocalTime.of(6, 0))) {//3-6点
            return "这个点，太阳都要出来了呀！\n(▼ヘ▼#),下次不要这么晚啦，好吗\n";
        } else if (now.isAfter(LocalTime.of(6, 0)) && now.isBefore(LocalTime.of(11, 0))) {//6-11点
            return "太阳都晒屁股了想起来睡觉啦？\n(〝▼皿▼),下次不要这么晚啦，好吗\n";
        } else if (now.isAfter(LocalTime.of(11, 0)) && now.isBefore(LocalTime.of(14, 0))) {//11-14点
            return "午安~~~\n";
        } else if (now.isAfter(LocalTime.of(14, 0)) && now.isBefore(LocalTime.of(18, 0))) {//14-18点
            return "现在午睡...有点晚了呢，不管啦，总之，午安~";
        } else if (now.isAfter(LocalTime.of(18, 0)) && now.isBefore(LocalTime.of(21, 0))) {//18-21点
            return "晚安——今天睡得很早呢，好梦~ (〃^ω^) \n";
        } else if (now.isAfter(LocalTime.of(21, 0)) && now.isBefore(LocalTime.of(23, 0))) {//21-23点
            return "唔——都这个点了呢，晚安奥~(〃'▽'〃)";
        }
        return "";
    }
}