package org.example.zybot.begin.Modules.ZYRandomEvent.main.hard;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DAction;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DActionId;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DActionRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Component
public class RandomEvent {//世间轮回 相关处理操作

    final static String eventsFilePath="./data/RandomEvent/events.txt";
    final static String punishmentsFilePath="./data/RandomEvent/punishments.txt";

    private final DActionRepository dActionRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EntityManager entityManager;
    private static final Logger log = LoggerFactory.getLogger(RandomEvent.class);

    public RandomEvent(DActionRepository dActionRepository, DataSource dataSource, EntityManager entityManager) {
        this.dActionRepository = dActionRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entityManager = entityManager;
    }

    public String RandomEvents(@NotNull String[] massage, String byid, String room,String name,String pid, int[] k) {
        return switch (massage[0]) {
            case "查看事件" -> readEventsFile();
            case "查看惩罚" -> readchengfaFile();
            case "创建游戏" -> createFoldersAndFiles(byid, room,k);
            case "关闭游戏" -> deleteFolder(byid, room,k);
            case "加入游戏" -> processFiles(byid, room,pid, name, k);
            case "查看成员" -> findAndRead(byid, room,k);
            case "开始游戏" -> processFolders(byid, room,k);
            case "退出游戏" -> EndGame(byid, room, pid, k);
            case "查看状态" -> ViewStatus(byid, room, pid, k);
            case "抽取事件" -> ViewEvents(byid, room,pid,k);
            case "受罚" -> performFileOperations(byid, room, massage[1], pid, k);
            case "抽取惩罚" -> searchFilesAndFolders(byid, room,pid, k);
            case "照相机" -> camera(byid, room,pid, massage[1], k);
            case "惩罚" -> EventPenalty(byid, room, pid, massage[1],k);//拥有事件6专用指令
            case "查看进度" -> ViewProgress(byid, room,k);
            case "小姐何在" -> FollowThePunishment(byid, room, pid, k);
            default ->
                    "唔...这条指令我不认识呢,是打错字了吗？\n要不...使用 /世间轮回 帮助 来查看一下相关指令？（砂糖_乖巧）";
        };
    }

    //查看事件
    private @NotNull String readEventsFile() {//读取存储 事件 文档的内容
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(eventsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        } catch (IOException e) {
            log.error("世间轮回：读取events.txt文档失败", e);
            return "读取事件内容时出错啦呜呜呜QAQ\n我也不知道发生了什么...总之，再试几次吧，\n要是没有解决，就请跟我的开发者联系一下啦，谢谢你~";
        }
        return "\n当前版本的全部事件：\n" + content;
    }

