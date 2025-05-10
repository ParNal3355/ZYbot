package org.example.zybot.begin.Modules.ZYDaemonicDisc.main.hard;

import org.example.zybot.begin.ModulesFront.ZYdatabase.Again;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DAction;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DActionId;
import org.example.zybot.begin.ModulesFront.ZYdatabase.hard.Action.DActionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class DaemonicDisc {//恶魔轮盘赌

    final static String propFilePath="./data/DaemonicDisc/prop.txt";

    private final DActionRepository dActionRepository;
    private final ResourceLoader resourceLoader;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(DaemonicDisc.class);


    public DaemonicDisc(ResourceLoader resourceLoader, DActionRepository dActionRepository, DataSource dataSource) {
        this.resourceLoader = resourceLoader;
        this.dActionRepository = dActionRepository;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String select(String a, String b, String byid, String room, String id) {
        //内容a，内容b，别野id/群聊id：byid,别野房间room，群聊中为0  玩家id：id
        return switch (a) {
            case "" -> "唔—— 指令少东西了奥，是不知道该填写什么吗？\nemmm......可以使用 /轮盘 帮助 来获取帮助奥";
            case "道具明细" -> propDetails();
            case "创建游戏" -> createGame(byid, room);
            case "加入游戏" -> joinGame(byid, room, id);
            case "关闭游戏" -> closeGame(byid, room);
            case "开枪" -> shoot(byid, room, b, id);
            case "道具" -> item(byid, room, b, id);
            default -> "唔...这条指令我不认识呢,是打错字了吗？\n要不...使用 /轮盘 帮助 来查看一下相关指令？（砂糖_乖巧）";
        };
    }

    //关于 道具明细 的相关方法
    String propDetails() {
        StringBuilder content = new StringBuilder();
        try {
            Resource resource = resourceLoader.getResource("file:" + propFilePath);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                    content.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "读取道具明细内容时出错啦呜呜呜QAQ\n我也不知道发生了什么...总之，再试几次吧，\n要是没有解决，就请跟我的开发者联系一下啦，谢谢你~";
        }
        return content.toString();
    }

    //关于 创建游戏 的相关方法
    String createGame(String gid, String room) {
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(gid, room);
        if (actionOptional.isPresent()) {//该 房间 游戏存在时
            DAction action = actionOptional.get();
            String n = action.getNum().toString();
            if (!n.equals("1"))
                return "创建游戏失败...(；д；)\n存在其他小游戏占用了该房间游戏资源";
            else
                return "创建游戏失败，该游戏已创建，不需要再次创建~~~";
        } else {
            // 创建新的DAction记录
            DActionId actionId = new DActionId(gid, room);//创建复合主键类
            DAction newAction = new DAction();//创建实体类
            newAction.setId(actionId);//添加复合主键
            newAction.setNum(1);//游戏类型
            newAction.setBata1("1");//伤害 默认为1
            newAction.setBata2("0");//是否使用锁链
            newAction.setBata3("0");//是否为锁链额外回合
            dActionRepository.save(newAction);

            //String insertQuery = "INSERT INTO " + "Action" + " (id,room, num, bata1, bata2, bata3) VALUES ( ?, ?, ?, ?, ?, ?)";
            //jdbcTemplate.update(insertQuery, gid, room,1,1,0,0);
            // 动态创建新表
            createCustomTable(gid,room);

            return "创建游戏成功~~~\n可以使用 /轮盘 加入游戏 指令在此房间加入游戏啦~~~";
        }
    }

    //创建游戏 辅助类 用于创建以uuid为名的表
    void createCustomTable(String gid,String room) {
        // 生成一个随机UUID
        UUID uuid = UUID.randomUUID();
        // 将UUID转换为字符串并截取部分字符
        String Name = "Disc_"+uuid.toString().replace("-", "").substring(0, 11);
        //更新对数据表的操作
        String UpUuid = "UPDATE Action SET uuid = ? WHERE id = ? AND room = ?";
        jdbcTemplate.update(UpUuid, Name,gid, room);
        //创建以name为名的表
        String sql = "CREATE TABLE IF NOT EXISTS " + Name + " (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "player_id TEXT, " + "blood INTEGER, " + "data1 INTEGER, " + "data2 INTEGER, " + "data3 INTEGER, " + "data4 INTEGER, " + "data5 INTEGER, " + "sum INTEGER)";
        jdbcTemplate.execute(sql);
    }

    //加入游戏 相关方法
    String joinGame(String byid, String room, String playerId) {
        String s = "";
        // 检查 Action 表中是否存在具有相同 byid 和 room 值的记录
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        if (actionOptional.isEmpty()) {
            return "该游戏本房间还没有创建呢，无法使用该指令奥~";
        }
        // 获取 DAction 实例
        int num = actionOptional.map(DAction::getNum).orElse(null);
        // 保存 num 属性的值
        if (num != 1) return "本房间的游戏资源运行的并不是该游戏（轮盘）呢，无法使用该指令奥~";
        //获取游戏对应表的表名
        String table = actionOptional.map(DAction::getUuid).orElse(null);

        String checkPlayerQuery = "SELECT player_id FROM " + table + " WHERE player_id = ?";
        List<Map<String, Object>> results = jdbcTemplate.query(checkPlayerQuery, new Object[]{playerId}, (rs, rowNum) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("player_id", rs.getString("player_id"));
            return map;
        });

        boolean isPlayerExists = !results.isEmpty();

        if (isPlayerExists) {
            return "您已加入该场游戏啦，所以，请不要在重复加入啦";
        }
        // 检查动态表中 id 列的最大值是否大于 2
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

        if (maxId >= 2) {
            return "本房间的轮盘游戏已经满员啦，无法加入游戏奥~";
        }

        // 向动态表中写入数据
        String insertQuery = "INSERT INTO " + table + " (player_id, blood, data1, data2, data3, data4, data5, sum) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertQuery, playerId, 4,0,0,0,0,0,0);

        if (maxId == 0) {
            maxId = 1;
        } else if (maxId == 1) {
            maxId = 2;
            s = startGame(byid, room);
        }
        return "加入成功~~~ヽ(○´∀`)ﾉ♪\n现在你是" + maxId + "号，请不要忘记奥~" + s;
    }

    //开始游戏逻辑 人员满足会在加入游戏后自动自动开始
    String startGame(String byid, String room) {

        // 根据 byid和room 获取对应的记录
        DAction action = dActionRepository.findByIdAndRoom(byid, room).orElseThrow();

        // 确保act不为null
        if (action.getAct() == null) {
            action.setAct(0);
        }

        // 随机选择数字 1 或 2作为行动的人的编号
        Random random = new Random();
        int actionNumber = random.nextInt(2) + 1;
        // 向act列添加随机数字
        action.setAct(action.getAct() + actionNumber);

        // 保存更新后的记录
        dActionRepository.save(action);
        String s = LoadingBullets(byid, room);//执行装填子弹和装填道具指令
        // 返回结果
        return "\n人数达标~游戏开始——\n请编号为：" + actionNumber + "号的小伙伴使用道具and开枪叭\n指令：" + "\n道具：/轮盘 道具 xxxx\n开枪：/轮盘 开枪 自己or对方\n\n" + s;
    }

    //辅助类，装填子弹，其会 自动调用 装填道具 的方法
    String LoadingBullets(String byid, String room) {

        //获取表的uuid名
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        String uuid = actionOptional.map(DAction::getUuid).orElse(null);

        // 创建以“bu_”+uuid为名的表 作为子弹表
        String tableName = "bu_" + uuid;

        // 检查表是否存在
        String checkTableExistenceSQL = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try {
            String existingTableName = jdbcTemplate.queryForObject(checkTableExistenceSQL, new Object[]{tableName}, String.class);
            if (existingTableName != null) {
                // 表存在，删除它
                String dropTableSQL = "DROP TABLE IF EXISTS " + tableName;
                jdbcTemplate.execute(dropTableSQL);
            }
        } catch (Exception e) {
            //找不到表，说明是刚开始游戏还未创建表格 不必理会
        }

        // 创建新表
        String createTableSQL = "CREATE TABLE " + tableName + " (id INTEGER PRIMARY KEY AUTOINCREMENT, bu INT NULL)";
        jdbcTemplate.execute(createTableSQL);

        Random random = new Random();
        int q = 3 + random.nextInt(10); // 生成3到12之间的随机数
        List<Integer> list = new ArrayList<>();
        int q1 = 0; // 0出现的次数
        int q2 = 0; // 1出现的次数

        // 从0和1之间取随机数q次，并记录每次随机到的内容
        for (int i = 0; i < q; i++) {
            int num = random.nextInt(2);
            list.add(num);
            if (num == 0) {
                q1++;
            } else {
                q2++;
            }
        }

        // 对得到的内容进行检验
        boolean valid = false;
        while (!valid) {
            int count = (int) list.stream().filter(n -> n == 0).count(); // 计算0的数量
            int oneCount = q - count; // 计算1的数量
            if ((q == 3 && count >= 1 && oneCount >= 1) || // 当q=3，且0和1的数量均至少为1
                    (q > 3 && q < 7 && (count >= 2 || oneCount >= 2)) || // 当q大于3小于7时，0和1的数量至少为2
                    (q >= 6 && q < 10 && (count >= 3 && oneCount >= 3)) || // 当q大于等于6小于10时，0和1的数量均至少为3
                    (q >= 10 && q < 13 && (count >= 4 && oneCount >= 4))) { // 当q大于等于10小于13时，0和1的数量均至少为4
                valid = true;
            } else {
                // 如果不满足条件，重新生成
                list.clear();
                q1 = 0;
                q2 = 0;
                for (int i = 0; i < q; i++) {
                    int num = random.nextInt(2);
                    list.add(num);
                    if (num == 0) {
                        q1++;
                    } else {
                        q2++;
                    }
                }
            }
        }

        //向该表的qu列依次添加随机到的内容list
        for (int num : list) {
            String insertSQL = "INSERT INTO " + tableName + " (bu) VALUES (?)";
            jdbcTemplate.update(insertSQL, num);
        }

        String l = LoadProps(uuid);
        // 输出结果
        return "开始装弹......本次共装填了" + q + "颗子弹，其中有" + q2 + "颗实弹，" + q1 + "颗空弹\n" + l;
    }

    //辅助 填装道具 由装填子弹引用
    String LoadProps(String uuid) {
        StringBuilder s = new StringBuilder();
        s.append("开始分配道具...\n");
        // 3. 随机生成list1和list2
        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                int num = random.nextInt(5) + 1; // 生成1到5的随机数
                if (i == 0) {
                    list1.add(num);
                } else {
                    list2.add(num);
                }
            }
        }
        //读取sum1和sum2的值
        int sum1, sum2;
        String sql = "SELECT sum FROM " + uuid + " WHERE id IN (1, 2) ORDER BY id";
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            if (results.size() == 2) {
                Integer sumValue1 = (Integer) results.get(0).get("sum");
                Integer sumValue2 = (Integer) results.get(1).get("sum");
                sum1 = sumValue1 != null ? sumValue1 : 0;
                sum2 = sumValue2 != null ? sumValue2 : 0;
            } else {
                sum1 = 0;
                sum2 = 0;
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            log.error("轮盘简易版-装填道具：预期有1或2个结果，但得到了{}",e.getMessage());
            sum1 = 0;
            sum2 = 0;
        }
        // 计算n1和n2
        int k1 = 1, n1 = 8 - sum1;
        int k2 = 1, n2 = 8 - sum2;
        if (n1 >= 4) {
            n1 = 4;
            k1 = 0;
        }
        if (n2 >= 4) {
            n2 = 4;
            k2 = 0;
        }

        //将list1、2转换为d1、2并按照要求舍弃多余的数据
        List<String> resultList1 = new ArrayList<>();
        for (int i = 0; i < Math.min(n1, list1.size()); i++) {
            resultList1.add(list1.get(i).toString());
        }
            // 将List转换为String数组
        String[] d1 = resultList1.toArray(new String[0]);
        List<String> resultList2 = new ArrayList<>();
        for (int i = 0; i < Math.min(n2, list2.size()); i++) {
            resultList2.add(list2.get(i).toString());
        }
        // 将List转换为String数组
        String[] d2 = resultList2.toArray(new String[0]);
        // 5. 根据n1和n2更新数据
        s.append(updateDataByList(uuid, d1, 1));
        s.append(updateDataByList(uuid, d2, 2));
        s.append("分配完成 \n");
        if (k1 == 1) s.append("由于1号道具空间不足（最多8个），本次分配道具减少\n");
        if (k2 == 1) s.append("由于2号道具空间不足（最多8个），本次分配道具减少\n");
        s.append("1号：");
        AssignPropNames(s, d1);
        s.append("\n二号：");
        AssignPropNames(s, d2);
        s.append("\n道具总和：\n");
        //获得道具总量
        n1=n1+sum1;
        n2=n2+sum2;
        //更新道具总量的数据
        String sql1 = "UPDATE " + uuid + " SET sum = "+ n1 +" WHERE id = 1";
        String sql2 ="UPDATE " + uuid + " SET sum = "+ n2 +" WHERE id = 2";
        jdbcTemplate.update(sql1);
        jdbcTemplate.update(sql2);
        //显示所有人当前拥有的道具数量
        s.append(PropInformation(uuid, 1));
        s.append(PropInformation(uuid, 2));
        return s.toString();
    }

    //辅助函数 由 装填道具 部分引用，用来分配道具名
    private void AssignPropNames(StringBuilder s, String[] d1) {
        for (String string1 : d1) {
            switch (string1) {
                case "1":
                    s.append("小刀，");
                    break;
                case "2":
                    s.append("汽水，");
                    break;
                case "3":
                    s.append("放大镜，");
                    break;
                case "4":
                    s.append("华子，");
                    break;
                case "5":
                    s.append("锁链，");
                    break;
            }
        }
    }

    //辅助类 辅助装填道具
     String updateDataByList(String table, String[] data, int n) {
        StringBuilder s = new StringBuilder();
        // 遍历data数组中的每个元素
        for (String column : data) {
            try {//data不可能为0
                // 构建列名
                String columnIndex="data"+column;
                //获取columnIndex列对应的内容
                String sql1 = "SELECT " + columnIndex + " FROM " + table + " WHERE id = "+n;
                Integer d= jdbcTemplate.queryForObject(sql1,Integer.class);
                if (d==null)
                    d=0;
                d=d+1;
                // 构建SQL更新语句，使用COALESCE确保空值被视为0
                String sql2 = "UPDATE " + table + " SET " + columnIndex + " = "+ d +" WHERE id = " + n;
                // 执行更新操作
                jdbcTemplate.update(sql2);
            } catch (NumberFormatException e) {
                // 如果转换失败，打印错误信息并继续下一个迭代
                s.append("装填").append(n).append("号的道具时出现错误...道具会丢的哇哇哇哇哇QAQ\n");
            }
        }
        return s.toString();
    }

    //辅助类，用于获得k号玩家所拥有的所有道具
    String PropInformation(String table, int k) {
        StringBuilder s = new StringBuilder();
        s.append(k).append("号：\n");
        // SQL查询语句，用于获取第k行的data1到data5的数据
        String sql = "SELECT data1, data2, data3, data4, data5 FROM " + table + " WHERE id = " + k;

        try {
            // 执行查询
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            // 处理第k行数据
            Map<String, Object> row = rows.getFirst();
            String[] data = new String[5];
            for (int i = 1; i <= 5; i++) {
                data[i - 1] = (row.get("data" + i) != null) ? row.get("data" + i).toString() : "0";
            }

            for (int i = 0; i < data.length; i++) {
                if (!Objects.equals(data[i], "0")) {
                String o = switch (i + 1) {
                    case 1 -> "小刀x";
                    case 2 -> "汽水x";
                    case 3 -> "放大镜x";
                    case 4 -> "华子x";
                    case 5 -> "锁链x";
                    default -> "";
                };
                    s.append(o).append(data[i]).append(",");
                }
            }
            s.append("\n");
            return s.toString();

        } catch (Exception e) {
            log.error("轮盘简易版-辅助类-获取玩家所有道具：查询玩家道具类报错。{}",e.getMessage());
            return "获取玩家道具总数时出错！QAQ";
        }
    }

    //关闭游戏 相关方法
    public String closeGame(String byid, String room) {
        //检查游戏是否已开始
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        if (actionOptional.isEmpty()) {
            return "该房间没有创建游戏呢，所以，不需要关闭~~~";
        }
        // 获取 DAction 实例
        int num = actionOptional.map(DAction::getNum).orElse(null);
        // 保存 num 属性的值
        if (num != 1) return "本房间的游戏资源运行的并不是该游戏（轮盘）呢，无法使用该指令奥~";
        // 2. 获取uuid并存储到变量table中
        DAction action = actionOptional.get();
        String table = action.getUuid();
        // 3. 删除action表中的这一行
        dActionRepository.delete(actionOptional.get());
        // 4. 删除以table为名的表，删除以“bu_”+table为名的表
        deleteTableIfExist(table);
        try {
            deleteTableIfExist("bu_" + table);
        }catch (Exception e){
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

    //执行 开枪 相关用法
    String shoot(String byid, String room, String b, String id) {
        // 1. 检测b内容是否为“自己”或“对方”
        if (!"自己".equals(b) && !"对方".equals(b)) {
            return "指令  /轮盘 开枪 xxx  中xxx是必须要有的奥，须填写“自己”或者“对方”，不然我没法知道你打算这一枪怎样打呢";
        }

        // 2. 根据byid和room定位到action表的对应行
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        if (actionOptional.isEmpty()) {
            return "该房间没有创建游戏呢，无法使用该指令奥~";
        }
        DAction action = actionOptional.get();

        if (action.getNum() != 1) {
            return "本房间的游戏资源运行的并不是该游戏（轮盘）呢，无法使用该指令奥~";
        }
        if (action.getAct() == null) {
            return "该房间还没有开始游戏呢，请先开始游戏奥,人员到达2位时会自动开始游戏哒~";
        }

        String table = action.getUuid();
        String bata1 = action.getBata1() != null ? action.getBata1() : "1";
        String bata2 = action.getBata2() != null ? action.getBata2() : "0";
        String bata3 = action.getBata3() != null ? action.getBata3() : "0";

        // 3. 在table表中找到player_id列中含有变量id内容的行
        String sqlFindPlayer = "SELECT id FROM " + table + " WHERE player_id = ?";
        String p = jdbcTemplate.queryForObject(sqlFindPlayer, new Object[]{id}, String.class);
        if (p == null || !p.equals(action.getAct().toString())) {
            return "现在还没有轮到您行动奥";
        }

        // 获取bu列第一个不为-1的值
        String Bu1 = "SELECT bu FROM bu_" + table + " WHERE bu <> -1 ORDER BY id LIMIT 1";
        Integer bu=jdbcTemplate.queryForObject(Bu1, Integer.class);
        if (bu != null) {//如果获取结果不为空，则修改该内容为-1
            String ID="SELECT id FROM bu_" + table + " WHERE bu <> -1 ORDER BY id LIMIT 1";
            int i=jdbcTemplate.queryForObject(ID, Integer.class);
            String Bu2="UPDATE bu_" + table + " SET bu = -1 WHERE id ="+i;
            jdbcTemplate.update(Bu2);
        } else {
            //理论上不会出现
            return "数据错误，抢中已无子弹";
        }

        StringBuilder s = new StringBuilder();
        //获取到对方的编号
        int r;
        if (p.equals("1")) r = 2;
        else r = 1;

        int k = 0; //判断游戏是否结束
        if (bu == 0) {
            //为空弹
            s.append("咔嚓......看来是空弹呢\n");
        } else {//为实弹
            s.append("砰！看来是实弹，希望人没事......\n");
            // 5. 判断b内容
            if ("自己".equals(b)) {
                //打到自己
                // 读取a行的blood列的内容
                String sqlFindBlood = "SELECT blood FROM " + table + " WHERE player_id = ?";
                Integer blood = jdbcTemplate.queryForObject(sqlFindBlood, new Object[]{id}, Integer.class);
                if (blood == null) return "血量出现未知错误...(ó﹏ò｡)";
                //血量为-1 断电模式掉血，游戏结束
                if (blood == -1) {
                    int next;
                    if (p.equals("1")) next = 2;
                    else next = 1;
                    s.append("血量归零，游戏结束，获胜者是...\n").append(next).append("号！⸜₍๑•⌔•๑₎⸝\n正在关闭游戏ing~\n");
                    s.append(closeGame(byid, room));
                    k = 1;
                } else {//不是断电模式受伤
                    blood -= Integer.parseInt(bata1);
                    //血量首次小于1，血量归零，进入断电模式
                    if (blood < 1) {
                        jdbcTemplate.update("UPDATE " + table + " SET blood = -1 WHERE player_id = ?", id);
                        s.append(p).append("号血量过低，触发断电,当前血量:1点，且无法回血");
                    } else {//血量大于0
                        jdbcTemplate.update("UPDATE " + table + " SET blood = ? WHERE player_id = ?", blood, id);
                        s.append(p).append("号当前血量：").append(blood).append("点");
                    }
                }
            } else {
                //打到对方
                // 读取对方的blood列的内容
                String sqlFindBlood = "SELECT blood FROM " + table + " WHERE id = ?";
                Integer blood = jdbcTemplate.queryForObject(sqlFindBlood, new Object[]{r}, Integer.class);
                if (blood == null)//不会出现，仅为消除警告
                    return "血量出现未知错误...(ó﹏ò｡)";
                if (blood == -1) {//断电模式收到攻击，游戏结束
                    s.append("血量归零，游戏结束，获胜者是...\n").append(p).append("号！⸜₍๑•⌔•๑₎⸝\n正在关闭游戏ing~\n");
                    s.append(closeGame(byid, room));
                    k = 1;
                } else {//非断电模式受到攻击
                    blood -= Integer.parseInt(bata1);
                    if (blood < 1) {//触发断电
                        jdbcTemplate.update("UPDATE " + table + " SET blood = -1 WHERE id = ?", r);
                        s.append(r).append("号血量过低，触发断电,当前血量:1点，且无法回血");
                    } else {//血量大于0
                        jdbcTemplate.update("UPDATE " + table + " SET blood = ? WHERE id = ?", blood, r);
                        s.append(r).append("号当前血量：").append(blood).append("点");
                    }
                }
            }
        }
        // 6. 更新bata列  更新回合 判断行动人员是否要更新
        int l=0;//为0时代表不用处理行动人员问题，为1时代表需要处理
        if (bu == 0){ //空弹
            action.setBata1("1");
            dActionRepository.save(action);
            s.append("由于打自己时为空弹，").append(p).append("号继续");
            if ("对方".equals(b))//打对方 需要处理
                l=1;
        }
        else {//为实弹 无论是打到自己还是对方，都要处理
            l=1;
        }
        if (l==1){ //需要处理逻辑
            if (k==0){// 游戏未结束
                if (bata2.equals("1")) {//该回合使用锁链了 不换人员 bata1为1，bata2为0，bata3为1
                    action.setBata1("1");
                    action.setBata2("0");
                    action.setBata3("1");
                    // 保存更新
                    dActionRepository.save(action);
                    s.append("\n由于使用锁链，").append(p).append("号继续");

                } else if (Objects.equals(bata3, "1")) {//该回合是锁链的额外回合 更换行动人员
                    //bata1为1 bata2不用管 bata3为0 act更新
                    action.setBata1("1");
                    action.setBata3("0");
                    action.setAct(r);
                    // 保存更新
                    dActionRepository.save(action);
                    s.append("\n现在由").append(r).append("号使用道具and开枪");
                } else {//普通回合 更换行动人员 bata1为1 bata2和3不用管 act更新
                    action.setBata1("1");
                    action.setAct(r);
                    // 保存更新
                    dActionRepository.save(action);
                    s.append("\n现在由").append(r).append("号使用道具and开枪");
                }
            }
        }
        //检测是否还有子弹
        if (k==0) {//游戏还未结束
            try {
                jdbcTemplate.queryForObject(Bu1, Integer.class);
            } catch (EmptyResultDataAccessException e) {
                //如果获取结果为空，则调用装弹开始新的回合
                s.append("\n子弹已空，开始新的回合...\n").append(LoadingBullets(byid, room));
            }
        }
        return s.toString();
    }


    String item(String byid, String room, String b, String id) {
        // 1. 检查action表中是否存在指定的记录
        Optional<DAction> actionOptional = dActionRepository.findByIdAndRoom(byid, room);
        if (actionOptional.isEmpty()) {
            return "该房间没有创建游戏呢，所以无法使用该指令";
        }
        // 获取 DAction 实例
        int num = actionOptional.map(DAction::getNum).orElse(null);
        // 保存 num 属性的值
        if (num != 1) return "本房间的游戏资源运行的并不是该游戏（轮盘）呢，无法使用该指令奥~";

        DAction action = actionOptional.get();
        //目前该谁行动
        Integer a = action.getAct();
        if (a == null) {
            //没有人行动，代表没有开始游戏
            return "该房间还没有开始游戏呢，请先开始游戏奥,人员到达2位时会自动开始游戏哒~";
        }

        //相关表的表名
        String table = action.getUuid();
        // 2. 根据b的值设置变量i
        int i;
        switch (b) {
            case "小刀":
                i = 1;
                break;
            case "汽水":
                i = 2;
                break;
            case "放大镜":
                i = 3;
                break;
            case "华子":
                i = 4;
                break;
            case "锁链":
                i = 5;
                break;
            default:
                return "输入错误，指令  /轮盘 道具 xxx  中xxx只能为“小刀”、“汽水”、“放大镜”、“华子”、“锁链”中的一个奥";
        }

        // 3. 检查动态创建的表中是否有匹配的行
        String sql = "SELECT id FROM " + table + " WHERE player_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);

        // 检查查询结果是否为0，或者id是否不等于a
        if (count == null || count == 0 || !count.equals(a)) {
            return "现在还没有轮到您行动奥";
        }
        //列名
        String l = "data" + i;

        String sql1 = "SELECT " + l + " FROM " + table + " WHERE player_id = ?";
        Integer result = jdbcTemplate.queryForObject(sql1, Integer.class, id);

        // 检查查询结果是否为 null 或者是否为 0
        if (result == null || result == 0) {
            return "您目前没有该道具，无法使用";
        }

        int d=0;
        StringBuilder s = new StringBuilder();
        switch (i) {
            case 1: {//小刀
                String data1 = action.getBata1();
                if (data1 == null) {
                    data1 = "1"; // 如果为空就当做1处理
                }

                if (!data1.equals("2")) {
                    action.setBata1("2"); // 如果不为2，则将其改为2
                    dActionRepository.save(action); // 保存更改
                    s.append("道具-小刀 使用成功，伤害翻倍");
                } else {
                    return "道具-小刀 使用失败，每次开枪前只能使用一次小刀奥"; // 如果为2，则返回提示信息
                }
                break;
            }
            case 2: {//汽水
                String tableName = "bu_" + table;
                String sqlFind = "SELECT bu FROM " + tableName + " WHERE bu <> -1 ORDER BY id LIMIT 1";

                // 从上向下查询第一个内容不为-1的数据
                Integer bu = jdbcTemplate.queryForObject(sqlFind, Integer.class);
                //若查询不到不等于-1的情况  实际不会存在
                if (bu == null) {
                    s.append("\n错误#弹夹内子弹用光，准备开始下一轮...\n");
                    s.append(LoadProps(table));
                    break;
                }
                // 根据bu的值返回相应的结果
                if (bu == 0) {
                    s.append("道具-汽水 使用成功，弹出一颗子弹\n看这个颜色，是空弹呢");
                } else if (bu == 1) {
                    s.append("道具-汽水 使用成功，弹出一颗子弹\n看这个颜色，是实弹呢");
                } else {
                    // 如果bu的值不是预期的0或1，可能需要处理异常情况  不可能的
                    s.append("获取子弹信息时数据错误..(｡•ˇ‸ˇ•｡)…");
                }

                // 2. 将该数据改为-1
                String ID="SELECT id FROM " + tableName + " WHERE bu <> -1 ORDER BY id LIMIT 1";
                int o=jdbcTemplate.queryForObject(ID, Integer.class);
                String Bu2="UPDATE " + tableName + " SET bu = -1 WHERE id ="+o;
                jdbcTemplate.update(Bu2);

                // 3. 修改后再次查询第一个不是-1的数据
                try {
                    jdbcTemplate.queryForObject(sqlFind, Integer.class);
                } catch (EmptyResultDataAccessException e) {
                    d=1;
                    //如果获取结果为空，代表所有子弹都没了，开启下一轮
                    s.append("\n子弹已空，开始新的回合...\n").append(LoadingBullets(byid, room));
                }
                break;
            }
            case 3: {//放大镜
                String Name = "bu_" + table;
                String sqlFind = "SELECT bu FROM " + Name + " WHERE bu <> -1 ORDER BY id LIMIT 1";

                // 从上向下查询第一个内容不为-1的数据
                Integer bu = jdbcTemplate.queryForObject(sqlFind, Integer.class);
                //如果bu为空  实际上不会出现
                if (bu == null) {
                    s.append("\n弹夹内子弹用光，准备开始下一轮...\n");
                    s.append(LoadProps(table));
                    break;
                }
                // 根据bu的值返回相应的结果
                if (bu == 0) {
                    s.append("道具-放大镜 使用成功，看样子，是空弹呢");
                } else {
                    s.append("道具-放大镜 使用成功，看样子，是实弹呢");
                }
                break;
            }
            case 4: {//华子
                // 1. 构建查询SQL，动态地使用 table 变量
                String sqlFind = "SELECT blood FROM " + table + " WHERE player_id = ?";
                String sqlUpdate = "UPDATE " + table + " SET blood = ? WHERE player_id = ?";

                // 执行查询获取blood值
                Integer blood = jdbcTemplate.queryForObject(sqlFind, new Object[] {id}, Integer.class);

                if (blood == null) {
                    // 如果blood值为null，可能表示没有找到对应的行  实际上不会出现
                    return "获取血量信息时数据错误..(｡•ˇ‸ˇ•｡)…";
                }

                // 2. 判断blood是否小于4
                if (blood >= 4) {
                    return "道具-华子 使用失败，因为当前血量已饱和";
                } else {
                    //判断血量是否为-1 即断电模式
                    if (blood == -1)
                        s.append("道具-华子 使用成功，由于进入断电模式，无法恢复血量");
                    else {
                        // 3. 如果小于4，则将该数值+1后修改回去
                        jdbcTemplate.update(sqlUpdate, blood + 1, id);
                        s.append("道具-华子 使用成功，回复一点血量，当前血量：").append(blood + 1).append("点");
                    }
                }
                break;
            }
            case 5: {//锁链
                // 检查 data2 和 data3 的值，并根据条件返回不同的结果
                String data2 = action.getBata2();
                String data3 = action.getBata3();
                if (!Objects.equals(data2, "0")) {
                    return "道具-锁链 使用失败，原因：每次开枪枪前只允许使用一次锁链";
                } else if (!Objects.equals(data3, "0")) {
                    return "道具-锁链 使用失败，原因：锁链的额外回合内不允许使用锁链";
                } else {
                    // 如果 data2 为 0，将其设置为 1 并保存
                    action.setBata2("1");
                    dActionRepository.save(action);
                    s.append( "道具-锁链 使用成功，禁锢对方一回合，即我方连续行动两回合");
                }
                break;
            }
        }

        // 构建更新SQL，动态地使用 table 变量和 l 列名
        String sqlUpdate = "UPDATE " + table + " SET " + l + " = " + l + " - 1, sum = sum - 1 WHERE player_id = ?";
        // 执行更新操作
        int updatedRows = jdbcTemplate.update(sqlUpdate, id);
        // 检查是否成功更新了行
        if (updatedRows == 0) {
            // 如果更新的行数为0，可能表示没有找到对应的行
            return ("更新道具时出现错误，未能将对应道具数量修改o(╥﹏╥)o");
        }
        if (d==0) {
            int x=action.getAct();
            s.append("\n").append(PropInformation(table, x));
        }
        return s.toString();
    }
}