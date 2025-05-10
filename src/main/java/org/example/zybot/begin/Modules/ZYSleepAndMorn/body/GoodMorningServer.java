package org.example.zybot.begin.Modules.ZYSleepAndMorn.body;

import org.example.zybot.begin.Modules.ZYSleepAndMorn.SQL.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
//早安 指令的处理类
public class GoodMorningServer {

    private final DSleepMornRecordsRepository recordsRepository;
    private final DSleepMornStatisticsRepository statisticsRepository;
    private final DSleepMornStatisticsGroupRepository statisticsGroupRepository;

    @Autowired
    public GoodMorningServer(DSleepMornRecordsRepository recordsRepository,DSleepMornStatisticsRepository statisticsRepository
            ,DSleepMornStatisticsGroupRepository statisticsGroupRepository) {
        this.recordsRepository = recordsRepository;
        this.statisticsRepository=statisticsRepository;
        this.statisticsGroupRepository=statisticsGroupRepository;
    }

    /**
     * 处理早安逻辑
     * @param bid 群聊/频道编号
     * @param uid 人员编号
     * @return 返回早安信息
     */
    public String processGoodMorning(String bid, String uid) {
        StringBuilder message = new StringBuilder();
        DSleepMornRecords record = recordsRepository.findByUidAndBid(uid,bid);

        // 向SleepMornStatistics表中查找有没有Bid所在的行，如果没有则添加该行，并将该行的num设置为1
        DSleepMornStatistics statistics = statisticsRepository.findByUidAndBid(uid,bid);
        if (statistics == null) {
            statistics = new DSleepMornStatistics();
            statistics.setUid(uid);
            statistics.setBid(bid);
            statistics.setNum(0);
            statistics.setFuc1(0);
            statistics.setFuc2(0);
            statistics.setFuc3(0);
            statisticsRepository.save(statistics);
        }
        //没有睡觉记录
        if (statistics.getFuc1()==0&&statistics.getFuc2()==0) {
            if (statistics.getFuc3()==1){//在该群聊/频道早安过
                return "你今天起过床啦，去其他群聊/频道试试吧~(｡･ω･｡)";
            }
            if (record==null) {//如果没有记录，代表没有使用晚安指令
                //添加该条记录
                DSleepMornRecords r = new DSleepMornRecords();
                r.setUid(uid);
                r.setK(1);//作为标记，代表是正常起床还是直接执行早安指令
                r.setBid(bid);
                recordsRepository.save(r);
            }

            statistics.setFuc3(1);//代表今天使用过早安指令
            statisticsRepository.save(statistics);
            appendMorningGreeting(message);//针对当前时间的早安问候语
            appendGroupStatistics(message, bid);//添加当前频道/群聊早安人数和所有早安人数 添加返回语
            SleepMornStatistics(statistics,uid,message, 0);//更新人员累计天数
        }
        else {//有睡觉记录
            int k=record.getK() != null ? record.getK() : 0;
            if (k==1){//在该群聊/频道早安过
                return "你今天起过床啦，去其他群聊/频道试试吧~(｡･ω･｡)";
            }
            if (statistics.getFuc3()==1){//在该群聊/频道早安过
                return "你今天起过床啦，去其他群聊/频道试试吧~(｡･ω･｡)";
            }

            LocalDateTime sleepTime = record.getTime();
            if (Duration.between(sleepTime, LocalDateTime.now()).toMinutes() < 5) {
                return "这么短的时间...你根本没睡吧(▼ヘ▼#)";
            }

            int f = statistics.getFuc2(); // 获取Fuc2列内容 1：午睡 0：晚睡

            if (f == 0) { // 夜晚睡眠
                appendMorningGreeting(message);//针对当前时间的早安问候语
                appendSleepDuration(message, sleepTime, LocalDateTime.now());//返回睡眠时间
                appendSleepQuality(message, sleepTime, LocalDateTime.now());//返回 非午睡 时的睡眠质量语
                appendGroupStatistics(message, bid);//添加当前频道/群聊早安人数和所有早安人数 添加返回语
                SleepMornStatistics(statistics,uid,message,k);//更新人员累计天数
                statistics.setFuc3(1);//修改早安标记 代表如果还有，则为午安
                record.setK(1);
                recordsRepository.save(record); // 修改标记，表明在该群聊/频道已经说过早安
            } else { // 午睡
                message.append("姆——你醒啦ヾ(ｏ･ω･)ﾉ，");
                appendSleepDuration(message, sleepTime, LocalDateTime.now());
                appendSleepQualityNap(message, sleepTime, LocalDateTime.now());//返回 午睡 时的睡眠质量语
                statistics.setFuc3(0);//修改该标记为0
                recordsRepository.delete(record);//删除记录
            }
            statisticsRepository.save(statistics);
        }
        return message.toString();
    }

