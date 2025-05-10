package org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//当没有数据库时，调用该类初始化数据库
@Service
public class CreateImpl implements Create {

    private static final Logger log = LoggerFactory.getLogger(CreateImpl.class);
    private final DataSource dataSource;

    @Autowired
    public CreateImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createDatabase() {
        // 获取数据库连接
        try (Connection connection = dataSource.getConnection()) {
            // 创建表的SQL语句
            String actionTableSql = "CREATE TABLE IF NOT EXISTS Action ("
                    + "id TEXT, "
                    + "room TEXT, "
                    + "uuid TEXT, "
                    + "num INTEGER, "
                    + "act INTEGER, "
                    + "bata1 TEXT, "
                    + "bata2 TEXT, "
                    + "bata3 TEXT, "
                    + "bata4 TEXT, "
                    + "bata5 TEXT,"
                    + "CTime LocalDateTime,"
                    + "PRIMARY KEY(id,room))";

            String dailyLuckTableSql = "CREATE TABLE IF NOT EXISTS dailyLuck ("
                    + "id TEXT PRIMARY KEY, "
                    + "data1 TEXT, "
                    + "data2 TEXT)";

            // 创建表
            try (Statement statement = connection.createStatement()) {
                statement.execute(actionTableSql);
                statement.execute(dailyLuckTableSql);
            }
        } catch (Exception e) {
            log.error("ModulesFront\\ZYdatabase\\Assistant: 初始化数据库表失败", e);
        }
    }
}