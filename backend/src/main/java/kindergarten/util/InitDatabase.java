package kindergarten.util;

import java.sql.*;
import java.time.LocalDate;

/**
 * 数据库初始化类
 *
 * @author 开发团队
 * @date 2026-07-06
 * @version 1.0
 * @description 系统首次运行时自动创建数据库、建表、插入预置数据。
 *              使用CREATE IF NOT EXISTS确保可重复执行。
 */
public class InitDatabase {

    /**
     * 执行完整初始化流程
     */
    public void init() {
        System.out.println("[系统] 正在初始化数据库...");
        createDatabase();
        createTables();
        insertPresetData();
        System.out.println("[系统] 数据库初始化完成！");
    }

    /** 创建kindergarten数据库（如不存在） */
    private void createDatabase() {
        String sql = "CREATE DATABASE IF NOT EXISTS kindergarten DEFAULT CHARACTER SET utf8mb4";
        try (Connection conn = DBUtil.getBaseConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
            System.out.println("  ✓ 数据库 kindergarten 已就绪");
        } catch (SQLException e) {
            System.out.println("  ✗ 创建数据库失败：" + e.getMessage());
            throw new RuntimeException("数据库创建失败", e);
        }
    }

    /** 创建所有数据表 */
    private void createTables() {
        String[] sqls = {
            // 班级表
            "CREATE TABLE IF NOT EXISTS t_class_info (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '班级ID'," +
            "  class_name VARCHAR(20) NOT NULL UNIQUE COMMENT '班级名称'," +
            "  grade VARCHAR(10) NOT NULL COMMENT '年级'," +
            "  max_count INT NOT NULL DEFAULT 10 COMMENT '最大人数'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") COMMENT '班级信息表'",

            // 用户表
            "CREATE TABLE IF NOT EXISTS t_user (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID'," +
            "  username VARCHAR(30) NOT NULL UNIQUE COMMENT '用户名'," +
            "  password VARCHAR(64) NOT NULL COMMENT '密码'," +
            "  real_name VARCHAR(30) NOT NULL COMMENT '真实姓名'," +
            "  role TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1管理员 2教师'," +
            "  class_id INT DEFAULT NULL COMMENT '教师负责班级'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (class_id) REFERENCES t_class_info(id)" +
            ") COMMENT '用户表'",

            // 幼儿表
            "CREATE TABLE IF NOT EXISTS t_child (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '幼儿ID'," +
            "  name VARCHAR(50) NOT NULL COMMENT '姓名'," +
            "  gender CHAR(1) NOT NULL COMMENT '性别 M/F'," +
            "  birth_date DATE NOT NULL COMMENT '出生日期'," +
            "  parent_name VARCHAR(50) NOT NULL COMMENT '家长姓名'," +
            "  parent_phone VARCHAR(20) NOT NULL COMMENT '家长电话'," +
            "  class_id INT NOT NULL COMMENT '所在班级'," +
            "  enrollment_date DATE NOT NULL COMMENT '入园日期'," +
            "  status TINYINT NOT NULL DEFAULT 1 COMMENT '1在园 0离园'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  FOREIGN KEY (class_id) REFERENCES t_class_info(id)" +
            ") COMMENT '幼儿信息表'",

            // 课程表
            "CREATE TABLE IF NOT EXISTS t_course (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '课程ID'," +
            "  course_name VARCHAR(50) NOT NULL UNIQUE COMMENT '课程名'," +
            "  max_count INT NOT NULL DEFAULT 15 COMMENT '容量上限'," +
            "  description VARCHAR(200) DEFAULT NULL COMMENT '课程描述'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") COMMENT '兴趣课程表'",

            // 选课关系表
            "CREATE TABLE IF NOT EXISTS t_child_course (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  child_id INT NOT NULL COMMENT '幼儿ID'," +
            "  course_id INT NOT NULL COMMENT '课程ID'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  UNIQUE KEY uk_child_course (child_id, course_id)," +
            "  FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE," +
            "  FOREIGN KEY (course_id) REFERENCES t_course(id)" +
            ") COMMENT '选课关系表'",

