package org.example.zybot.begin.Modules.ZYTodayLuck.main.hard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

//定时：  0点清空DailyLuck表的所有内容
@Component
public class RegularClean {
    private static final Logger log = LoggerFactory.getLogger(RegularClean.class);
    private final DataSource dataSource;

    public RegularClean(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void clearDailyLuckTable() {
        // 创建一个SimpleDateFormat对象，并设置日期时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            // 获取当前日期时间
            Date now = new Date();
            // 使用SimpleDateFormat对象格式化日期时间
            String formattedDateTime = sdf.format(now);
            stmt.executeUpdate("DELETE FROM dailyLuck");
            log.info("{}: DailyLuck表（今日运气）内容已清空", formattedDateTime);
        } catch (SQLException e) {
            log.error("重置 今日运气 失败", e);
        }
    }
}