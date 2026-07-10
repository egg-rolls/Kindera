package kindergarten;

import kindergarten.dao.ChildDao;
import kindergarten.entity.Child;
import kindergarten.service.ChildService;
import kindergarten.util.InitDatabase;
import java.util.List;

public class SearchVerifyTest {
    public static void main(String[] args) {
        new InitDatabase().init();

        ChildDao dao = new ChildDao();
        ChildService service = new ChildService();

        System.out.println("=== 搜索功能验证 ===\n");

        // 测试1：DAO层直接搜索
        System.out.println("[测试1] DAO层直接搜索'韩志豪'");
        List<Child> daoResult = dao.selectByName("韩志豪");
        System.out.println("  DAO返回: " + daoResult.size() + "条");
        for (Child c : daoResult) {
            System.out.printf("    ID:%d 姓名:%s 状态:%d\n", c.getId(), c.getName(), c.getStatus());
        }

        // 测试2：Service层搜索
        System.out.println("\n[测试2] Service层搜索'韩志豪'");
        List<Child> serviceResult = service.searchByName("韩志豪");
        System.out.println("  Service返回: " + serviceResult.size() + "条");
        for (Child c : serviceResult) {
            System.out.printf("    ID:%d 姓名:%s 状态:%d\n", c.getId(), c.getName(), c.getStatus());
        }

        // 测试3：按ID查询
        System.out.println("\n[测试3] 按ID查询34");
        Child byId = dao.selectById(34);
        if (byId != null) {
            System.out.printf("  ID:%d 姓名:%s 状态:%d\n", byId.getId(), byId.getName(), byId.getStatus());
        } else {
            System.out.println("  未找到");
        }

        // 测试4：搜索其他名字
        System.out.println("\n[测试4] 搜索'张小明'");
        List<Child> result4 = service.searchByName("张小明");
        System.out.println("  返回: " + result4.size() + "条");

        // 测试5：搜索部分名字
        System.out.println("\n[测试5] 搜索'韩'");
        List<Child> result5 = service.searchByName("韩");
        System.out.println("  返回: " + result5.size() + "条");

        // 测试6：搜索数字
        System.out.println("\n[测试6] 搜索'34'");
        List<Child> result6 = service.searchByName("34");
        System.out.println("  返回: " + result6.size() + "条");
    }
}