            // 菜品表
            "CREATE TABLE IF NOT EXISTS t_dish (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID'," +
            "  dish_name VARCHAR(50) NOT NULL COMMENT '菜品名'," +
            "  dish_type TINYINT NOT NULL COMMENT '1主食 2荤菜 3素菜 4汤 5水果'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ") COMMENT '菜品库表'",

            // 每周食谱表
            "CREATE TABLE IF NOT EXISTS t_weekly_menu (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  week_start DATE NOT NULL COMMENT '周起始日期'," +
            "  day_of_week TINYINT NOT NULL COMMENT '1周一~5周五'," +
            "  meal_type TINYINT NOT NULL COMMENT '1早 2中 3晚'," +
            "  dish_id INT NOT NULL COMMENT '菜品ID'," +
            "  FOREIGN KEY (dish_id) REFERENCES t_dish(id)" +
            ") COMMENT '每周食谱表'",

            // 考勤表
            "CREATE TABLE IF NOT EXISTS t_attendance (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  child_id INT NOT NULL COMMENT '幼儿ID'," +
            "  attend_date DATE NOT NULL COMMENT '考勤日期'," +
            "  status TINYINT NOT NULL COMMENT '1出勤 2缺勤 3请假 4迟到'," +
            "  remark VARCHAR(100) DEFAULT NULL COMMENT '备注'," +
            "  create_time DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "  UNIQUE KEY uk_child_date (child_id, attend_date)," +
            "  FOREIGN KEY (child_id) REFERENCES t_child(id) ON DELETE CASCADE" +
            ") COMMENT '考勤记录表'",

            // 调班记录表
            "CREATE TABLE IF NOT EXISTS t_transfer_log (" +
            "  id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID'," +
            "  child_id INT NOT NULL COMMENT '幼儿ID'," +
            "  old_class_id INT NOT NULL COMMENT '原班级'," +
            "  new_class_id INT NOT NULL COMMENT '新班级'," +
            "  operator_id INT NOT NULL COMMENT '操作人'," +
            "  transfer_date DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '调班时间'," +
            "  remark VARCHAR(100) DEFAULT NULL COMMENT '备注'," +
            "  FOREIGN KEY (child_id) REFERENCES t_child(id)," +
            "  FOREIGN KEY (old_class_id) REFERENCES t_class_info(id)," +
            "  FOREIGN KEY (new_class_id) REFERENCES t_class_info(id)," +
            "  FOREIGN KEY (operator_id) REFERENCES t_user(id)" +
            ") COMMENT '调班记录表'"
        };

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                stmt.executeUpdate(sql);
            }
            System.out.println("  ✓ 数据表已就绪（共9张表）");
        } catch (SQLException e) {
            System.out.println("  ✗ 创建数据表失败：" + e.getMessage());
            throw new RuntimeException("建表失败", e);
        }
    }

    /** 插入预置数据（仅在表为空时插入） */
    private void insertPresetData() {
        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            // 检查是否已有数据
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM t_class_info");
            rs.next();
            if (rs.getInt(1) > 0) {
                System.out.println("  ✓ 预置数据已存在，跳过初始化");
                rs.close();
                return;
            }
            rs.close();

            // 1. 插入9个班级（容量多样化）
            String[] classData = {
                "('大一班','大班',25)", "('大二班','大班',25)", "('大三班','大班',20)",
                "('中一班','中班',25)", "('中二班','中班',25)", "('中三班','中班',20)",
                "('小一班','小班',20)", "('小二班','小班',20)", "('小三班','小班',15)"
            };
            for (String data : classData) {
                stmt.executeUpdate("INSERT INTO t_class_info(class_name,grade,max_count) VALUES" + data);
            }
            System.out.println("  ✓ 班级数据已插入（9个班级，容量15~25人）");

            // 2. 插入用户账号（密码SHA-256哈希存储）
            String adminHash = PasswordUtil.hash("admin123");
            stmt.executeUpdate("INSERT INTO t_user(username,password,real_name,role,class_id) " +
                "VALUES('admin','" + adminHash + "','系统管理员',1,NULL)");
            String teacherHash = PasswordUtil.hash("123456");
            String[] teacherNames = {"张敏","李芳","王静","刘婷","陈燕","杨丽","赵雪","周慧","吴琼"};
            for (int i = 1; i <= 9; i++) {
                stmt.executeUpdate(String.format(
                    "INSERT INTO t_user(username,password,real_name,role,class_id) " +
                    "VALUES('teacher%02d','%s','%s',2,%d)",
                    i, teacherHash, teacherNames[i - 1], i));
            }
            System.out.println("  ✓ 用户账号已插入（1管理员+9教师）");

            // 3. 插入8门兴趣课程（多样化）
            String[] courseData = {
                "('舞蹈',30,'培养幼儿舞蹈兴趣和形体美感，学习基本舞蹈动作和节奏感')",
                "('跆拳道',25,'强身健体，培养纪律意识和自我保护能力')",
                "('钢琴',15,'音乐启蒙，培养艺术修养和手指灵活性')",
                "('美术',30,'培养色彩感知、想象力和创造力')",
                "('书法',20,'培养专注力和汉字书写规范意识')",
                "('围棋',20,'锻炼逻辑思维和策略规划能力')",
                "('科学实验',25,'激发好奇心，培养观察和动手能力')",
                "('英语启蒙',25,'培养英语语感和基础口语表达能力')"
            };
            for (String data : courseData) {
                stmt.executeUpdate("INSERT INTO t_course(course_name,max_count,description) VALUES" + data);
            }
            System.out.println("  ✓ 兴趣课程已插入（8门，含书法/围棋/科学实验/英语启蒙）");

            // 4. 插入菜品库（32道菜品，更丰富多样）
            String[] dishes = {
                // 主食 (1~6)
                "('白米饭',1)", "('小米粥',1)", "('馒头',1)", "('面条',1)", "('花卷',1)", "('红豆粥',1)",
                // 荤菜 (7~14)
                "('红烧排骨',2)", "('糖醋里脊',2)", "('番茄炒蛋',2)", "('可乐鸡翅',2)",
                "('清蒸鲈鱼',2)", "('宫保鸡丁',2)", "('虾仁滑蛋',2)", "('牛肉炖土豆',2)",
                // 素菜 (15~22)
                "('清炒西兰花',3)", "('白菜炖豆腐',3)", "('醋溜土豆丝',3)", "('胡萝卜炒肉',3)",
                "('蒜蓉菠菜',3)", "('香菇青菜',3)", "('西红柿炒茄条',3)", "('玉米粒炒豌豆',3)",
                // 汤 (23~28)
                "('紫菜蛋花汤',4)", "('西红柿蛋汤',4)", "('玉米排骨汤',4)", "('冬瓜虾皮汤',4)",
                "('菠菜猪肝汤',4)", "('银耳莲子羹',4)",
                // 水果 (29~34)
                "('苹果',5)", "('香蕉',5)", "('橘子',5)", "('西瓜',5)", "('葡萄',5)", "('火龙果',5)"
            };
            for (String d : dishes) {
                stmt.executeUpdate("INSERT INTO t_dish(dish_name,dish_type) VALUES" + d);
            }
            System.out.println("  ✓ 菜品库已插入（34道菜品，6主食+8荤菜+8素菜+6汤+6水果）");

            // 5. 插入幼儿数据（多样化姓名，含少数民族风格）
            String[] boyNames = {
                // 汉族常见姓名
                "张小明","李小强","王小华","刘小杰","陈小宇",
                "杨小磊","赵小军","周小伟","吴小鹏","孙小浩",
                "马天宇","林子轩","黄志远","何建国","郑凯文",
                "罗俊杰","谢明辉","唐浩然","韩志豪","曹文博",
                // 更多风格
                "邓伟明","萧国庆","龙飞鸿","万嘉伟","段鹏程",
                "雷震宇","钱多多","尹志平","孔令辉","白敬亭",
                "石家豪","崔大伟","潘明轩","秦少华","尤浩然",
                "许文龙","龚子昂","严浩翔","贺子龙","向天歌",
                // 少数民族风格
                "阿木尔","巴图","扎西","丁真","木拉提",
                // 更多汉族
                "聂风华","廖俊逸","庞大海","樊少皇","顾明哲",
                "江浩然","方志远","叶俊杰","余天翔","田宇航"
            };
            String[] girlNames = {
                "李小红","王小芳","张小丽","刘小美","陈小雪",
                "杨小玲","赵小燕","周小莉","吴小娟","孙小婷",
                "马思纯","林心如","黄诗琪","何芷若","郑秀晶",
                "罗玉凤","谢雨欣","唐嫣然","韩冰冰","曹颖慧",
                "邓紫棋","萧亚轩","龙婉清","万绮雯","段思思",
                "钱佩玲","尹甜甜","孔雪儿","白雪晴","石雪兰",
                "崔文静","潘晓婷","秦海璐","尤小茹","许晴儿",
                "龚琳娜","严莉莉","贺子枫","向语嫣","聂小倩",
                // 少数民族风格
                "卓玛","古丽","阿依努尔","娜仁花","萨日娜",
                // 更多汉族
                "廖碧儿","庞晓燕","顾小婉","江雨桐","叶知秋",
                "余诗涵","田甜","方思琪","宋雅琴","董梦瑶"
            };
            String[] parentSuffix = {"爸爸","妈妈"};

            int childId = 1;
            int boyIndex = 0;
            int girlIndex = 0;
            // 不同入园日期
            String[] enrollDates = {"2024-09-01","2025-03-01","2025-09-01"};

            String childSql = "INSERT INTO t_child(name,gender,birth_date,parent_name,parent_phone,class_id,enrollment_date,status) " +
                              "VALUES(?,?,?,?,?,?,?,?)";
            try (PreparedStatement psChild = conn.prepareStatement(childSql)) {
                for (int classId = 1; classId <= 9; classId++) {
                    int birthYear;
                    if (classId <= 3) birthYear = 2020;      // 大班（5~6岁）
                    else if (classId <= 6) birthYear = 2021;  // 中班（4~5岁）
                    else birthYear = 2022;                    // 小班（3~4岁）

                    // 每班10人，但最后3个幼儿中有1个标记为离园（测试软删除）
                    int childCount = 10;
                    for (int i = 0; i < childCount; i++) {
                        boolean isBoy = i < 5;
                        String name = isBoy ? boyNames[boyIndex % boyNames.length] : girlNames[girlIndex % girlNames.length];
                        if (isBoy) boyIndex++; else girlIndex++;
                        String gender = isBoy ? "M" : "F";

                        // 家长姓名多样化
                        String[] pSuffix = {"爸爸","妈妈","爷爷","奶奶"};
                        String parentName = name.substring(0, Math.min(3, name.length())) + pSuffix[(childId + i) % pSuffix.length];

                        // 手机号多样化（不同运营商号段）
                        String[] prefixes = {"138","139","150","151","186","188","135","136"};
                        String phone = prefixes[childId % prefixes.length] + String.format("%08d", classId * 10000 + i * 100 + childId);

                        int month = 1 + (childId % 12);
                        int day = 1 + (childId % 28);
                        String birthDate = String.format("%d-%02d-%02d", birthYear, month, day);

                        // 每班最后1个幼儿标记为离园（测试软删除）
                        int status = (i == childCount - 1 && classId <= 3) ? 0 : 1;
                        String enrollDate = enrollDates[(childId + classId) % enrollDates.length];

                        psChild.setString(1, name);
                        psChild.setString(2, gender);
                        psChild.setString(3, birthDate);
                        psChild.setString(4, parentName);
                        psChild.setString(5, phone);
                        psChild.setInt(6, classId);
                        psChild.setString(7, enrollDate);
                        psChild.setInt(8, status);
                        psChild.executeUpdate();
                        childId++;
                    }
                }
            }
            System.out.println("  ✓ 幼儿数据已插入（90名幼儿，含3名已离园，多样化姓名和家长信息）");

            // 6. 选课数据（8门课程，更差异化分配）
            String ccSql = "INSERT INTO t_child_course(child_id,course_id) VALUES(?,?)";
            try (PreparedStatement psCC = conn.prepareStatement(ccSql)) {
                for (int cid = 1; cid <= 90; cid++) {
                    // 跳过已离园幼儿
                    boolean isDropped = (cid % 10 == 0 && cid <= 30);
                    if (isDropped) continue;

                    int courseCount = 2 + (cid % 4); // 2~5门（但最多4门由业务逻辑控制，这里用2~4）
                    courseCount = Math.min(courseCount, 4);
                    for (int j = 0; j < courseCount; j++) {
                        int courseId = 1 + ((cid + j * 7) % 8); // 课程1~8交叉分配
                        psCC.setInt(1, cid);
                        psCC.setInt(2, courseId);
                        psCC.executeUpdate();
                    }
                }
            }
            System.out.println("  ✓ 选课数据已插入（8门课程，差异化分配）");

            // 7. 插入上周食谱（测试"复制上周"功能）
            LocalDate thisMonday = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
            LocalDate lastMonday = thisMonday.minusWeeks(1);
            String menuSql = "INSERT INTO t_weekly_menu(week_start,day_of_week,meal_type,dish_id) VALUES(?,?,?,?)";
            try (PreparedStatement psMenu = conn.prepareStatement(menuSql)) {
                // 上周食谱
                insertWeekMenu(psMenu, lastMonday);
                // 本周食谱（不同搭配）
                insertWeekMenu(psMenu, thisMonday);
            }
            System.out.println("  ✓ 食谱数据已插入（上周+本周，每天早午晚餐完整搭配）");

            // 8. 插入最近10天考勤数据（多样化状态分布）
            LocalDate today = LocalDate.now();
            String attSql = "INSERT INTO t_attendance(child_id,attend_date,status,remark) VALUES(?,?,?,?)";
            try (PreparedStatement psAtt = conn.prepareStatement(attSql)) {
                for (int d = 0; d < 10; d++) {
                    LocalDate date = today.minusDays(d);
                    if (date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY ||
                        date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) continue;
                    for (int cid = 1; cid <= 90; cid++) {
                        // 跳过已离园幼儿
                        boolean isDropped = (cid % 10 == 0 && cid <= 30);
                        if (isDropped) continue;

                        // 更多样化的考勤分布
                        int status;
                        int hash = (cid * 31 + d * 17) % 100;
                        if (hash < 82) status = 1;       // 82% 出勤
                        else if (hash < 89) status = 2;   // 7% 缺勤
                        else if (hash < 95) status = 3;   // 6% 请假
                        else status = 4;                  // 5% 迟到

                        String remark = null;
                        if (status == 3) {
                            String[] reasons = {"感冒发烧","家里有事","身体不适","打预防针"};
                            remark = reasons[cid % reasons.length];
                        } else if (status == 4) {
                            remark = "交通拥堵";
                        } else if (status == 2) {
                            String[] reasons = {"未说明原因","家长请假未批"};
                            remark = reasons[cid % reasons.length];
                        }

                        psAtt.setInt(1, cid);
                        psAtt.setDate(2, java.sql.Date.valueOf(date));
                        psAtt.setInt(3, status);
                        psAtt.setString(4, remark);
                        psAtt.executeUpdate();
                    }
                }
            }
            System.out.println("  ✓ 考勤数据已插入（10天，含出勤/缺勤/请假/迟到，带备注）");

            // 9. 插入调班记录（6条，多样化原因）
            String[] transferData = {
                "(1,1,4,1,'适应能力较强，建议调至中班')",
                "(11,2,5,1,'家长要求调班，就近接送方便')",
                "(21,3,6,1,'原班级人数过多，均衡班级人数')",
                "(31,4,1,1,'幼儿调班申请：与好友同班')",
                "(41,5,2,1,'教师建议：该幼儿适合大班教学节奏')",
                "(51,6,3,1,'家长工作调动，需要调整接送路线')"
            };
            for (String data : transferData) {
                stmt.executeUpdate("INSERT INTO t_transfer_log(child_id,old_class_id,new_class_id,operator_id,remark) VALUES" + data);
            }
            System.out.println("  ✓ 调班记录已插入（6条，多样化原因）");

        } catch (SQLException e) {
            System.out.println("  ✗ 插入预置数据失败：" + e.getMessage());
            throw new RuntimeException("插入预置数据失败", e);
        }
    }

    /** 为指定周插入完整食谱（周一~周五，早午晚餐） */
    private void insertWeekMenu(PreparedStatement psMenu, LocalDate monday) throws SQLException {
        // 菜品ID：1~6主食, 7~14荤菜, 15~22素菜, 23~28汤, 29~34水果
        int[][] breakfast = {{1,29},{2,30},{3,31},{4,32},{5,33}}; // 主食+水果
        int[][] lunchMain = {{6,7,15,23},{1,8,16,24},{2,9,17,25},{3,10,18,26},{4,11,19,27}}; // 主食+荤菜+素菜+汤
        int[][] dinner = {{5,20,28},{6,21,23},{1,22,24},{2,15,25},{3,16,26}}; // 主食+素菜+汤

        for (int day = 1; day <= 5; day++) {
            int idx = day - 1;
            // 早餐：主食+水果
            psMenu.setDate(1, java.sql.Date.valueOf(monday));
            psMenu.setInt(2, day);
            psMenu.setInt(3, 1);
            psMenu.setInt(4, breakfast[idx][0]);
            psMenu.executeUpdate();
            psMenu.setInt(4, breakfast[idx][1]);
            psMenu.executeUpdate();

            // 午餐：主食+荤菜+素菜+汤
            for (int dishId : lunchMain[idx]) {
                psMenu.setDate(1, java.sql.Date.valueOf(monday));
                psMenu.setInt(2, day);
                psMenu.setInt(3, 2);
                psMenu.setInt(4, dishId);
                psMenu.executeUpdate();
            }

            // 晚餐：主食+素菜+汤
            for (int dishId : dinner[idx]) {
                psMenu.setDate(1, java.sql.Date.valueOf(monday));
                psMenu.setInt(2, day);
                psMenu.setInt(3, 3);
                psMenu.setInt(4, dishId);
                psMenu.executeUpdate();
            }
        }
    }

    /** 根据班级ID获取班级名称 */
    private String getClassName(int classId) {
        String[] names = {"大一班","大二班","大三班","中一班","中二班","中三班","小一班","小二班","小三班"};
        return names[classId - 1];
    }
}
