package org.example.zybot.begin.ModulesFront.ZYdatabase.other;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AboutDailyLuck {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AboutDailyLuck(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void wData(String a, String b, String c) {
        String sql = "INSERT INTO dailyLuck (id,data1, data2) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, a, b, c);
    }

    public int qData(String b) {
        String sql = "SELECT EXISTS(SELECT 1 FROM dailyLuck WHERE id = ?)";
        return jdbcTemplate.queryForObject(sql, new Object[]{b}, (rs, rowNum) -> rs.getInt(1));
    }

    public String[] cData(String a) {
        String sql = "SELECT data1, data2 FROM dailyLuck WHERE id = ?";
        List<String[]> results = jdbcTemplate.query(
                sql,
                new Object[]{a},
                new RowMapper<String[]>() {
                    @Override
                    public String[] mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new String[]{rs.getString("data1"), rs.getString("data2")};
                    }
                }
        );

        if (!results.isEmpty()) {
            return results.getFirst(); // 返回查询到的第一个结果
        } else {
            return new String[]{"", "-1"}; // 查询不到结果时返回默认值
        }
    }
}