    private @NotNull String readchengfaFile() {//读取存储 惩罚 文档的内容
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(punishmentsFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
                content.append("\n");
            }
        } catch (IOException e) {
            log.error("世间轮回：读取chengfa.txt文档失败", e);
            return "读取惩罚内容时出错啦呜呜呜QAQ\n我也不知道发生了什么...总之，再试几次吧，\n要是没有解决，就请跟我的开发者联系一下啦，谢谢你~";
        }
        return "\n当前版本的全部惩罚：\n" + content;
    }

    //当为~创建游戏 时，为加入游戏创建相关文件
    @NotNull
    private String createFoldersAndFiles(String gid, String room, @NotNull int[] k) {
        if (k[0] == 2) {//该 房间 游戏存在时
            return "创建游戏失败，该游戏已创建，不需要再次创建~~~";
        } else {
            // 创建新的DAction记录
            DActionId actionId = new DActionId(gid, room);//创建复合主键类
            DAction newAction = new DAction();//创建实体类
            newAction.setId(actionId);//添加复合主键
            newAction.setNum(2);//游戏类型
            newAction.setBata1("0");//当前行动为事件:1 还是惩罚:2 还是指定受罚人员环节3
            newAction.setBata2("0");//小姐人员id
            newAction.setBata3("0");//事件11 的次数
            dActionRepository.save(newAction);


            //String insertQuery = "INSERT INTO " + "Action" + " (id,room, num, bata1, bata2, bata3) VALUES ( ?, ?, ?, ?, ?, ?)";
            //jdbcTemplate.update(insertQuery, gid, room, 2, 0, 0, 0);
            //动态创建新表
            createCustomTable(gid, room);

            return "创建游戏成功~~~\n可以使用 /世间轮回 加入游戏 指令在此房间加入游戏啦~~~";
        }
    }

    //创建游戏 辅助类 用于创建以uuid为名的表
    void createCustomTable(String gid, String room) {
        // 生成一个随机UUID
        UUID uuid = UUID.randomUUID();
        // 将UUID转换为字符串并截取部分字符
        String Name = "Rand_" + uuid.toString().replace("-", "").substring(0, 11);
        //更新对数据表的操作
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(gid, room);
        if (actionOptional.isPresent()) {
            DAction action = actionOptional.get();
            action.setUuid(Name);
            dActionRepository.save(action);
        } else {
            // 处理未找到记录的情况
            log.error("世间轮回：未找到对应的成员表，id: {}, room: {}", gid, room);
        }
        //创建以name为名的表
        String sql = "CREATE TABLE IF NOT EXISTS " + Name + " (" +
                "id INTEGER PRIMARY KEY, " + // 手动加1
                "Player_id TEXT, " + // 人员id
                "blood INTEGER, " + // 血量
                "Data1 INTEGER, " + // 是否滞空
                "Data2 INTEGER, " + // 惩罚4摇到的次数
                "Data3 INTEGER, " + // 免罚次数
                "Data4 INTEGER, " + // 是否跟其说话就要受罚
                "Data5 INTEGER," + // 照相机次数
                "Name TEXT)";  //人员昵称 群聊暂不可用
        jdbcTemplate.execute(sql);
    }

    //执行游戏的关闭操作
    @NotNull
    public String deleteFolder(String byid, String room, @NotNull int []k) {
        if (k[0] == 1) {
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        }
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        //获取uuid并存储到变量table中
        DAction action = actionOptional.get();
        String table = action.getUuid();
        // 删除action表中的这一行
        dActionRepository.delete(actionOptional.get());
        //删除以table为名的表，删除以“bu_”+table为名的表
        deleteTableIfExist(table);
        try {
            deleteTableIfExist("Pen_" + table);
        } catch (Exception e) {
            //该表可能不存在，如果不存在不影响继续使用
        }

        return "游戏关闭成功~期待下一次相见  (〃'▽'〃)";
    }

    //辅助 辅助关闭游戏方法删除表
    private void deleteTableIfExist(String tableName) {
        // 构建SQL语句
        String sql = "DROP TABLE IF EXISTS " + tableName;
        // 执行SQL语句
        jdbcTemplate.execute(sql);
    }

    //执行 加入游戏 时相关操作
    @NotNull
    private String processFiles(String byid, String room, String playerId, String name, @NotNull int[] k) {
        // 检查 Action 表中是否存在具有相同 byid 和 room 值的记录
        if (k[0] == 1) {
            return "该游戏本房间还没有创建呢，无法使用该指令奥~";
        }
        if (k[1]==2){
            return "该游戏在本房间以开始，无法中途加入奥~";
        }
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        //获取游戏对应表的表名
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        String checkPlayerQuery = "SELECT player_id FROM  `" + table + "` WHERE player_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.query(checkPlayerQuery, new Object[]{playerId}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("player_id", rs.getString("player_id"));
            return map;
        });

        boolean isPlayerExists = !results.isEmpty();

        if (isPlayerExists) {
            return "您已加入该场游戏啦，所以，请不要在重复加入啦";
        }
        // 检查动态表中 id 列的最大值是否大于 12
        String maxIdQuery = "SELECT MAX(id) as max_id FROM " + table;
        List<Map<String, Object>> resultss = jdbcTemplate.query(maxIdQuery, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("max_id", rs.getInt("max_id"));
            return map;
        });
        int maxId = 0;
        if (!resultss.isEmpty()) {
            Map<String, Object> result = resultss.getFirst();
            maxId = (Integer) result.get("max_id");
        }

        if (maxId >= 12) {
            return "本房间的轮盘游戏已经满员啦，无法加入游戏奥~";
        }

        // 向动态表中写入数据
        if (!name.equals("0")) {
            String insertQuery = "INSERT INTO " + table + " (id,Player_id, blood, Data1, Data2, Data3, Data4, Data5) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(insertQuery, maxId + 1, playerId, 3, 0, 0, 0, 0, 0);
        } else {
            String insertQuery = "INSERT INTO " + table + " (id,Player_id, blood, Data1, Data2, Data3, Data4, Data5,Name) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";
            jdbcTemplate.update(insertQuery, maxId + 1, playerId, 3, 0, 0, 0, 0, 0, name);
        }

        return "加入成功~~~ヽ(○´∀`)ﾉ♪\n现在你是" + (maxId+1) + "号，请不要忘记奥~\ntps：开始指令：/世间轮回 开始游戏";
    }

    //将当前成员列表中的成员输出出来并加上序号 查看成员
    @NotNull
    private String findAndRead(String byid, String room, @NotNull int[] k) {
        if (k[0] == 1) {
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        }
        if (room.equals("0"))
            return "由于官方限制，无法获取到用户名称，因此该指令无法使用QAQ";
        // 使用Optional获取指定的action表中的数据
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        StringBuilder output = new StringBuilder();
        if (actionOptional.isPresent()) {
            DAction action = actionOptional.get();
            String uuid = action.getUuid();

            // 查找以uuid为名的表，获取其内部的所有内容
            String query = "SELECT id, Name FROM " + uuid;
            Query nativeQuery = entityManager.createNativeQuery(query);
            List<Object[]> results = nativeQuery.getResultList();

            // 将每行的id+Name内容输出，每行内容用回车分隔开
            for (Object[] result : results) {
                output.append(result[0]).append(" ").append(result[1]).append("\n");
            }
        }
        return "当前成员：\n" + output;
    }

    //执行 开始游戏 时的相关指令。
    @NotNull
    private String processFolders(String byid, String room, @NotNull int[] k) {
        //判断是否创建游戏
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";

        //判断是否早已开始游戏
        if (k[1] == 2) {
            return "游戏已经开始了奥，不需要再次开始游戏了";
        }
        //检查人数是否大于1
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        String maxIdQuery = "SELECT MAX(id) as max_id FROM " + table;
        List<Map<String, Object>> resultss = jdbcTemplate.query(maxIdQuery, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("max_id", rs.getInt("max_id"));
            return map;
        });

        int maxId = 0;
        if (!resultss.isEmpty()) {
            Map<String, Object> result = resultss.getFirst();
            maxId = (Integer) result.get("max_id");
        }
        if (maxId < 2) {
            return "人数不足2，无法开始游戏奥，在找些小伙伴一起来玩吧";
        }

        DAction action = dActionRepository.findByIdAndRoom(byid, room).orElseThrow();
        //随机行动人员编号
        Random random = new Random();
        int actionNumber = random.nextInt(maxId) + 1;
        // 添加行动人员编号
        action.setAct(actionNumber);
        //添加行动标记
        action.setBata1("1");
        // 保存更新后的记录
        dActionRepository.save(action);

        //创建惩罚表Pen_+uuid
        String sql = "CREATE TABLE IF NOT EXISTS " + "Pen_" + table + " (" +
                "n INTEGER PRIMARY KEY AUTOINCREMENT, " + // 序号 自增
                "id TEXT," + //成员pid
                "Data1 INTEGER)"; //标记，以保证每个惩罚最多叫一次小姐
        jdbcTemplate.execute(sql);

        return "游戏开始——我看看，随机到的幸运儿是...\n" + actionNumber + "号！请这位小伙伴发送\n /世间轮回 抽取事件 指令开始游戏吧\ntips：之后是顺时针轮序号奥";
    }

    //辅助函数，判断当前人员是否在游戏中  不存在为1，存在为2
    int compareFiles(String byid, String room, String pid) {
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        // 构造SQL查询，检查以table为名的表中Player_id列是否有变量id的内容
        String query = "SELECT CASE WHEN (SELECT COUNT(*) FROM `" + table + "` WHERE `Player_id` = :id) > 0 THEN 2 ELSE 1 END";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("id", pid);

        // 执行查询并返回结果
        Object result = sqlQuery.getSingleResult();
        return (int) result;
    }

    //辅助函数，删除对应人员内容并检测游戏是否结束
    int[] readContent(String byid, String room, String pid) {//[0]=1 游戏继续，[0]=0 游戏结束
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        int a1, a2, k = 0;
        int[] m = new int[2];
        // 1. 查找table表，获取Player_id列中与id相同的行，获取该行id列的内容保存在a1中
        String sql = "SELECT id FROM `" + table + "` WHERE Player_id = :id";
        Number a1Result = (Number)entityManager.createNativeQuery(sql)
                .setParameter("id", pid)
                .getSingleResult();
        a1 = a1Result.intValue();

        // 获取该行下一行的id列内容保存在a2中，如果没有下一行则获取第一行的id列内容保存在a2中
        sql = "SELECT id FROM `" + table + "` WHERE id = (SELECT min(id) FROM `" + table + "` WHERE id > :id)";
        try {
            Number idValue = (Number) entityManager.createNativeQuery(sql)
                    .setParameter("id", a1)
                    .getSingleResult();
            a2 = idValue.intValue();
        } catch (jakarta.persistence.NoResultException e) {
            // 处理异常，将a2设置为表中的第一行的id值
            String firstSql = "SELECT id FROM `" + table + "` ORDER BY id LIMIT 1";
            Number firstIdValue = (Number) entityManager.createNativeQuery(firstSql) .getSingleResult();
            a2 = firstIdValue.intValue();

        }

        // 删除a1所在行
        sql = "DELETE FROM `" + table + "` WHERE id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("id", a1)
                .executeUpdate();

        // 2. 查询table表，表中行数是否为1，如果是，则k=1
        sql = "SELECT COUNT(*) FROM " + table;
        Number count = (Number) entityManager.createNativeQuery(sql).getSingleResult();
        if (count.intValue() == 1) {
            k = 1;
        }

        //如果没有结束，则检测编号为a2的小伙伴是否滞空，并找到那个没有滞空的小伙伴输出
        if (k==1) {
            int[] z = hover(table, a2);
            if (z[0] == 0) {//a2被滞空
                int i=z.length;
                a2=z[i-1];
            }
        }

        DAction action = actionOptional.get();
        String penTable = "Pen_" + table;
        if (k == 0) {//k=0,代表游戏还未结束
            //获取Action表中该行act列的内容，如果为a1，则更新为a2
            if (action.getAct() == a1) {//如果为该小伙伴行动
                action.setAct(a2);//更新行动人员编号
                //获取Action表中该行bata1列的内容，如果为2，则更新为1
                action.setBata1("1");//更新行动标记
            } else
                a2 = 0;

            //获取Action表中该行bata2列的内容，如果为id，则更新为“0”
            if (action.getBata2().equals(pid)) {
                action.setBata2("0");
            }

            dActionRepository.save(action);//保存对action表的修改
            m[0] = 1;
            m[1] = a2;
            return m;
        } else {
            // 6. 删除table表，删除“Pen_”+table表，Action表对应的那一行
            String deleteTableSql = "DROP TABLE " + table;
            String deletePenTableSql = "DROP TABLE " + penTable;
            entityManager.createNativeQuery(deleteTableSql).executeUpdate();
            entityManager.createNativeQuery(deletePenTableSql).executeUpdate();
            dActionRepository.delete(action);
            m[1] = a2;
            return m;
        }
    }

    //检查id对应小伙伴是否滞空，如果滞空则查找下一位是否滞空，如果找不到则返回0
    int[] hover(String table, int nid) {
        List<Integer> intList = new ArrayList<>();
        String sql;
        int b=nid,k1=0;
        boolean k=true;

        while (k) {
            sql = "SELECT Data1 FROM `" + table + "` WHERE id = :id";
            Query sqlQuery = entityManager.createNativeQuery(sql);
            sqlQuery.setParameter("id", nid);
            Number idValue = (Number) sqlQuery.getSingleResult();
            if (idValue != null) {//一定不为空
                if (idValue.intValue() != 0) {
                    //被滞空，则重新寻找下一位小伙伴
                    //清除滞空
                    XColumn(table,nid,1,0);
                    if (k1==0) {//定义[0]=0 代表滞空，正在寻找不滞空的人员
                        intList.add(0);
                        k1=1;
                    }
                    intList.add(nid);//添加被滞空小伙伴的编号

                    //获取该行下一行的id列内容保存在id中，如果没有下一行则获取第一行的id列内容保存在id中
                    sql = "SELECT id FROM `" + table + "` WHERE id = (SELECT min(id) FROM `" + table + "` WHERE id > :id)";
                    try {

                        Number Value = (Number) entityManager.createNativeQuery(sql)
                                .setParameter("id", b)
                                .getSingleResult();
                        nid = Value.intValue();
                    } catch (jakarta.persistence.NoResultException e) {
                        // 处理异常，将id设置为表中的第一行的id值
                        String firstSql = "SELECT id FROM `" + table + "` ORDER BY id LIMIT 1";
                        Number Value = (Number) entityManager.createNativeQuery(firstSql)
                                .getSingleResult();
                        nid = Value.intValue();
                    }
                    b=nid;
                }else {//没有被滞空，结束循环
                    intList.add(nid);
                    k = false;
                }
            }
        }

        //将ArrayList转换回数组
        int[] intArray = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            intArray[i] = intList.get(i);
        }
        return intArray;
    }

    //执行 退出游戏 时的相关指令。
    @NotNull
    private String EndGame(String byid, String room, String pid, @NotNull int[] k) {
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        int w = compareFiles(byid, room, pid);
        if (w == 1)
            return "您并没有在该场游戏中奥，所以不需要退出喵";

        //执行退出逻辑
        int[] r = readContent(byid, room, pid);
        if (r[0] == 1) {//为1，代表游戏未结束
            if (r[1] == 0) {//为0，代表未更新行动人员
                return "退出游戏成功~";
            } else
                return "退出游戏成功~当前由" + r[1] + "号抽取事件~\n指令：/世间轮回 抽取事件";
        } else {//游戏结束
            return "退出游戏成功~游戏结束！胜利者是..." + r[1] + "号!" + "\n\n已自动关闭游戏~期待下一次相见(〃'▽'〃)";
        }
    }

    //执行 查看状态 时的相关指令
    @NotNull
    private String ViewStatus(String byid, String room, String pid, @NotNull int[] k) {
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        int w = compareFiles(byid, room, pid);
        if (w == 1)
            return "您并没有在该场游戏中奥，无法查看状态喵";
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        DAction action = actionOptional.get();
        String query = "SELECT * FROM `" + table + "` WHERE Player_id = :id";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("id", pid);
        // 执行查询
        Object[] result = (Object[]) sqlQuery.getSingleResult();

        //是否成为小姐
        String x1;
        if (action.getBata2().equals(pid))
            x1 = "是";
        else x1 = "否";

        if (room.equals("0"))
            return "个人状态：\n编号：" + result[0] + "\n血量：" + result[2] + "           照相机次数："
                    + result[7] + "\n免罚次数：" + result[5] + "    是否滞空：" + result[3] + "\n抽取惩罚 4 的次数：" + result[4] +
                    "\n是否为小姐：" + x1 + "\n是否抽取到事件 6：" + result[6];
        else
            return "个人状态：\n姓名：" + result[8] + "\n编号：" + result[0] + "\n血量：" + result[2] + "           照相机次数："
                    + result[7] + "\n免罚次数：" + result[5] + "    是否滞空：" + result[3] + "\n抽取惩罚 4 的次数：" + result[4] +
                    "\n是否为小姐：" + x1 + "\n是否抽取到事件 6：" + result[6];
    }

    //辅助函数，查询表对应bata列内容
    int CColumn(String table, int nid,int i) {
        String l="Data"+i;
        String sql = "SELECT "+l+" FROM `" + table + "` WHERE id = :id";
        Query sqlQuery = entityManager.createNativeQuery(sql);
        sqlQuery.setParameter("id", nid);
        Number idValue = (Number) sqlQuery.getSingleResult();
        int idInt=0;
        if (idValue != null) {
            idInt = idValue.intValue();
        }
        return idInt;
    }

    //辅助函数，修改表对应bata列内容
    void XColumn(String table, int nid,int i,int m) {
        String l="Data"+i;
        String sql="UPDATE `" + table + "` SET "+l+" = "+m+ " WHERE id = :id";
        Query sqlQuery = entityManager.createNativeQuery(sql);
        sqlQuery.setParameter("id", nid);
        sqlQuery.executeUpdate();
    }

    //辅助函数，向惩罚表中添加人员
    void punishment(String pen,String pid){
        String query = "INSERT INTO " + pen + " (id, Data1) VALUES (:pid, 0)";
        Query sqlInsert = entityManager.createNativeQuery(query);
        sqlInsert.setParameter("pid", pid);
        sqlInsert.executeUpdate();
    }

    //进行 抽取事件 时相关指令
    @NotNull
    private String ViewEvents(String byid, String room, String pid, @NotNull int[] k) {
        //检查逻辑
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        if (k[1] == 1)
            return "该房间还没有开始游戏呢，所以该指令暂不能使用奥（乖巧）";

        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        DAction action = actionOptional.get();
        if (!Objects.equals(action.getBata1(), "1")) {
            if (action.getBata1().equals("3"))
                return "还没有轮到抽取事件的时候呢（乖巧）,是不知道该谁抽取惩罚了吗？\n可以使用/世间轮回 查看进度 指令查看那些小伙伴需要受罚奥~";
            return "还没有轮到抽取事件的时候呢（乖巧）";
        }

        //查询是否为该小伙伴行动
        String query = "SELECT id FROM `" + table + "` WHERE Player_id = :id";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("id", pid);
        Number idValue = (Number) sqlQuery.getSingleResult();
        int nid;// 将 Number 转换为 int
        if (idValue != null) {//理论上一定不为空
            nid = idValue.intValue();
            if (action.getAct() != nid)
                return "还没有轮到你奥，现在轮到" + action.getAct() + "号小伙伴来抽取事件了";
        } else
            return "您并没有在该场游戏中奥，因此不能使用该指令奥（乖巧）";

        //内容逻辑
        // 生成1到12中的随机数字
        int q = (int) (Math.random() * 12) + 1;
        String s;//获取到的事件介绍
        String m11 = "";//如果抽取到事件11，对此的附加输出内容
        // 读取events.txt中第q行的内容
        try {
            BufferedReader shijianReader = new BufferedReader(new FileReader(eventsFilePath));
            String shijianContent = null;
            for (int i = 0; i < q; i++) {
                shijianContent = shijianReader.readLine();
            }
            shijianReader.close();
            s = shijianContent;
        } catch (IOException e) {
            log.error("世间轮回-抽取事件：读取events.txt内容失败", e);
            return "读取相关文件时出错QAQ  重新输入一遍指令再尝试一下把（乖巧）\n如果一直出现这种情况，请跟我的开发者联系一下，谢谢您~";
        }

        int nid1, nid2;//上家编号，下家编号
        switch (q) {
            case 1: {//指定某人受罚
                action.setBata1("3");
                break;
            }
            case 2: {//成为小姐
                action.setBata2(pid);
                break;
            }
            case 3: {//逛三园
                action.setBata1("3");
                break;
            }
            case 4: {//照相机
                int m = CColumn(table, nid, 5);//获取当前次数
                XColumn(table, nid, 5, m + 1);
                break;
            }
            case 5: {//免罚
                int m = CColumn(table, nid, 3);//获取当前次数
                XColumn(table, nid, 3, m + 1);
                break;
            }
            case 6: {//与其说话的受罚
                int m = CColumn(table, nid, 4);//获取当前次数
                if (m != 1)//不为1 更新为1
                    XColumn(table, nid, 3, 1);
                break;
            }
            case 7: {//逢七必过
                action.setBata1("3");
                break;
            }
            case 8: {//自己受罚
                action.setBata1("2");
                //插入惩罚表内容
                punishment("Pen_"+table, pid);
                break;
            }
            case 9: {//上家受罚
                action.setBata1("2");
                //获取上家的nid1
                query = "SELECT COALESCE(" +
                        "    (SELECT id FROM `" + table + "` WHERE id < :nid ORDER BY id DESC LIMIT 1), " +
                        "    (SELECT id FROM `" + table + "` ORDER BY id DESC LIMIT 1)" +
                        ") AS previous_id";
                sqlQuery = entityManager.createNativeQuery(query);
                sqlQuery.setParameter("nid", nid);
                Object result = sqlQuery.getSingleResult();
                nid1 = ((Number) result).intValue();

                //获取上家pid
                query = "SELECT player_id FROM `" + table + "` WHERE id = :id";
                sqlQuery = entityManager.createNativeQuery(query);
                sqlQuery.setParameter("id", nid1);
                result = sqlQuery.getSingleResult();
                String pid1 = (String) result;

                //插入惩罚表内容
                punishment("Pen_"+table, pid1);
                break;
            }
            case 10: {//下家受罚
                action.setBata1("2");

                // 获取该行下一行的id列内容保存在nid2中
                query = "SELECT id FROM `" + table + "` WHERE id = (SELECT min(id) FROM `" + table + "` WHERE id > :id)";
                try {
                    Number Value = (Number) entityManager.createNativeQuery(query)
                            .setParameter("id", nid)
                            .getSingleResult();
                        nid2 = Value.intValue();
                } catch (jakarta.persistence.NoResultException e) {
                    // 处理异常，将a2设置为表中的第一行的id值
                    String firstSql = "SELECT id FROM `" + table + "` ORDER BY id LIMIT 1";
                    Number Value = (Number) entityManager.createNativeQuery(firstSql)
                            .getSingleResult();
                    nid2 = Value.intValue();
                }

                //获取下家pid
                query = "SELECT player_id FROM `" + table + "` WHERE id = :id";
                sqlQuery = entityManager.createNativeQuery(query);
                sqlQuery.setParameter("id", nid2);
                Object result = sqlQuery.getSingleResult();
                String pid2 = (String) result;

                //插入惩罚表内容
                punishment("Pen_"+table, pid2);
                break;
            }
            case 11: {//自己受罚，下一次抽到的接受两次惩罚
                action.setBata1("2");
                String b3 = action.getBata3();//查看该事件的抽取次数
                if (b3.equals("0")) {//第一次抽到
                    action.setBata3("1");
                    m11 = "\n第一次抽取到该事件，需要抽取一次惩罚";
                    //插入惩罚表内容
                    punishment("Pen_"+table, pid);
                } else {//第二次抽到
                    action.setBata3("0");
                    m11 = "\n由于是第二次抽取到该事件，需要抽取两次惩罚";
                    //插入惩罚表内容
                    punishment("Pen_"+table, pid);
                    punishment("Pen_"+table, pid);
                }
                break;
            }
            case 12: {//摇骰子
                action.setBata1("3");
            }
        }
        //保存对action表的修改
        dActionRepository.save(action);

        //根据事件返回内容
        StringBuilder massage = new StringBuilder();
        switch (action.getBata1()) {
            case "1": {//事件结束后不需要进行惩罚指令，更新act列内容
                // 获取该行下一行的id列内容保存在nid2中
                query = "SELECT id FROM `" + table + "` WHERE id = (SELECT min(id) FROM `" + table + "` WHERE id > :id)";
                try {
                    Number Value = (Number) entityManager.createNativeQuery(query)
                            .setParameter("id", nid)
                            .getSingleResult();
                    nid2 = Value.intValue();
                } catch (jakarta.persistence.NoResultException e) {
                    // 处理异常，将a2设置为表中的第一行的id值
                    String firstSql = "SELECT id FROM `" + table + "` ORDER BY id LIMIT 1";
                    Number Value = (Number) entityManager.createNativeQuery(firstSql)
                            .getSingleResult();
                    nid2 = Value.intValue();
                }

                //让下一位非滞空人员抽取事件
                int[] z = hover(table, nid2);
                massage.append("我看看，随机到的事件是...\n").append(s).append("\n");
                if (z[0] == 0) {
                    massage.append("正在寻找下一位小伙伴抽取事件...由于");
                    int i = z.length - 1;
                    //添加滞空人员编号
                    massage.append(z[1]);
                    for (int j = 2; j < i; j++) {
                        massage.append(",").append(z[j]);
                    }
                    action.setAct(z[i]);
                    dActionRepository.save(action);
                    massage.append("号小伙伴被滞空，现在由").append(z[i]).append("号小伙伴发送\n /世间轮回 抽取事件 指令抽取事件吧");
                } else {
                    action.setAct(z[0]);
                    dActionRepository.save(action);
                    massage.append("现在由").append(z[0]).append("号小伙伴发送\n /世间轮回 抽取事件 指令抽取事件吧");
                }
                break;
            }
            case "2": {//事件结束后进入抽取惩罚环节
                massage.append("我看看，随机到的事件是...\n").append(s).append(m11).append("\n请所有需要抽取惩罚的小伙伴使用 /世间轮回 抽取惩罚 来抽取相关惩罚");
                break;
            }
            case "3": {//需要执行受罚指令指出受罚人员后再进入抽取惩罚环节
                if (q==1)
                    massage.append("我看看，随机到的事件是...\n").append(s).append("\n请使用 /世间轮回 受罚 xxx 来让序号为xxx的小伙伴受罚吧");
                else
                    massage.append("我看看，随机到的事件是...\n").append(s).append("\n做完游戏后使用 /世间轮回 受罚 xxx 来让序号为xxx的小伙伴受罚吧");
                break;
            }
        }
        return massage.toString();
    }

    //执行 受罚 时相关指令
    @NotNull
    private String performFileOperations(String byid, String room, String m, String pid, @NotNull int []k) {
        //检查逻辑
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        if (k[1] == 1)
            return "该房间还没有开始游戏呢，所以该指令暂不能使用奥（乖巧）";
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        DAction action = actionOptional.get();
        int w =compareFiles(byid,room,pid);
        if (w == 1)
            return "您并没有在该场游戏中奥，无法使用该指令喵";
        if (Objects.equals(m, ""))
            return "使用该指令时，要带有一个小伙伴的编号奥";

        //检测输入是否为数字
        try {
            Integer.parseInt(m);
        } catch (NumberFormatException e) {
            return "输入错误，~受罚 后面要添加的内容是要惩罚的小伙伴的序号奥";
        }
        //检测是否存在该序号
        String query = "SELECT 1 FROM `" + table + "` WHERE id = :id";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("id", m);
        List<Object> results = sqlQuery.getResultList();
        if (results.isEmpty()) {
            return "序号输入错误，目前没有序号为" + m + "的小伙伴奥";
        }

        //获取人员编号nid备用
        query = "SELECT id FROM `" + table + "` WHERE Player_id = :pid";
        sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("pid", pid);
        Number nidNumber = (Number) sqlQuery.getSingleResult();
        int nid = nidNumber.intValue();

        //是否该使用该指令
        String act= action.getBata1();
        if (!act.equals("3"))//如果行动序列不是3
            return "不是执行该指令的时候奥~";

        StringBuilder massage = new StringBuilder();
        massage.append("操作成功~");
        //检测受罚玩家是否具有免罚次数
        int mf=CColumn(table,nid,3);
        if (mf>0) {//有免罚次数
            massage.append("由于对方有免罚次数，已做相关处理，");
            XColumn(table,nid,3,mf-1);//次数-1
            action.setBata1("1");//更新行动标记
            dActionRepository.save(action);

            //更新行动人员
            int[] z=hover(table,nid);
            if (z[0] ==0){//下一位小伙伴被滞空
                massage.append("由于");
                massage.append(z[1]);
                int i = z.length - 1;
                //添加滞空人员编号
                massage.append(z[1]);
                for (int j = 2; j < i; j++) {
                    massage.append(",").append(z[j]);
                }
                massage.append("小伙伴被滞空，现在由").append(z[i]).append("号小伙伴抽取事件啦~\n可使用 /世间轮回 抽取事件 指令抽取事件");
            }else {//下一位小伙伴没有被滞空
                massage.append("现在由").append(z[0]).append("号小伙伴抽取事件啦~\n可使用 /世间轮回 抽取事件 指令抽取事件");
            }
        }else {//不具有免罚次数
            //将对方pid加入到惩罚表
            query = "SELECT Player_id FROM `" + table + "` WHERE id = :id";
            sqlQuery = entityManager.createNativeQuery(query);
            sqlQuery.setParameter("id", m);
            List<?> resultList = sqlQuery.getResultList();
            //获取对方pid
            Object pidObject = resultList.getFirst(); // 获取列表中的第一个元素
            String side = pidObject.toString(); // 将该元素转换为字符串
            punishment("Pen_"+table,side);
            action.setBata1("2");
            dActionRepository.save(action);
            massage.append("请所有需要抽取惩罚的小伙伴使用 /世间轮回 抽取惩罚 指令抽取惩罚~~~");
        }
        return massage.toString();
    }

    //辅助函数 删除人员并检测游戏是否结束
    int CancelPersonnel(String table,String pid){
        //删除table中的内容
        String sql = "DELETE FROM `" + table + "` WHERE Player_id = :pid";
        entityManager.createNativeQuery(sql)
                .setParameter("pid", pid)
                .executeUpdate();
        int k=1;
        // 2. 查询table表，表中行数是否为1，如果是，则k=0
        sql = "SELECT COUNT(*) FROM " + table;
        Number count = (Number) entityManager.createNativeQuery(sql).getSingleResult();
        if (count.intValue() == 1) {
            k = 0;
        }

        if (k==1){//游戏没有结束，删除惩罚表可能存在的该人员内容
            sql = "DELETE FROM `" + table + "` WHERE Player_id = :pid";
            entityManager.createNativeQuery(sql)
                    .setParameter("pid", pid)
                    .executeUpdate();
        }
        return k;
    }

    //执行 抽取惩罚 相关指令
    @NotNull
    private String searchFilesAndFolders(String byid, String room, String pid, @NotNull int []k) {
        //检查逻辑
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        if (k[1] == 1)
            return "该房间还没有开始游戏呢，所以该指令暂不能使用奥（乖巧）";
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        String pen="Pen_"+table;
        DAction action = actionOptional.get();
        if (!action.getBata1().equals("2"))
            return "现在不是执行该指令的时候奥";

        //检查该人员是否需要抽取惩罚
        int side;
        String query = "SELECT n FROM `" + pen + "` WHERE id = :pid  LIMIT 1";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("pid", pid);
        List<Object> results = sqlQuery.getResultList();
        if (results.isEmpty()) {
            // 没有找到对应的行
            return "您不需要抽取惩罚奥૮₍ᵔ⤙ᵔ ₎ა";
        }else{//获得在惩罚表中的编号备用
            Number ber = (Number) sqlQuery.getSingleResult();
            side = ber.intValue();
        }

        Random random = new Random();
        int q = random.nextInt(6) + 1;

        //读取对应的惩罚内容
        String s;
        try {
            BufferedReader shijianReader = new BufferedReader(new FileReader(punishmentsFilePath));
            String shijianContent = null;
            for (int i = 0; i < q; i++) {
                shijianContent = shijianReader.readLine();
            }
            shijianReader.close();
            s = shijianContent;
        } catch (IOException e) {
            log.error("世间轮回-抽取惩罚：读取chengfa.txt内容失败", e);
            return "读取相关文件时出错QAQ  重新输入一遍指令再尝试一下把（乖巧）\n如果一直出现这种情况，请跟我的开发者联系一下，谢谢您~";
        }

        //小姐标记，检测该小伙伴是否为小姐
        boolean miss = action.getBata2().equals(pid);

        //获取该成员的nid备用
        query = "SELECT id FROM `" + table + "` WHERE Player_id = :pid ";
        sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("pid", pid);
        Number ber = (Number) sqlQuery.getSingleResult();
        int nid = ber.intValue();

        StringBuilder massage = new StringBuilder();
        massage.append("让我康康...抽取到的惩罚是......\n").append(s).append("\n");
        //执行惩罚逻辑
        switch (q) {
            case 1:{//更新小姐
                massage.append("成为小姐，上一位小姐作废\n");
                action.setBata2(pid);
                dActionRepository.save(action);
                break;
            }
            case 2: {//扣一滴血
                if (!miss){//不是小姐，扣血
                    //获取血量
                    query = "SELECT blood FROM `" + table + "` WHERE id = :id ";
                    sqlQuery = entityManager.createNativeQuery(query);
                    sqlQuery.setParameter("id", nid);
                    Number tem = (Number) sqlQuery.getSingleResult();
                    int blood = tem.intValue();
                    if (blood==1){//死亡，执行删除逻辑
                       int c= CancelPersonnel(table,pid);
                       massage.append("您已阵亡喵......已自动帮忙清扫战场°¯᷄◠¯᷅°\n");
                       if (c==0){//游戏结束
                           //获取获胜人员nid
                           query = "SELECT id FROM `" + table + "` LIMIT 1";
                           sqlQuery = entityManager.createNativeQuery(query);
                           sqlQuery.setParameter("pid", pid);
                           tem = (Number) sqlQuery.getSingleResult();
                           int r = tem.intValue();

                           massage.append("游戏结束，胜利者是...").append(r).append("号！\n");
                           massage.append(deleteFolder(byid,room,k));
                           return massage.toString();
                       }
                    }else {//存活，更新血量
                        blood=blood-1;
                        query="UPDATE `" + table + "` SET blood = "+ blood+" WHERE id = :id";
                        sqlQuery = entityManager.createNativeQuery(query);
                        sqlQuery.setParameter("id", nid);
                        sqlQuery.executeUpdate();
                        massage.append("血量已扣除，当前血量：").append(blood).append("点\n");
                    }
                }else {//是小姐 重投
                    massage.append("由于拥有小姐身份，请重新再执行一次该指令叭~");
                    return massage.toString();
                }
                break;
            }
            case 3: {//扣三滴血
                //获取血量
                query = "SELECT blood FROM `" + table + "` WHERE id = :id ";
                sqlQuery = entityManager.createNativeQuery(query);
                sqlQuery.setParameter("id", nid);
                Number tem = (Number) sqlQuery.getSingleResult();
                int blood = tem.intValue();
                if (blood<4){//死亡，执行删除逻辑
                    int c= CancelPersonnel(table,pid);
                    massage.append("您已阵亡喵......已自动帮忙清扫战场°¯᷄◠¯᷅°\n");
                    if (c==0){//游戏结束
                        //获取获胜人员nid
                        query = "SELECT id FROM `" + table + "` LIMIT 1";
                        sqlQuery = entityManager.createNativeQuery(query);
                        tem = (Number) sqlQuery.getSingleResult();
                        int r = tem.intValue();

                        massage.append("游戏结束，胜利者是...").append(r).append("号！\n");
                        massage.append(deleteFolder(byid,room,k));
                        return massage.toString();
                    }
                }else {//存活，更新血量
                    blood=blood-1;
                    query="UPDATE `" + table + "` SET blood = "+ blood+" WHERE id = :id";
                    sqlQuery = entityManager.createNativeQuery(query);
                    sqlQuery.setParameter("id", nid);
                    sqlQuery.executeUpdate();
                    massage.append("血量已扣除，当前血量：").append(blood).append("点\n");
                }
                break;
            }
            case 4:{//重投，累计两次回血
                //获取重投次数
                int c =CColumn(table,nid,2);
                if (c==1){//达到两次，回一滴血
                    //获取血量
                    query = "SELECT blood FROM `" + table + "` WHERE id = :id ";
                    sqlQuery = entityManager.createNativeQuery(query);
                    sqlQuery.setParameter("id", nid);
                    Number tem = (Number) sqlQuery.getSingleResult();
                    int blood = tem.intValue()+1;

                    query="UPDATE `" + table + "` SET blood = "+ blood+" WHERE id = :id";
                    sqlQuery = entityManager.createNativeQuery(query);
                    sqlQuery.setParameter("id", nid);
                    sqlQuery.executeUpdate();
                    //更新重投记录
                    XColumn(table,nid,2,0);
                    massage.append("摇到  惩罚 4  的次数达到两次，回一滴血，当前血量：").append(blood).append("点\n");
                }else {//不足两次，保存重投记录
                   XColumn(table,nid,2,1);
                }
                massage.append("内容已记录，请重投一次吧~");
                return massage.toString();
            }
            case 5:{//真心话大冒险
                massage.append("tips:如果不喜欢可以自主换成其他的奥~如果有更好的建议请告诉我，谢谢你——\n");
                break;
            }
            case 6:{//滞空
                XColumn(table,nid,1,1);
                massage.append("由于被滞空，已清除该小伙伴可能存在的所有惩罚\n");
                query = "DELETE FROM `" + pen + "` WHERE id = :pid";
                entityManager.createNativeQuery(query)
                        .setParameter("pid", pid)
                        .executeUpdate();
                break;
            }
        }

        //清除惩罚人员
        query = "DELETE FROM `" + pen + "` WHERE n = :n LIKE 1";
        entityManager.createNativeQuery(query)
                .setParameter("n",side)
                .executeUpdate();

        //检测惩罚表是否为空
        query = "SELECT COUNT(*) FROM " + pen;
        Number count = (Number) entityManager.createNativeQuery(query).getSingleResult();
        if (count.intValue() == 0) {//惩罚表已空，更新行动标记
            action.setBata1("1");
            dActionRepository.save(action);
            massage.append("惩罚列表已清空~");

            // 获取该行下一行的id列内容保存在nid2中
            int nid2;
            query = "SELECT id FROM `" + table + "` WHERE id = (SELECT min(id) FROM `" + table + "` WHERE id > :id)";
            try {
                Number idValue = (Number) entityManager.createNativeQuery(query)
                        .setParameter("id", action.getAct())
                        .getSingleResult();
                nid2 = idValue.intValue();
            } catch (jakarta.persistence.NoResultException e) {
                // 处理异常，将a2设置为表中的第一行的id值
                String firstSql = "SELECT id FROM `" + table + "` ORDER BY id LIMIT 1";
                Number idValue = (Number) entityManager.createNativeQuery(firstSql)
                        .getSingleResult();
               nid2 = idValue.intValue();
            }
            //更新行动人员
            int[] z = hover(table, nid2);
            if (z[0] == 0) {
                massage.append("正在寻找下一位小伙伴抽取事件...由于");
                int i = z.length - 1;
                //添加滞空人员编号
                massage.append(z[1]);
                for (int j = 2; j < i; j++) {
                    massage.append(",").append(z[j]);
                }
                action.setAct(z[i]);
                dActionRepository.save(action);
                massage.append("号小伙伴被滞空，现在由").append(z[i]).append("号小伙伴发送\n /世间轮回 抽取事件 指令抽取事件吧");
            } else {
                action.setAct(z[0]);
                dActionRepository.save(action);
                massage.append("现在由").append(z[0]).append("号小伙伴发送\n /世间轮回 抽取事件 指令抽取事件吧");
            }
        }else
            massage.append("请其他需要抽取惩罚的小伙伴使用 /世间轮回 抽取惩罚 指令抽取惩罚吧~" +
                    "\ntips：查看惩罚表成员：/世间轮回 查看进度");
        return massage.toString();
    }

    //执行 照相机 相关指令
    @NotNull
    private String camera(String byid, String room, String pid, String m, @NotNull int []k) {
        //检查逻辑
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        if (k[1] == 1)
            return "该房间还没有开始游戏呢，所以该指令暂不能使用奥（乖巧）";
        int w = compareFiles(byid, room, pid);
        if (w == 1)
            return "您并没有在该场游戏中奥，无法使用该指令喵";
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);

        //获取使用该指令人员的nid
        String query = "SELECT id FROM `" + table + "` WHERE Player_id = :pid ";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("pid", pid);
        Number nidNumber = (Number) sqlQuery.getSingleResult();
        int nid = nidNumber.intValue();

        //检测使用该指令的小伙伴是否拥有照相机次数
        int intValue=CColumn(table,nid,5);
        if (intValue <= 0)
            return "您暂时没有照相机次数，不可以使用该指令奥";

        //检测使用该指令者是否已经滞空
        int s=CColumn(table,nid,1);
        if (s == 1)
            return "抱歉，您已经被滞空，暂时无法使用该指令呢…~(～o￣▽￣)～o 。。。";
        //检查输入是否为空
        if (Objects.equals(m, ""))
            return "使用该指令时，要带有一个小伙伴的编号奥";
        //检测输入是否为数字
        try {
            Integer.parseInt(m);
        } catch (NumberFormatException e) {
            return "输入错误，~照相机 后面要添加的内容是要惩罚的小伙伴的序号奥";
        }
        //检测输入的序号是否相同
        if (Objects.equals(nid, Integer.parseInt(m)))
            return "哎？？！自己的照相机不会对自己生效啦o(ﾟДﾟ)っ！";


        //检测是否存在该序号
        query = "SELECT 1 FROM `" + table + "` WHERE id = :id";
        sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("id", m);
        List<Object> results = sqlQuery.getResultList();
        if (results.isEmpty()) {
            return "序号输入错误，目前没有序号为" + m + "的小伙伴奥";
        }
        int nid1=Integer.parseInt(m);
        //照相机生效内容
        //检测被惩罚的人是否有免罚次数
        int c=CColumn(table,nid1,3);
        if (c>0){//有免罚次数
            XColumn(table,nid1,3,c-1);
            return "操作成功~由于该小伙伴有免罚次数，以将其免罚次数-1（乖巧）";
        }

        //获取受罚人员的pid1
        query = "SELECT Player_id FROM `" + table + "` WHERE id = :nid1";
        sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("nid1", nid1);
        Object result =  sqlQuery.getSingleResult();
        String pid1 = result.toString();

        //将受罚人员添加到惩罚表中
        punishment("Pen_"+table,pid1);

        //减少使用者的照相机次数
        XColumn(table,nid,5,intValue-1);

        return "操作成功~该小伙伴已进入惩罚序列，请在惩罚环节使用/世间轮回 抽取惩罚 指令抽取惩罚奥~";
    }

    //惩罚 相关指令
    @NotNull
    private String EventPenalty(String byid, String room, String pid, String m, @NotNull int []k) {
        //检查逻辑
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        if (k[1] == 1)
            return "该房间还没有开始游戏呢，所以该指令暂不能使用奥（乖巧）";
        int w = compareFiles(byid, room, pid);
        if (w == 1)
            return "您并没有在该场游戏中奥，无法使用该指令喵";
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);

        //获取使用该指令人员的nid
        String query = "SELECT id FROM `" + table + "` WHERE Player_id = :pid ";
        Query sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("pid", pid);
        Number nidNumber = (Number) sqlQuery.getSingleResult();
        int nid = nidNumber.intValue();

        //检测使用该指令的小伙伴是否拥有事件4
        int intValue=CColumn(table,nid,4);
        if (intValue <= 0)
            return "您暂时未获得事件 4，不可以使用该指令奥";

        //检测使用该指令者是否已经滞空
        int s=CColumn(table,nid,1);
        if (s == 1)
            return "抱歉，您已经被滞空，暂时无法使用该指令呢…~(～o￣▽￣)～o 。。。";
        //检查输入是否为空
        if (Objects.equals(m, ""))
            return "使用该指令时，要带有一个小伙伴的编号奥";
        //检测输入是否为数字
        try {
            Integer.parseInt(m);
        } catch (NumberFormatException e) {
            return "输入错误，~惩罚 后面要添加的内容是要惩罚的小伙伴的序号奥";
        }
        //检测输入的序号是否相同
        if (Objects.equals(nid, Integer.parseInt(m)))
            return "哎？？！该事件效果不会对自己生效啦o(ﾟДﾟ)っ！";

        //检测是否存在该序号
        query = "SELECT 1 FROM `" + table + "` WHERE id = :id";
        sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("id", m);
        List<Object> results = sqlQuery.getResultList();
        if (results.isEmpty()) {
            return "序号输入错误，目前没有序号为" + m + "的小伙伴奥";
        }
        int nid1=Integer.parseInt(m);
        //惩罚生效内容
        //检测被惩罚的人是否有免罚次数
        int c=CColumn(table,nid1,3);
        if (c>0){//有免罚次数
            XColumn(table,nid1,3,c-1);
            return "操作成功~由于该小伙伴有免罚次数，以将其免罚次数-1（乖巧）";
        }

        //获取受罚人员的pid1
        query = "SELECT Player_id FROM `" + table + "` WHERE id = :nid1";
        sqlQuery = entityManager.createNativeQuery(query);
        sqlQuery.setParameter("nid1", nid1);
        Object result =  sqlQuery.getSingleResult();
        String pid1 = result.toString();

        //将受罚人员添加到惩罚表中
        punishment("Pen_"+table,pid1);

        return "操作成功~该小伙伴已进入惩罚序列，请在惩罚环节使用/世间轮回 抽取惩罚 指令抽取惩罚奥~";
    }

    //执行 查看进度 相关指令
    @NotNull
    private String ViewProgress(String byid, String room, @NotNull int []k) {
        //检查逻辑
        if (k[0] == 1)
            return "当前进度：未创建游戏";
        if (k[1] == 1)
            return "当前进度：未开始游戏";

        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        DAction action = actionOptional.get();

        int j=Integer.parseInt(action.getBata1());
        return switch (j) {
            case 1 -> "当前进度：抽取事件阶段，请  " + action.getAct() + "  号小伙伴使用 /世间轮回 抽取事件 指令抽取事件";
            case 2 ->
                    "当前进度：抽取惩罚阶段，请  " + punishment(table) + "  号小伙伴使用 /世间轮回 抽取惩罚 指令抽取惩罚";
            case 3 -> "当前进度：受罚阶段，请在场的小伙伴使用 /世间轮回 受罚 xx x 指令来指定对应的小伙伴受罚";
            default -> "404 for world 未知错误.......";
        };
    }

    //查看惩罚表内的人员编号
    String punishment(String table){
        String pen="Pen_"+table;
        // 从table1读取id列的所有内容
        List<Object> idsFromTable1 = entityManager.createNativeQuery("SELECT id FROM "+pen)
                .getResultList();

        String ids = idsFromTable1.stream()
                .map(Object::toString)
                .collect(Collectors.joining("','"));
        String query = "SELECT t.id FROM `" + table + "` t " +
                "INNER JOIN `" + pen + "` p ON t.Player_id = p.id " +
                "WHERE p.id IN (SELECT id FROM `" + pen + "`)";
        List<Object> idsFromTable2 = entityManager.createNativeQuery(query)
                .getResultList();

        StringBuilder message = new StringBuilder();
        for (Object idFromTable2 : idsFromTable2) {
            message.append(idFromTable2).append(",");
        }

        // 删除最后的逗号
        if (!message.isEmpty()) {
            message.setLength(message.length() - 1);
        }

        return message.toString();
    }

    //小姐何在 相关指令
    String FollowThePunishment(String byid, String room,String pid ,@NotNull int []k) {
        //检查逻辑
        if (k[0] == 1)
            return "该房间还没有创建游戏呢，所以该指令暂不能使用奥（乖巧）";
        if (k[1] == 1)
            return "该房间还没有开始游戏呢，所以该指令暂不能使用奥（乖巧）";
        int w = compareFiles(byid, room, pid);
        if (w == 1)
            return "您并没有在该场游戏中奥，无法使用该指令喵";
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String table = actionOptional.map(DAction::getUuid).orElse(null);
        DAction action = actionOptional.get();
        String pen="Pen_"+table;
        String x=action.getBata2();
        //小姐是否存在
        if (x.equals("0"))
            return "当前没有人抽取到 事件 2 ，该指令暂不可用~";
        //使用者是否是小姐
        if(x.equals(pid))
            return "拥有 事件2 的小伙伴不可以使用该指令奥~";

        //修改惩罚表中的标记
        String updateQuery = "UPDATE `" + pen + "` SET Data1 = '1' WHERE id = :pid AND Data1 = '0' AND n = (SELECT MIN(n) FROM " + pen + " WHERE id = :pid AND Data1 = '0')";
        Query sqlUpdate = entityManager.createNativeQuery(updateQuery);
        sqlUpdate.setParameter("pid", pid);
        int affectedRows = sqlUpdate.executeUpdate();

        if (affectedRows == 0)//没有符合要求的行
            return "使用失败，您不在惩罚队列或每次惩罚都已使用过该指令\ntips；每次进入惩罚队列可使用一次该指令";

        //向惩罚表添加内容
        punishment(pen,action.getBata2());
        return "使用成功~已将对应小伙伴添加至惩罚序列，请在惩罚环节使用/世间轮回 抽取惩罚 指令抽取惩罚奥~";
    }
}