    /**
     * 追加早安问候信息
     * @param message 信息构建器
     */
    private void appendMorningGreeting(StringBuilder message) {
        LocalTime now = LocalTime.now();
        if (now.isAfter(LocalTime.of(6, 0)) && now.isBefore(LocalTime.of(8, 0))) {
            message.append("早上好呀~(๑╹◡╹)ﾉ\"\"\"今天起的很早呢\n");
        } else if (now.isAfter(LocalTime.of(8, 0)) && now.isBefore(LocalTime.of(10, 0))) {
            message.append("早上好——耽误太多时间，事情可就做不完了奥~\n");
        } else if (now.isAfter(LocalTime.of(10, 0)) && now.isBefore(LocalTime.of(14, 0))) {
            message.append("哇，都中午了怎么才起床呀，小懒猫\n");
        } else {
            message.append("唔——这也，太晚了叭！\n…（⊙＿⊙；）…\n");
        }
    }

    /**
     * 追加睡眠时间信息
     * @param message 信息构建器
     * @param sleepTime 睡眠时间
     * @param now 当前时间
     */
    private void appendSleepDuration(StringBuilder message, LocalDateTime sleepTime, LocalDateTime now) {
        Duration duration = Duration.between(sleepTime, now);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        message.append(String.format(" 这次睡了 %d时：%d分：%d秒。", hours, minutes, seconds)).append("\n");
    }

    /**
     * 追加睡眠质量信息（非午睡）
     * @param message 信息构建器
     * @param sleepTime 睡眠时间
     * @param now 当前时间
     */
    private void appendSleepQuality(StringBuilder message, LocalDateTime sleepTime, LocalDateTime now) {
        Duration duration = Duration.between(sleepTime, now);
        if (duration.toHours() < 6) {
            message.append(" 这睡眠...不够的吧，中午记得午睡奥，不然一整天都要没精神了\n");
        } else if (duration.toHours() < 7) {
            message.append(" 睡眠质量还好奥~\n");
        } else if (duration.toHours() < 9) {
            message.append(" 睡眠质量很不错呢\n");
        } else if (duration.toHours() < 24) {
            message.append(" 嗯？！太能睡了吧(°Д°)\n");
        } else {
            message.append(" 这个时间...忘记说早安了，对把(つД`)\n");
        }
    }

    /**
     * 追加睡眠质量信息（午睡）
     * @param message 信息构建器
     * @param sleepTime 睡眠时间
     * @param now 当前时间
     */
    private void appendSleepQualityNap(StringBuilder message, LocalDateTime sleepTime, LocalDateTime now) {
        Duration duration = Duration.between(sleepTime, now);
        if (duration.toMinutes() < 30) {//0.5h
            message.append(" 这睡眠时间,或许有点不太够吧。希望接下来的时间不会犯困哦");
        } else if (duration.toMinutes() < 180) {//0.5-3h
            message.append(" 唔——，是充足的睡眠奥(oﾟ▽ﾟ)o  ");
        } else if (duration.toMinutes() < 360) {//3-6h
            message.append(" 唔——午睡睡的也太久了吧，这样的话夜晚还会睡着吗...[・ヘ・?]");
        } else {//大于6h
            message.append(" 唔——，这么久，是不是忘记说午安啦(′へ`、)");
        }
    }

    /**
     * 追加组统计信息
     * @param message 信息构建器
     * @param bid 群聊/频道编号
     */
    private void appendGroupStatistics(StringBuilder message, String bid) {
        int s=1;//今日群聊/频道内所有早安人员数量
        int b=1;//今日所有早安人员数量

        //获取并更新群聊/频道内今日早安人员数量
        DSleepMornStatisticsGroup groupStatistics = statisticsGroupRepository.findByBid(bid);
        if (groupStatistics == null) {
            groupStatistics = new DSleepMornStatisticsGroup();
            groupStatistics.setBid(bid);
            groupStatistics.setSnum(0);
            groupStatistics.setGnum(1);
            statisticsGroupRepository.save(groupStatistics);
        } else {
            s=groupStatistics.getGnum() + 1;
            groupStatistics.setGnum(s);
            statisticsGroupRepository.save(groupStatistics);
        }

        //获取并更新今日所有早安人员数量
        DSleepMornStatisticsGroup totalStatistics = statisticsGroupRepository.findByBid("statistic");
        if (totalStatistics == null) {
            totalStatistics = new DSleepMornStatisticsGroup();
            totalStatistics.setBid("statistic");
            totalStatistics.setSnum(0);
            totalStatistics.setGnum(1);
            statisticsGroupRepository.save(totalStatistics);
        } else {
            b=totalStatistics.getGnum()+1;
            totalStatistics.setSnum(b);
            statisticsGroupRepository.save(totalStatistics);
        }

        message.append("☀你是本群聊/频道第").append(s).append("位早安的，也是");
        message.append("今天第").append(b).append("位早安的奥~\n");
    }

    //更新人员的累计表
    private void SleepMornStatistics(DSleepMornStatistics statistics,String uid,StringBuilder message,int k) {
        if (k == 0) {
            DSleepMornRecords record = recordsRepository.findByUid(uid);
            if (record == null) {//该 群聊/频道 第二次使用该指令
                message.append("\uD83C\uDF61已经累计早安").append(statistics.getNum()).append("天啦~");
            } else {
                statistics.setNum(statistics.getNum() + 1);
                message.append("\uD83C\uDF61已经累计早安").append(statistics.getNum()).append("天啦~");
                statisticsRepository.save(statistics);
            }
        }
    }
}