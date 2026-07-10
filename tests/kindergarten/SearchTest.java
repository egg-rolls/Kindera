package kindergarten;

import kindergarten.dao.ChildDao;
import kindergarten.entity.Child;
import kindergarten.util.InitDatabase;
import java.util.List;

public class SearchTest {
    public static void main(String[] args) {
        new InitDatabase().init();
        ChildDao dao = new ChildDao();

        System.out.println("=== 搜索测试 ===");

        // 测试1：按姓名搜索
        List<Child> results = dao.selectByName("张小明");
        System.out.println("\n搜索'张小明'结果：");
        for (Child c : results) {
            System.out.printf("  ID:%d 姓名:%s 班级ID:%d 班级名:%s 状态:%d\n",
                c.getId(), c.getName(), c.getClassId(), c.getClassName(), c.getStatus());
        }

        // 测试2：按ID查询
        Child child = dao.selectById(1);
        System.out.println("\n按ID查询幼儿1：");
        if (child != null) {
            System.out.printf("  ID:%d 姓名:%s 班级ID:%d 班级名:%s\n",
                child.getId(), child.getName(), child.getClassId(), child.getClassName());
        }

        // 测试3：查询所有幼儿
        List<Child> all = dao.selectAll();
        System.out.println("\n所有幼儿（前5个）：");
        for (int i = 0; i < Math.min(5, all.size()); i++) {
            Child c = all.get(i);
            System.out.printf("  ID:%d 姓名:%s 班级ID:%d 班级名:%s\n",
                c.getId(), c.getName(), c.getClassId(), c.getClassName());
        }
    }
}
