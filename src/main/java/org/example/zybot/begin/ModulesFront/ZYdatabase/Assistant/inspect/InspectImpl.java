package org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.inspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//数据库检测逻辑，检测表结构是否完整
@Service
public class InspectImpl implements Inspect {
    private static final Logger log = LoggerFactory.getLogger(InspectImpl.class);
    private final DataSource dataSource;

    public InspectImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void inspectDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT sql FROM sqlite_master WHERE type='table' AND name=?")) {
                checkTable(preparedStatement, "Action",
                        "id TEXT, room TEXT, uuid TEXT, num INTEGER, act INTEGER, bata1 TEXT, bata2 TEXT, bata3 TEXT, bata4 TEXT, bata5 TEXT, CTime LocalDateTime, PRIMARY KEY(id,room)");
                checkTable(preparedStatement, "dailyLuck",
                        "id TEXT PRIMARY KEY, data1 TEXT, data2 TEXT");

                log.info("数据库基本结构检测完毕");
            }
        } catch (SQLException e) {
            log.error("数据库检测错误，请删除./data文件夹下的.db文件重启项目以初始化数据库", e);
        }
    }

    private void checkTable(PreparedStatement preparedStatement, String tableName, String expectedColumnsSql) throws SQLException {
        preparedStatement.setString(1, tableName);
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                String tableSql = resultSet.getString("sql");
                // 去除多余的空格和换行符
                tableSql = tableSql.replaceAll("\\s+", " ").trim();
                // 将预期的列定义也进行同样的处理
                expectedColumnsSql = expectedColumnsSql.replaceAll("\\s+", " ").trim();

                // 比较处理后的 SQL 语句和预期的列定义
                if (!tableSql.contains(expectedColumnsSql)) {
                    log.error("数据库检测错误：表 {} 结构不正确", tableName);
                    throw new SQLException("表 " + tableName + " 结构不正确");
                }
            } else {
                log.error("数据库检测错误：表 {} 不存在", tableName);
                throw new SQLException("表 " + tableName + " 不存在");
            }
        }
    }
}