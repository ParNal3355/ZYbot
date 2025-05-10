package org.example.zybot.begin.ModulesFront.ZYdatabase;

import jakarta.annotation.PostConstruct;
import org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.create.Create;
import org.example.zybot.begin.ModulesFront.ZYdatabase.Assistant.inspect.Inspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

//ZY数据库基本结构检测函数
@Component
public class Again {

    private static final Logger log = LoggerFactory.getLogger(Again.class);
    private final Create create;
    private final Inspect inspect;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Again(DataSource dataSource, Create create, Inspect inspect) {
        this.create = create;
        this.inspect = inspect;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init() {
        // 检查sqlite_master表中是否有数据
        boolean hasDataInSqliteMaster = checkForDataInSqliteMaster();
        if (hasDataInSqliteMaster) {
            log.info("开始执行数据库基本结构检查");
            inspect.inspectDatabase();
        } else {
            log.info("数据库为空，执行数据库初始化");
            create.createDatabase();
        }
    }

    private boolean checkForDataInSqliteMaster() {
        String sql = "SELECT * FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%' LIMIT 1";
        try {
            // 使用JdbcTemplate查询sqlite_master表的第一行数据
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            return !results.isEmpty();
        } catch (Exception e) {
            log.error("查询sqlite_master表失败", e);

            return true; // 假设查询失败意味着没有数据
        }
    }
}

